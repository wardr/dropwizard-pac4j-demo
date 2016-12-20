package com.uberlogik.demo.client.views;

public class ClientErrorView extends BaseView
{
    final static String TEMPLATE = "/com/uberlogik/demo/client/views/error_4xx.mustache";
    final String header;
    final String message;

    public ClientErrorView(String header, String message)
    {
        super(TEMPLATE);
        this.header = header;
        this.message = message;
    }

    public static ClientErrorView status401()
    {
        return new ClientErrorView("401 Unauthorized", "You must be logged in to access that page.");
    }

    public static ClientErrorView status403()
    {
        return new ClientErrorView("403 Forbidden", "Insufficient privileges to access that page.");
    }

    public static ClientErrorView status404()
    {
        return new ClientErrorView("404 Not Found", "That page does not exist.");
    }

    public static ClientErrorView status422()
    {
        return new ClientErrorView("422 Unprocessable Entity", "Bad data.");
    }

    public String getHeader()
    {
        return header;
    }

    public String getMessage()
    {
        return message;
    }
}
