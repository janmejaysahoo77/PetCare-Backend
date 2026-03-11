package com.petcare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-path}")
    private Resource serviceAccountResource;

    @Value("${firebase.firestore-emulator-host:}")
    private String firestoreEmulatorHost;

    @PostConstruct
    public void initializeFirebase() {
        try {
            // Force IPv4 to avoid common gRPC connection issues on Windows
            System.setProperty("java.net.preferIPv4Stack", "true");

            // Set Firestore emulator host system property if provided
            if (firestoreEmulatorHost != null && !firestoreEmulatorHost.isBlank()) {
                System.setProperty("FIRESTORE_EMULATOR_HOST", firestoreEmulatorHost);
                log.info("⚙️  Using Firestore Emulator at: {}", firestoreEmulatorHost);
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(serviceAccountResource.getInputStream()))
                        .setConnectTimeout(60000)   // 60-second connect timeout
                        .setReadTimeout(60000)      // 60-second read timeout
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Admin SDK initialized successfully");
            } else {
                log.info("ℹ️  Firebase Admin SDK already initialized");
            }
        } catch (Exception e) {
            log.error("❌ Failed to initialize Firebase Admin SDK: {}", e.getMessage());
            throw new RuntimeException("Firebase initialization failed — check service-account.json and connectivity", e);
        }
    }

    @Bean(destroyMethod = "")
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
