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
package cc.kune.common.client.actions.ui.descrip;

import cc.kune.common.client.actions.AbstractAction;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractParentGuiActionDescrip.
 *
 * @author vjrj@ourproject.org (Vicente J. Ruiz Jurado)
 */
public abstract class AbstractParentGuiActionDescrip extends AbstractGuiActionDescrip implements
    HasChilds {

  /** The childs. */
  private final GuiActionDescCollection childs;

  /**
   * Instantiates a new abstract parent gui action descrip.
   *
   * @param action the action
   */
  public AbstractParentGuiActionDescrip(final AbstractAction action) {
    super(action);
    childs = new GuiActionDescCollection();
  }

  /**
   * Adds the.
   *
   * @param descriptors the descriptors
   */
  public void add(final GuiActionDescrip... descriptors) {
    childs.add(descriptors);
  }

  /**
   * Clear.
   */
  public void clear() {
    childs.clear();
  }

  /**
   * Gets the childs.
   *
   * @return the childs
   */
  public GuiActionDescCollection getChilds() {
    return childs;
  }

}
