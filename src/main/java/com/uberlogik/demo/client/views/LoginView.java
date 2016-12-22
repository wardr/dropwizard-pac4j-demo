package com.uberlogik.demo.client.views;

import org.pac4j.http.client.indirect.FormClient;

public class LoginView extends BaseView
{
    private static final String TEMPLATE = "/com/uberlogik/demo/client/views/login.mustache";
    private final FormClient formClient;

    public LoginView(FormClient formClient)
    {
        super(TEMPLATE);
        this.formClient = formClient;
    }

    public String getActionUrl()
    {
        return formClient.getCallbackUrl();
    }
}
