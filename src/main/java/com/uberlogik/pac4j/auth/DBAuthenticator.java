package com.uberlogik.pac4j.auth;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.InitializableWebObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Enables use of local database via your preferred method (JOOQ, JDBI, JDBC or an ORM)
 *
 * @Author Rob Ward
 */
public abstract class DBAuthenticator<T> extends InitializableWebObject implements Authenticator<UsernamePasswordCredentials>
{
    private static final Logger LOG = LoggerFactory.getLogger(DBAuthenticator.class);

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context)
            throws HttpAction, CredentialsException
    {
        init(context);

        String username = credentials.getUsername();

        if (username == null || username.isEmpty())
        {
            throw new BadCredentialsException(labelNoUsername());
        }

        Collection<T> users = getUsers(username);

        if (users.isEmpty())
        {
            throw new AccountNotFoundException(labelNoSuchUser(username));
        }

        if (users.size() > 1)
        {
            // should never get here
            String msg = labelMultipleUsers(username);
            LOG.error(msg);
            throw new MultipleAccountsFoundException(msg);
        }

        T user = users.iterator().next();

        SaltedPassword password = saltedPassword(user);
        if (password.matches(credentials.getPassword()))
        {
            credentials.setUserProfile(profileOf(user, credentials.getClientName()));
        }
        else
        {
            throw new BadCredentialsException(labelBadPassword(username));
        }
    }

    protected abstract Collection<T> getUsers(String username);

    protected abstract SaltedPassword saltedPassword(T user);

    protected abstract CommonProfile profileOf(T user, String clientName);

    // optionally override these to customize error messages

    protected String labelNoUsername()
    {
        return "Username required";
    }

    protected String labelNoSuchUser(String username)
    {
        return "Invalid account";
    }

    protected String labelMultipleUsers(String username)
    {
        return "More than one user [username: " + username + "] found. Username must be unique.";
    }

    protected String labelBadPassword(String username)
    {
        return "Invalid credentials for: " + username;
    }
}
