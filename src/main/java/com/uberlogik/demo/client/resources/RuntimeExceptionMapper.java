package com.uberlogik.demo.client.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey exception to response mapping.
 * Note that we cannot map exceptions of type Throwable because otherwise this exception mapper will not override
 * Dropwizard's LoggingExceptionMapper.
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException>
{
    private static final Logger log = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException e)
    {
        // Build default response
        Response defaultResponse = Response
                .serverError()
                .entity(new ErrorResource().view500())
                .build();

        // Check for any specific handling
        if (e instanceof WebApplicationException)
        {
            return handle((WebApplicationException) e, defaultResponse);
        }

        logError(e, e.getMessage());
        return defaultResponse;
    }

    private Response handle(WebApplicationException e, Response defaultResponse)
    {
        if (e.getResponse().getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
        {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResource().view401())
                    .build();
        }

        if (e.getResponse().getStatus() == Response.Status.FORBIDDEN.getStatusCode())
        {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(new ErrorResource().view403())
                    .build();
        }

        if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode())
        {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResource().view404())
                    .build();
        }

        if (e.getResponse().getStatus() == ErrorResource.UNPROCESSABLE_ENTITY.getStatusCode())
        {
            return Response
                    .status(ErrorResource.UNPROCESSABLE_ENTITY.getStatusCode())
                    .entity(new ErrorResource().view422())
                    .build();
        }


        logError(e);
        return defaultResponse;
    }

    private void logError(WebApplicationException e)
    {
        String msg = "HTTP Response: " + Integer.toString(e.getResponse().getStatus());
        logError(e, msg);
    }

    private void logError(Throwable e, String msg)
    {
        msg = (msg != null && !msg.isEmpty()) ? msg : "Unhandled Error";
        Throwable t = e.getCause() != null ? e.getCause() : e;
        log.error(msg, t);
    }

}
