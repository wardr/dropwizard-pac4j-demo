package com.uberlogik.demo.security;

import com.uberlogik.demo.data.User;
import com.uberlogik.demo.data.UserDAO;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JDBIAuthenticator extends ProfileDefinitionAware<DemoProfile> implements Authenticator<UsernamePasswordCredentials>
{
    private static final Logger LOG = LoggerFactory.getLogger(JDBIAuthenticator.class);
    private final DBI jdbi;

    public JDBIAuthenticator(DBI jdbi)
    {
        this.jdbi = jdbi;
    }

    @Override
    protected void internalInit(final WebContext webContext)
    {
        setProfileDefinition(new CommonProfileDefinition<>(x -> new DemoProfile()));
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context)
            throws HttpAction, CredentialsException
    {
        init(context);

        String username = credentials.getUsername();

        if (username == null || username.isEmpty())
        {
            throw new BadCredentialsException("Username required");
        }

        final UserDAO dao = jdbi.onDemand(UserDAO.class);
        List<User> users = dao.findByUsername(username);

        if (users.isEmpty())
        {
            throw new AccountNotFoundException("Invalid account");
        }

        if (users.size() > 1)
        {
            // should never get here
            String msg = "More than one row found for username [" + username + "]. Username must be unique.";
            LOG.error(msg);
            throw new MultipleAccountsFoundException(msg);
        }

        User user = users.get(0);

        SaltedPassword password = SaltedPassword.of(user.getPasswordHash(), user.getSalt());
        if (password.matches(credentials.getPassword()))
        {
            final DemoProfile profile = DemoProfile.of(user, credentials.getClientName());
            credentials.setUserProfile(profile);
        }
        else
        {
            throw new BadCredentialsException("Invalid credentials for: " + username);
        }
    }
}
