/*
 *
 * Copyright (C) 2007-2009 The kune development team (see CREDITS for details)
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
package org.ourproject.kune.platf.client.actions.toolbar;

import org.ourproject.kune.platf.client.actions.ActionDescriptor;
import org.ourproject.kune.platf.client.actions.ActionItemCollection;
import org.ourproject.kune.platf.client.actions.ActionToolbarPosition;

public interface ActionToolbar<T> {

    ActionToolbarPosition IN_ANY = new ActionToolbarPosition("in-all");

    void addActions(ActionItemCollection<T> actionItemCollection, ActionToolbarPosition actionToolbarPosition);

    void attach();

    void clear();

    void detach();

    void disableMenusAndClearButtons();

    int getLeftPosition(ActionDescriptor<T> action);

    int getTopPosition(ActionDescriptor<T> action);

    ActionToolbarView<T> getView();

    void hideAllMenus();

    void setButtonEnable(ActionDescriptor<T> action, boolean enable);

    void setCleanStyle();

    void setNormalStyle();

    void setParentMenuTitle(ActionToolbarPosition position, String origTitle, String origTooltip, String newTitle);

    void setPushButtonPressed(ActionDescriptor<T> action, boolean pressed);

}
