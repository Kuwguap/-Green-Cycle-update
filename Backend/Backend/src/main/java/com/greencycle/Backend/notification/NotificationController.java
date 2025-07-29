package com.greencycle.Backend.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Send notification to a specific device token
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequest request) {
        try {
            String response = notificationService.sendNotificationToToken(
                    request.getToken(),
                    request.getTitle(),
                    request.getBody(),
                    request.getData()
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("messageId", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Send notification to multiple device tokens
     */
    @PostMapping("/send-multiple")
    public ResponseEntity<?> sendNotificationToMultiple(@RequestBody MultipleNotificationRequest request) {
        try {
            var response = notificationService.sendNotificationToMultipleTokens(
                    request.getTokens(),
                    request.getTitle(),
                    request.getBody(),
                    request.getData()
            );
            
            Map<String, Object> result = new HashMap<>();
            if (response != null) {
                result.put("success", true);
                result.put("successCount", response.getSuccessCount());
                result.put("failureCount", response.getFailureCount());
            } else {
                result.put("success", false);
                result.put("error", "Failed to send notifications");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Send notification to a topic (broadcast)
     */
    @PostMapping("/send-topic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendNotificationToTopic(@RequestBody TopicNotificationRequest request) {
        try {
            String response = notificationService.sendNotificationToTopic(
                    request.getTopic(),
                    request.getTitle(),
                    request.getBody(),
                    request.getData()
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("messageId", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Subscribe tokens to a topic
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToTopic(@RequestBody TopicSubscriptionRequest request) {
        try {
            notificationService.subscribeToTopic(request.getTokens(), request.getTopic());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Successfully subscribed to topic: " + request.getTopic());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Unsubscribe tokens from a topic
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeFromTopic(@RequestBody TopicSubscriptionRequest request) {
        try {
            notificationService.unsubscribeFromTopic(request.getTokens(), request.getTopic());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Successfully unsubscribed from topic: " + request.getTopic());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}

// Request DTOs
class NotificationRequest {
    private String token;
    private String title;
    private String body;
    private Map<String, String> data;
    
    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }
}

class MultipleNotificationRequest {
    private List<String> tokens;
    private String title;
    private String body;
    private Map<String, String> data;
    
    // Getters and setters
    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }
}

class TopicNotificationRequest {
    private String topic;
    private String title;
    private String body;
    private Map<String, String> data;
    
    // Getters and setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }
}

class TopicSubscriptionRequest {
    private List<String> tokens;
    private String topic;
    
    // Getters and setters
    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}