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

package org.ourproject.kune.platf.client.rpc;

import java.util.Date;
import java.util.List;

import org.ourproject.kune.platf.client.dto.I18nLanguageDTO;
import org.ourproject.kune.platf.client.dto.StateToken;
import org.ourproject.kune.platf.client.dto.TagResultDTO;
import org.ourproject.kune.workspace.client.dto.StateDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContentServiceAsync {

    void addAuthor(String userHash, String groupShortName, String documentId, String authorShortName,
            AsyncCallback<Object> asyncCallback);

    void addContent(String user, String groupShortName, Long parentFolderId, String name,
            AsyncCallback<StateDTO> callback);

    void addFolder(String hash, String groupShortName, Long parentFolderId, String title,
            AsyncCallback<StateDTO> callback);

    void addRoom(String user, String groupShortName, Long parentFolderId, String name, AsyncCallback<StateDTO> callback);

    void delContent(String userHash, String groupShortName, String documentId, AsyncCallback<Object> asyncCallback);

    void getContent(String user, String groupShortName, StateToken newState, AsyncCallback<StateDTO> callback);

    void getSummaryTags(String userHash, String groupShortName, AsyncCallback<List<TagResultDTO>> asyncCallback);

    void rateContent(String userHash, String groupShortName, String documentId, Double value,
            AsyncCallback<Object> asyncCallback);

    void removeAuthor(String userHash, String groupShortName, String documentId, String authorShortName,
            AsyncCallback<Object> asyncCallback);

    void rename(String userHash, String groupShortName, String token, String newName,
            AsyncCallback<String> asyncCallback);

    void save(String user, String groupShortName, String documentId, String content,
            AsyncCallback<Integer> asyncCallback);

    void setLanguage(String userHash, String groupShortName, String documentId, String languageCode,
            AsyncCallback<I18nLanguageDTO> asyncCallback);

    void setPublishedOn(String userHash, String groupShortName, String documentId, Date publishedOn,
            AsyncCallback<Object> asyncCallback);

    void setTags(String userHash, String groupShortName, String documentId, String tags,
            AsyncCallback<List<TagResultDTO>> asyncCallback);
}
