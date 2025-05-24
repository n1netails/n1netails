package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.LoginAttemptService;
import com.n1netails.n1netails.api.service.UserService;
import com.n1netails.n1netails.api.model.response.UserResponse; // Added
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Added
import org.springframework.data.domain.Pageable; // Added
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException; // Added
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
    public static final String INCORRECT_CURRENT_PASSWORD = "Incorrect current password."; // Added

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final BCryptPasswordEncoder passwordEncoder;

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
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(newUser.getUsername()));
        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<UsersEntity> usersPage = userRepository.findAll(pageable);
        return usersPage.map(this::convertToUserResponse);
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        UsersEntity user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException(INCORRECT_CURRENT_PASSWORD);
        }

        // Validate new password complexity if needed (already done in request DTO for basic length)
        // String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;'\"\\\\|,.<>/?]).{8,}$";
        // if (!newPassword.matches(passwordPattern)) {
        //     throw new IllegalArgumentException("Password does not meet complexity requirements.");
        // }

        user.setPassword(encodePassword(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user {}", email);
    }

    private UserResponse convertToUserResponse(UsersEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .role(entity.getRole()) // Assuming UsersEntity has getRole() returning String
                .active(entity.isActive())
                .notLocked(entity.isNotLocked())
                .joinDate(entity.getJoinDate())
                .build();
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
}
