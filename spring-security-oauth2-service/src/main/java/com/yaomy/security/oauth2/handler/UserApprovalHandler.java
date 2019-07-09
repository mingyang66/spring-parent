/*
package com.yaomy.security.oauth2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;

*/
/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.UserApprovalHandler
 * @Date: 2019/7/9 17:03
 * @Version: 1.0
 *//*

@Component
public class UserApprovalHandler extends ApprovalStoreUserApprovalHandler {
    private boolean useApprovalStore = true;
    @Autowired
    private ClientDetailsService clientDetailsService;

    */
/**
     * Service to load client details (optional) for auto approval checks.
     *
     * @param clientDetailsService
     *            a client details service
     *//*

    @Override
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
        super.setClientDetailsService(clientDetailsService);
    }

    */
/**
     * @param useApprovalStore
     *            the useTokenServices to set
     *//*

    public void setUseApprovalStore(boolean useApprovalStore) {
        this.useApprovalStore = useApprovalStore;
    }

    */
/**
     * Allows automatic approval for a white list of clients in the implicit
     * grant case.
     *
     * @param authorizationRequest
     *            The authorization request.
     * @param userAuthentication
     *            the current user authentication
     *
     * @return An updated request if it has already been approved by the current
     *         user.
     *//*

    @Override
    public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest,
                                                    Authentication userAuthentication) {

        boolean approved = false;
        // If we are allowed to check existing approvals this will short circuit
        // the decision
        if (useApprovalStore) {
            authorizationRequest = super.checkForPreApproval(authorizationRequest, userAuthentication);
            approved = authorizationRequest.isApproved();
        } else {
            if (clientDetailsService != null) {
                Collection<String> requestedScopes = authorizationRequest.getScope();
                try {
                    ClientDetails client = clientDetailsService
                            .loadClientByClientId(authorizationRequest.getClientId());
                    for (String scope : requestedScopes) {
                        if (client.isAutoApprove(scope)) {
                            approved = true;
                            break;
                        }
                    }
                } catch (ClientRegistrationException e) {
                }
            }
        }
        authorizationRequest.setApproved(approved);

        return authorizationRequest;

    }

}
*/
