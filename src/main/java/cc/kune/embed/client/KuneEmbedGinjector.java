/*
 *
 * Copyright (C) 2007-2015 Licensed to the Comunes Association (CA) under
 * one or more contributor license agreements (see COPYRIGHT for details).
 * The CA licenses this file to you under the GNU Affero General Public
 * License version 3, (the "License"); you may not use this file except in
 * compliance with the License. This file is part of kune.
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
package cc.kune.embed.client;

import cc.kune.bootstrap.client.BSGuiProvider;
import cc.kune.common.client.actions.ui.GuiProvider;
import cc.kune.common.client.events.EventBusWithLogging;
import cc.kune.common.shared.i18n.I18nTranslationService;
import cc.kune.core.client.cookies.CookiesManager;
import cc.kune.core.client.errors.ErrorHandler;
import cc.kune.core.client.sitebar.ErrorsDialog;
import cc.kune.core.client.state.SiteTokenListeners;
import cc.kune.core.client.state.TokenMatcher;
import cc.kune.embed.client.panels.EmbedPresenter;
import cc.kune.embed.client.panels.EmbedSitebar;
import cc.kune.wave.client.kspecific.WaveEmbedGinModule;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Interface KuneEmbedGinjector.
 *
 * @author vjrj@ourproject.org (Vicente J. Ruiz Jurado)
 */
@GinModules(value = { EmbedCoreGinModule.class, WaveEmbedGinModule.class }, properties = { "gin.ginjector.modules" })
public interface KuneEmbedGinjector extends Ginjector {
  // FIXME: Seems that hablar is needed in some point (Wave actions, probably)

  /*
   * You have to add here all the GWTPresenters (as Provider or AsyncProvider)
   * see the GWTPlatform doc
   */

  /**
   * Gets the gwt gui provider.
   *
   * @return the gwt gui provider
   */
  BSGuiProvider getBSGuiProvider();

  /**
   * Gets the cookies manager.
   *
   * @return the cookies manager
   */
  AsyncProvider<CookiesManager> getCookiesManager();

  Provider<EmbedPresenter> getEmbedPresenter();

  EmbedSitebar getEmbedSitebar();

  /**
   * Gets the error handler.
   *
   * @return the error handler
   */
  ErrorHandler getErrorHandler();

  /**
   * Gets the errors dialog.
   *
   * @return the errors dialog
   */
  ErrorsDialog getErrorsDialog();

  /**
   * Gets the event bus.
   *
   * @return the event bus
   */
  EventBus getEventBus();

  /**
   * Gets the event logger.
   *
   * @return the event logger
   */
  EventBusWithLogging getEventBusWithLogger();

  /**
   * Gets the gui provider.
   *
   * @return the gui provider
   */
  GuiProvider getGuiProvider();

  /**
   * Gets the i18n.
   *
   * @return the i18n
   */
  I18nTranslationService getI18n();

  /**
   * Gets the site token listeners.
   *
   * @return the site token listeners
   */
  SiteTokenListeners getSiteTokenListeners();

  /**
   * Gets the token matcher.
   *
   * @return the token matcher
   */
  TokenMatcher getTokenMatcher();

}
