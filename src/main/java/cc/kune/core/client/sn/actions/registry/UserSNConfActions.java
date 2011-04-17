package cc.kune.core.client.sn.actions.registry;

import cc.kune.common.client.actions.ui.descrip.ButtonDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuRadioItemDescriptor;
import cc.kune.common.client.actions.ui.descrip.SubMenuDescriptor;
import cc.kune.core.client.resources.CoreResources;
import cc.kune.core.client.sn.actions.AddNewBuddiesAction;
import cc.kune.core.client.sn.actions.UserSNVisibilityMenuItem;
import cc.kune.core.client.sn.actions.conditions.IsGroupCondition;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.StateChangedEvent;
import cc.kune.core.client.state.StateChangedEvent.StateChangedHandler;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.shared.domain.UserSNetVisibility;
import cc.kune.core.shared.dto.StateAbstractDTO;
import cc.kune.core.shared.i18n.I18nTranslationService;

import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings("serial")
public class UserSNConfActions extends AbstractSNActionsRegistry {

    public static final MenuDescriptor OPTIONS_MENU = new MenuDescriptor();
    public static final SubMenuDescriptor VISIBILITY_SUBMENU = new SubMenuDescriptor();

    @Inject
    public UserSNConfActions(final Session session, final StateManager stateManager, final I18nTranslationService i18n,
            final Provider<UserSNVisibilityMenuItem> userBuddiesVisibility, final CoreResources res,
            final IsGroupCondition isGroupCondition, final AddNewBuddiesAction addNewBuddiesAction) {
        OPTIONS_MENU.withToolTip(i18n.t("Options")).withIcon(res.arrowdownsitebar()).withStyles(
                "k-sn-options-menu, k-noborder, k-nobackcolor");
        final MenuRadioItemDescriptor anyoneItem = userBuddiesVisibility.get().withVisibility(UserSNetVisibility.anyone);
        final MenuRadioItemDescriptor onlyYourBuddiesItem = userBuddiesVisibility.get().withVisibility(
                UserSNetVisibility.yourbuddies);
        final MenuRadioItemDescriptor onlyYou = userBuddiesVisibility.get().withVisibility(UserSNetVisibility.onlyyou);
        assert anyoneItem.getAction() != onlyYourBuddiesItem.getAction();
        assert anyoneItem.getAction() != onlyYou.getAction();
        add(OPTIONS_MENU);
        add(VISIBILITY_SUBMENU.withText(i18n.t("Those who can view your network")).withParent(OPTIONS_MENU));
        add(anyoneItem.withParent(VISIBILITY_SUBMENU).withText(i18n.t("anyone")));
        add(onlyYourBuddiesItem.withParent(VISIBILITY_SUBMENU).withText(i18n.t("only your buddies")));
        add(onlyYou.withParent(VISIBILITY_SUBMENU).withText(i18n.t("only you")));

        final ButtonDescriptor addBuddieBtn = new ButtonDescriptor(addNewBuddiesAction);

        add(addBuddieBtn.withStyles("k-no-backimage"));

        stateManager.onStateChanged(true, new StateChangedHandler() {
            @Override
            public void onStateChanged(final StateChangedEvent event) {
                final StateAbstractDTO state = event.getState();
                final boolean administrable = state.getGroupRights().isAdministrable();
                OPTIONS_MENU.setVisible(administrable);
                OPTIONS_MENU.setEnabled(administrable);
                if (state.getGroup().isPersonal()) {
                    final UserSNetVisibility visibility = state.getSocialNetworkData().getUserBuddiesVisibility();
                    switch (visibility) {
                    case anyone:
                        anyoneItem.setChecked(true);
                        break;
                    case yourbuddies:
                        onlyYourBuddiesItem.setChecked(true);
                        break;
                    case onlyyou:
                        onlyYou.setChecked(true);
                        break;
                    }
                    // NotifyUser.info(i18n.t("Visibility of your network is " +
                    // visibility.toString()));
                }
                addBuddieBtn.setVisible(session.isLogged() && state.getGroup().isPersonal()
                        && session.getCurrentUser().getShortName().equals(state.getGroup().getShortName()));
            }
        });
    }
}
