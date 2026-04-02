package com.civicaid.config;

import com.civicaid.entity.User;
import com.civicaid.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Runs once on every application startup.

 * Seeds a default set of staff accounts if they do not already exist in the
 * database (checked by email). This ensures there is always at least one
 * ADMINISTRATOR available to log in and create further accounts via
 * POST /users.
 * ⚠️  IMPORTANT: Change all default passwords immediately after first login
 *     in any non-development environment.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------------------------------------------------------------------------
    // Seed definitions — adjust names / emails / passwords before deploying.
    // ---------------------------------------------------------------------------
    private static final List<SeedUser> SEED_USERS = List.of(
            new SeedUser("System Administrator",  "admin@civicaid.gov",            "Admin@1234",    User.Role.ADMINISTRATOR),
            new SeedUser("Welfare Officer",        "officer@civicaid.gov",          "Officer@1234",  User.Role.WELFARE_OFFICER),
            new SeedUser("Program Manager",        "manager@civicaid.gov",          "Manager@1234",  User.Role.PROGRAM_MANAGER),
            new SeedUser("Compliance Officer",     "compliance@civicaid.gov",       "Comply@1234",   User.Role.COMPLIANCE_OFFICER),
            new SeedUser("Government Auditor",     "auditor@civicaid.gov",          "Audit@1234",    User.Role.GOVERNMENT_AUDITOR)
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("DataInitializer: checking seed accounts...");
        int created = 0;

        for (SeedUser seed : SEED_USERS) {
            if (userRepository.existsByEmail(seed.email())) {
                log.debug("DataInitializer: '{}' already exists, skipping.", seed.email());
                continue;
            }

            User user = User.builder()
                    .name(seed.name())
                    .email(seed.email())
                    .password(passwordEncoder.encode(seed.rawPassword()))
                    .role(seed.role())
                    .status(User.UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);
            log.info("DataInitializer: seeded {} → {} [{}]", seed.role(), seed.name(), seed.email());
            created++;
        }

        if (created == 0) {
            log.info("DataInitializer: all seed accounts already present, nothing to do.");
        } else {
            log.info("DataInitializer: {} account(s) seeded. Change default passwords before going to production!", created);
        }
    }

    // Simple record to hold seed definition data.
    private record SeedUser(String name, String email, String rawPassword, User.Role role) {}
}