package com.petcare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-path}")
    private Resource serviceAccountResource;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(serviceAccountResource.getInputStream()))
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Admin SDK initialized successfully");
            } else {
                log.info("ℹ️  Firebase Admin SDK already initialized");
            }
        } catch (IOException e) {
            log.error("❌ Failed to initialize Firebase Admin SDK: {}", e.getMessage());
            // Do NOT crash on startup so the app can still run for non-auth endpoints
        }
    }
}
