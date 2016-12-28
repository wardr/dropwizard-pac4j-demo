package com.uberlogik.demo.security;

import com.uberlogik.demo.db.tables.pojos.User;
import com.uberlogik.demo.db.tables.records.UserPermissionRecord;
import com.uberlogik.pac4j.auth.DBAuthenticator;
import com.uberlogik.pac4j.auth.SaltedPassword;
import org.jooq.Configuration;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.uberlogik.demo.db.Tables.USER;
import static com.uberlogik.demo.db.Tables.USER_PERMISSION;

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
                .where(USER.USERNAME.eq(username.toLowerCase()))
                .fetch().into(User.class);
    }

    @Override
    protected SaltedPassword saltedPassword(User user)
    {
        return SaltedPassword.of(user.getPwd(), user.getPwdSalt());
    }

    protected Map<String, Set<String>> getRolePermissions(User user)
    {
        Map<String, Set<String>> result = new HashMap<>();

        Result<UserPermissionRecord> records = DSL.using(jooqConfig).selectFrom(USER_PERMISSION)
                .where(USER_PERMISSION.USER_ID.eq(user.getUserId()))
                .fetch();

        for (UserPermissionRecord record : records)
        {
            String role = record.getRoleName();
            if (result.containsKey(role))
            {
                Set<String> permissions = result.get(role);
                permissions.add(record.getPermissionName());
            }
            else
            {
                Set<String> permissions = new HashSet<>();
                permissions.add(record.getPermissionName());
                result.put(role, permissions);
            }
        }

        return result;
    }

    @Override
    protected CommonProfile profileOf(User user, Map<String, Set<String>> rolePermissions, String clientName)
    {
        return UserProfile.of(user, rolePermissions, clientName);
    }

    @Override
    protected String errorNoUsername()
    {
        return "Username required";
    }

    @Override
    protected String errorNoSuchUser()
    {
        return "Invalid Username address";
    }

    @Override
    protected String errorBadPassword()
    {
        return "Login failed";
    }
}
