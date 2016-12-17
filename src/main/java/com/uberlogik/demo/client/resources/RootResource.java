package com.uberlogik.demo.client.resources;

import com.uberlogik.demo.client.views.BaseView;
import com.uberlogik.demo.client.views.LoginView;
import io.dropwizard.views.View;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RootResource
{
    @GET
    @Produces({MediaType.TEXT_HTML})
    public View home()
    {
        return new BaseView("/com/uberlogik/demo/client/views/index.mustache");
    }

    @GET
    @Path("/login")
    @Produces({MediaType.TEXT_HTML})
    public View login()
    {
        return new LoginView("/callback");
    }

    @POST
    @Path("/callback")
    @Pac4JCallback(multiProfile = false, renewSession = false, defaultUrl = "/")
    public void callbackPost()
    {
        // nothing to do here, pac4j handles everything
        // note that in jax-rs, you can't have two different http method on the
        // same resource method hence the duplication
    }

    @GET
    @Path("/callback")
    @Pac4JCallback(multiProfile = false, renewSession = false, defaultUrl = "/")
    public void callbackGet()
    {
        // nothing to do here, pac4j handles everything
        // note that in jax-rs, you can't have two different http method on the
        // same resource method hence the duplication
    }

    @GET
    @Path("/logout")
    @Pac4JLogout(defaultUrl = "/")
    public void logout()
    {
        // nothing to do here, pac4j handles everything
    }

    // Example of a page that only authenticated AND authorized users can access
    // TODO: add authorization
    @GET
    @Path("/admin")
    @Produces({MediaType.TEXT_HTML})
    public View admin()
    {

        return new BaseView("/com/uberlogik/demo/client/views/admin.mustache");
    }

}
