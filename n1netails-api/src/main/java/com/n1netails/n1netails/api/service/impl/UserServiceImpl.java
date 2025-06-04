package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.constant.Authority;
import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.LoginAttemptService;
import com.n1netails.n1netails.api.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.n1netails.n1netails.api.model.enumeration.Role.ROLE_USER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_EMAIL = "No user found by email: ";

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsersEntity user = userRepository.findUserByEmail(email).orElse(null);

        if (user != null) {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);

            return new UserPrincipal(user);
        }
        return null;
    }

    @Override
    public UsersEntity editUser(UsersEntity user) {

        UsersEntity currentUser = findUserByEmail(user.getEmail());
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setUsername(user.getUsername());

        userRepository.save(currentUser);
        return currentUser;
    }

    @Override
    public UsersEntity findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElse(null);
    }

    @Override
    public UsersEntity register(UserRegisterRequest newUser) throws UserNotFoundException, EmailExistException {
        validateEmail("", newUser.getEmail());
        String encodedPassword = encodePassword(newUser.getPassword());

        UsersEntity user = new UsersEntity();
        user.setUserId(generateUserId());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setProfileImageUrl(getTemporaryProfileImageUrl(newUser.getUsername()));
        user.setEnabled(true);

        boolean isFirstUserEver = userRepository.count() == 0;

        if (isFirstUserEver) {
            user.setRole(com.n1netails.n1netails.api.model.enumeration.Role.ROLE_SUPER_ADMIN.name());
            user.setAuthorities(Authority.SUPER_ADMIN_AUTHORITIES);
        } else {
            user.setRole(com.n1netails.n1netails.api.model.enumeration.Role.ROLE_USER.name());
            // Using Authority.USER_AUTHORITIES directly as per discussion, though ROLE_USER.getAuthorities() is equivalent here.
            user.setAuthorities(Authority.USER_AUTHORITIES);
        }

        userRepository.save(user); // Save the user with correct role and authorities

        // Associate with "n1netails" organization
        OrganizationEntity n1netailsOrg = organizationRepository.findByName("n1netails")
            .orElseThrow(() -> new RuntimeException("Default 'n1netails' organization not found. Liquibase script might have failed."));

        // Add user to n1netails org users set, if not already part of it
        if (n1netailsOrg.getUsers() == null) { // Defensive check
            n1netailsOrg.setUsers(new java.util.HashSet<>());
        }
        if (!n1netailsOrg.getUsers().contains(user)) {
             n1netailsOrg.getUsers().add(user);
             organizationRepository.save(n1netailsOrg);
        }

        return user;
    }

    private String getTemporaryProfileImageUrl(String username) {
        return "https://robohash.org/"+username+"?set=set4";
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateUserId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build();
        return generator.generate(10);
    }

    private void validateLoginAttempt(UsersEntity user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private UsersEntity validateEmail(String currentEmail, String email)
            throws UserNotFoundException, EmailExistException
    {
        UsersEntity userByNewEmail = findUserByEmail(email);
        if (StringUtils.isNotBlank(currentEmail)) {
            UsersEntity currentUser = findUserByEmail(currentEmail);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if(userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public UsersEntity updatePassword(String email, String newPassword) throws UserNotFoundException {
        UsersEntity user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return user;
    }
}
