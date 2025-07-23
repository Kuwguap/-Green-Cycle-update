# Green Cycle - Complete Feature Implementation Guide

## 🏗️ Architecture Overview

### Backend Structure (Spring Boot)
```
backend/
├── src/main/java/com/greencycle/
│   ├── auth/                    # Authentication & Security
│   ├── bin/                     # Smart QR Code Bin Reporting
│   ├── marketplace/             # Upcycling & Recycling Marketplace
│   ├── spot/                    # Adopt-a-Spot
│   ├── dao/                     # DAO & Governance
│   ├── reward/                  # Tokenized Rewards ($CYCLE)
│   ├── leaderboard/             # Leaderboard System
│   ├── nft/                     # NFT Badges & Achievements
│   ├── cleanup/                 # Before & After Photos
│   ├── news/                    # News Feed & Notifications
│   ├── waas/                    # Waste-as-a-Service
│   ├── blockchain/              # Algorand Integration
│   └── common/                  # Shared Components
```

## 📋 Feature Implementation Roadmap

### Phase 1: Core Infrastructure
1. **Authentication & User Management** ✅ (Already implemented)
2. **Smart QR Code System**
3. **Basic Reward System**

### Phase 2: Core Features
4. **Before & After Photos**
5. **Leaderboard**
6. **NFT Badges**

### Phase 3: Advanced Features
7. **Marketplace**
8. **Adopt-a-Spot**
9. **DAO Governance**

### Phase 4: Professional Features
10. **Waste-as-a-Service**
11. **News & Notifications**

---

## 🚀 Detailed Implementation Guide

### 1. Smart QR Code for Bin Reporting

#### Database Schema
```sql
-- Bins table
CREATE TABLE bins (
    id BIGSERIAL PRIMARY KEY,
    qr_code_id VARCHAR(255) UNIQUE NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    status VARCHAR(50) DEFAULT 'EMPTY', -- EMPTY, FULL, REPORTED, COLLECTED
    last_reported TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bin reports table
CREATE TABLE bin_reports (
    id BIGSERIAL PRIMARY KEY,
    bin_id BIGINT REFERENCES bins(id),
    user_id BIGINT REFERENCES users(id),
    report_type VARCHAR(50), -- FULL, DAMAGED, OVERFLOW
    photo_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Spring Boot Implementation

**Model Classes:**
```java
// Bin.java
@Entity
@Table(name = "bins")
public class Bin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "qr_code_id", unique = true, nullable = false)
    private String qrCodeId;
    
    @Column(name = "location_name")
    private String locationName;
    
    @Column(name = "latitude")
    private BigDecimal latitude;
    
    @Column(name = "longitude")
    private BigDecimal longitude;
    
    @Enumerated(EnumType.STRING)
    private BinStatus status;
    
    @Column(name = "last_reported")
    private LocalDateTime lastReported;
    
    // Getters, setters, constructors
}

// BinReport.java
@Entity
@Table(name = "bin_reports")
public class BinReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "bin_id")
    private Bin bin;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    private String description;
    
    // Getters, setters, constructors
}
```

**Controller:**
```java
@RestController
@RequestMapping("/api/bins")
@CrossOrigin(origins = "*")
public class BinController {
    
    @Autowired
    private BinService binService;
    
