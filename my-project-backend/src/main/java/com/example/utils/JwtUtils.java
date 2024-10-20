package com.example.utils;

//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${spring}")
    String key;


    public String creatJwt(UserDetails details) {
        Algorithm algorithm = Algorithm.HMAC256();
    }
}
