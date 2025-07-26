package com.greencycle.Backend.blockchain;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlockchainService {

    @Value("${algorand.network}")
    private String network;

    @Value("${algorand.application.id}")
    private Long applicationId;

    @Value("${algorand.creator.address}")
    private String creatorAddress;

    @Value("${algorand.node.url}")
    private String nodeUrl;

    @Value("${algorand.indexer.url}")
    private String indexerUrl;

    private AlgodClient algodClient;
    private IndexerClient indexerClient;
    private Account creatorAccount;

    @PostConstruct
    public void init() {
        // Initialize Algorand clients
        algodClient = new AlgodClient(nodeUrl, "", "");
        indexerClient = new IndexerClient(indexerUrl, "", "");
    }

    /**
     * Mint $CYCLE tokens to a user's wallet
     * @param userAddress The Algorand address of the user
     * @param amount The amount of tokens to mint
     * @return The transaction ID
     */
    public String mintTokens(String userAddress, BigDecimal amount) {
        try {
            // Get transaction parameters
            TransactionParametersResponse params = algodClient.TransactionParams().execute().body();

            // Convert amount to microAlgos (1 Algo = 1,000,000 microAlgos)
            BigInteger microAmount = amount.multiply(BigDecimal.valueOf(1000000)).toBigInteger();

            // Create transaction to mint $CYCLE tokens
            Transaction txn = Transaction.ApplicationCallTransactionBuilder()
                    .sender(new Address(creatorAddress))
                    .applicationId(applicationId)
                    .args(Arrays.asList(
                            Encoder.encodeToMsgPack("mint"),
                            Encoder.encodeToMsgPack(userAddress),
                            Encoder.encodeToMsgPack(microAmount)
                    ))
                    .suggestedParams(params)
                    .build();

            // Sign transaction with creator account
            SignedTransaction signedTxn = creatorAccount.signTransaction(txn);

            // Submit transaction
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
            Response<String> response = algodClient.RawTransaction().rawtxn(encodedTxBytes).execute();
            String txId = response.body();

            // Wait for confirmation
            waitForConfirmation(txId);

            return txId;
        } catch (Exception e) {
            throw new BlockchainException("Failed to mint tokens", e);
        }
    }

    /**
     * Get the $CYCLE token balance for a user
     * @param userAddress The Algorand address of the user
     * @return The token balance
     */
    public BigDecimal getTokenBalance(String userAddress) {
        try {
            // Query account information
            Response<com.algorand.algosdk.v2.client.model.Account> response = 
                algodClient.AccountInformation(new Address(userAddress)).execute();
            com.algorand.algosdk.v2.client.model.Account account = response.body();

            // Find $CYCLE token in assets
            for (com.algorand.algosdk.v2.client.model.AssetHolding asset : account.assets) {
                if (asset.assetId == applicationId) {
                    // Convert from microAlgos to Algos
                    return BigDecimal.valueOf(asset.amount).divide(BigDecimal.valueOf(1000000));
                }
            }

            // If no asset found, return 0
            return BigDecimal.ZERO;
        } catch (Exception e) {
            throw new BlockchainException("Failed to get token balance", e);
        }
    }

    /**
     * Mint an NFT badge on Algorand
     * @param userAddress The Algorand address of the user
     * @param metadataUrl The IPFS URL of the metadata
     * @return The asset ID of the minted NFT
     */
    public Long mintNft(String userAddress, String metadataUrl) {
        try {
            // Get transaction parameters
            TransactionParametersResponse params = algodClient.TransactionParams().execute().body();

            // Create asset creation transaction
            Transaction txn = Transaction.AssetCreateTransactionBuilder()
                    .sender(new Address(creatorAddress))
                    .assetTotal(1) // NFTs have a total supply of 1
                    .assetDecimals(0) // NFTs have 0 decimals
                    .defaultFrozen(false)
                    .assetUnitName("BADGE")
                    .assetName("GreenCycleBadge")
                    .url(metadataUrl)
                    .suggestedParams(params)
                    .build();

            // Sign transaction with creator account
            SignedTransaction signedTxn = creatorAccount.signTransaction(txn);

            // Submit transaction
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
            Response<String> response = algodClient.RawTransaction().rawtxn(encodedTxBytes).execute();
            String txId = response.body();

            // Wait for confirmation
            PendingTransactionResponse ptx = waitForConfirmation(txId);

            // Get asset ID
            Long assetId = ptx.assetIndex;

            // Transfer NFT to user
            transferNft(userAddress, assetId);

            return assetId;
        } catch (Exception e) {
            throw new BlockchainException("Failed to mint NFT", e);
        }
    }

    /**
     * Transfer an NFT to a user
     * @param userAddress The Algorand address of the user
     * @param assetId The asset ID of the NFT
     * @return The transaction ID
     */
    private String transferNft(String userAddress, Long assetId) {
        try {
            // Get transaction parameters
            TransactionParametersResponse params = algodClient.TransactionParams().execute().body();

            // Create asset transfer transaction
            Transaction txn = Transaction.AssetTransferTransactionBuilder()
                    .sender(new Address(creatorAddress))
                    .assetReceiver(new Address(userAddress))
                    .assetAmount(1) // Transfer 1 NFT
                    .assetIndex(assetId)
                    .suggestedParams(params)
                    .build();

            // Sign transaction with creator account
            SignedTransaction signedTxn = creatorAccount.signTransaction(txn);

            // Submit transaction
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
            Response<String> response = algodClient.RawTransaction().rawtxn(encodedTxBytes).execute();
            String txId = response.body();

            // Wait for confirmation
            waitForConfirmation(txId);

            return txId;
        } catch (Exception e) {
            throw new BlockchainException("Failed to transfer NFT", e);
        }
    }

    /**
     * Notarize data on the Algorand blockchain
     * @param data The data to notarize
     * @return The transaction ID
     */
    public String notarizeData(String data) {
        try {
            // Get transaction parameters
            TransactionParametersResponse params = algodClient.TransactionParams().execute().body();

            // Create note field with data
            byte[] noteField = data.getBytes();

            // Create transaction with note field
            Transaction txn = Transaction.PaymentTransactionBuilder()
                    .sender(new Address(creatorAddress))
                    .receiver(new Address(creatorAddress))
                    .amount(0) // 0 Algo transfer (just for notarization)
                    .note(noteField)
                    .suggestedParams(params)
                    .build();

            // Sign transaction with creator account
            SignedTransaction signedTxn = creatorAccount.signTransaction(txn);

            // Submit transaction
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
            Response<String> response = algodClient.RawTransaction().rawtxn(encodedTxBytes).execute();
            String txId = response.body();

            // Wait for confirmation
            waitForConfirmation(txId);

            return txId;
        } catch (Exception e) {
            throw new BlockchainException("Failed to notarize data", e);
        }
    }

    /**
     * Wait for a transaction to be confirmed
     * @param txId The transaction ID
     * @return The pending transaction response
     */
    private PendingTransactionResponse waitForConfirmation(String txId) throws Exception {
        long lastRound = algodClient.GetStatus().execute().body().lastRound;
        while (true) {
            try {
                // Check if transaction has been confirmed
                Response<PendingTransactionResponse> pendingInfo = algodClient.PendingTransactionInformation(txId).execute();
                if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                    return pendingInfo.body();
                }

                // Wait for the next round
                lastRound++;
                algodClient.WaitForBlock(lastRound).execute();
            } catch (Exception e) {
                throw new Exception("Error waiting for confirmation", e);
            }
        }
    }

    /**
     * Set the creator account for signing transactions
     * @param mnemonic The mnemonic phrase for the creator account
     */
    public void setCreatorAccount(String mnemonic) {
        try {
            creatorAccount = new Account(mnemonic);
            // Verify that the address matches the expected creator address
            if (!creatorAccount.getAddress().toString().equals(creatorAddress)) {
                throw new BlockchainException("Mnemonic does not match creator address");
            }
        } catch (Exception e) {
            throw new BlockchainException("Failed to set creator account", e);
        }
    }
}