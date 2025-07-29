# Spring Boot Integration Guide for Green Cycle React Native App

## Overview

This guide provides a comprehensive step-by-step approach to integrate Spring Boot backend with the Green Cycle Expo React Native project. The integration includes PostgreSQL database, OAuth2 security, JWT authentication, and Firebase push notifications.

## 🏗️ Architecture Overview

### Backend stack
- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL (production) / H2 (development)
- **Security**: Spring Security + JWT + OAuth2
- **Authentication**: JWT tokens + OAuth2 (Google, Facebook)
- **Notifications**: Firebase Cloud Messaging
- **Blockchain**: Algorand integration for $CYCLE tokens
- **File Storage**: IPFS via Pinata

### Frontend Stack
- **Framework**: React Native with Expo
- **Authentication**: JWT token-based
- **API Communication**: REST API calls to Spring Boot backend

## 📋 Implementation Steps

### Step 1: Database Setup (PostgreSQL)

#### 1.1 Install PostgreSQL
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# macOS
brew install postgresql
brew services start postgresql

# Windows
# Download and install from https://www.postgresql.org/download/windows/
```

#### 1.2 Create Database
```sql
-- Connect to PostgreSQL
sudo -u postgres psql

-- Create database
CREATE DATABASE gcycle;

-- Create user (optional)
CREATE USER gcycle_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE gcycle TO gcycle_user;
```

#### 1.3 Database Schema
```sql
-- Connect to the database
\c gcycle

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    wallet_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles table (for role-based access control)
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Bins table (for recycling bins with QR codes)
CREATE TABLE bins (
    id BIGSERIAL PRIMARY KEY,
    qr_code_id VARCHAR(255) UNIQUE NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    status VARCHAR(50) DEFAULT 'EMPTY',
    last_reported TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bin reports table (for reporting bin status)
CREATE TABLE bin_reports (
    id BIGSERIAL PRIMARY KEY,
    bin_id BIGINT REFERENCES bins(id) NOT NULL,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    report_type VARCHAR(50),
    photo_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dump reports table (for reporting illegal dumping)
CREATE TABLE dump_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) NOT NULL,
    photo_url VARCHAR(500),
    report_type VARCHAR(50),
    description TEXT,
    location VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Marketplace items table (for upcycling & recycling marketplace)
CREATE TABLE marketplace_items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    condition_rating INTEGER,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    posted_by_user_id BIGINT REFERENCES users(id),
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Marketplace item photos table
CREATE TABLE marketplace_item_photos (
    item_id BIGINT REFERENCES marketplace_items(id),
    photo_url VARCHAR(500) NOT NULL
);

-- Artisan profiles table (for marketplace artisans)
CREATE TABLE artisan_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) UNIQUE,
    business_name VARCHAR(255),
    description TEXT,
    rating DECIMAL(3, 2) DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Artisan specialties table
CREATE TABLE artisan_specialties (
    artisan_id BIGINT REFERENCES artisan_profiles(id),
    specialty VARCHAR(100) NOT NULL
);

-- Artisan portfolio table
CREATE TABLE artisan_portfolio (
    artisan_id BIGINT REFERENCES artisan_profiles(id),
    portfolio_url VARCHAR(500) NOT NULL
);

-- Leaderboard entries table
CREATE TABLE leaderboard_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    period VARCHAR(20) NOT NULL, -- WEEKLY, MONTHLY, ALL_TIME
    cycle_balance DECIMAL(20, 8) DEFAULT 0,
    cleanup_count INTEGER DEFAULT 0,
    spot_adoptions INTEGER DEFAULT 0,
    marketplace_sales INTEGER DEFAULT 0,
    rank INTEGER,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, period)
);

-- Create indexes for performance
CREATE INDEX idx_bin_reports_bin_id ON bin_reports(bin_id);
CREATE INDEX idx_bin_reports_user_id ON bin_reports(user_id);
CREATE INDEX idx_dump_reports_user_id ON dump_reports(user_id);
CREATE INDEX idx_marketplace_items_user_id ON marketplace_items(posted_by_user_id);
CREATE INDEX idx_marketplace_items_status ON marketplace_items(status);
CREATE INDEX idx_marketplace_items_category ON marketplace_items(category);
CREATE INDEX idx_leaderboard_entries_period ON leaderboard_entries(period);
CREATE INDEX idx_leaderboard_entries_rank ON leaderboard_entries(rank);
```

#### 1.4 Configure Database Connection
Update `Backend/Backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gcycle
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### Step 2: JWT Configuration

#### 2.1 JWT Properties
The JWT configuration is already set up in `application.properties`:
```properties
jwt.secret=mySecretKey123456789012345678901234567890123456789012345678901234567890
jwt.expiration=86400000
```

#### 2.2 JWT Components
- **JwtUtil**: Handles token generation and validation
- **JwtAuthFilter**: Filters incoming requests for JWT authentication
- **SecurityConfig**: Configures Spring Security with JWT

### Step 3: OAuth2 Integration

#### 3.1 OAuth2 Configuration
Update OAuth2 credentials in `application.properties`:
```properties
# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret

# Facebook OAuth2
spring.security.oauth2.client.registration.facebook.client-id=your-facebook-app-id
spring.security.oauth2.client.registration.facebook.client-secret=your-facebook-app-secret
```

#### 3.2 OAuth2 Setup Steps

