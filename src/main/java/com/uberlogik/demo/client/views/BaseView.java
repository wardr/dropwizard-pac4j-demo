package com.uberlogik.demo.client.views;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

public class BaseView extends View
{
    public BaseView(String templateName)
    {
        super(templateName, StandardCharsets.UTF_8);
    }
}
