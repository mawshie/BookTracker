package org.example.booktracker.bootstrap;

import org.example.booktracker.dao.UserRepository;
import org.example.booktracker.domain.Role;
import org.example.booktracker.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootStrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootStrapData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0){
            User john = new User();
            john.setUsername("Johnny");
            john.setPwd(passwordEncoder.encode("thisismypwd"));
            john.setFirstName("John");
            john.setLastName("Yana");
            john.setRole(Role.ADMIN);
            john.setEnabled(true);
            userRepository.save(john);
        }

    }
}
