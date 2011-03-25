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
package cc.kune.core.server.rack.filters.gwts;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cc.kune.core.server.rack.RackHelper;
import cc.kune.core.server.rack.filters.AbstractInjectedFilter;

import com.google.gwt.user.client.rpc.RemoteService;

public class GWTServiceFilter extends AbstractInjectedFilter {
    public static final Log LOG = LogFactory.getLog(GWTServiceFilter.class);

    private final Class<? extends RemoteService> serviceClass;
    private final DelegatedRemoteServlet servlet;

    public GWTServiceFilter(final Class<? extends RemoteService> serviceClass) {
        this.serviceClass = serviceClass;
        this.servlet = new DelegatedRemoteServlet();
    }

    @Override
    public void destroy() {
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        LOG.info("--------------------------------------------------------------------------------");
        LOG.debug("SERVICE: " + RackHelper.getURI(request) + " - " + serviceClass.getSimpleName());
        final RemoteService service = getInstance(serviceClass);
        servlet.setService(service);
        servlet.doPost((HttpServletRequest) request, (HttpServletResponse) response);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        servlet.setServletContext(filterConfig.getServletContext());
    }

}