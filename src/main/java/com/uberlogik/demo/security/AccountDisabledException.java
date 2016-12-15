package com.uberlogik.demo.security;

import org.pac4j.core.exception.CredentialsException;

public class AccountDisabledException extends CredentialsException
{
    public AccountDisabledException(String message)
    {
        super(message);
    }
}
