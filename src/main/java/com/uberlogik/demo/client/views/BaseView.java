package com.uberlogik.demo.client.views;

import com.uberlogik.demo.db.tables.pojos.User;
import com.uberlogik.demo.security.UserProfile;
import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

public class BaseView extends View
{
    private UserProfile profile;

    public BaseView(String templateName)
    {
        super(templateName, StandardCharsets.UTF_8);
        profile = null;
    }

    public BaseView(String templateName, UserProfile profile)
    {
        super(templateName, StandardCharsets.UTF_8);
        this.profile = profile;
    }

    // override to display loggedin user in nav bar
    public UserProfile getProfile()
    {
        return profile;
    }
}
