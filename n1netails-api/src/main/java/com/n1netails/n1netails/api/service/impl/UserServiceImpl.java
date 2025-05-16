package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.Users;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findUserByEmail(email).orElse(null);

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
    public Users findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElse(null);
    }

    @Override
    public Users register(UserRegisterRequest newUser) throws UserNotFoundException, EmailExistException {

        validateEmail("", newUser.getEmail());
        String encodedPassword = encodePassword(newUser.getPassword());

        Users user = new Users();
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
//        user.setProfileImageUrl(getTemporaryProfileImageUrl(newUser.getUsername()));
        userRepository.save(user);
        return user;
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

    private void validateLoginAttempt(Users user) {
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

    private Users validateEmail(String currentEmail, String email)
            throws UserNotFoundException, EmailExistException
    {
        Users userByNewEmail = findUserByEmail(email);
        if (StringUtils.isNotBlank(currentEmail)) {
            Users currentUser = findUserByEmail(currentEmail);
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
