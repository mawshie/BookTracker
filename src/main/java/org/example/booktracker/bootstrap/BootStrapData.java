package org.example.booktracker.bootstrap;

import org.example.booktracker.dao.UserRepository;
import org.example.booktracker.domain.Role;
import org.example.booktracker.domain.User;
import org.example.booktracker.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootStrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public BootStrapData(UserRepository userRepository, PasswordEncoder passwordEncoder, S3Service s3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
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

        //doesn't work
        //s3Service.putObject("foo", "Hello World".getBytes());

        //byte[] obj = s3Service.getObject("foo");

        //turn byte array into string new String(name of array)
       // System.out.println("Hooray: " + new String(obj));
    }
}
