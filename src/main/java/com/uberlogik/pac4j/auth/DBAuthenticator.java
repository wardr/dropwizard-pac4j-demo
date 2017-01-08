package com.uberlogik.pac4j.auth;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enables use of local database via your preferred method (JOOQ, JDBI, JDBC or an ORM)
 *
 * @Author Rob Ward
 */
public abstract class DBAuthenticator<T> extends AbstractUsernamePasswordAuthenticator
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
            throw new BadCredentialsException(errorNoUsername());
        }

        Collection<T> users = getUsers(username);

        if (users.isEmpty())
        {
            throw new AccountNotFoundException(errorNoSuchUser());
        }

        if (users.size() > 1)
        {
            // should never get here
            String msg = "More than one user for [username: " + username + "] found. Username must be unique.";
            LOG.error(msg);
            throw new MultipleAccountsFoundException("Username not unique");
        }

        T user = users.iterator().next();

        if (getPasswordEncoder().matches(credentials.getPassword(), password(user)))
        {
            loginSuccessful(user);
            Map<String, Set<String>> rolePermissions = getRolePermissions(user);
            credentials.setUserProfile(profileOf(user, rolePermissions, credentials.getClientName()));

        }
        else
        {
            loginFailed(user);
            throw new BadCredentialsException(errorBadPassword());
        }
    }

    protected abstract Collection<T> getUsers(String username);

    protected abstract String password(T user);

    /**
     * Returns a Map, with contents:
     *   Key: role name
     *   Value: set of permissions for the associated role
     */
    protected abstract Map<String, Set<String>> getRolePermissions(T user);

    protected abstract CommonProfile profileOf(T user, Map<String, Set<String>> rolePermissions, String clientName);

    /**
     * Override this method to perform an action after a successful login, e.g. recording a login event.
     */
    protected void loginSuccessful(T user) {}

    /**
     * Override this method to perform an action after a failed login, e.g. throttling failed logins
     */
    protected void loginFailed(T user) {}

    // optionally override these to customize error messages

    protected String errorNoUsername()
    {
        return "Username required";
    }

    protected String errorNoSuchUser()
    {
        return "Invalid account.";
    }

    protected String errorBadPassword()
    {
        return "Login failed";
    }
}
