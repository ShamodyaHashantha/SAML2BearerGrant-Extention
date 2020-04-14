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

package org.wso2.sample.saml2.grant.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml2.core.Assertion;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.saml.SAML2BearerGrantHandler;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.sample.saml2.grant.handler.internal.ExtendedSAML2BearerGrantHandlerDataHolder;

public class ExtendedSAML2BearerGrantHandler extends SAML2BearerGrantHandler {

    private static final Log log = LogFactory.getLog(ExtendedSAML2BearerGrantHandler.class);

    public static ExtendedSAML2BearerGrantHandler getInstance() {
        return new ExtendedSAML2BearerGrantHandler();
    }

    @Override
    protected void setLocalUser(OAuthTokenReqMessageContext tokReqMsgCtx, Assertion assertion, String spTenantDomain)
            throws UserStoreException, IdentityOAuth2Exception {

        RealmService realmService = ExtendedSAML2BearerGrantHandlerDataHolder.getInstance().getRealmService();
        UserStoreManager userStoreManager = null;
        ServiceProvider serviceProvider = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving service provider for client id : " + tokReqMsgCtx.getOauth2AccessTokenReqDTO()
                        .getClientId() + ". Tenant domain : " + spTenantDomain);
            }
            serviceProvider = ExtendedSAML2BearerGrantHandlerDataHolder.getApplicationMgtService().getServiceProviderByClientId(
                    tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId(), OAuthConstants.Scope.OAUTH2,
                    spTenantDomain);
        } catch (IdentityApplicationManagementException e) {
            throw new IdentityOAuth2Exception("Error while retrieving service provider for client id : " +
                    tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId() + " in tenant domain " + spTenantDomain);
        }

        AuthenticatedUser authenticatedUser = buildLocalUser(tokReqMsgCtx, assertion, serviceProvider, spTenantDomain);
        if (log.isDebugEnabled()) {
            log.debug("Setting local user with username :" + authenticatedUser.getUserName() + ". User store domain :" +
                    authenticatedUser.getUserStoreDomain() + ". Tenant domain : " + authenticatedUser.getTenantDomain
                    () + " . Authenticated subjectIdentifier : " + authenticatedUser
                    .getAuthenticatedSubjectIdentifier());
        }

        if (!spTenantDomain.equalsIgnoreCase(authenticatedUser.getTenantDomain()) && !serviceProvider.isSaasApp()) {
            throw new IdentityOAuth2Exception("Non SaaS app tries to issue token for a different tenant domain. User " +
                    "tenant domain : " + authenticatedUser.getTenantDomain() + ". SP tenant domain : " +
                    spTenantDomain);
        }

        userStoreManager = realmService.getTenantUserRealm(IdentityTenantUtil.getTenantId(authenticatedUser
                .getTenantDomain())).getUserStoreManager();

        if (log.isDebugEnabled()) {
            log.debug("Checking whether the user exists in local user store");
        }
        if (!isUserExist(userStoreManager, authenticatedUser)) {
            if (log.isDebugEnabled()) {
                log.debug("User " + authenticatedUser.getUsernameAsSubjectIdentifier(true,false) +
                        " doesn't exist in local user store.");
            }
            throw new IdentityOAuth2Exception("User: " + authenticatedUser.getAuthenticatedSubjectIdentifier() + " does not exist or multiple users found.");
        }
        tokReqMsgCtx.setAuthorizedUser(authenticatedUser);
    }

    private boolean isUserExist(UserStoreManager userStoreManager, AuthenticatedUser authenticatedUser) throws UserStoreException {

        int userCount = 0;
        String userName = authenticatedUser.getAuthenticatedSubjectIdentifier();
        while (userStoreManager != null) {
            if (userStoreManager.isExistingUser(userName)) {
                userCount++;
                String domainName = UserCoreUtil.getDomainName(((AbstractUserStoreManager)userStoreManager).getRealmConfiguration());
                authenticatedUser.setUserStoreDomain(domainName);
                log.debug("User " + userName + " found in " + domainName);
            }
            userStoreManager = ((AbstractUserStoreManager) userStoreManager).getSecondaryUserStoreManager();
        }
        if (userCount == 0) {
            return false;
        } else if (userCount == 1) {
            return true;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Users count found for " + userName + " is " + userCount);
            }
            return false;
        }
    }

    @Override
    protected void setUserInMessageContext(OAuthTokenReqMessageContext tokReqMsgCtx, IdentityProvider identityProvider, Assertion assertion, String spTenantDomain) throws IdentityOAuth2Exception {
        super.setUserInMessageContext(tokReqMsgCtx, identityProvider, assertion, spTenantDomain);
    }
}
