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

package org.ourproject.kune.platf.server.rpc;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.ourproject.kune.chat.server.managers.ChatConnection;
import org.ourproject.kune.chat.server.managers.XmppManager;
import org.ourproject.kune.platf.client.dto.I18nLanguageDTO;
import org.ourproject.kune.platf.client.dto.StateToken;
import org.ourproject.kune.platf.client.dto.TagResultDTO;
import org.ourproject.kune.platf.client.errors.AccessViolationException;
import org.ourproject.kune.platf.client.errors.ContentNotFoundException;
import org.ourproject.kune.platf.client.errors.GroupNotFoundException;
import org.ourproject.kune.platf.client.errors.ToolNotFoundException;
import org.ourproject.kune.platf.client.rpc.ContentService;
import org.ourproject.kune.platf.server.UserSession;
import org.ourproject.kune.platf.server.access.Access;
import org.ourproject.kune.platf.server.access.AccessService;
import org.ourproject.kune.platf.server.access.AccessType;
import org.ourproject.kune.platf.server.auth.Authenticated;
import org.ourproject.kune.platf.server.auth.Authorizated;
import org.ourproject.kune.platf.server.content.ContainerManager;
import org.ourproject.kune.platf.server.content.ContentManager;
import org.ourproject.kune.platf.server.content.CreationService;
import org.ourproject.kune.platf.server.domain.Container;
import org.ourproject.kune.platf.server.domain.Content;
import org.ourproject.kune.platf.server.domain.Group;
import org.ourproject.kune.platf.server.domain.User;
import org.ourproject.kune.platf.server.manager.GroupManager;
import org.ourproject.kune.platf.server.manager.SocialNetworkManager;
import org.ourproject.kune.platf.server.manager.TagManager;
import org.ourproject.kune.platf.server.mapper.Mapper;
import org.ourproject.kune.platf.server.state.State;
import org.ourproject.kune.platf.server.state.StateService;
import org.ourproject.kune.workspace.client.dto.StateDTO;

import com.google.gwt.user.client.rpc.SerializableException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.wideplay.warp.persist.TransactionType;
import com.wideplay.warp.persist.Transactional;

@Singleton
public class ContentRPC implements ContentService, RPC {
    private final StateService stateService;
    private final Provider<UserSession> userSessionProvider;
    private final Mapper mapper;
    private final GroupManager groupManager;
    private final AccessService accessService;
    private final CreationService creationService;
    private final XmppManager xmppManager;
    private final ContentManager contentManager;
    private final ContainerManager containerManager;
    private final TagManager tagManager;
    private final SocialNetworkManager socialNetworkManager;

