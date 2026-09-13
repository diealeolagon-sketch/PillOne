package com.pillone.pillone.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordService {

    private static final String PREFIX="PBKDF2";
    private static final String ALGORITHM="PBKDF2WithHmacSHA256";
    private static final int ITERATIONS=210000;
    private static final int SALT_BYTES=16;
    private static final int HASH_BYTES=32;

    private final SecureRandom secureRandom=new SecureRandom();

    public String hash(String password){
        if(password==null || password.isBlank()){
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        byte[] salt=new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);

        byte[] hash=derive(
                password.toCharArray(),
                salt,
                ITERATIONS,
                HASH_BYTES
        );

        return PREFIX+"$"+
                ITERATIONS+"$"+
                Base64.getEncoder().encodeToString(salt)+"$"+
                Base64.getEncoder().encodeToString(hash);
    }

    public boolean matches(String password,String stored){
        if(password==null || stored==null || !stored.startsWith(PREFIX+"$")){
            return false;
        }

        try{
            String[] parts=stored.split("\\$");

            if(parts.length!=4){
                return false;
            }

            int iterations=Integer.parseInt(parts[1]);

            byte[] salt=
                    Base64.getDecoder().decode(parts[2]);

            byte[] expected=
                    Base64.getDecoder().decode(parts[3]);

            byte[] actual=derive(
                    password.toCharArray(),
                    salt,
                    iterations,
                    expected.length
            );

            return MessageDigest.isEqual(expected,actual);

        }catch(Exception e){
            return false;
        }
    }

    public boolean esFormatoActual(String stored){
        return stored!=null &&
                stored.startsWith(PREFIX+"$");
    }

    private byte[] derive(
            char[] password,
            byte[] salt,
            int iterations,
            int bytes
    ){
        PBEKeySpec spec=
                new PBEKeySpec(
                        password,
                        salt,
                        iterations,
                        bytes*8
                );

        try{
            return SecretKeyFactory
                    .getInstance(ALGORITHM)
                    .generateSecret(spec)
                    .getEncoded();

        }catch(Exception e){
            throw new IllegalStateException(
                    "No fue posible procesar la contraseña.",
                    e
            );

        }finally{
            spec.clearPassword();
        }
    }
}