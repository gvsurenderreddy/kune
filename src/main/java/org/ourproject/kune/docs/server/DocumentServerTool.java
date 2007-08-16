package org.ourproject.kune.docs.server;

import org.ourproject.kune.platf.server.content.ContentManager;
import org.ourproject.kune.platf.server.content.ContainerManager;
import org.ourproject.kune.platf.server.domain.Content;
import org.ourproject.kune.platf.server.domain.Container;
import org.ourproject.kune.platf.server.domain.Group;
import org.ourproject.kune.platf.server.domain.ToolConfiguration;
import org.ourproject.kune.platf.server.domain.User;
import org.ourproject.kune.platf.server.manager.ToolConfigurationManager;
import org.ourproject.kune.platf.server.tool.ServerTool;
import org.ourproject.kune.platf.server.tool.ToolRegistry;

import com.google.inject.Inject;

public class DocumentServerTool implements ServerTool {
    public static final String NAME = "docs";
    private final ContentManager contentManager;
    private final ToolConfigurationManager configurationManager;
    private final ContainerManager containerManager;

    @Inject
    public DocumentServerTool(final ContentManager contentManager, final ContainerManager containerManager,
	    final ToolConfigurationManager configurationManager) {
	this.contentManager = contentManager;
	this.containerManager = containerManager;
	this.configurationManager = configurationManager;
    }

    @Inject
    public void register(final ToolRegistry registry) {
	registry.register(this);
    }

    public String getName() {
	return NAME;
    }

    public Group initGroup(final User user, final Group group) {
	ToolConfiguration config = group.getToolConfiguration(NAME);
	if (config == null) {
	    config = new ToolConfiguration();
	    // i18n
	    Container container = containerManager.createRootFolder(group, NAME, "docs", "docs.rootFolder");
	    config.setRoot(container);
	    group.setToolConfig(NAME, config);
	    configurationManager.persist(config);
	    Content descriptor = contentManager.createContent("Kune docs!", user, container);
	    group.setDefaultContent(descriptor);
	}
	return group;
    }

}
