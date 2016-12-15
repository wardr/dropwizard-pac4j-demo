package com.uberlogik.demo.security;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.matching.Matcher;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AuthPathMatcher implements Matcher
{
    // matching via hashes is more efficient than using a Regex
    private final Set<String> excludedPaths = new HashSet<String>();
    private final Set<Pattern> excludedPatterns = new HashSet<>();

    /**
     * Any path exactly matching this string will be excluded. Use this is you are excluding a single path.
     */
    public void addExcludedPath(String path)
    {
        excludedPaths.add(path);
    }

    /**
     * Convenience method for excluding all paths starting with a prefix e.g. "/foo" would exclude "/foo", "/foo/bar", etc.
     */
    public void addExcludedBranch(String root)
    {
        addExcludedRegex("^" + root + "(/.*)?$");
    }

    /**
     * Any path matching this regex will be excluded. Regexes are expensive to try to use them sparingly.
     */
    public void addExcludedRegex(String regex)
    {
        excludedPatterns.add(Pattern.compile(regex));
    }

    @Override
    public boolean matches(WebContext context) throws HttpAction
    {
        return matches(context.getPath());
    }

    boolean matches(String path)
    {
        if (excludedPaths.contains(path))
        {
            return false;
        }

        for (Pattern pattern : excludedPatterns)
        {
            if (pattern.matcher(path).matches())
            {
                return false;
            }
        }

        return true;
    }
}
