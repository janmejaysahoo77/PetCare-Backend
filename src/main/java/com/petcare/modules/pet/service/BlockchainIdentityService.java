package com.petcare.modules.pet.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class BlockchainIdentityService {

    /**
     * Generates a deterministic, Ethereum-style 0x hash representing the pet's
     * immovable identity on the (simulated) blockchain.
     */
    public String generatePetIdentityHash(String petId, String ownerId) {
        String input = petId + ownerId + System.currentTimeMillis();
        return "0x" + sha256Hex(input).substring(0, 40); // 40 chars mimicking ETH addresses
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
