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

package org.ourproject.kune.platf.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AccessListsDTO implements IsSerializable {
    private GroupListDTO admins;
    private GroupListDTO editors;
    private GroupListDTO viewers;

    public AccessListsDTO() {
        this(null, null, null);
    }

    public AccessListsDTO(final GroupListDTO admins, final GroupListDTO editors, final GroupListDTO viewers) {
        this.admins = admins;
        this.editors = editors;
        this.viewers = viewers;
    }

    public GroupListDTO getAdmins() {
        return admins;
    }

    public void setAdmins(final GroupListDTO admins) {
        this.admins = admins;
    }

    public GroupListDTO getEditors() {
        return editors;
    }

    public void setEditors(final GroupListDTO editors) {
        this.editors = editors;
    }

    public GroupListDTO getViewers() {
        return viewers;
    }

    public void setViewers(final GroupListDTO viewers) {
        this.viewers = viewers;
    }

}
