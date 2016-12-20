package com.uberlogik.demo.client.resources;


import com.uberlogik.demo.client.views.BaseView;
import com.uberlogik.demo.client.views.ClientErrorView;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/error")
@Produces(MediaType.TEXT_HTML)
public class ErrorResource
{
    public static final Response.StatusType UNPROCESSABLE_ENTITY = new Response.StatusType()
    {
        public int getStatusCode() { return 422; }
        public String getReasonPhrase() { return "Unprocessable Entity"; }
        public Response.Status.Family getFamily() { return Response.Status.Family.CLIENT_ERROR; }
    };

    /**
     * Provide the 401 Unauthorized page
     *
     * @return A localised view containing HTML
     */
    @GET
    @Path("/401")
    @CacheControl(noCache = true)
    public View view401()
    {
        return ClientErrorView.status401();
    }

    /**
     * Provide the 403 Forbidden page
     *
     * @return A localised view containing HTML
     */
    @GET
    @Path("/403")
    @CacheControl(noCache = true)
    public View view403()
    {
        return ClientErrorView.status403();
    }

    /**
     * Provide the 404 Not Found page
     *
     * @return A localised view containing HTML
     */
    @GET
    @Path("/404")
    @CacheControl(noCache = true)
    public View view404()
    {
        return ClientErrorView.status404();
    }

    /**
     * Provide the 422 Unprocessable Entity page
     *
     * @return A localised view containing HTML
     */
    @GET
    @Path("/422")
    @CacheControl(noCache = true)
    public View view422()
    {
        return ClientErrorView.status422();
    }

    /**
     * Provide the 500 Internal Server Error page
     *
     * @return A localised view containing HTML
     */
    @GET
    @Path("/500")
    @CacheControl(noCache = true)
    public View view500()
    {
        return new BaseView("/com/uberlogik/demo/client/views/error_5xx.mustache");
    }
}
