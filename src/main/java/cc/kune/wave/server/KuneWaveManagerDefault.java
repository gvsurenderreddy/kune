/*
 *
 * Copyright (C) 2007-2011 The kune development team (see CREDITS for details)
 * This file is part of kune.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cc.kune.wave.server;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.waveprotocol.box.server.CoreSettings;
import org.waveprotocol.box.server.robots.OperationContextImpl;
import org.waveprotocol.box.server.robots.OperationServiceRegistry;
import org.waveprotocol.box.server.robots.util.ConversationUtil;
import org.waveprotocol.box.server.robots.util.OperationUtil;
import org.waveprotocol.box.server.waveserver.WaveletProvider;
import org.waveprotocol.box.server.waveserver.WaveletProvider.SubmitRequestListener;
import org.waveprotocol.wave.model.id.InvalidIdException;
import org.waveprotocol.wave.model.version.HashedVersion;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.waveref.WaveRef;

import cc.kune.common.client.utils.TextUtils;
import cc.kune.core.client.errors.DefaultException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.wave.api.ApiIdSerializer;
import com.google.wave.api.Blip;
import com.google.wave.api.BlipData;
import com.google.wave.api.BlipThread;
import com.google.wave.api.Element;
import com.google.wave.api.Gadget;
import com.google.wave.api.JsonRpcConstant.ParamsProperty;
import com.google.wave.api.JsonRpcResponse;
import com.google.wave.api.OperationQueue;
import com.google.wave.api.OperationRequest;
import com.google.wave.api.OperationRequest.Parameter;
import com.google.wave.api.ProtocolVersion;
import com.google.wave.api.Wavelet;
import com.google.wave.api.data.converter.EventDataConverterManager;
import com.google.wave.api.impl.DocumentModifyAction;
import com.google.wave.api.impl.DocumentModifyAction.BundledAnnotation;
import com.google.wave.api.impl.DocumentModifyAction.ModifyHow;
import com.google.wave.api.impl.WaveletData;
import com.google.wave.splash.rpc.ClientAction;
import com.google.wave.splash.web.template.WaveRenderer;

public class KuneWaveManagerDefault implements KuneWaveManager {
  public static final Log LOG = LogFactory.getLog(KuneWaveManagerDefault.class);

  // See: DocumentModifyServiceTest
  private static final String NO_ANNOTATION_KEY = null;
  private static final List<BundledAnnotation> NO_BUNDLED_ANNOTATIONS = Collections.emptyList();
  private static final String NO_TITLE = "";

  private static final List<String> NO_VALUES = Collections.<String> emptyList();

  private final ConversationUtil conversationUtil;
  private final EventDataConverterManager converterManager;
  private final String domain;
  private final OperationServiceRegistry operationRegistry;
  private final ParticipantUtils participantUtils;
  private final WaveletProvider waveletProvider;

  private final WaveRenderer waveRenderer;

  @Inject
  public KuneWaveManagerDefault(final EventDataConverterManager converterManager,
      @Named("DataApiRegistry") final OperationServiceRegistry operationRegistry,
      final WaveletProvider waveletProvider, final ConversationUtil conversationUtil,
      final ParticipantUtils participantUtils, final WaveRenderer waveRenderer,
      @Named(CoreSettings.WAVE_SERVER_DOMAIN) final String domain) {
    this.converterManager = converterManager;
    this.waveletProvider = waveletProvider;
    this.conversationUtil = conversationUtil;
    this.operationRegistry = operationRegistry;
    this.participantUtils = participantUtils;
    this.waveRenderer = waveRenderer;
    this.domain = domain;
  }

  @Override
  public void addGadget(final WaveRef waveName, final String author, final String gadgetUrl) {
    // See DocumentModifyServiceTest
    final List<Element> elementsIn = Lists.newArrayListWithCapacity(1);
    final Map<String, String> properties = Maps.newHashMap();
    properties.put(Gadget.URL, gadgetUrl);
    properties.put(Gadget.AUTHOR, participantUtils.of(author).getAddress());
    final Gadget gadget = new Gadget(properties);

    elementsIn.add(gadget);
    final Wavelet wavelet = fetchWavelet(waveName, author);
    final OperationQueue opQueue = new OperationQueue();
    final Blip rootBlip = wavelet.getRootBlip();

    opQueue.modifyDocument(rootBlip).addParameter(
        Parameter.of(ParamsProperty.MODIFY_ACTION, new DocumentModifyAction(ModifyHow.INSERT, NO_VALUES,
            NO_ANNOTATION_KEY, elementsIn, NO_BUNDLED_ANNOTATIONS, false)));
    opQueue.modifyDocument(rootBlip).addParameter(Parameter.of(ParamsProperty.INDEX, 1));
    doOperation(author, opQueue, "add gadget");
  }

  @Override
  public void addParticipant(final WaveRef waveName, final String author, final String userWhoAdds,
      final String participant) {
    final Wavelet wavelet = fetchWavelet(waveName, author);
    final String whoAdd = wavelet.getParticipants().contains(participantUtils.of(userWhoAdds)) ? userWhoAdds
        : author;
    final OperationQueue opQueue = new OperationQueue();
    opQueue.addParticipantToWavelet(wavelet, participantUtils.of(participant).toString());
    doOperation(whoAdd, opQueue, "add participant");

  }

  @Override
  public WaveRef createWave(final String message, final ParticipantId participant) {
    return createWave(NO_TITLE, message, participant);
  }

  @Override
  public WaveRef createWave(@Nonnull final String title, final String message,
      @Nonnull final ParticipantId... participantsArray) {
    return createWave(title, message, WITHOUT_GADGET, participantsArray);
  }

  @Override
  public WaveRef createWave(@Nonnull final String title, final String message, final URL gadgetUrl,
      @Nonnull final ParticipantId... participantsArray) {
    String newWaveId = null;
    String newWaveletId = null;
    final Set<String> participants = new HashSet<String>();
    for (final ParticipantId participant : participantsArray) {
      participants.add(participant.toString());
    }
    final ParticipantId user = participantsArray[0];
    final OperationQueue opQueue = new OperationQueue();
    final Wavelet newWavelet = opQueue.createWavelet(domain, participants);
    opQueue.setTitleOfWavelet(newWavelet, title);
    final Blip rootBlip = newWavelet.getRootBlip();
    // rootBlip.append(new com.google.wave.api.Markup(message).getText());
    rootBlip.appendMarkup(message);
    if (gadgetUrl != WITHOUT_GADGET) {
      final Gadget gadget = new Gadget(gadgetUrl.toString());
      rootBlip.append(gadget);
    }

    final OperationContextImpl context = new OperationContextImpl(waveletProvider,
        converterManager.getEventDataConverter(ProtocolVersion.DEFAULT), conversationUtil);
    for (final OperationRequest req : opQueue.getPendingOperations()) {
      OperationUtil.executeOperation(req, operationRegistry, context, user);
      final String reqId = req.getId();
      final JsonRpcResponse response = context.getResponse(reqId);
      if (response != null) {
        if (response.isError()) {
          onFailure(context.getResponse(reqId).getErrorMessage());
        } else {
          final Object responseWaveId = response.getData().get(ParamsProperty.WAVE_ID);
          final Object responseWaveletId = response.getData().get(ParamsProperty.WAVELET_ID);
          if (responseWaveId != null && responseWaveletId != null) {
            // This is serialized use
            // ApiIdSerializer.instance().deserialiseWaveId (see
            // WaveService)
            newWaveId = (String) responseWaveId;
            newWaveletId = (String) responseWaveletId;
          }
        }
      }
    }
    OperationUtil.submitDeltas(context, waveletProvider, new SubmitRequestListener() {
      @Override
      public void onFailure(final String arg0) {
        KuneWaveManagerDefault.this.onFailure("Wave creation failed, onFailure: " + arg0);
      }

      @Override
      public void onSuccess(final int arg0, final HashedVersion arg1, final long arg2) {
        LOG.info("Wave creation success: " + arg1);
      }
    });
    LOG.info("WaveId: " + newWaveId + " waveletId: " + newWaveletId);
    WaveRef wavename;
    try {
      wavename = WaveRef.of(ApiIdSerializer.instance().deserialiseWaveId(newWaveId),
          ApiIdSerializer.instance().deserialiseWaveletId(newWaveletId));
    } catch (final InvalidIdException e) {
      throw new DefaultException("Error getting wave id");
    }
    return wavename;
  }

  private void doOperation(final String author, final OperationQueue opQueue, final String logComment) {
    final OperationContextImpl context = new OperationContextImpl(waveletProvider,
        converterManager.getEventDataConverter(ProtocolVersion.DEFAULT), conversationUtil);
    final OperationRequest request = opQueue.getPendingOperations().get(0);
    OperationUtil.executeOperation(request, operationRegistry, context, participantUtils.of(author));
    final String reqId = request.getId();
    final JsonRpcResponse response = context.getResponse(reqId);
    if (response != null && response.isError()) {
      onFailure(context.getResponse(reqId).getErrorMessage());
    }
    OperationUtil.submitDeltas(context, waveletProvider, new SubmitRequestListener() {
      @Override
      public void onFailure(final String arg0) {
        KuneWaveManagerDefault.this.onFailure("Wave " + logComment + " failed, onFailure: " + arg0);
      }

      @Override
      public void onSuccess(final int arg0, final HashedVersion arg1, final long arg2) {
        LOG.info("Wave " + logComment + " success: " + arg1);
      }
    });
  }

  public void doOperations(final WaveRef waveName, final String author, final OperationQueue opQueue,
      final SubmitRequestListener listener) {
    final OperationContextImpl context = new OperationContextImpl(waveletProvider,
        converterManager.getEventDataConverter(ProtocolVersion.DEFAULT), conversationUtil);
    for (final OperationRequest req : opQueue.getPendingOperations()) {
      OperationUtil.executeOperation(req, operationRegistry, context, participantUtils.of(author));
    }
    OperationUtil.submitDeltas(context, waveletProvider, listener);
  }

  @Override
  public Wavelet fetchWavelet(final WaveRef waveName, final String author) {
    Wavelet wavelet = null;
    final OperationQueue opQueue = new OperationQueue();
    opQueue.fetchWavelet(waveName.getWaveId(), waveName.getWaveletId());
    final OperationContextImpl context = new OperationContextImpl(waveletProvider,
        converterManager.getEventDataConverter(ProtocolVersion.DEFAULT), conversationUtil);
    final OperationRequest request = opQueue.getPendingOperations().get(0);
    OperationUtil.executeOperation(request, operationRegistry, context, participantUtils.of(author));
    final String reqId = request.getId();
    final JsonRpcResponse response = context.getResponse(reqId);
    if (response != null && response.isError()) {
      onFailure(context.getResponse(reqId).getErrorMessage());
    } else {
      // Duplicate code from WaveService
      final WaveletData waveletData = (WaveletData) response.getData().get(ParamsProperty.WAVELET_DATA);
      final Map<String, Blip> blips = new HashMap<String, Blip>();
      final Map<String, BlipThread> threads = new HashMap<String, BlipThread>();
      wavelet = Wavelet.deserialize(opQueue, blips, threads, waveletData);

      // Deserialize threads.
      @SuppressWarnings("unchecked")
      final Map<String, BlipThread> tempThreads = (Map<String, BlipThread>) response.getData().get(
          ParamsProperty.THREADS);
      for (final Map.Entry<String, BlipThread> entry : tempThreads.entrySet()) {
        final BlipThread thread = entry.getValue();
        threads.put(entry.getKey(),
            new BlipThread(thread.getId(), thread.getLocation(), thread.getBlipIds(), blips));
      }

      // Deserialize blips.
      @SuppressWarnings("unchecked")
      final Map<String, BlipData> blipDatas = (Map<String, BlipData>) response.getData().get(
          ParamsProperty.BLIPS);
      for (final Map.Entry<String, BlipData> entry : blipDatas.entrySet()) {
        blips.put(entry.getKey(), Blip.deserialize(opQueue, wavelet, entry.getValue()));
      }
    }
    return wavelet;
  }

  @Override
  public boolean isParticipant(final Wavelet wavelet, final String user) {
    return wavelet.getParticipants().contains(participantUtils.of(user).toString());
  }

  private void onFailure(final String message) {
    final String errorMsg = TextUtils.notEmpty(message) ? message : "Wave operation failed";
    LOG.error(errorMsg);
    throw new DefaultException(errorMsg);
  }

  @Override
  public String render(final Wavelet wavelet) {
    final ClientAction clientPage = waveRenderer.render(wavelet, 0);
    return clientPage.getHtml();
  }

  @Override
  public String render(final WaveRef waveRef, final String author) {
    return render(fetchWavelet(waveRef, author));
  }

  @Override
  public void setTitle(final WaveRef waveName, final String title, final String author) {
    final Wavelet wavelet = fetchWavelet(waveName, author);
    final OperationQueue opQueue = new OperationQueue();
    opQueue.setTitleOfWavelet(wavelet, title);
    doOperation(author, opQueue, "set title");
  }
}
