/*
 *
 * Copyright (C) 2007 The kune development team (see CREDITS for details)
 * This file is part of kune.
 *
 * Kune is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kune is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ourproject.kune.platf.client.state;

import org.ourproject.kune.platf.client.app.Application;
import org.ourproject.kune.platf.client.app.HistoryWrapper;
import org.ourproject.kune.platf.client.dto.GroupDTO;
import org.ourproject.kune.platf.client.dto.StateToken;
import org.ourproject.kune.platf.client.dto.UserSimpleDTO;
import org.ourproject.kune.platf.client.rpc.AsyncCallbackSimple;
import org.ourproject.kune.platf.client.services.Kune;
import org.ourproject.kune.platf.client.tool.ClientTool;
import org.ourproject.kune.sitebar.client.Site;
import org.ourproject.kune.workspace.client.dto.StateDTO;
import org.ourproject.kune.workspace.client.workspace.ContentSubTitleComponent;
import org.ourproject.kune.workspace.client.workspace.ContentTitleComponent;
import org.ourproject.kune.workspace.client.workspace.Workspace;

import to.tipit.gwtlib.FireLog;

public class StateManagerDefault implements StateManager {
    private final Application app;
    private final ContentProvider provider;
    private final Workspace workspace;
    private StateDTO oldState;
    private String lastTheme;
    private final Session1 session;
    private final HistoryWrapper history;

    public StateManagerDefault(final ContentProvider provider, final Application app, final Session1 session,
            final HistoryWrapper history) {
        this.provider = provider;
        this.app = app;
        this.session = session;
        this.history = history;
        this.workspace = app.getWorkspace();
        this.oldState = null;
    }

    /**
     * <p>
     * Reload current state (using client cache if available)
     * </p>
     */
    public void reload() {
        onHistoryChanged(history.getToken());
    }

    public Session1 getSession() {
        return session;
    }

    public void onHistoryChanged(final String historyToken) {
        String oldStateEncoded = "";
        if (oldState != null) {
            oldStateEncoded = oldState.getState().getEncoded();
        }
        if (historyToken.equals(Site.NEWGROUP_TOKEN)) {
            Site.doNewGroup(oldStateEncoded);
        } else if (historyToken.equals(Site.LOGIN_TOKEN)) {
            Site.doLogin(oldStateEncoded);
        } else if (historyToken.equals(Site.FIXME_TOKEN)) {
            if (oldState == null) {
                onHistoryChanged(new StateToken());
            } else {
                loadContent(oldState);
            }
        } else {
            onHistoryChanged(new StateToken(historyToken));
        }
    }

    public void setRetrievedState(final StateDTO content) {
        final StateToken state = content.getState();
        provider.cache(state, content);
        setState(state);
    }

    public void setState(final StateToken state) {
        history.newItem(state.getEncoded());
    }

    public void reloadSocialNetwork() {
        Site.sitebar.reloadUserInfo(session.getUserHash());
        loadSocialNetwork();
    }

    private void onHistoryChanged(final StateToken newState) {
        provider.getContent(session.getUserHash(), newState, new AsyncCallbackSimple() {
            public void onSuccess(final Object result) {
                StateDTO newStateDTO = (StateDTO) result;
                loadContent(newStateDTO);
                oldState = newStateDTO;
            }
        });
    }

    private void loadContent(final StateDTO state) {
        session.setCurrent(state);
        final GroupDTO group = state.getGroup();
        app.setGroupState(group.getShortName());
        boolean isAdmin = state.getGroupRights().isAdministrable();
        if (isAdmin) {
            workspace.getThemeMenuComponent().setVisible(true);
        } else {
            workspace.getThemeMenuComponent().setVisible(false);
        }
        setWsTheme(group);
        workspace.showGroup(group, isAdmin);
        final String toolName = state.getToolName();
        workspace.setTool(toolName);

        final ClientTool clientTool = app.getTool(toolName);
        clientTool.setContent(state);
        ContentTitleComponent contentTitleComponent = workspace.getContentTitleComponent();
        ContentSubTitleComponent contentSubTitleComponent = workspace.getContentSubTitleComponent();
        if (state.hasDocument()) {
            contentTitleComponent.setContentTitle(state.getTitle(), state.getContentRights().isEditable());
            contentTitleComponent.setContentDateVisible(true);
            contentTitleComponent.setContentDate(Kune.I18N.t("Published on: [%s]", state.getPublishedOn().toString()));
            contentSubTitleComponent.setContentSubTitleLeft(Kune.I18N.tWithNT("by: [%s]", "used in a list of authors",
                    ((UserSimpleDTO) state.getAuthors().get(0)).getName()));
            contentSubTitleComponent.setContentSubTitleLeftVisible(true);
        } else {
            if (state.getFolder().getParentFolderId() == null) {
                // We translate root folder names (documents, chat room,
                // etcetera)
                contentTitleComponent.setContentTitle(Kune.I18N.t(state.getTitle()), false);
            } else {
                contentTitleComponent.setContentTitle(state.getTitle(), state.getContentRights().isEditable());
            }
            contentTitleComponent.setContentDateVisible(false);
            contentSubTitleComponent.setContentSubTitleLeftVisible(false);
        }
        if (state.getLanguage() != null) {
            contentSubTitleComponent.setContentSubTitleRight(Kune.I18N.t("Language: [%s]", state.getLanguage()
                    .getEnglishName()));
            contentSubTitleComponent.setContentSubTitleRightVisible(true);
        } else {
            contentSubTitleComponent.setContentSubTitleRightVisible(false);
        }

        workspace.getContentBottomToolBarComponent().setRate(state.isRateable(), session.isLogged(), state.getRate(),
                state.getRateByUsers(), state.getCurrentUserRate());
        workspace.setContent(clientTool.getContent());
        workspace.setContext(clientTool.getContext());
        workspace.getLicenseComponent().setLicense(state.getGroup().getLongName(), state.getLicense());
        workspace.getTagsComponent().setTags("FIXME");

        if (oldState != null && oldState.getGroup().getShortName().equals(state.getGroup().getShortName())
                && oldState.getGroupRights().isAdministrable() == state.getGroupRights().isAdministrable()
                && oldState.getGroupRights().isEditable() == state.getGroupRights().isEditable()) {
            // Same group, same rights, do nothing
            FireLog.debug("Same group, same rights, not reloading SN");
        } else {
            loadSocialNetwork();
        }

        // only for UI tests:
        workspace.getBuddiesPresenceComponent().setBuddiesPresence();
        Site.hideProgress();
    }

    private void setWsTheme(final GroupDTO group) {
        String nextTheme = group.getWorkspaceTheme();
        if (lastTheme == null || lastTheme != nextTheme) {
            workspace.setTheme(nextTheme);
        }
        lastTheme = nextTheme;
    }

    private void loadSocialNetwork() {
        StateDTO state;
        if (session != null && (state = session.getCurrentState()) != null) {
            workspace.getGroupMembersComponent().getGroupMembers(session.getUserHash(), state.getGroup(),
                    state.getGroupRights());
            workspace.getParticipationComponent().getParticipation(session.getUserHash(), state.getGroup(),
                    state.getGroupRights());
        }
    }

}
