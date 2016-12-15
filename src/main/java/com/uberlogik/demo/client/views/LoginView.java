package com.uberlogik.demo.client.views;

public class LoginView extends BaseView
{
    private static final String TEMPLATE = "/com/uberlogik/demo/client/views/login.mustache";
    private final String actionUrl;

    public LoginView(String actionUrl)
    {
        super(TEMPLATE);
        this.actionUrl = actionUrl;
    }

    public String getActionUrl()
    {
        return actionUrl;
    }
}
