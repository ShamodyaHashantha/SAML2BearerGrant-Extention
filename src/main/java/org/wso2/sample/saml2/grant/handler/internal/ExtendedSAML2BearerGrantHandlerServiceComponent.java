/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.sample.saml2.grant.handler.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.user.core.service.RealmService;

@Component(
        name = "identity.saml2.grant.handler.component",
        immediate = true
)
public class ExtendedSAML2BearerGrantHandlerServiceComponent {
    private static final Log log = LogFactory.getLog(ExtendedSAML2BearerGrantHandlerServiceComponent.class);

    @SuppressWarnings("unchecked")
    @Activate
    protected void activate(ComponentContext context) {

        try {
            /*ExtendedSAML2BearerGrantHandler extendedSAML2BearerGrantHandler =
                    ExtendedSAML2BearerGrantHandler.getInstance();
            context.getBundleContext().registerService(ExtendedSAML2BearerGrantHandler.class.getName(),
                    extendedSAML2BearerGrantHandler, null);*/
            log.info("ExtendedSAML2BearerGrantHandler bundle is activated");

        } catch (Throwable e) {
            log.error("Error while activating extendedSAML2BearerGrantHandler.", e);
        }
    }

    /**
     * Set Application management service implementation
     *
     * @param applicationMgtService Application management service
     */
    @Reference(
            name = "application.mgt.service",
            service = ApplicationManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationMgtService"
    )
    protected void setApplicationMgtService(ApplicationManagementService applicationMgtService) {

        if (log.isDebugEnabled()) {
            log.debug("ApplicationManagementService set in Identity SAML2BearerGrantHandlerServiceComponent bundle");
        }
        ExtendedSAML2BearerGrantHandlerDataHolder.setApplicationMgtService(applicationMgtService);
    }

    /**
     * Unset Application management service implementation
     *
     * @param applicationMgtService Application management service
     */
    protected void unsetApplicationMgtService(ApplicationManagementService applicationMgtService) {

        if (log.isDebugEnabled()) {
            log.debug("ApplicationManagementService unset in Identity SAML2BearerGrantHandlerServiceComponent bundle");
        }
        ExtendedSAML2BearerGrantHandlerDataHolder.setApplicationMgtService(null);
    }

    /**
     * Set Application management service implementation
     *
     * @param realmService Application management service
     */
    @Reference(
            name = "realm.service",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        ExtendedSAML2BearerGrantHandlerDataHolder.getInstance().setRealmService(realmService);
    }

    /**
     * Unset Application management service implementation
     *
     * @param realmService Application management service
     */
    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the Realm Service");
        }
        ExtendedSAML2BearerGrantHandlerDataHolder.getInstance().setRealmService(null);
    }
}

