package com.G2T5203.wingit.user;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public List<WingitUser> getAllUsers() {
        return repo.findAll();
    }

    public WingitUser getById(String username) {
        return repo.findById(username).orElse(null);
    }

    @Transactional
    public WingitUser createUser(WingitUser newUser) {
        if (repo.existsById(newUser.getUsername())) throw new UserBadRequestException("Username already exists");
        if (repo.existsByEmail(newUser.getEmail())) throw new UserBadRequestException("Email already used for existing account.");
        newUser.setAuthorityRole("ROLE_USER"); // ALWAYS non-admin
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        return repo.save(newUser);
    }

    @Transactional
    public WingitUser createAdmin(WingitUser newAdmin) {
        if (repo.existsById(newAdmin.getUsername())) throw new UserBadRequestException("Username already exists");
        if (repo.existsByEmail(newAdmin.getEmail())) throw new UserBadRequestException("Email already used for existing account.");
        newAdmin.setAuthorityRole("ROLE_ADMIN"); // ALWAYS admin
        newAdmin.setPassword(encoder.encode(newAdmin.getPassword()));
        return repo.save(newAdmin);
    }

    @Transactional
    public void deleteUserById(String username) {
        if (repo.existsById(username)) {
            repo.deleteById(username);
        } else {
            throw new UserNotFoundException(username);
        }
    }

    @Transactional
    public WingitUser updateUser(WingitUser updatedUser) {
        Optional<WingitUser> retrievedUser = repo.findById(updatedUser.getUsername());
        if (retrievedUser.isEmpty()) throw new UserNotFoundException(updatedUser.getUsername());

        // Enforce no changes to password and role
        updatedUser.setPassword(retrievedUser.get().getPassword());
        updatedUser.setAuthorityRole(retrievedUser.get().getAuthorityRole());
        return repo.save(updatedUser);
    }

    @Transactional
    public WingitUser updatePassword(String username, String newPassword) {
        Optional<WingitUser> retrievedUser = repo.findById(username);
        if (retrievedUser.isEmpty()) throw new UserNotFoundException(username);

        String newHashedPassword = encoder.encode(newPassword);
        retrievedUser.get().setPassword(newHashedPassword);
        return repo.save(retrievedUser.get());
    }
}
