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
package cc.kune.polymer.client;

import cc.kune.bootstrap.client.actions.ui.BSCheckMenuItemGui;
import cc.kune.bootstrap.client.actions.ui.BSIconLabelGui;
import cc.kune.bootstrap.client.actions.ui.BSLabelGui;
import cc.kune.bootstrap.client.actions.ui.BSMenuGui;
import cc.kune.bootstrap.client.actions.ui.BSMenuHeaderGui;
import cc.kune.bootstrap.client.actions.ui.BSMenuItemGui;
import cc.kune.bootstrap.client.actions.ui.BSMenuSeparatorGui;
import cc.kune.bootstrap.client.actions.ui.BSRadioMenuItemGui;
import cc.kune.bootstrap.client.actions.ui.BSSubMenuGui;
import cc.kune.bootstrap.client.actions.ui.BSToolbarItemGui;
import cc.kune.bootstrap.client.actions.ui.BSToolbarMenuGui;
import cc.kune.bootstrap.client.actions.ui.BSWidgetMenuGui;
import cc.kune.common.client.actions.ui.GuiProvider;
import cc.kune.common.client.actions.ui.descrip.ButtonDescriptor;
import cc.kune.common.client.actions.ui.descrip.IconLabelDescriptor;
import cc.kune.common.client.actions.ui.descrip.LabelDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuCheckItemDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuItemDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuRadioItemDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuSeparatorDescriptor;
import cc.kune.common.client.actions.ui.descrip.MenuTitleItemDescriptor;
import cc.kune.common.client.actions.ui.descrip.PushButtonDescriptor;
import cc.kune.common.client.actions.ui.descrip.SubMenuDescriptor;
import cc.kune.common.client.actions.ui.descrip.ToolbarDescriptor;
import cc.kune.common.client.actions.ui.descrip.ToolbarItemDescriptor;
import cc.kune.common.client.actions.ui.descrip.ToolbarMenuDescriptor;
import cc.kune.common.client.actions.ui.descrip.ToolbarSeparatorDescriptor;
import cc.kune.common.client.actions.ui.descrip.WidgetMenuDescriptor;
import cc.kune.polymer.client.actions.ui.PoButtonGui;
import cc.kune.polymer.client.actions.ui.PoPushButtonGui;
import cc.kune.polymer.client.actions.ui.PoToolbarGui;
import cc.kune.polymer.client.actions.ui.PoToolbarSeparatorGui;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The Class BootstrapGuiProvider.
 *
 * @author vjrj@ourproject.org (Vicente J. Ruiz Jurado)
 */
public class PolymerGuiProvider {

  /**
   * Instantiates a new gwtbootstrap gui provider.
   */
  @Inject
  public PolymerGuiProvider(final GuiProvider guiProvider, final Provider<BSSubMenuGui> subMenuGui,
      final Provider<BSToolbarMenuGui> toolbarMenuGui, final Provider<BSMenuItemGui> menuItemGui,
      final Provider<BSCheckMenuItemGui> checkMenuItemGui,
      final Provider<BSRadioMenuItemGui> radioMenuItemGui,
      final Provider<BSMenuSeparatorGui> menuSeparatorGui,
      final Provider<PoPushButtonGui> pushButtonGui, final Provider<PoButtonGui> buttonGui,
      final Provider<BSLabelGui> labelGui, final Provider<BSIconLabelGui> iconLabelGui,
      final Provider<PoToolbarGui> toolbarGui,
      final Provider<PoToolbarSeparatorGui> toolbarSeparatorGui,
      final Provider<BSMenuHeaderGui> menuHeaderGui, final Provider<BSToolbarItemGui> toolbarItem,
      final Provider<BSWidgetMenuGui> widgetMenu, final Provider<BSMenuGui> menuGui) {

    guiProvider.register(SubMenuDescriptor.class, subMenuGui);
    guiProvider.register(MenuDescriptor.class, menuGui);
    guiProvider.register(MenuRadioItemDescriptor.class, radioMenuItemGui);
    guiProvider.register(MenuCheckItemDescriptor.class, checkMenuItemGui);
    guiProvider.register(MenuTitleItemDescriptor.class, menuHeaderGui);
    guiProvider.register(MenuItemDescriptor.class, menuItemGui);
    guiProvider.register(MenuSeparatorDescriptor.class, menuSeparatorGui);
    guiProvider.register(PushButtonDescriptor.class, pushButtonGui);
    guiProvider.register(ButtonDescriptor.class, buttonGui);
    guiProvider.register(IconLabelDescriptor.class, iconLabelGui);
    guiProvider.register(LabelDescriptor.class, labelGui);
    guiProvider.register(ToolbarDescriptor.class, toolbarGui);
    guiProvider.register(ToolbarSeparatorDescriptor.class, toolbarSeparatorGui);
    guiProvider.register(ToolbarMenuDescriptor.class, toolbarMenuGui);
    guiProvider.register(ToolbarItemDescriptor.class, toolbarItem);
    guiProvider.register(WidgetMenuDescriptor.class, widgetMenu);

  }

}
