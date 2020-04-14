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

import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.user.core.service.RealmService;

public class ExtendedSAML2BearerGrantHandlerDataHolder {

    private static ExtendedSAML2BearerGrantHandlerDataHolder instance = new ExtendedSAML2BearerGrantHandlerDataHolder();
    private static ApplicationManagementService applicationMgtService;
    private static RealmService realmService;

    /**
     * Get Application management service
     *
     * @return ApplicationManagementService
     */
    public static ApplicationManagementService getApplicationMgtService() {

        return ExtendedSAML2BearerGrantHandlerDataHolder.applicationMgtService;
    }

    /**
     * Set Application management service
     *
     * @param applicationMgtService ApplicationManagementService
     */
    public static void setApplicationMgtService(ApplicationManagementService applicationMgtService) {

        ExtendedSAML2BearerGrantHandlerDataHolder.applicationMgtService = applicationMgtService;
    }

    /**
     * Get Application management service
     *
     * @return realmService
     */
    public static RealmService getRealmService(){
        return ExtendedSAML2BearerGrantHandlerDataHolder.realmService;
    }

    /**
     * Set Application management service implementation
     *
     * @param realmService Application management service
     */
    public static void setRealmService(RealmService realmService){
        ExtendedSAML2BearerGrantHandlerDataHolder.realmService = realmService;
    }

    public static ExtendedSAML2BearerGrantHandlerDataHolder getInstance(){
        return instance;
    }
}
