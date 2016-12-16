package com.uberlogik.demo.security;

import com.uberlogik.demo.data.User;
import com.uberlogik.demo.data.UserDAO;
import com.uberlogik.pac4j.auth.DBAuthenticator;
import com.uberlogik.pac4j.auth.SaltedPassword;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.skife.jdbi.v2.DBI;

import java.util.Collection;

public class JDBIAuthenticator extends DBAuthenticator<User>
{
    private final DBI jdbi;

    public JDBIAuthenticator(DBI jdbi)
    {
        this.jdbi = jdbi;
    }

    @Override
    protected void internalInit(final WebContext context)
    {
        // nothing to do
    }

    @Override
    protected Collection<User> getUsers(String username)
    {
        final UserDAO dao = jdbi.onDemand(UserDAO.class);
        return dao.findByUsername(username);
    }

    @Override
    protected SaltedPassword saltedPassword(User user)
    {
        return SaltedPassword.of(user.getPasswordHash(), user.getSalt());
    }

    @Override
    protected CommonProfile profileOf(User user, String clientName)
    {
        return DemoProfile.of(user, clientName);
    }
}
