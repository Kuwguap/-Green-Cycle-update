package com.greencycle.Backend.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    /**
     * Send a notification to a specific device token
     */
    public String sendNotificationToToken(String token, String title, String body, Map<String, String> data) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.warn("Firebase not initialized. Cannot send notification.");
                return "Firebase not configured";
            }
            
            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            
            // Add custom data if provided
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            
            logger.info("Successfully sent notification to token: {}", token);
            return response;
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send notification to token {}: {}", token, e.getMessage());
            return "Failed to send notification: " + e.getMessage();
        }
    }
    
    /**
     * Send notification to multiple device tokens
     */
    public BatchResponse sendNotificationToMultipleTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.warn("Firebase not initialized. Cannot send notifications.");
                return null;
            }
            
            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            
            // Add custom data if provided
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            MulticastMessage message = messageBuilder.build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            
            logger.info("Successfully sent notifications to {} tokens. Success count: {}, Failure count: {}", 
                    tokens.size(), response.getSuccessCount(), response.getFailureCount());
            
            return response;
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send notifications to multiple tokens: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Send notification to a topic (for broadcasting to all subscribed users)
     */
    public String sendNotificationToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.warn("Firebase not initialized. Cannot send notification.");
                return "Firebase not configured";
            }
            
            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            
            // Add custom data if provided
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            
            logger.info("Successfully sent notification to topic: {}", topic);
            return response;
            
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send notification to topic {}: {}", topic, e.getMessage());
            return "Failed to send notification: " + e.getMessage();
        }
    }
    
    /**
     * Subscribe a token to a topic
     */
    public void subscribeToTopic(List<String> tokens, String topic) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.warn("Firebase not initialized. Cannot subscribe to topic.");
                return;
            }
            
            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(tokens, topic);
            logger.info("Successfully subscribed {} tokens to topic: {}. Success count: {}, Failure count: {}", 
                    tokens.size(), topic, response.getSuccessCount(), response.getFailureCount());
                    
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to subscribe tokens to topic {}: {}", topic, e.getMessage());
        }
    }
    
    /**
     * Unsubscribe a token from a topic
     */
    public void unsubscribeFromTopic(List<String> tokens, String topic) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.warn("Firebase not initialized. Cannot unsubscribe from topic.");
                return;
            }
            
            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(tokens, topic);
            logger.info("Successfully unsubscribed {} tokens from topic: {}. Success count: {}, Failure count: {}", 
                    tokens.size(), topic, response.getSuccessCount(), response.getFailureCount());
                    
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to unsubscribe tokens from topic {}: {}", topic, e.getMessage());
        }
    }
}