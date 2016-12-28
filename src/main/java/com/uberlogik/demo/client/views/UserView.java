package com.uberlogik.demo.client.views;

import com.uberlogik.demo.db.tables.pojos.User;
import com.uberlogik.demo.security.UserProfile;

public class UserView extends BaseView
{
    private static final String READ_ONLY_TEMPLATE = "/com/uberlogik/demo/client/views/user.mustache";
    private static final String EDIT_TEMPLATE = "/com/uberlogik/demo/client/views/user_edit.mustache";
    private final User user;
    private final UserProfile profile;
    private final String errors;

    private UserView(String template, User user, UserProfile profile, String errors)
    {
        super(template);
        this.user = user;
        this.profile = profile;
        this.errors = errors;
    }

    public static UserView readOnly(User user, UserProfile profile)
    {
        return new UserView(READ_ONLY_TEMPLATE, user, profile, "");
    }

    public static UserView edit(User user, UserProfile profile)
    {
        return edit(user, profile, "");
    }

    public static UserView edit(User user, UserProfile profile, String errors)
    {
        assert user != null;
        return new UserView(EDIT_TEMPLATE, user, profile, errors);
    }

    public Integer getUserId()
    {
        return user.getUserId();
    }

    public String getUsername()
    {
        return user.getUsername();
    }

    public String getEmail()
    {
        return user.getEmail();
    }

    public String getErrors() { return errors; }

    @Override
    public UserProfile getProfile()
    {
        return profile;
    }
}
