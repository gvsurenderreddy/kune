/*
 *
 * Copyright (C) 2007-2012 The kune development team (see CREDITS for details)
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
package cc.kune.chat.client;

import java.util.Date;

import cc.kune.chat.client.ShowChatDialogEvent.ShowChatDialogHandler;
import cc.kune.chat.client.ToggleShowChatDialogEvent.ToggleShowChatDialogHandler;
import cc.kune.chat.client.resources.ChatResources;
import cc.kune.chat.client.snd.KuneSoundManager;
import cc.kune.common.client.actions.AbstractExtendedAction;
import cc.kune.common.client.actions.Action;
import cc.kune.common.client.actions.ActionEvent;
import cc.kune.common.client.actions.ActionStyles;
import cc.kune.common.client.actions.KeyStroke;
import cc.kune.common.client.actions.Shortcut;
import cc.kune.common.client.actions.ui.ParentWidget;
import cc.kune.common.client.actions.ui.descrip.IconLabelDescriptor;
import cc.kune.common.client.actions.ui.descrip.ToolbarSeparatorDescriptor;
import cc.kune.common.client.actions.ui.descrip.ToolbarSeparatorDescriptor.Type;
import cc.kune.common.client.log.Log;
import cc.kune.common.client.notify.NotifyUser;
import cc.kune.common.client.shortcuts.GlobalShortcutRegister;
import cc.kune.common.client.utils.WindowUtils;
import cc.kune.common.shared.i18n.I18nTranslationService;
import cc.kune.common.shared.utils.SimpleResponseCallback;
import cc.kune.common.shared.utils.TextUtils;
import cc.kune.core.client.events.AppStartEvent;
import cc.kune.core.client.events.AppStopEvent;
import cc.kune.core.client.events.AvatarChangedEvent;
import cc.kune.core.client.events.NewUserRegisteredEvent;
import cc.kune.core.client.events.UserSignInEvent;
import cc.kune.core.client.events.UserSignInEvent.UserSignInHandler;
import cc.kune.core.client.events.UserSignOutEvent;
import cc.kune.core.client.events.UserSignOutEvent.UserSignOutHandler;
import cc.kune.core.client.resources.CoreResources;
import cc.kune.core.client.services.ClientFileDownloadUtils;
import cc.kune.core.client.sitebar.SitebarActions;
import cc.kune.core.client.state.Session;
import cc.kune.core.shared.dto.UserInfoDTO;

import com.calclab.emite.core.client.xmpp.session.XmppSession;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURI;
import com.calclab.emite.im.client.chat.ChatManager;
import com.calclab.emite.im.client.presence.PresenceManager;
import com.calclab.emite.im.client.roster.RosterItem;
import com.calclab.emite.im.client.roster.SubscriptionHandler;
import com.calclab.emite.im.client.roster.SubscriptionHandler.Behaviour;
import com.calclab.emite.im.client.roster.SubscriptionManager;
import com.calclab.emite.im.client.roster.SubscriptionState;
import com.calclab.emite.im.client.roster.XmppRoster;
import com.calclab.emite.im.client.roster.events.SubscriptionRequestReceivedEvent;
import com.calclab.emite.im.client.roster.events.SubscriptionRequestReceivedHandler;
import com.calclab.emite.xep.avatar.client.AvatarManager;
import com.calclab.emite.xep.chatstate.client.StateManager;
import com.calclab.emite.xep.muc.client.Room;
import com.calclab.emite.xep.muc.client.RoomManager;
import com.calclab.emite.xep.muc.client.subject.RoomSubject;
import com.calclab.emite.xep.mucchatstate.client.MUCChatStateManager;
import com.calclab.emite.xep.mucdisco.client.RoomDiscoveryManager;
import com.calclab.emite.xep.storage.client.PrivateStorageManager;
import com.calclab.hablar.chat.client.HablarChat;
import com.calclab.hablar.client.HablarConfig;
import com.calclab.hablar.core.client.Hablar;
import com.calclab.hablar.core.client.HablarCore;
import com.calclab.hablar.core.client.browser.BrowserFocusHandler;
import com.calclab.hablar.dock.client.HablarDock;
import com.calclab.hablar.editbuddy.client.HablarEditBuddy;
import com.calclab.hablar.group.client.HablarGroup;
import com.calclab.hablar.groupchat.client.HablarGroupChat;
import com.calclab.hablar.icons.client.AvatarProviderRegistry;
import com.calclab.hablar.openchat.client.HablarOpenChat;
import com.calclab.hablar.rooms.client.HablarRooms;
import com.calclab.hablar.roster.client.HablarRoster;
import com.calclab.hablar.roster.client.page.RosterPage;
import com.calclab.hablar.signals.client.sound.HablarSoundSignals;
import com.calclab.hablar.user.client.HablarUser;
import com.calclab.hablar.usergroups.client.HablarUserGroups;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ChatClientDefault implements ChatClient {

  public class ChatClientAction extends AbstractExtendedAction {

    private final ChatResources res;

    public ChatClientAction(final ChatResources res) {
      super();
      this.res = res;
      kuneEventBus.addHandler(NewUserRegisteredEvent.getType(),
          new NewUserRegisteredEvent.NewUserRegisteredHandler() {
            @Override
            public void onNewUserRegistered(final NewUserRegisteredEvent event) {
              // Blink the chat some seconds
              setBlink(true);
              new Timer() {
                @Override
                public void run() {
                  setBlink(false);
                }
              }.schedule(20000);
            }
          });
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      kuneEventBus.fireEvent(new ToggleShowChatDialogEvent());
    }

    public void setBlink(final boolean blink) {
      final ImageResource icon = blink ? res.chatBlink() : res.chatNoBlink();
      putValue(Action.SMALL_ICON, icon);
      dialog.setIcon(AbstractImagePrototype.create(icon));
    }

  }

  private static final String CHAT_TITLE = "Chat ;)";
  private final ChatClientAction action;
  private final Provider<KuneChatAvatarConfig> avatarConfig;
  private final Provider<AvatarManager> avatarManager;
  private final Provider<AvatarProviderRegistry> avatarProviderRegistry;
  protected IconLabelDescriptor chatIcon;
  private final Provider<ChatManager> chatManager;
  private final ChatOptions chatOptions;
  private final ChatResources chatResources;
  private Dialog dialog;
  private final ClientFileDownloadUtils downUtils;
  private final I18nTranslationService i18n;
  private final EventBus kuneEventBus;
  private final Provider<MUCChatStateManager> mucChatStateManager;
  private final Provider<PresenceManager> presenceManager;
  private final Provider<PrivateStorageManager> privateStorageManager;
  private final CoreResources res;
  private final Provider<RoomDiscoveryManager> roomDiscoveryManager;
  private final Provider<RoomManager> roomManager;
  private final Provider<XmppRoster> roster;
  private final Session session;
  private final GlobalShortcutRegister shorcutRegister;
  private final Provider<SubscriptionHandler> subscriptionHandler;
  private final Provider<SubscriptionManager> subscriptionManager;
  private final Provider<XmppSession> xmppSession;
  private final Provider<StateManager> xmppStateManager;

  @Inject
  public ChatClientDefault(final EventBus kuneEventBus, final I18nTranslationService i18n,
      final SitebarActions siteActions, final Session session, final CoreResources res,
      final ClientFileDownloadUtils downUtils, final GlobalShortcutRegister shorcutRegister,
      final ChatOptions chatOptions, final ChatResources chatResources,
      final Provider<XmppSession> xmppSession, final Provider<XmppRoster> roster,
      final Provider<ChatManager> chatManager, final Provider<RoomManager> roomManager,
      final Provider<AvatarManager> avatarManager,
      final Provider<SubscriptionManager> subscriptionManager,
      final Provider<PresenceManager> presenceManager, final Provider<StateManager> xmppStateManager,
      final Provider<RoomDiscoveryManager> roomDiscoveryManager,
      final Provider<MUCChatStateManager> mucChatStateManager,
      final Provider<AvatarProviderRegistry> avatarProviderRegistry,
      final Provider<PrivateStorageManager> privateStorageManager,
      final Provider<SubscriptionHandler> subscriptionHandler,
      final Provider<KuneChatAvatarConfig> avatarConfig) {
    this.kuneEventBus = kuneEventBus;
    this.i18n = i18n;
    this.res = res;
    this.downUtils = downUtils;
    this.presenceManager = presenceManager;
    this.xmppStateManager = xmppStateManager;
    this.roomDiscoveryManager = roomDiscoveryManager;
    this.mucChatStateManager = mucChatStateManager;
    this.avatarProviderRegistry = avatarProviderRegistry;
    this.privateStorageManager = privateStorageManager;
    this.subscriptionHandler = subscriptionHandler;
    this.session = session;
    this.shorcutRegister = shorcutRegister;
    this.chatOptions = chatOptions;
    this.chatResources = chatResources;
    this.xmppSession = xmppSession;
    this.roster = roster;
    this.chatManager = chatManager;
    this.roomManager = roomManager;
    this.avatarManager = avatarManager;
    this.subscriptionManager = subscriptionManager;
    this.avatarConfig = avatarConfig;
    action = new ChatClientAction(chatResources);

    session.onAppStart(true, new AppStartEvent.AppStartHandler() {
      @Override
      public void onAppStart(final AppStartEvent event) {
        chatOptions.domain = event.getInitData().getChatDomain();
        chatOptions.httpBase = event.getInitData().getChatHttpBase();
        chatOptions.roomHost = event.getInitData().getChatRoomHost();
        checkChatDomain(chatOptions.domain);
        session.onUserSignIn(true, new UserSignInHandler() {
          @Override
          public void onUserSignIn(final UserSignInEvent event) {
            doLogin(event.getPassword());
          }
        });
        session.onUserSignOut(true, new UserSignOutHandler() {
          @Override
          public void onUserSignOut(final UserSignOutEvent event) {
            createActionIfNeeded();
            chatIcon.setVisible(false);
            logout();
          }
        });
        kuneEventBus.addHandler(ShowChatDialogEvent.getType(), new ShowChatDialogHandler() {
          @Override
          public void onShowChatDialog(final ShowChatDialogEvent event) {
            createActionIfNeeded();
            showDialog(event.show);
          }
        });
        kuneEventBus.addHandler(ToggleShowChatDialogEvent.getType(), new ToggleShowChatDialogHandler() {
          @Override
          public void onToggleShowChatDialog(final ToggleShowChatDialogEvent event) {
            toggleShowDialog();
          }
        });
        kuneEventBus.addHandler(AvatarChangedEvent.getType(),
            new AvatarChangedEvent.AvatarChangedHandler() {
              @Override
              public void onAvatarChanged(final AvatarChangedEvent event) {
                setAvatar(event.getPhotoBinary());
              }
            });
      }
    });
    kuneEventBus.addHandler(AppStopEvent.getType(), new AppStopEvent.AppStopHandler() {
      @Override
      public void onAppStop(final AppStopEvent event) {
        logout();
      }
    });
  }

  @Override
  public void addNewBuddy(final String shortName) {
    roster.get().requestAddItem(uriFrom(shortName), shortName);
  }

  @Override
  public void chat(final String shortName) {
    chat(uriFrom(shortName));
  }

  @Override
  public void chat(final XmppURI jid) {
    chatManager.get().open(jid);
  }

  // Put this in Panel object
  private void checkChatDomain(final String chatDomain) {
    final String httpDomain = WindowUtils.getHostName();
    if (!chatDomain.equals(httpDomain)) {
      Log.error("Your http domain (" + httpDomain + ") is different from the chat domain (" + chatDomain
          + "). This will cause problems with the chat functionality. "
          + "Please check kune.properties on the server.");
    }
  }

  private void createActionIfNeeded() {
    if (chatIcon == null) {
      chatIcon = new IconLabelDescriptor(action);
      chatIcon.setParent(SitebarActions.LEFT_TOOLBAR);
      chatIcon.setId(CHAT_CLIENT_ICON_ID);
      chatIcon.setStyles(ActionStyles.SITEBAR_STYLE + ", k-chat-icon");
      chatIcon.putValue(Action.NAME, i18n.t(CHAT_TITLE));
      chatIcon.putValue(Action.SMALL_ICON, chatResources.chatNoBlink());
      chatIcon.putValue(Action.TOOLTIP, i18n.t("Show/hide the chat window"));
      final KeyStroke shortcut = Shortcut.getShortcut(false, true, false, false, Character.valueOf('C'));
      shorcutRegister.put(shortcut, action);
      action.setShortcut(shortcut);
      chatIcon.setVisible(session.isLogged());
      ToolbarSeparatorDescriptor.build(Type.spacer, SitebarActions.LEFT_TOOLBAR);
      ToolbarSeparatorDescriptor.build(Type.spacer, SitebarActions.LEFT_TOOLBAR);
      ToolbarSeparatorDescriptor.build(Type.spacer, SitebarActions.LEFT_TOOLBAR);
    }
  }

  private void createDialog(final KuneHablarWidget widget, final CustomHtmlConfig htmlConfig) {
    widget.addStyleName("k-chat-panel");
    setSize(widget, htmlConfig);
    dialog.add(widget);
  }

  private void createDialogIfNeeded() {
    if (dialog == null) {
      dialog = new Dialog();
      dialog.setHeadingText(i18n.t(CHAT_TITLE));
      dialog.setClosable(true);
      dialog.setResizable(true);
      dialog.setButtons("");
      dialog.setBodyStyleName("k-chat-window");
      dialog.setScrollMode(Scroll.NONE);
      dialog.setHideOnButtonClick(true);
      dialog.setCollapsible(true);
      dialog.setPosition(0, 0);
      dialog.setIcon(AbstractImagePrototype.create(chatResources.chatNoBlink()));
      // dialog.getItem(0).getFocusSupport().setIgnore(true);
      initEmite();
    }
  }

  private boolean dialogVisible() {
    return dialog != null && dialog.isVisible();
  }

  @Override
  public void doLogin() {
    doLogin(null);
  }

  private void doLogin(final String password) {
    assert session.getCurrentUserInfo() != null;
    doLogin(session.getCurrentUserInfo(), password == null ? session.getUserHash() : password);
  }

  private void doLogin(final UserInfoDTO user, final String tokenOrPassword) {
    createActionIfNeeded();
    createDialogIfNeeded();
    chatOptions.username = user.getChatName();
    chatOptions.passwd = tokenOrPassword;
    chatOptions.resource = "emite-" + new Date().getTime() + "-kune";
    chatOptions.useruri = XmppURI.uri(chatOptions.username, chatOptions.domain, chatOptions.resource);
    createActionIfNeeded();
    createDialogIfNeeded();
    chatIcon.setVisible(true);
    login(chatOptions.useruri, chatOptions.passwd);
  }

  private void initEmite() {
    // Adapted from HablarHtml.java
    BrowserFocusHandler.getInstance();
    final HablarConfig config = HablarConfig.getFromMeta();
    final CustomHtmlConfig htmlConfig = CustomHtmlConfig.getFromMeta();

    config.dockConfig.headerSize = 0;
    config.dockConfig.rosterWidth = 150;
    config.dockConfig.rosterDock = "left";
    final KuneHablarWidget widget = new KuneHablarWidget(config.layout, config.tabHeaderSize);
    final Hablar hablar = widget.getHablar();
    avatarProviderRegistry.get().put("kune-avatars", avatarConfig.get());

    new HablarCore(hablar);
    new HablarChat(hablar, config.chatConfig, roster.get(), chatManager.get(), xmppStateManager.get(),
        avatarProviderRegistry.get());
    new HablarRooms(hablar, config.roomsConfig, xmppSession.get(), roster.get(), roomManager.get(),
        roomDiscoveryManager.get(), mucChatStateManager.get(), avatarProviderRegistry.get());
    new HablarGroupChat(hablar, config.roomsConfig, xmppSession.get(), roster.get(), chatManager.get(),
        roomManager.get(), avatarProviderRegistry.get());
    new HablarDock(hablar, config.dockConfig);
    new HablarUser(hablar, xmppSession.get(), presenceManager.get(), privateStorageManager.get());

    final HablarRoster hablarRoster = new HablarRoster(hablar, config.rosterConfig, xmppSession.get(),
        roster.get(), chatManager.get(), subscriptionHandler.get());
    final RosterPage rosterPage = hablarRoster.getRosterPage();

    new HablarOpenChat(hablar, xmppSession.get(), roster.get(), chatManager.get());
    new HablarEditBuddy(hablar, roster.get());
    new HablarUserGroups(rosterPage, hablar, roster.get());
    new HablarGroup(hablar, xmppSession.get(), roster.get(), avatarProviderRegistry.get());
    hablarRoster.addLowPriorityActions();

    new KuneHablarSignals(kuneEventBus, xmppSession.get(), hablar, action, privateStorageManager.get(),
        i18n, downUtils);
    new HablarSoundSignals(hablar);

    // if (htmlConfig.hasLogger) {
    // new HablarConsole(hablar, ginjector.getXmppConnection(), session);
    // }
    //
    // if (htmlConfig.hasLogin) {
    // new HablarLogin(hablar, LoginConfig.getFromMeta(), session);
    // }

    new KuneSoundManager(kuneEventBus);
    createDialog(widget, htmlConfig);
    subscriptionHandler.get().setBehaviour(Behaviour.none);
    subscriptionManager.get().addSubscriptionRequestReceivedHandler(
        new SubscriptionRequestReceivedHandler() {
          @Override
          public void onSubscriptionRequestReceived(final SubscriptionRequestReceivedEvent event) {
            final XmppURI uri = event.getFrom();
            final String nick = event.getNick();
            NotifyUser.askConfirmation(res.question32(), i18n.t("Confirm new buddy"), i18n.t(
                "[%s] had added you as a buddy. Do you want to add him/her also?",
                uri.getJID().toString()), new SimpleResponseCallback() {
              @Override
              public void onCancel() {
                subscriptionManager.get().refuseSubscriptionRequest(uri.getJID());
              }

              @Override
              public void onSuccess() {
                subscriptionManager.get().approveSubscriptionRequest(uri.getJID(), nick);
              }
            });
          }
        });
  }

  @Override
  public boolean isBuddy(final String shortName) {
    return isBuddy(uriFrom(shortName));
  }

  @Override
  public boolean isBuddy(final XmppURI jid) {
    if (roster.get().isRosterReady()) {
      final RosterItem rosterItem = roster.get().getItemByJID(jid);
      if (rosterItem != null && rosterItem.getSubscriptionState().equals(SubscriptionState.both)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isXmppLoggedIn() {
    return xmppSession.get().isReady();
  }

  @Override
  public Room joinRoom(final String roomName, final String userAlias) {
    return joinRoom(roomName, null, userAlias);
  }

  @Override
  public Room joinRoom(final String roomName, final String subject, final String userAlias) {
    Room room = null;
    if (xmppSession.get().isReady()) {
      final XmppURI roomURI = XmppURI.uri(roomName + "@" + chatOptions.roomHost + "/"
          + chatOptions.username);
      room = roomManager.get().open(roomURI, roomManager.get().getDefaultHistoryOptions());
      if (TextUtils.notEmpty(subject)) {
        RoomSubject.requestSubjectChange(room, subject);
      }
    } else {
      NotifyUser.error(i18n.t("Error"), i18n.t("In order to join a chatroom you need to be 'online'"),
          true);
    }
    return room;
  }

  @Override
  public void login(final XmppURI uri, final String passwd) {
    xmppSession.get().login(uri, passwd);
  }

  @Override
  public boolean loginIfNecessary() {
    if (!isXmppLoggedIn() && session.isLogged()) {
      doLogin();
      return true;
    }
    return false;
  }

  @Override
  public void logout() {
    if (dialogVisible()) {
      dialog.hide();
    }
    if (isXmppLoggedIn()) {
      xmppSession.get().logout();
    }
  }

  @Override
  public XmppURI roomUriFrom(final String shortName) {
    return XmppURI.jid(shortName + "@" + chatOptions.roomHost);
  }

  @Override
  public void setAvatar(final String photoBinary) {
    avatarManager.get().setVCardAvatar(photoBinary);
  }

  private void setSize(final Widget widget, final CustomHtmlConfig htmlConfig) {
    if (htmlConfig.width != null) {
      widget.setWidth("98%");
      dialog.setWidth(htmlConfig.width);
    }
    if (htmlConfig.height != null) {
      widget.setHeight("98%");
      dialog.setHeight(htmlConfig.height);
    }
  }

  @Override
  public void show() {
    showDialog(true);
  }

  private void showDialog(final boolean show) {
    Log.info("Show dialog: " + show);
    if (session.isLogged()) {
      createDialogIfNeeded();
      if (dialog.getAbsoluteTop() == 0 && dialog.getAbsoluteLeft() == 0) {
        dialog.setPosition(((Widget) chatIcon.getValue(ParentWidget.PARENT_UI)).getAbsoluteLeft() + 20,
            20);
      }
      if (show) {
        dialog.show();
        dialog.setZIndex(0);
        dialog.getHeader().setZIndex(0);
      } else {
        dialog.hide();
      }
    }
  }

  private void toggleShowDialog() {
    Log.info("Toggle!");
    showDialog(dialog == null ? true : !dialogVisible());
  }

  @Override
  public XmppURI uriFrom(final String shortName) {
    return chatOptions.uriFrom(shortName);
  }
}
