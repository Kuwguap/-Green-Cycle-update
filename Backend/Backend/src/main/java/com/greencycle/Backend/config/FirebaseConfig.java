package com.greencycle.Backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${firebase.config.path}")
    private String firebaseConfigPath;
    
    @Value("${firebase.database.url}")
    private String firebaseDatabaseUrl;
    
    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Try to load Firebase service account key
                InputStream serviceAccount = null;
                try {
                    ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
                    if (resource.exists()) {
                        serviceAccount = resource.getInputStream();
                        
                        FirebaseOptions options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .setDatabaseUrl(firebaseDatabaseUrl)
                                .build();
                        
                        FirebaseApp.initializeApp(options);
                        logger.info("Firebase Admin SDK initialized successfully");
                    } else {
                        logger.warn("Firebase service account file not found. Push notifications will not work.");
                        logger.info("To enable push notifications, add firebase-service-account.json to src/main/resources/");
                    }
                } catch (IOException e) {
                    logger.error("Failed to initialize Firebase Admin SDK: {}", e.getMessage());
                    logger.info("Push notifications will not work without proper Firebase configuration");
                } finally {
                    if (serviceAccount != null) {
                        serviceAccount.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error during Firebase initialization: {}", e.getMessage());
        }
    }
}