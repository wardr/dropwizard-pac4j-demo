package com.uberlogik.demo.security;

import com.uberlogik.pac4j.matching.PathMatcher;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.dropwizard.DefaultFeatureSupport;
import org.pac4j.dropwizard.Pac4jFeatureSupport;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.features.Pac4JSecurityFilterFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.pac4j.JaxRsCallbackUrlResolver;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import org.pac4j.jax.rs.servlet.pac4j.ServletSessionStore;

import java.util.ArrayList;
import java.util.Collection;


public class SecurityBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    public static final String SESSION_KEY = "s";
    private static final String FORM_CLIENT_CALLBACK = "/callback";
    private static final String FORM_CLIENT_NAME = "form";
    private static final String PATH_MATCHER = "pathMatcher";


    private Config config;

    public Config getConfig()
    {
        return config;
    }

    public FormClient getFormClient()
    {
        return (FormClient) config.getClients().findClient(FORM_CLIENT_NAME);
    }

    @Override
    public final void initialize(Bootstrap<?> bootstrap)
    {
        for (Pac4jFeatureSupport fs : supportedFeatures())
        {
            fs.setup(bootstrap);
        }
    }

    public Collection<Pac4jFeatureSupport> supportedFeatures()
    {
        ArrayList<Pac4jFeatureSupport> res = new ArrayList<>();
        res.add(new DefaultFeatureSupport());
        return res;
    }

    @Override
    public final void run(T configuration, Environment environment)
            throws Exception
    {
        // see build method
    }

    public void build(Environment environment, org.jooq.Configuration jooqConfig)
    {
        // Dropwizard does not enable sessions by default, so add a session handler
        MutableServletContextHandler contextHandler = environment.getApplicationContext();
        contextHandler.setSessionHandler(new SessionHandler());
        contextHandler.getServletContext().getSessionCookieConfig().setName(SESSION_KEY);

        // pac4j configuration

        config = new Config();
        config.setSessionStore(new ServletSessionStore());
        environment.jersey().register(new ServletJaxRsContextFactoryProvider(config));
        environment.jersey().register(new Pac4JSecurityFeature(config));
        environment.jersey().register(new Pac4JValueFactoryProvider.Binder());

        PathMatcher matcher = new PathMatcher()
                .excludePath("/")
                .excludePath("/login")
                .excludeBranch("/user")
                .excludePath(FORM_CLIENT_CALLBACK);
        config.addMatcher(PATH_MATCHER, matcher);

        JooqAuthenticator authenticator = new JooqAuthenticator(jooqConfig);
        FormClient formClient = new FormClient("/login", authenticator);
        formClient.setName(FORM_CLIENT_NAME);

        Clients clients = new Clients(FORM_CLIENT_CALLBACK, formClient);
        clients.setCallbackUrlResolver(new JaxRsCallbackUrlResolver());
        config.setClients(clients);

        // set a global security filter; require authentication everywhere, except for whitelisted paths.
        // Note that authorization (roles, permissions) must be handled separately.
        environment.jersey()
                .register(new Pac4JSecurityFilterFeature(config, null,
                        "isAuthenticated", FORM_CLIENT_NAME,
                        PATH_MATCHER, null));

        // An authorizer that only allows superusers
        config.addAuthorizer("superuser", new RequireAnyRoleAuthorizer<>(RoleName.SUPERUSER));

    }
}
