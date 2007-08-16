package org.ourproject.kune.platf.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ourproject.kune.chat.server.ChatServerTool;
import org.ourproject.kune.docs.server.DocumentServerTool;
import org.ourproject.kune.platf.server.domain.Container;
import org.ourproject.kune.platf.server.domain.Group;
import org.ourproject.kune.platf.server.domain.ToolConfiguration;
import org.ourproject.kune.platf.server.manager.GroupManager;
import org.ourproject.kune.platf.server.manager.LicenseManager;

import com.google.inject.Inject;

public class DatabaseInitializationTest {
    @Inject
    GroupManager manager;
    @Inject
    LicenseManager licenseManager;
    private Group group;

    @Before
    public void init() {
	new IntegrationTestHelper(this);
	group = manager.getDefaultGroup();
    }

    @Test
    public void testToolConfiguration() {
	assertNotNull(group);
	ToolConfiguration docToolConfig = group.getToolConfiguration(DocumentServerTool.NAME);
	assertNotNull(docToolConfig);
	ToolConfiguration chatToolConfig = group.getToolConfiguration(ChatServerTool.NAME);
	assertNotNull(chatToolConfig);
    }

    @Test
    public void testDefaultDocumentContent() {
	Container rootDocFolder = group.getDefaultContent().getFolder();
	assertEquals("/" + DocumentServerTool.ROOT_NAME, rootDocFolder.getAbsolutePath());
    }

    @Test
    public void testDefaultContentAndLicenses() {
	assertNotNull(group.getDefaultContent());
	assertTrue(licenseManager.getAll().size() > 0);
    }
}
