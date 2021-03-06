package org.camunda.bpm.extension.keycloak;

import java.util.List;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

public class KeycloakGroupQuery extends GroupQueryImpl {

    private static final long serialVersionUID = 1L;

    public KeycloakGroupQuery() {
        super();
    }

    public KeycloakGroupQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    // execute queries ////////////////////////////

    public long executeCount(CommandContext commandContext) {
        final KeycloakIdentityProviderSession identityProvider = getKeycloakIdentityProvider(commandContext);
        return identityProvider.findGroupCountByQueryCriteria(this);
    }

    public List<Group> executeList(CommandContext commandContext, Page page) {
        final KeycloakIdentityProviderSession identityProvider = getKeycloakIdentityProvider(commandContext);
        List<Group> result = identityProvider.findGroupByQueryCriteria(this);
        for (Group group: result) {
            if (group.getName().equals("client_name")) {
                group.setName("Central");
                group.setId("Central");
            }
            if (group.getName().equals("validation")) {
                group.setName("France");
                group.setId("France");
            }
            if (group.getName().equals("development")) {
                group.setName("Belgium");
                group.setId("Belgium");
            }
        }
        return result;
    }

    protected KeycloakIdentityProviderSession getKeycloakIdentityProvider(CommandContext commandContext) {
        return (KeycloakIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
    }

    // unimplemented features //////////////////////////////////

    @Override
    public GroupQuery memberOfTenant(String tenantId) {
        throw new UnsupportedOperationException("The Keycloak identity provider does currently not support tenant queries.");
    }

}
