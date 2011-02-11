/*
 *
 * Copyright (C) 2007-2011 The kune development team (see CREDITS for details)
 * This file is part of kune.
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
package cc.kune.core.client.ui.footer.license;

import cc.kune.core.client.state.StateChangedEvent;
import cc.kune.core.client.state.StateChangedEvent.StateChangedHandler;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.shared.dto.LicenseDTO;
import cc.kune.core.shared.dto.StateAbstractDTO;
import cc.kune.core.shared.dto.StateContainerDTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class EntityLicensePresenter extends
        Presenter<EntityLicensePresenter.EntityLicenseView, EntityLicensePresenter.EntityLicenseProxy> {

    @ProxyCodeSplit
    public interface EntityLicenseProxy extends Proxy<EntityLicensePresenter> {
    }
    public interface EntityLicenseView extends View {
        void attach();

        void detach();

        HasClickHandlers getImage();

        HasClickHandlers getLabel();

        void openWindow(String url);

        void showLicense(String groupName, LicenseDTO licenseDTO);

    }

    private LicenseDTO license;

    @Inject
    public EntityLicensePresenter(final EventBus eventBus, final EntityLicenseView view,
            final EntityLicenseProxy proxy, final StateManager stateManager) {
        super(eventBus, view, proxy);
        stateManager.onStateChanged(new StateChangedHandler() {

            @Override
            public void onStateChanged(final StateChangedEvent event) {
                final StateAbstractDTO state = event.getState();
                if (state instanceof StateContainerDTO) {
                    setLicense((StateContainerDTO) state);
                } else {
                    getView().detach();
                }
            }
        });
        final ClickHandler clickHandler = new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                getView().openWindow(license.getUrl());
            }
        };
        getView().getImage().addClickHandler(clickHandler);
        getView().getLabel().addClickHandler(clickHandler);
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }

    private void setLicense(final StateContainerDTO state) {
        this.license = state.getLicense();
        getView().showLicense(state.getGroup().getShortName(), license);
        getView().attach();
    }
}
