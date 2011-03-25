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
 \*/
package cc.kune.core.server.state;

import java.util.List;

import cc.kune.core.shared.domain.utils.AccessRights;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.domain.Group;
import cc.kune.domain.SocialNetwork;
import cc.kune.domain.utils.ParticipationData;
import cc.kune.domain.utils.SocialNetworkData;
import cc.kune.domain.utils.UserBuddiesData;

public abstract class StateAbstract {

    private List<String> enabledTools;
    private Group group;
    private StateToken stateToken;
    private String title;
    private SocialNetworkData socialNetworkData;

    public StateAbstract() {
    }

    public List<String> getEnabledTools() {
        return enabledTools;
    }

    public Group getGroup() {
        return group;
    }

    public SocialNetwork getGroupMembers() {
        return socialNetworkData.getGroupMembers();
    }

    public AccessRights getGroupRights() {
        return socialNetworkData.getGroupRights();
    }

    public ParticipationData getParticipation() {
        return socialNetworkData.getUserParticipation();
    }

    public SocialNetworkData getSocialNetworkData() {
        return socialNetworkData;
    }

    public StateToken getStateToken() {
        return stateToken;
    }

    public String getTitle() {
        return title;
    }

    public UserBuddiesData getUserBuddies() {
        return socialNetworkData.getUserBuddies();
    }

    public void setEnabledTools(List<String> enabledTools) {
        this.enabledTools = enabledTools;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setSocialNetworkData(SocialNetworkData socialNetworkData) {
        this.socialNetworkData = socialNetworkData;
    }

    public void setStateToken(StateToken stateToken) {
        this.stateToken = stateToken;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "State[" + getStateToken() + "]";
    }

}