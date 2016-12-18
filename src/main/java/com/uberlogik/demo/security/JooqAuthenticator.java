package com.uberlogik.demo.security;

import com.uberlogik.demo.db.tables.pojos.User;
import com.uberlogik.pac4j.auth.DBAuthenticator;
import com.uberlogik.pac4j.auth.SaltedPassword;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.uberlogik.demo.db.Tables.USER;

public class JooqAuthenticator extends DBAuthenticator<User>
{
    private static final Logger LOG = LoggerFactory.getLogger(JooqAuthenticator.class);
    private final Configuration jooqConfig;

    public JooqAuthenticator(Configuration jooqConfig)
    {
        this.jooqConfig = jooqConfig;
    }

    @Override
    protected void internalInit(final WebContext context)
    {
        // nothing to do
    }

    @Override
    protected Collection<User> getUsers(String username)
    {
        return DSL.using(jooqConfig).selectFrom(USER)
                .where(USER.EMAIL.eq(username.toLowerCase()))
                .fetch().into(User.class);
    }

    @Override
    protected SaltedPassword saltedPassword(User user)
    {
        return SaltedPassword.of(user.getPwd(), user.getPwdSalt());
    }

    @Override
    protected CommonProfile profileOf(User user, String clientName)
    {
        return DemoProfile.of(user, clientName);
    }

    @Override
    protected String errorNoUsername()
    {
        return "Email address required";
    }

    @Override
    protected String errorNoSuchUser()
    {
        return "Invalid email address";
    }

    @Override
    protected String errorBadPassword()
    {
        return "Login failed";
    }
}
