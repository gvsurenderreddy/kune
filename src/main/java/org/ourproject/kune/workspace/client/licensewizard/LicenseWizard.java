package org.ourproject.kune.workspace.client.licensewizard;

import org.ourproject.kune.platf.client.dto.LicenseDTO;

import com.calclab.suco.client.listener.Listener;

public interface LicenseWizard {

    void start(Listener<LicenseDTO> listener);

}
