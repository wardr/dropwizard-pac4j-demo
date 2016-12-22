package com.uberlogik.demo;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.uberlogik.demo.client.resources.ErrorResource;
import com.uberlogik.demo.client.resources.RootResource;
import com.uberlogik.demo.client.resources.RuntimeExceptionMapper;
import com.uberlogik.demo.client.resources.UserResource;
import com.uberlogik.demo.security.SecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;

public class DemoApplication extends Application<DemoConfiguration>
{
    private final SecurityBundle<DemoConfiguration> securityBundle = new SecurityBundle<DemoConfiguration>();

    final JooqBundle<DemoConfiguration> dbBundle = new JooqBundle<DemoConfiguration>()
    {
        /**
         * Required override to define default DataSourceFactory.
         */
        @Override
        public DataSourceFactory getDataSourceFactory(DemoConfiguration configuration)
        {
            return configuration.dataSourceFactory();
        }

        @Override
        public JooqFactory getJooqFactory(DemoConfiguration configuration)
        {
            return configuration.jooq();
        }
    };


    public static void main(String[] args) throws Exception
    {
        new DemoApplication().run(args);
    }

    @Override
    public String getName()
    {
        return "dropwizard-pac4j-demo";
    }

    @Override
    public void initialize(Bootstrap<DemoConfiguration> bootstrap)
    {
        bootstrap.addBundle(new ViewBundle<DemoConfiguration>());
        bootstrap.addBundle(dbBundle);
        bootstrap.addBundle(securityBundle);
    }

    @Override
    public void run(DemoConfiguration config, Environment env) throws Exception
    {
        securityBundle.build(env, dbBundle.getConfiguration());

        // HTML Pages
        env.jersey().register(new RootResource(securityBundle.getFormClient()));
        env.jersey().register(new UserResource());
        registerErrorHandlers(env);

    }

    private void registerErrorHandlers(Environment env)
    {
        env.jersey().register(new ErrorResource());

        // handles HTTP 404, 500, etc errors
        env.jersey().register(RuntimeExceptionMapper.class);

        final ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        // 400 - Bad Request, leave alone
        errorHandler.addErrorPage(401, "/error/401");
        errorHandler.addErrorPage(403, "/error/403");
        errorHandler.addErrorPage(404, "/error/404");
        errorHandler.addErrorPage(500, 599, "/error/500");
        env.getApplicationContext().setErrorHandler(errorHandler);
    }
}