    @Inject
    public ContentRPC(final Provider<UserSession> userSessionProvider, final AccessService accessService,
            final StateService stateService, final CreationService creationService, final GroupManager groupManager,
            final XmppManager xmppManager, final ContentManager contentManager,
            final ContainerManager containerManager, final TagManager tagManager,
            final SocialNetworkManager socialNetworkManager, final Mapper mapper) {
        this.userSessionProvider = userSessionProvider;
        this.accessService = accessService;
        this.stateService = stateService;
        this.creationService = creationService;
        this.groupManager = groupManager;
        this.xmppManager = xmppManager;
        this.contentManager = contentManager;
        this.containerManager = containerManager;
        this.tagManager = tagManager;
        this.socialNetworkManager = socialNetworkManager;
        this.mapper = mapper;
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public void addAuthor(final String userHash, final String groupShortName, final String documentId,
            final String authorShortName) throws SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        contentManager.addAuthor(user, contentId, authorShortName);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT)
    @Transactional(type = TransactionType.READ_WRITE)
    public StateDTO addContent(final String userHash, final String groupShortName, final Long parentFolderId,
            final String title) throws SerializableException {
        final Group group = groupManager.findByShortName(groupShortName);
        UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        final Access access = accessService.getFolderAccess(group, parentFolderId, user, AccessType.EDIT);
        access.setContentWidthFolderRights(creationService.createContent(title, "", user, access.getFolder()));
        final State state = stateService.create(access);
        return mapper.map(state, StateDTO.class);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT)
    @Transactional(type = TransactionType.READ_WRITE)
    public StateDTO addFolder(final String userHash, final String groupShortName, final Long parentFolderId,
            final String title) throws SerializableException {
        return createFolder(groupShortName, parentFolderId, title);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT)
    @Transactional(type = TransactionType.READ_WRITE)
    public StateDTO addRoom(final String userHash, final String groupShortName, final Long parentFolderId,
            final String roomName) throws SerializableException {
        UserSession userSession = getUserSession();
        final String userShortName = userSession.getUser().getShortName();
        final ChatConnection connection = xmppManager.login(userShortName, userSession.getUser().getPassword(),
                userHash);
        xmppManager.createRoom(connection, roomName, userShortName + userHash);
        xmppManager.disconnect(connection);
        try {
            return createFolder(groupShortName, parentFolderId, roomName);
        } catch (ContentNotFoundException e) {
            xmppManager.destroyRoom(connection, roomName);
            throw new ContentNotFoundException();
        } catch (AccessViolationException e) {
            xmppManager.destroyRoom(connection, roomName);
            throw new AccessViolationException();
        } catch (GroupNotFoundException e) {
            xmppManager.destroyRoom(connection, roomName);
            throw new GroupNotFoundException();
        }
    }

    private StateDTO createFolder(final String groupShortName, final Long parentFolderId, final String title)
            throws SerializableException {
        UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        final Group group = groupManager.findByShortName(groupShortName);

        Access access = accessService.getFolderAccess(group, parentFolderId, user, AccessType.EDIT);

        final Container container = creationService.createFolder(group, parentFolderId, title, user.getLanguage());
        final String toolName = container.getToolName();
        // Trying not to enter in new folder:
        // final StateToken token = new StateToken(group.getShortName(),
        // toolName, container.getId().toString(), null);
        final StateToken token = new StateToken(group.getShortName(), toolName, parentFolderId.toString(), null);
        access = accessService.getAccess(user, token, group, AccessType.READ);
        final State state = stateService.create(access);
        return mapper.map(state, StateDTO.class);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.ADMIN, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public void delContent(final String userHash, final String groupShortName, final String documentId)
            throws SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        contentManager.delContent(user, contentId);
    }

    // Not using @Authorizated because accessService is doing this job and is
    // more complex than other access checks (we use getContent to get default
    // contents for instance)
    @Authenticated(mandatory = false)
    @Transactional(type = TransactionType.READ_ONLY)
    public StateDTO getContent(final String userHash, final String groupShortName, final StateToken token)
            throws SerializableException {
        Group defaultGroup;
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        boolean userIsLoggedIn = userSession.isUserLoggedIn();
        if (userIsLoggedIn) {
            defaultGroup = groupManager.getGroupOfUserWithId(user.getId());
        } else {
            defaultGroup = groupManager.getDefaultGroup();
        }
        Access access;
        try {
            access = accessService.getAccess(user, token, defaultGroup, AccessType.READ);
        } catch (NoResultException e) {
            throw new ContentNotFoundException();
        } catch (ToolNotFoundException e) {
            throw new ContentNotFoundException();
        }
        final State state = stateService.create(access);
        if (state.isRateable()) {
            final Long contentId = parseId(state.getDocumentId());
            Content content = contentManager.find(contentId);
            if (userIsLoggedIn) {
                state.setCurrentUserRate(contentManager.getRateContent(user, content));
            }
            state.setRate(contentManager.getRateAvg(content));
            state.setRateByUsers(contentManager.getRateByUsers(content));
        }
        Group group = state.getGroup();
        state.setGroupTags(tagManager.getSummaryByGroup(group));
        state.setGroupMembers(socialNetworkManager.find(user, group));
        state.setParticipation(socialNetworkManager.findParticipation(user, group));
        return mapper.map(state, StateDTO.class);
    }

