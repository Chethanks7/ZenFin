package com.ZenFin;

import com.ZenFin.role.Role;
import com.ZenFin.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = "com.ZenFin")
public class ZenFinApplication {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(ZenFinApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(Role
                        .builder()
                        .name("USER")
                        .build());
            }
        };
    }

}
