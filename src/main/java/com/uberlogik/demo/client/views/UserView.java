package com.uberlogik.demo.client.views;

import com.uberlogik.demo.security.DemoProfile;

public class UserView extends BaseView
{
    private static final String TEMPLATE = "/com/uberlogik/demo/client/views/user.mustache";
    private final DemoProfile profile;

    public UserView(DemoProfile profile)
    {
        super(TEMPLATE);
        this.profile = profile;
    }

    public String getId()
    {
        return profile.getId();
    }

    public String getUsername()
    {
        return profile.getUsername();
    }

    public String getEmail()
    {
        return profile.getEmail();
    }
}