    @PostMapping("/{qrCodeId}/report")
    public ResponseEntity<BinReportResponse> reportBin(
            @PathVariable String qrCodeId,
            @RequestBody BinReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        BinReportResponse response = binService.reportBin(qrCodeId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<Bin>> getNearbyBins(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        
        List<Bin> bins = binService.findNearbyBins(latitude, longitude, radiusKm);
        return ResponseEntity.ok(bins);
    }
}
```

**Service:**
```java
@Service
public class BinService {
    
    @Autowired
    private BinRepository binRepository;
    
    @Autowired
    private BinReportRepository binReportRepository;
    
    @Autowired
    private RewardService rewardService;
    
    @Autowired
    private NotificationService notificationService;
    
    public BinReportResponse reportBin(String qrCodeId, BinReportRequest request, String userEmail) {
        // Find bin by QR code
        Bin bin = binRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bin not found"));
        
        // Create report
        BinReport report = new BinReport();
        report.setBin(bin);
        report.setUser(userRepository.findByEmail(userEmail).orElseThrow());
        report.setReportType(request.getReportType());
        report.setPhotoUrl(request.getPhotoUrl());
        report.setDescription(request.getDescription());
        
        binReportRepository.save(report);
        
        // Update bin status
        bin.setStatus(BinStatus.REPORTED);
        bin.setLastReported(LocalDateTime.now());
        binRepository.save(bin);
        
        // Award rewards
        RewardResponse reward = rewardService.awardForBinReport(userEmail, report);
        
        // Notify waste collection service
        notificationService.notifyWasteCollection(bin, report);
        
        return new BinReportResponse(report, reward);
    }
}
```

### 2. Upcycling & Recycling Marketplace

#### Database Schema
```sql
-- Marketplace items
CREATE TABLE marketplace_items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    condition_rating INTEGER, -- 1-5 stars
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    photo_urls TEXT[], -- Array of photo URLs
    status VARCHAR(50) DEFAULT 'AVAILABLE', -- AVAILABLE, RESERVED, SOLD
    posted_by_user_id BIGINT REFERENCES users(id),
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Artisan profiles
CREATE TABLE artisan_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    business_name VARCHAR(255),
    description TEXT,
    specialties TEXT[], -- Array of specialties
    portfolio_urls TEXT[], -- Array of portfolio URLs
    rating DECIMAL(3, 2),
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Marketplace conversations
CREATE TABLE marketplace_conversations (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES marketplace_items(id),
    buyer_id BIGINT REFERENCES users(id),
    seller_id BIGINT REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT REFERENCES marketplace_conversations(id),
    sender_id BIGINT REFERENCES users(id),
    content TEXT,
    message_type VARCHAR(50) DEFAULT 'TEXT', -- TEXT, IMAGE, OFFER
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Spring Boot Implementation

**Controller:**
```java
@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {
    
    @Autowired
    private MarketplaceService marketplaceService;
    
    @GetMapping("/items")
    public ResponseEntity<Page<MarketplaceItem>> getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) Double radiusKm) {
        
        Page<MarketplaceItem> items = marketplaceService.findItems(page, size, category, latitude, longitude, radiusKm);
        return ResponseEntity.ok(items);
    }
    
    @PostMapping("/items")
    public ResponseEntity<MarketplaceItem> createItem(
            @RequestBody CreateItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MarketplaceItem item = marketplaceService.createItem(request, userDetails.getUsername());
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/items/{itemId}/contact")
    public ResponseEntity<Conversation> contactSeller(
            @PathVariable Long itemId,
            @RequestBody ContactRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Conversation conversation = marketplaceService.createConversation(itemId, request, userDetails.getUsername());
        return ResponseEntity.ok(conversation);
    }
}
```

### 3. Adopt-a-Spot

#### Database Schema
```sql
-- Spots table
CREATE TABLE spots (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    polygon_coordinates TEXT, -- GeoJSON polygon
    difficulty_level VARCHAR(50), -- EASY, MEDIUM, HARD
    estimated_cleanup_time INTEGER, -- minutes
    status VARCHAR(50) DEFAULT 'AVAILABLE', -- AVAILABLE, ADOPTED, CLEANED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Spot adoptions
CREATE TABLE spot_adoptions (
    id BIGSERIAL PRIMARY KEY,
    spot_id BIGINT REFERENCES spots(id),
    user_id BIGINT REFERENCES users(id),
    adoption_type VARCHAR(50), -- INDIVIDUAL, GROUP, BUSINESS
    group_name VARCHAR(255), -- For group adoptions
    commitment_months INTEGER,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, COMPLETED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Spot cleanup reports
CREATE TABLE spot_cleanups (
    id BIGSERIAL PRIMARY KEY,
    spot_id BIGINT REFERENCES spots(id),
    user_id BIGINT REFERENCES users(id),
    before_photo_url VARCHAR(500),
    after_photo_url VARCHAR(500),
    cleanup_duration INTEGER, -- minutes
    description TEXT,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. DAO, Tokenized Rewards ($CYCLE), and Leaderboard

#### Database Schema
```sql
-- User wallets
CREATE TABLE user_wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    algorand_address VARCHAR(255) UNIQUE,
    cycle_balance DECIMAL(20, 8) DEFAULT 0,
    total_earned DECIMAL(20, 8) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DAO proposals
CREATE TABLE dao_proposals (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    proposal_type VARCHAR(50), -- REWARD_CHANGE, FEATURE_REQUEST, GOVERNANCE
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, PASSED, REJECTED, EXPIRED
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    min_votes INTEGER DEFAULT 10,
    created_by_user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DAO votes
CREATE TABLE dao_votes (
    id BIGSERIAL PRIMARY KEY,
    proposal_id BIGINT REFERENCES dao_proposals(id),
    user_id BIGINT REFERENCES users(id),
    vote_type VARCHAR(10), -- YES, NO, ABSTAIN
    voting_power DECIMAL(20, 8), -- Based on $CYCLE balance
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Leaderboard entries
CREATE TABLE leaderboard_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    period VARCHAR(20), -- WEEKLY, MONTHLY, ALL_TIME
    cycle_balance DECIMAL(20, 8),
    cleanup_count INTEGER DEFAULT 0,
    spot_adoptions INTEGER DEFAULT 0,
    marketplace_sales INTEGER DEFAULT 0,
    rank INTEGER,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Spring Boot Implementation

**Blockchain Service:**
```java
@Service
public class BlockchainService {
    
    @Value("${algorand.network}")
    private String network;
    
    @Value("${algorand.application.id}")
    private Long applicationId;
    
    private AlgodClient algodClient;
    private IndexerClient indexerClient;
    
    @PostConstruct
    public void init() {
        algodClient = new AlgodClient(network, 443, "");
        indexerClient = new IndexerClient(network, 443, "");
    }
    
    public String mintTokens(String userAddress, BigDecimal amount) {
        try {
            // Create transaction to mint $CYCLE tokens
            Transaction txn = Transaction.ApplicationCallTransactionBuilder()
                    .sender(userAddress)
                    .applicationId(applicationId)
                    .args(Arrays.asList("mint", amount.toString()))
                    .build();
            
            // Sign and submit transaction
            SignedTransaction signedTxn = userWallet.signTransaction(txn);
            String txId = algodClient.Transaction().rawtxn(signedTxn.signedTx).execute().body().txId;
            
            return txId;
        } catch (Exception e) {
            throw new BlockchainException("Failed to mint tokens", e);
        }
    }
    
    public BigDecimal getTokenBalance(String userAddress) {
        try {
            Account account = algodClient.AccountInformation(userAddress).execute().body();
            // Parse $CYCLE token balance from account assets
            return parseCycleBalance(account);
        } catch (Exception e) {
            throw new BlockchainException("Failed to get token balance", e);
        }
    }
}
```

**DAO Service:**
```java
@Service
public class DaoService {
    
    @Autowired
    private DaoProposalRepository proposalRepository;
    
    @Autowired
    private DaoVoteRepository voteRepository;
    
    @Autowired
    private BlockchainService blockchainService;
    
    public DaoProposal createProposal(CreateProposalRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        
        DaoProposal proposal = new DaoProposal();
        proposal.setTitle(request.getTitle());
        proposal.setDescription(request.getDescription());
        proposal.setProposalType(request.getProposalType());
        proposal.setStartDate(LocalDateTime.now());
        proposal.setEndDate(LocalDateTime.now().plusDays(7)); // 7-day voting period
        proposal.setCreatedBy(user);
        
        return proposalRepository.save(proposal);
    }
    
    public VoteResponse vote(Long proposalId, VoteRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        DaoProposal proposal = proposalRepository.findById(proposalId).orElseThrow();
        
        // Check if user already voted
        if (voteRepository.existsByProposalAndUser(proposal, user)) {
            throw new IllegalStateException("User already voted on this proposal");
        }
        
        // Get user's voting power based on $CYCLE balance
        BigDecimal votingPower = blockchainService.getTokenBalance(user.getAlgorandAddress());
        
        DaoVote vote = new DaoVote();
        vote.setProposal(proposal);
        vote.setUser(user);
        vote.setVoteType(request.getVoteType());
        vote.setVotingPower(votingPower);
        
        voteRepository.save(vote);
        
        return new VoteResponse(vote);
    }
}
```

### 5. Immutable Before & After / NFT Badges

#### Database Schema
```sql
-- Cleanup reports
CREATE TABLE cleanup_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    spot_id BIGINT REFERENCES spots(id),
    before_photo_ipfs_hash VARCHAR(255),
    after_photo_ipfs_hash VARCHAR(255),
    cleanup_duration INTEGER, -- minutes
    description TEXT,
    verified BOOLEAN DEFAULT FALSE,
    verified_by_user_id BIGINT REFERENCES users(id),
    verified_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NFT badges
CREATE TABLE nft_badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100), -- CLEANUP, SPOT_GUARDIAN, MARKETPLACE, SPECIAL
    rarity VARCHAR(50), -- COMMON, RARE, EPIC, LEGENDARY
    image_ipfs_hash VARCHAR(255),
    metadata_ipfs_hash VARCHAR(255),
    algorand_asset_id BIGINT,
    requirements JSONB, -- Criteria for earning the badge
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User badges
CREATE TABLE user_badges (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    badge_id BIGINT REFERENCES nft_badges(id),
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    algorand_transaction_id VARCHAR(255)
);
```

#### Spring Boot Implementation

**IPFS Service:**
```java
@Service
public class IpfsService {
    
    @Autowired
    private Web3j web3j;
    
    public String uploadImage(MultipartFile file) {
        try {
            // Upload to IPFS
            String ipfsHash = uploadToIpfs(file.getBytes());
            return ipfsHash;
        } catch (Exception e) {
            throw new IpfsException("Failed to upload image to IPFS", e);
        }
    }
    
    public String uploadMetadata(NftMetadata metadata) {
        try {
            String jsonMetadata = objectMapper.writeValueAsString(metadata);
            String ipfsHash = uploadToIpfs(jsonMetadata.getBytes());
            return ipfsHash;
        } catch (Exception e) {
            throw new IpfsException("Failed to upload metadata to IPFS", e);
        }
    }
}
```

**NFT Service:**
```java
@Service
public class NftService {
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Autowired
    private IpfsService ipfsService;
    
    public NftBadge mintBadge(Long userId, Long badgeId) {
        User user = userRepository.findById(userId).orElseThrow();
        Badge badge = badgeRepository.findById(badgeId).orElseThrow();
        
        // Create NFT metadata
        NftMetadata metadata = new NftMetadata();
        metadata.setName(badge.getName());
        metadata.setDescription(badge.getDescription());
        metadata.setImage(badge.getImageIpfsHash());
        metadata.setAttributes(createBadgeAttributes(badge));
        
        // Upload metadata to IPFS
        String metadataIpfsHash = ipfsService.uploadMetadata(metadata);
        
        // Mint NFT on Algorand
        Long assetId = blockchainService.mintNft(user.getAlgorandAddress(), metadataIpfsHash);
        
        // Save user badge
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setAlgorandTransactionId(assetId.toString());
        
        userBadgeRepository.save(userBadge);
        
        return new NftBadge(userBadge, assetId);
    }
}
```

### 6. News Feed & Notifications

#### Database Schema
```sql
-- News articles
CREATE TABLE news_articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author VARCHAR(255),
    category VARCHAR(100), -- ENVIRONMENTAL, COMMUNITY, TIPS, ACHIEVEMENTS
    image_url VARCHAR(500),
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_published BOOLEAN DEFAULT FALSE
);

-- User notifications
CREATE TABLE user_notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    title VARCHAR(255),
    message TEXT,
    notification_type VARCHAR(50), -- REWARD, BADGE, QUEST, NEWS, REMINDER
    data JSONB, -- Additional data
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Push notification tokens
CREATE TABLE push_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    device_token VARCHAR(500),
    platform VARCHAR(20), -- ANDROID, IOS
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 7. Waste-as-a-Service (WaaS)

#### Database Schema
```sql
-- WaaS providers
CREATE TABLE waas_providers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    business_name VARCHAR(255),
    business_license VARCHAR(255),
    service_areas TEXT[], -- Array of service areas
    vehicle_info JSONB,
    rating DECIMAL(3, 2),
    verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Collection jobs
CREATE TABLE collection_jobs (
    id BIGSERIAL PRIMARY KEY,
    job_type VARCHAR(50), -- BIN_COLLECTION, MARKETPLACE_PICKUP, SPOT_CLEANUP
    location_latitude DECIMAL(10, 8),
    location_longitude DECIMAL(11, 8),
    description TEXT,
    estimated_duration INTEGER, -- minutes
    payment_amount DECIMAL(10, 2),
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, ASSIGNED, IN_PROGRESS, COMPLETED
    assigned_provider_id BIGINT REFERENCES waas_providers(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Job assignments
CREATE TABLE job_assignments (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT REFERENCES collection_jobs(id),
    provider_id BIGINT REFERENCES waas_providers(id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ASSIGNED'
);
```

---

## 🔧 Implementation Steps

### Step 1: Set up Dependencies

Add to `pom.xml`:
```xml
<!-- Blockchain -->
<dependency>
    <groupId>com.algorand</groupId>
    <artifactId>algorand-sdk</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- IPFS -->
<dependency>
    <groupId>com.github.ipfs</groupId>
    <artifactId>java-ipfs-http-client</artifactId>
    <version>1.4.3</version>
</dependency>

<!-- Firebase for notifications -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.1.1</version>
</dependency>

<!-- File upload -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.5</version>
</dependency>
```

### Step 2: Configuration

Add to `application.properties`:
```properties
# Blockchain Configuration
algorand.network=testnet
algorand.application.id=123456
algorand.creator.address=YOUR_CREATOR_ADDRESS

# IPFS Configuration
ipfs.node.url=http://localhost:5001

# Firebase Configuration
firebase.credentials.path=classpath:firebase-credentials.json

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Step 3: Implementation Order

1. **Core Infrastructure** (Week 1)
   - Database schema setup
   - Basic CRUD operations
   - File upload service

2. **Smart QR Code System** (Week 2)
   - Bin management
   - QR code generation
   - Reporting system

3. **Reward System** (Week 3)
   - Basic token rewards
   - Leaderboard
   - Achievement system

4. **Marketplace** (Week 4)
   - Item listing
   - Search and filtering
   - Basic messaging

5. **Advanced Features** (Week 5-6)
   - DAO governance
   - NFT badges
   - WaaS platform

---

## 🚀 Getting Started

1. **Clone and setup the project**
2. **Run the database migrations**
3. **Configure blockchain credentials**
4. **Start with Phase 1 features**
5. **Test each feature thoroughly**
6. **Deploy incrementally**

This implementation provides a solid foundation for all the Green Cycle features while maintaining scalability and security. 