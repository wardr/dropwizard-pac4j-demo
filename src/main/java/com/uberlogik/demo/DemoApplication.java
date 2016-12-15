package com.uberlogik.demo;

import com.uberlogik.demo.client.resources.RootResource;
import com.uberlogik.demo.client.resources.UserResource;
import com.uberlogik.demo.security.SecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.skife.jdbi.v2.DBI;

public class DemoApplication extends Application<DemoConfiguration>
{
    private final SecurityBundle<DemoConfiguration> securityBundle = new SecurityBundle<DemoConfiguration>();


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
        bootstrap.addBundle(securityBundle);
    }

    @Override
    public void run(DemoConfiguration config, Environment env) throws Exception
    {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(env, config.getDataSourceFactory(), "mysql");
        securityBundle.build(env, jdbi);

        // HTML Pages
        env.jersey().register(new RootResource());
        env.jersey().register(new UserResource());
    }
}
