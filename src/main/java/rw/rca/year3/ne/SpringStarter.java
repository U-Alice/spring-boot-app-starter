package rw.rca.year3.ne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.models.Role;
import rw.rca.year3.ne.v1.repositories.IRoleRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class SpringStarter {

    @Autowired
    private final IRoleRepository roleRepository;

    public SpringStarter(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringStarter.class, args);
    }

    @Bean
    public void registerRoles(){
        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.ADMIN);
        roles.add(ERole.ACCREDITATION_SPECIALIST);
        roles.add(ERole.HEAD_TEACHER);
        roles.add(ERole.QA_STAFF);

        for (ERole role: roles){
            Optional<Role> roleByName = roleRepository.findByName(role);
            if(!roleByName.isPresent()){
                Role newRole = new Role(role,role.toString());
                roleRepository.save(newRole);
            }
        }
    }
}
