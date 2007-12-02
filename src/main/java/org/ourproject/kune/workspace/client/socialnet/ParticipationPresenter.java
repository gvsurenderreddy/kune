/*
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

package org.ourproject.kune.workspace.client.socialnet;

import java.util.Iterator;
import java.util.List;

import org.ourproject.kune.platf.client.AbstractPresenter;
import org.ourproject.kune.platf.client.View;
import org.ourproject.kune.platf.client.dispatch.DefaultDispatcher;
import org.ourproject.kune.platf.client.dto.AccessRightsDTO;
import org.ourproject.kune.platf.client.dto.GroupDTO;
import org.ourproject.kune.platf.client.dto.LinkDTO;
import org.ourproject.kune.platf.client.dto.ParticipationDataDTO;
import org.ourproject.kune.platf.client.rpc.SocialNetworkService;
import org.ourproject.kune.platf.client.rpc.SocialNetworkServiceAsync;
import org.ourproject.kune.sitebar.client.Site;
import org.ourproject.kune.workspace.client.WorkspaceEvents;
import org.ourproject.kune.workspace.client.workspace.ParticipationComponent;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ParticipationPresenter implements ParticipationComponent, AbstractPresenter {
    private static final String ADMIN_SUBTITLE = "admin in:";

    // i18n
    private final static MemberAction GOTO_GROUP_COMMAND = new MemberAction("Visit this group homepage",
            WorkspaceEvents.GOTO);

    private ParticipationView view;

    public void init(final ParticipationView view) {
        this.view = view;
    }

    public void getParticipation(final String user, final GroupDTO group, final AccessRightsDTO accessRightsDTO) {
        Site.showProgressProcessing();
        final SocialNetworkServiceAsync server = SocialNetworkService.App.getInstance();

        server.getParticipation(user, group.getShortName(), new AsyncCallback() {
            public void onFailure(final Throwable caught) {
                Site.hideProgress();
            }

            public void onSuccess(final Object result) {
                ParticipationDataDTO participation = (ParticipationDataDTO) result;
                setParticipation(participation, accessRightsDTO);
                Site.hideProgress();
            }
        });
    }

    public void doAction(final String action, final String group) {
        DefaultDispatcher.getInstance().fire(action, group, this);
    }

    public View getView() {
        return view;
    }

    private void setParticipation(final ParticipationDataDTO participation, final AccessRightsDTO rights) {
        view.setDropDownContentVisible(false);
        view.clear();
        MemberAction[] adminsActions = {
                new MemberAction("Don't participate more in this group", WorkspaceEvents.UNJOIN_GROUP),
                GOTO_GROUP_COMMAND };
        MemberAction[] collabActions = adminsActions;
        MemberAction[] viewerActions = { GOTO_GROUP_COMMAND };
        List groupsIsAdmin = participation.getGroupsIsAdmin();
        List groupsIsCollab = participation.getGroupsIsCollab();
        boolean userIsAdmin = rights.isAdministrable();
        boolean userIsCollab = !userIsAdmin && rights.isEditable();
        boolean userIsMember = isMember(userIsAdmin, userIsCollab);
        int numAdmins = groupsIsAdmin.size();
        int numCollaborators = groupsIsCollab.size();
        if (numAdmins > 0 || numCollaborators > 0) {
            addParticipants(groupsIsAdmin, groupsIsCollab, numAdmins, numCollaborators, userIsAdmin, userIsMember,
                    adminsActions, collabActions, viewerActions);
            view.setDropDownContentVisible(true);
            view.show();
        } else {
            hide();
        }

    }

    private void hide() {
        view.hide();
    }

    private void addParticipants(final List groupsIsAdmin, final List groupsIsCollab, final int numAdmins,
            final int numCollaborators, final boolean userIsAdmin, boolean userIsMember,
            final MemberAction[] adminsActions, final MemberAction[] collabActions, final MemberAction[] viewerActions) {
        MemberAction[] actions;
        String collabTitle;

        if (!userIsMember) {
            actions = viewerActions;
        } else {
            if (userIsAdmin) {
                actions = adminsActions;
            } else {
                actions = collabActions;
            }
        }
        if (numAdmins > 0) {
            // i18n
            view.addCategory(ADMIN_SUBTITLE, "Admisnistrate these groups");
            iteraList(ADMIN_SUBTITLE, groupsIsAdmin, actions);
            collabTitle = "and as collaborator in:";
        } else {
            collabTitle = "collaborator in:";
        }
        if (numCollaborators > 0) {
            // i18n
            view.addCategory(collabTitle, "Collaborate in these groups");
            iteraList(collabTitle, groupsIsCollab, actions);
        }

    }

    private void iteraList(final String categoryName, final List groupList, final MemberAction[] actions) {
        final Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            final LinkDTO group = (LinkDTO) iter.next();
            view.addCategoryMember(categoryName, group.getShortName(), group.getLongName(), actions);
        }
    }

    private boolean isMember(final boolean userIsAdmin, final boolean userIsCollab) {
        return userIsAdmin || userIsCollab;
    }
}