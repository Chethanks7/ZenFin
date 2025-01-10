package com.ZenFin;

import com.ZenFin.role.Role;
import com.ZenFin.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableAsync
@EnableCaching

public class ZenFinApplication {


    public static void main(String[] args) throws Exception {

        SpringApplication.run(ZenFinApplication.class, args);


    }


    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                roleRepository.save(Role
                        .builder()
                        .name("ROLE_USER")
                        .build());
            }
        };
    }
}
