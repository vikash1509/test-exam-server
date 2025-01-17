package com.exam.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier() {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("196206181599-pgheg8o4nr2tkm0am8u6hh11kg0sgm11.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verifyToken(String idToken) throws Exception {
        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken != null) {
            return googleIdToken.getPayload();
        }
        return null;
    }
}