**Google OAuth2 Setup:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth2 credentials
5. Add authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`

**Facebook OAuth2 Setup:**
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app
3. Add Facebook Login product
4. Configure OAuth redirect URIs: `http://localhost:8080/login/oauth2/code/facebook`

### Step 4: Firebase Push Notifications

#### 4.1 Firebase Project Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project
3. Add an Android/iOS app
4. Download `google-services.json` (Android) or `GoogleService-Info.plist` (iOS)
5. Generate a private key for service account:
   - Go to Project Settings > Service Accounts
   - Click "Generate new private key"
   - Save as `firebase-service-account.json`

#### 4.2 Backend Firebase Configuration
1. Place `firebase-service-account.json` in `src/main/resources/`
2. Update Firebase URL in `application.properties`:
```properties
firebase.database.url=https://your-project-id-default-rtdb.firebaseio.com/
```

#### 4.3 Frontend Firebase Setup
Install Firebase SDK in your React Native project:
```bash
cd Frontend
npm install @react-native-firebase/app @react-native-firebase/messaging
```

### Step 5: API Endpoints

#### 5.1 Authentication Endpoints
```
POST /api/auth/register - User registration
POST /api/auth/login - User login
GET /api/user/profile - Get user profile
GET /api/user/me - Get current user
```

#### 5.2 OAuth2 Endpoints
```
GET /oauth2/authorization/google - Google OAuth2 login
GET /oauth2/authorization/facebook - Facebook OAuth2 login
GET /api/oauth2/success - OAuth2 success callback
GET /api/oauth2/failure - OAuth2 failure callback
```

#### 5.3 Notification Endpoints
```
POST /api/notifications/send - Send notification to single token
POST /api/notifications/send-multiple - Send to multiple tokens
POST /api/notifications/send-topic - Send to topic (admin only)
POST /api/notifications/subscribe - Subscribe to topic
POST /api/notifications/unsubscribe - Unsubscribe from topic
```

### Step 6: Frontend Integration

#### 6.1 Update API Base URL
Update the API URLs in your React Native components:
```typescript
// Choose one of these based on your environment:

// For local development
const API_BASE_URL = 'http://localhost:8080/api';

// OR for production
// const API_BASE_URL = 'https://your-domain.com/api';
```

#### 6.2 Authentication Integration
The existing `auth-content.tsx` is already configured to work with the backend:
```typescript
// Example 1: Login
const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password }),
});

// Example 2: Get profile
const profileResponse = await fetch('http://localhost:8080/api/user/profile', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### Step 7: Running the Application

#### 7.1 Start Backend
```bash
cd Backend/Backend
./mvnw spring-boot:run
```

#### 7.2 Start Frontend
```bash
cd Frontend
npm start
# or
expo start
```

## 🔧 Configuration Files Summary

### Key Configuration Files Modified/Created:

1. **application.properties** - Database, JWT, OAuth2, Firebase config
2. **User.java** - Complete user entity with getters/setters
3. **AuthController.java** - Fixed to return JWT tokens
4. **UserController.java** - Added /profile endpoint
5. **JwtUtil.java** - Updated to use configurable properties
6. **SecurityConfig.java** - Integrated OAuth2 with JWT
7. **OAuth2AuthController.java** - Handles OAuth2 authentication
8. **FirebaseConfig.java** - Firebase initialization
9. **NotificationService.java** - Push notification service
10. **NotificationController.java** - Notification REST endpoints

## 🧪 Testing the Integration

### Test Authentication
```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password","name":"Test User"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Get profile (use token from login response)
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Test Notifications
```bash
# Send notification
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "token":"DEVICE_FCM_TOKEN",
    "title":"Test Notification",
    "body":"This is a test notification"
  }'
```

## 🚀 Deployment Considerations

### Production Configuration
1. **Database**: Use production PostgreSQL instance
2. **JWT Secret**: Use a secure, randomly generated secret
3. **OAuth2**: Update redirect URIs for production domain
4. **Firebase**: Use production Firebase project
5. **HTTPS**: Enable SSL/TLS for production

### Environment Variables
Consider using environment variables for sensitive data:
```properties
spring.datasource.password=${DB_PASSWORD:postgres}
jwt.secret=${JWT_SECRET:default-secret}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
```

## 📚 Additional Features

### Blockchain Integration
The backend includes Algorand blockchain integration for $CYCLE tokens:
- **WalletService**: Generates Algorand wallets for users
- **BlockchainService**: Handles token transactions

### IPFS Integration
For immutable file storage:
- **IpfsService**: Uploads files to IPFS via Pinata
- Used for storing before/after cleanup photos

## 🔍 Troubleshooting

### Common Issues:
1. **Database Connection**: Ensure PostgreSQL is running and credentials are correct
2. **JWT Errors**: Check token expiration and secret key
3. **OAuth2 Issues**: Verify client IDs and redirect URIs
4. **Firebase Errors**: Ensure service account file is in resources folder
5. **CORS Issues**: Configure CORS for frontend domain

### Logs to Check:
- Spring Boot application logs
- PostgreSQL logs
- Firebase console logs
- Browser network tab for API calls

## 📞 Support

For issues or questions:
1. Check the implementation_summary.md for feature details
2. Review Spring Boot and Firebase documentation
3. Check the project's existing backend implementation

This integration provides a robust, scalable backend for the Green Cycle application with modern authentication, real-time notifications, and blockchain capabilities.