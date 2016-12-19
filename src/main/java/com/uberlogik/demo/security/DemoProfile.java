package com.uberlogik.demo.security;

import com.uberlogik.demo.db.tables.pojos.User;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;

import java.util.Map;
import java.util.Set;

// Maps User into a pac4j UserProfile
public class DemoProfile extends CommonProfile
{
    private static final long serialVersionUID = 2696848236079309842L;

    public static DemoProfile of(User user, Map<String, Set<String>> rolePermissions, String clientName)
    {
        DemoProfile profile = new DemoProfile();

        // mandatory fields
        profile.setId(user.getUserId());
        profile.setClientName(clientName);
        profile.setRemembered(false); // i.e. is fully authenticated, not just "remembered"

        // Add roles and permissions
        for (Map.Entry<String, Set<String>> entry : rolePermissions.entrySet())
        {
            profile.addRole(entry.getKey());
            profile.addPermissions(entry.getValue());
        }

        // Override attributes in CommonProfile
        profile.addAttribute("email", user.getEmail());
        profile.addAttribute("display_name", user.getUsername());
        profile.addAttribute(Pac4jConstants.USERNAME, user.getUsername());
        profile.addAttribute("gender", Gender.UNSPECIFIED);
        profile.addAttribute("locale", "en_US");

        return profile;
    }


}