    private List<TagResultDTO> getSummaryTags(final Group group) {
        return mapper.mapList(tagManager.getSummaryByGroup(group), TagResultDTO.class);
    }

    @Authenticated(mandatory = false)
    @Authorizated(accessTypeRequired = AccessType.READ)
    @Transactional(type = TransactionType.READ_ONLY)
    public List<TagResultDTO> getSummaryTags(final String userHash, final String groupShortName) {
        final Group group = groupManager.findByShortName(groupShortName);
        return getSummaryTags(group);
    }

    private UserSession getUserSession() {
        return userSessionProvider.get();
    }

    private Long parseId(final String documentId) throws ContentNotFoundException {
        try {
            return new Long(documentId);
        } catch (final NumberFormatException e) {
            throw new ContentNotFoundException();
        }
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.READ, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public void rateContent(final String userHash, final String groupShortName, final String documentId,
            final Double value) throws SerializableException {
        UserSession userSession = getUserSession();
        User rater = userSession.getUser();
        final Long contentId = parseId(documentId);

        if (userSession.isUserLoggedIn()) {
            contentManager.rateContent(rater, contentId, value);
        } else {
            throw new AccessViolationException();
        }
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public void removeAuthor(final String userHash, final String groupShortName, final String documentId,
            final String authorShortName) throws SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        contentManager.removeAuthor(user, contentId, authorShortName);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT)
    @Transactional(type = TransactionType.READ_WRITE)
    public String rename(final String userHash, final String groupShortName, final String token, final String newName)
            throws SerializableException {
        String result;
        UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        StateToken stateToken = new StateToken(token);
        final Group group = groupManager.findByShortName(groupShortName);
        if (stateToken.isComplete()) {
            try {
                Content content = accessService.accessToContent(parseId(stateToken.getDocument()), user,
                        AccessType.EDIT);
                if (!content.getContainer().getOwner().equals(group)) {
                    throw new AccessViolationException();
                }
            } catch (NoResultException e) {
                throw new AccessViolationException();
            }
            result = renameContent(stateToken.getDocument(), newName);
        } else {
            result = renameFolder(groupShortName, parseId(stateToken.getFolder()), newName);
        }
        return result;
    }

    private String renameContent(final String documentId, final String newName) throws ContentNotFoundException,
            SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        return contentManager.renameContent(user, contentId, newName);
    }

    private String renameFolder(final String groupShortName, final Long folderId, final String newName)
            throws SerializableException {
        final Group group = groupManager.findByShortName(groupShortName);
        UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        Access folderAccess = accessService.getFolderAccess(group, folderId, user, AccessType.EDIT);
        return containerManager.renameFolder(group, folderAccess.getFolder(), newName);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public Integer save(final String userHash, final String groupShortName, final String documentId,
            final String textContent) throws SerializableException {

        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        final Content content = accessService.accessToContent(contentId, user, AccessType.EDIT);
        final Content descriptor = creationService.saveContent(user, content, textContent);
        return descriptor.getVersion();
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public I18nLanguageDTO setLanguage(final String userHash, final String groupShortName, final String documentId,
            final String languageCode) throws SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        return mapper.map(contentManager.setLanguage(user, contentId, languageCode), I18nLanguageDTO.class);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public void setPublishedOn(final String userHash, final String groupShortName, final String documentId,
            final Date publishedOn) throws SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        contentManager.setPublishedOn(user, contentId, publishedOn);
    }

    @Authenticated
    @Authorizated(accessTypeRequired = AccessType.EDIT, checkContent = true)
    @Transactional(type = TransactionType.READ_WRITE)
    public List<TagResultDTO> setTags(final String userHash, final String groupShortName, final String documentId,
            final String tags) throws SerializableException {
        final Long contentId = parseId(documentId);
        UserSession userSession = getUserSession();
        User user = userSession.getUser();
        final Group group = groupManager.findByShortName(groupShortName);
        contentManager.setTags(user, contentId, tags);
        return getSummaryTags(group);
    }
}
