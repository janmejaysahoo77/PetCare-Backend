package com.petcare.modules.auth.repository;

import com.google.cloud.firestore.Firestore;
import com.petcare.modules.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "users";

    public User save(User user) {
        try {
            firestore.collection(COLLECTION_NAME).document(user.getUid()).set(user).get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving user to Firestore", e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public Optional<User> findById(String uid) {
        try {
            var docRef = firestore.collection(COLLECTION_NAME).document(uid).get().get();
            if (docRef.exists()) {
                return Optional.ofNullable(docRef.toObject(User.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching user from Firestore", e);
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    public boolean existsById(String uid) {
        try {
            return firestore.collection(COLLECTION_NAME).document(uid).get().get().exists();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error checking user existence", e);
            return false;
        }
    }
}
