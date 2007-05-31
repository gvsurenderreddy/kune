/*
 * Copyright (C) 2007 The kune development team (see CREDITS for details)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 dated June, 1991.
 * 
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */
package org.ourproject.kune.client.ui.desktop;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Vicente J. Ruiz Jurado
 *
 */
public class ContextContents extends VerticalPanel {
	
	public ContextContents() {
		initialize();
		layout();
		setPropierties();
	}

	private void initialize() {
	}

	private void layout() {
    }

	private void setPropierties() {
        this.setBorderWidth(0);
        this.setSpacing(0);
        this.setStyleName("context-contents");
        this.addStyleName("context-contents");
	}
}
