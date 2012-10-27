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
package cc.kune.gspace.client.actions;

import cc.kune.common.client.actions.ActionEvent;
import cc.kune.common.client.actions.ui.descrip.ButtonDescriptor;
import cc.kune.common.shared.i18n.I18nTranslationService;
import cc.kune.core.client.actions.RolAction;
import cc.kune.core.client.rpcservices.ContentServiceHelper;
import cc.kune.core.shared.dto.AccessRolDTO;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;

public class NewContainerBtn extends ButtonDescriptor {

  public static class NewContainerAction extends RolAction {

    private final ContentServiceHelper contentService;

    @Inject
    public NewContainerAction(final ContentServiceHelper contentService) {
      super(AccessRolDTO.Editor, true);
      this.contentService = contentService;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      contentService.addContainer((String) getValue(ID), (String) getValue(NEW_NAME));
    }

  }

  public static final String BTN_ID = "k-newctner-id";
  private static final String ID = "ctnernewid";
  private static final String NEW_NAME = "ctnernewname";

  public NewContainerBtn(final I18nTranslationService i18n, final NewContainerAction action,
      final ImageResource icon, final String title, final String tooltip, final String newName,
      final String id) {
    super(action);
    // The name given to this new content
    action.putValue(NEW_NAME, newName);
    // The type id of the container
    action.putValue(ID, id);
    this.withText(title).withToolTip(tooltip).withIcon(icon).withStyles("k-def-docbtn, k-fl").withId(
        BTN_ID);
  }
}
