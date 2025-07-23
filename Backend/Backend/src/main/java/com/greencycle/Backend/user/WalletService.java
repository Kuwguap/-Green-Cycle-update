package com.greencycle.Backend.user;

import com.algorand.algosdk.account.Account;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    public WalletInfo generateWallet() {
        try {
            Account account = new Account();
            String address = account.getAddress().toString();
            String mnemonic = account.toMnemonic();
            return new WalletInfo(address, mnemonic);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Algorand wallet", e);
        }
    }

    public static class WalletInfo {
        private String address;
        private String mnemonic;
        public WalletInfo(String address, String mnemonic) {
            this.address = address;
            this.mnemonic = mnemonic;
        }
        public String getAddress() { return address; }
        public String getMnemonic() { return mnemonic; }
    }
} 