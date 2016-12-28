package com.uberlogik.demo.client.resources;

import com.bendb.dropwizard.jooq.jersey.JooqInject;
import com.uberlogik.demo.client.views.UserView;
import com.uberlogik.demo.db.tables.pojos.User;
import com.uberlogik.demo.db.tables.records.UserRecord;
import com.uberlogik.demo.security.UserProfile;
import com.uberlogik.demo.security.RoleName;
import io.dropwizard.views.View;
import org.jooq.DSLContext;
import org.pac4j.jax.rs.annotations.Pac4JProfile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.uberlogik.demo.db.Tables.USER;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.val;

@Path("/user")
public class UserResource
{
    private final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,60}$");
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

    // Example of a page that returns different views depending on whether the user is logged in or not.
    @GET
    @Path("/{username}")
    @Produces({MediaType.TEXT_HTML})
    public View user(@PathParam("username") String username, @Pac4JProfile UserProfile profile, @JooqInject("jooq") DSLContext dsl)
    {
        User user = dsl.selectFrom(USER)
                .where(USER.USERNAME.eq(username.toLowerCase()))
                .fetchOne().into(User.class);

        boolean canEdit = profile != null
                && (profile.getUsername().equals(username) || profile.getRoles().contains(RoleName.SUPERUSER));

        if (canEdit)
        {
            return UserView.edit(user, profile);
        }

        return UserView.readOnly(user, profile);
    }

    @POST
    @Path("/{username}")
    @Produces({MediaType.TEXT_HTML})
    public Response user(@PathParam("username") String username, MultivaluedMap<String, String> form,
                         @Pac4JProfile UserProfile profile, @JooqInject("jooq") DSLContext dsl)
    {
        UserRecord user = dsl.selectFrom(USER).where(USER.USERNAME.eq(username)).fetchAny();
        if (user == null)
        {
            throw new WebApplicationException(422); // bad data; should never happen
        }

        boolean canEdit = profile != null
                && (profile.getUsername().equals(username) || profile.getRoles().contains(RoleName.SUPERUSER));

        if (!canEdit)
        {
            throw new WebApplicationException(403); // insufficient privileges to edit
        }

        List<String> errors = new ArrayList<>();

        String newUserName = getValue("username", form, USERNAME_PATTERN, errors);

        if (errors.isEmpty())
        {
            if (!newUserName.equals(username))
            {
                boolean exists = dsl.fetchExists(select().from(USER).where(USER.USERNAME.eq(newUserName)));
                if (exists)
                {
                    errors.add(newUserName + " is in use already. Try a different username.");
                }
            }
        }

        String newEmail = getValue("email", form, EMAIL_PATTERN, errors);

        if (!errors.isEmpty())
        {
            final String errorMsg = errors.stream().collect(Collectors.joining(", "));
            UserView view = UserView.edit(user.into(User.class), profile, errorMsg);
            return Response.ok(view).build();
        }

        // store the updates
        user.setUsername(newUserName);
        user.setEmail(newEmail);
        user.update();

        // TODO: how do we flush pac4j's UserProfile cache?

        // we are done, return to home page
        URI uri = UriBuilder.fromUri("/").build();
        return Response.seeOther(uri).build();
    }

    private String getValue(String fieldName, MultivaluedMap<String, String> form, Pattern pattern, List<String> errors)
    {
        String value = form.getFirst(fieldName);

        if (value == null || value.isEmpty())
        {
            errors.add(fieldName + " is required.");
            return value;
        }

        value = value.trim().toLowerCase();

        if (!pattern.matcher(value).matches())
        {
            errors.add("Invalid " + fieldName);
        }

        return value;
    }
}
