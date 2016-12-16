package com.uberlogik.pac4j.auth;

import com.google.common.io.BaseEncoding;
import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Base64 encoded password hash and salt.
 */
public class SaltedPassword
{
    private static final Logger LOG = LoggerFactory.getLogger(SaltedPassword.class);
    private static final int SALT_SIZE = 16; //16 bytes == 128 bits
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int ADDITIONAL_HASH_ITERATIONS = 1023;

    private final byte[] passwordHash;
    private final byte[] salt;

    private SaltedPassword(byte[] passwordHash, byte[] salt)
    {
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public static SaltedPassword of(String passwordHash, String salt)
    {
        return new SaltedPassword(BaseEncoding.base64().decode(passwordHash), BaseEncoding.base64().decode(salt));
    }

    public static SaltedPassword from(String password)
    {
        byte[] salt = generateSalt();
        byte[] hash = hash(password, salt);
        return new SaltedPassword(hash, salt);
    }

    public String getPasswordHash()
    {
        return BaseEncoding.base64().encode(passwordHash);
    }

    public String getSalt()
    {
        return BaseEncoding.base64().encode(salt);
    }

    boolean matches(String password)
    {
        byte[] hash = hash(password, this.salt);
        return Arrays.equals(hash, passwordHash);
    }

    private static byte[] generateSalt()
    {
        byte[] buffer = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(buffer);
        return buffer;
    }

    private static byte[] hash(String password, byte[] salt)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.reset();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());

            for (int i = 0; i < ADDITIONAL_HASH_ITERATIONS; i++)
            {
                digest.reset();
                hash = digest.digest(hash);
            }

            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            // should never happen
            String msg = "Unable to instantiate MessageDigest: " + HASH_ALGORITHM;
            LOG.error(msg);
            throw new TechnicalException(msg);
        }
    }
}
