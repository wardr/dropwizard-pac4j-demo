package com.uberlogik.demo.security;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.credentials.password.SpringSecurityPasswordEncoder;
import org.pac4j.dropwizard.Pac4jBundle;
import org.pac4j.dropwizard.Pac4jFactory;
import org.pac4j.dropwizard.Pac4jFactory.FilterConfiguration;
import org.pac4j.http.client.indirect.FormClient;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.uberlogik.demo.DemoConfiguration;
import com.uberlogik.pac4j.matching.PathMatcher;

import io.dropwizard.Configuration;

public class SecurityBundle<T extends Configuration> extends Pac4jBundle<T>
{
    private static final String FORM_CLIENT_CALLBACK = "/callback";
    private static final String FORM_CLIENT_NAME = "FormLocalDB";
    private static final String PATH_MATCHER = "pathMatcher";
    
    private final JooqBundle<DemoConfiguration> dbBundle;

    public SecurityBundle(JooqBundle<DemoConfiguration> dbBundle) {
        this.dbBundle = dbBundle;
    }

    public FormClient getFormClient()
    {
        return (FormClient) getConfig().getClients().findClient(FORM_CLIENT_NAME);
    }

    @Override
    public Pac4jFactory getPac4jFactory(T configuration) {
        Pac4jFactory pac4j = new Pac4jFactory();
        
        PathMatcher matcher = new PathMatcher()
                .excludePath("/")
                .excludePath("/login")
                .excludeBranch("/user")
                .excludePath(FORM_CLIENT_CALLBACK);
        
        pac4j.getMatchers().put(PATH_MATCHER, matcher);
        
        // dbBundle should already be initialized now
        JooqAuthenticator authenticator = new JooqAuthenticator(dbBundle.getConfiguration());
        authenticator.setPasswordEncoder(new SpringSecurityPasswordEncoder(new Pbkdf2PasswordEncoder()));
        FormClient formClient = new FormClient("/login", authenticator);
        formClient.setName(FORM_CLIENT_NAME);
        
        pac4j.getClients().add(formClient);
        
        // this clients (multiple can be set, separated by commas) will be used by default with @Pac4JSecurity annotations
        pac4j.setDefaultClients(FORM_CLIENT_NAME);
        
        pac4j.setCallbackUrl(FORM_CLIENT_CALLBACK);

        // set a global security filter; require authentication everywhere, except for whitelisted paths.
        // Note that authorization (roles, permissions) must be handled separately.
        FilterConfiguration filter = new FilterConfiguration();
        filter.setAuthorizers("isAuthenticated");
        filter.setClients(FORM_CLIENT_NAME);
        filter.setMatchers(PATH_MATCHER);
        
        pac4j.getGlobalFilters().add(filter);

        // An authorizer that only allows superusers
        pac4j.getAuthorizers().put("superuser", new RequireAnyRoleAuthorizer<>(RoleName.SUPERUSER));
        
        return pac4j;
    }
}
