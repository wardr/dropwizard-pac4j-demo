package com.uberlogik.demo.client.resources;

import com.uberlogik.demo.client.views.BaseView;
import com.uberlogik.demo.client.views.LoginView;
import com.uberlogik.demo.security.DemoProfile;
import io.dropwizard.views.View;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

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
        // TODO: how do we get hold of formClient.getCallbackUrl()?
        return new LoginView("/callback");
    }

    // just something simple to handle the pac4j callback for now
    @POST
    @Path("/callback")
    @Pac4JCallback
    @Produces({MediaType.TEXT_HTML})
    public Response home(@Pac4JProfile DemoProfile profile)
    {
        if (profile != null)
        {
            URI uri = UriBuilder.fromPath("/").build();
            return Response.seeOther(uri).build();

        }
        else
        {
            // TODO: redirect to a login failed page
            throw new WebApplicationException(401);
        }
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
