package org.ourproject.kune.platf.server.manager;

import org.ourproject.kune.platf.server.domain.User;
import org.ourproject.kune.platf.server.model.Content;

public interface AccessManager {

    void check(User user, Content content);

}
