package com.dishapal.helpdesk.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil
{
    public static String hashPassword(String password)
    {
        try
        {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = messageDigest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();

            for(byte value : hashBytes)
            {
                String hexValue =
                        Integer.toHexString(0xff & value);

                if(hexValue.length() == 1)
                {
                    result.append('0');
                }

                result.append(hexValue);
            }

            return result.toString();
        }
        catch(Exception e)
        {
            throw new RuntimeException(
                    "Unable to protect password", e);
        }
    }
}