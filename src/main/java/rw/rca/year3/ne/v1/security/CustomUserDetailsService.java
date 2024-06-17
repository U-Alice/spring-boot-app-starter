package rw.rca.year3.ne.v1.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rw.rca.year3.ne.v1.enums.EUserStatus;
import rw.rca.year3.ne.v1.exceptions.BadRequestException;
import rw.rca.year3.ne.v1.models.User;
import rw.rca.year3.ne.v1.repositories.IUserRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDetails loadByUserId(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserByUsername(String s) throws BadRequestException {
        User user = userRepository.findByEmailOrPhoneNumber(s, s).orElseThrow(() -> new UsernameNotFoundException("user not found with email or mobile of " + s));
        if (user.getStatus() == EUserStatus.WAIT_EMAIL_VERIFICATION)
            throw new BadRequestException("You must verify your email to continue with the app, visit your email");
        else if (user.getStatus() == EUserStatus.DEACTIVATED)
            throw new BadRequestException("Your account is deactivated ask the re activation");
        else if (user.getStatus() == EUserStatus.PENDING)
            throw new BadRequestException("Your account is pending wait for the approval");
        else if (user.getStatus() == EUserStatus.REJECTED)
            throw new BadRequestException("Your account is rejected");
        else if (user.getStatus() == EUserStatus.ACTIVE)
            return UserPrincipal.create(user);
        else
            throw new BadRequestException("Invalid user type ");
    }
}
