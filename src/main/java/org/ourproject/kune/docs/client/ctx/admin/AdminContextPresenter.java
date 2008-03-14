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

package org.ourproject.kune.docs.client.ctx.admin;

import java.util.Date;
import java.util.List;

import org.ourproject.kune.docs.client.actions.DocsEvents;
import org.ourproject.kune.platf.client.AbstractPresenter;
import org.ourproject.kune.platf.client.View;
import org.ourproject.kune.platf.client.dispatch.DefaultDispatcher;
import org.ourproject.kune.platf.client.dto.AccessListsDTO;
import org.ourproject.kune.platf.client.dto.I18nLanguageDTO;
import org.ourproject.kune.platf.client.dto.UserSimpleDTO;
import org.ourproject.kune.workspace.client.dto.StateDTO;

public class AdminContextPresenter extends AbstractPresenter implements AdminContext {

    private AdminContextView view;

    public AdminContextPresenter() {
    }

    public void init(final AdminContextView view) {
        this.view = view;
    }

    public void setState(final StateDTO content) {
        // In the future check the use of these components by each tool
        I18nLanguageDTO language = content.getLanguage();
        AccessListsDTO accessLists = content.getAccessLists();
        Date publishedOn = content.getPublishedOn();
        String tags = content.getTags();
        List<UserSimpleDTO> authors = content.getAuthors();

        if (content.hasDocument()) {
            if (tags != null) {
                view.setTags(tags);
            } else {
                view.removeTagsComponent();
            }
            if (language != null) {
                view.setLanguage(language);
            } else {
                view.removeLangComponent();
            }
            if (authors != null) {
                view.setAuthors(authors);
            } else {
                view.removeAuthorsComponent();
            }
            if (publishedOn != null) {
                view.setPublishedOn(publishedOn);
            } else {
                view.removePublishedOnComponent();
            }
            if (accessLists != null) {
                view.setAccessLists(accessLists);
            } else {
                view.removeAccessListComponent();
            }
        }
    }

    public View getView() {
        return view;
    }

    public void setPublishedOn(final Date date) {
        DefaultDispatcher.getInstance().fire(DocsEvents.SET_PUBLISHED_ON, date, null);
    }

    public void setTags(final String tags) {
        DefaultDispatcher.getInstance().fire(DocsEvents.SET_TAGS, tags, null);
    }

    public void doChangeLanguage(final String langCode) {
        DefaultDispatcher.getInstance().fire(DocsEvents.SET_LANGUAGE, langCode, null);
    }

}
