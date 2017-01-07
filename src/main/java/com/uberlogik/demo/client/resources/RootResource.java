package com.uberlogik.demo.client.resources;

import com.uberlogik.demo.client.views.BaseView;
import com.uberlogik.demo.client.views.LoginView;
import com.uberlogik.demo.security.UserProfile;
import io.dropwizard.views.View;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RootResource
{
    final FormClient formClient;

    public RootResource(FormClient formClient)
    {
        this.formClient = formClient;
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    public View home(@Pac4JProfile UserProfile profile)
    {
        return new BaseView("/com/uberlogik/demo/client/views/index.mustache", profile);
    }

    @GET
    @Path("/login")
    @Produces({MediaType.TEXT_HTML})
    public View login()
    {
        return new LoginView(formClient);
    }

    @POST
    @Path("/callback")
    @Pac4JCallback(multiProfile = false, renewSession = false, defaultUrl = "/")
    public void callbackPost()
    {
        // nothing to do; pac4j handles everything
    }

    @GET
    @Path("/callback")
    @Pac4JCallback(multiProfile = false, renewSession = false, defaultUrl = "/")
    public void callbackGet()
    {
        // nothing to do; pac4j handles everything
    }

    @GET
    @Path("/logout")
    @Pac4JLogout(defaultUrl = "/")
    public void logout()
    {
        // nothing to do; pac4j handles everything
    }

    // Example of a page that only authenticated AND authorized users can access
    @GET
    @Path("/protected")
    @Pac4JSecurity(authorizers = "superuser")
    @Produces({MediaType.TEXT_HTML})
    public View protectedExample(@Pac4JProfile UserProfile profile)
    {
        return new BaseView("/com/uberlogik/demo/client/views/protected.mustache", profile);
    }

}
