package com.uberlogik.demo.client.resources;

import com.uberlogik.demo.client.views.BaseView;
import com.uberlogik.demo.client.views.UserView;
import com.uberlogik.demo.security.DemoProfile;
import io.dropwizard.views.View;
import org.pac4j.jax.rs.annotations.Pac4JProfile;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserResource
{
    // Example of a page that any authenticated user can access
    @GET
    @Path("/{username}")
    @Produces({MediaType.TEXT_HTML})
    public View user(@PathParam("username") String username, @Pac4JProfile DemoProfile profile)
    {
        return new UserView(profile);
    }
}
