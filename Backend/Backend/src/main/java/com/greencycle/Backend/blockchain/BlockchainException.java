package com.greencycle.Backend.blockchain;

/**
 * Exception thrown when blockchain operations fail
 */
public class BlockchainException extends RuntimeException {
    
    public BlockchainException(String message) {
        super(message);
    }
    
    public BlockchainException(String message, Throwable cause) {
        super(message, cause);
    }
}