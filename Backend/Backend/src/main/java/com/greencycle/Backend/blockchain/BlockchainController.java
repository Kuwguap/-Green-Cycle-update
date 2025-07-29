package com.greencycle.Backend.blockchain;

import com.greencycle.Backend.user.User;
import com.greencycle.Backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/blockchain")
@CrossOrigin(origins = "*")
public class BlockchainController {

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Notarize data on the blockchain
     * @param request The notarization request containing the data to notarize
     * @param userDetails The authenticated user
     * @return The transaction ID
     */
    @PostMapping("/notarize")
    public ResponseEntity<Map<String, String>> notarizeData(
            @RequestBody NotarizeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the user
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        
        // Add user information to the data
        String dataToNotarize = String.format(
            "User: %s, Data: %s, Timestamp: %s",
            user.getEmail(),
            request.getData(),
            System.currentTimeMillis()
        );
        
        // Notarize the data
        String txId = blockchainService.notarizeData(dataToNotarize);
        
        // Return the transaction ID
        Map<String, String> response = new HashMap<>();
        response.put("transactionId", txId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the token balance for the authenticated user
     * @param userDetails The authenticated user
     * @return The token balance
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getTokenBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the user
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        
        // Get the token balance
        BigDecimal balance = blockchainService.getTokenBalance(user.getWalletAddress());
        
        // Return the balance
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", balance);
        return ResponseEntity.ok(response);
    }

    /**
     * Mint tokens to a user's wallet
     * @param request The mint request containing the amount to mint
     * @param userDetails The authenticated user
     * @return The transaction ID
     */
    @PostMapping("/mint")
    public ResponseEntity<Map<String, String>> mintTokens(
            @RequestBody MintRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the user
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        
        // Mint tokens
        String txId = blockchainService.mintTokens(user.getWalletAddress(), request.getAmount());
        
        // Return the transaction ID
        Map<String, String> response = new HashMap<>();
        response.put("transactionId", txId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mint an NFT badge for the authenticated user
     * @param request The mint request containing the metadata URL
     * @param userDetails The authenticated user
     * @return The asset ID
     */
    @PostMapping("/mint-nft")
    public ResponseEntity<Map<String, Long>> mintNft(
            @RequestBody MintNftRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the user
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        
        // Mint NFT
        Long assetId = blockchainService.mintNft(user.getWalletAddress(), request.getMetadataUrl());
        
        // Return the asset ID
        Map<String, Long> response = new HashMap<>();
        response.put("assetId", assetId);
        return ResponseEntity.ok(response);
    }

    /**
     * Initialize the blockchain service with the creator account
     * @param request The initialization request containing the mnemonic
     * @return Success message
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, String>> initBlockchain(
            @RequestBody InitRequest request) {
        
        // Set the creator account
        blockchainService.setCreatorAccount(request.getMnemonic());
        
        // Return success message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Blockchain service initialized successfully");
        return ResponseEntity.ok(response);
    }

    // Request and response classes
    
    public static class NotarizeRequest {
        private String data;
        
        public String getData() {
            return data;
        }
        
        public void setData(String data) {
            this.data = data;
        }
    }
    
    public static class MintRequest {
        private BigDecimal amount;
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
    
    public static class MintNftRequest {
        private String metadataUrl;
        
        public String getMetadataUrl() {
            return metadataUrl;
        }
        
        public void setMetadataUrl(String metadataUrl) {
            this.metadataUrl = metadataUrl;
        }
    }
    
    public static class InitRequest {
        private String mnemonic;
        
        public String getMnemonic() {
            return mnemonic;
        }
        
        public void setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        }
    }
}