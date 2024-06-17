package rw.rca.year3.ne.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rw.rca.year3.ne.v1.dtos.AdminRegisterDTO;
import rw.rca.year3.ne.v1.enums.EGender;
import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.enums.EUserStatus;
import rw.rca.year3.ne.v1.exceptions.BadRequestException;
import rw.rca.year3.ne.v1.exceptions.ResourceNotFoundException;
import rw.rca.year3.ne.v1.models.Role;
import rw.rca.year3.ne.v1.models.User;
import rw.rca.year3.ne.v1.repositories.IUserRepository;
import rw.rca.year3.ne.v1.services.IRoleService;
import rw.rca.year3.ne.v1.services.IUserService;
import rw.rca.year3.ne.v1.services.MailService;
import rw.rca.year3.ne.v1.utils.Profile;

import java.util.*;

@Service
public class UserServiceImpl implements IUserService {

    private final MailService mailService;
    private final IRoleService roleService;
    private final IUserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Value("${admin_registration_key}")
    private String adminRegistrationKey;


    @Autowired
    public UserServiceImpl(IRoleService iRoleService, MailService mailService, IUserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mailService = mailService;
        this.roleService = iRoleService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public User findById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));
    }


    @Override
    public User getLoggedInUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser")
            throw new BadRequestException("You are not logged in, try to log in");

        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }

    @Override
    public Profile getLoggedInProfile() {
        User theUser = getLoggedInUser();
        Object profile = null;
        Optional<Role> role = theUser.getRoles().stream().findFirst();
        if (role.isPresent()) {
            profile = theUser;
            return new Profile(profile);
        }
        return null;
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public boolean isCodeValid(String email, String activationCode) {
        return userRepository.existsByActivationCodeAndEmail(activationCode, email);
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User registerAdmin(AdminRegisterDTO dto) {
        if(!dto.getKey().equals(adminRegistrationKey))
            throw new BadRequestException("Invalid key!");

        User user = new User();

        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        Role role = roleService.findByName(ERole.ADMIN);

        user.setEmail(dto.getEmail());
        user.setNationalId(dto.getNationalId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getMobile());
        user.setPassword(encodedPassword);
        user.setStatus(EUserStatus.ACTIVE);
        user.setRoles(Collections.singleton(role));

        validateNewRegistration(user);

        return this.userRepository.save(user);
    }


    @Override
    public void validateNewRegistration(User user) {
        if (isNotUnique(user)) {
            throw new BadRequestException(String.format("User with email '%s' or phone number '%s' or national id '%s' already exists", user.getEmail(), user.getPhoneNumber(),user.getNationalId()));
        }
    }

   @Override
    public boolean isNotUnique(User user) {
        Optional<User> userOptional = this.userRepository.findByEmailOrPhoneNumberOrNationalId(user.getEmail(), user.getPhoneNumber(), user.getNationalId());
        return userOptional.isPresent();
    }

    @Override
    public void verifyEmail(String email, String activationCode) {
        User user = getByEmail(email);

        if (user.getStatus() != EUserStatus.WAIT_EMAIL_VERIFICATION)
            throw new BadRequestException("Your account is " + user.getStatus().toString().toLowerCase(Locale.ROOT));

        if (!Objects.equals(user.getActivationCode(), activationCode))
            throw new BadRequestException("Invalid Activation Code ..");

        user.setStatus(EUserStatus.PENDING);

        userRepository.save(user);

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User approve(UUID id) {
        User user = findById(id);

        if (user.getStatus() == EUserStatus.ACTIVE)
            throw new BadRequestException("User Already Approved  ");

        user.setStatus(EUserStatus.ACTIVE);
        mailService.sendWelcomeEmail(user);

        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User reject(UUID id, String rejectionMessage) {
        User user = findById(id);

        if (user.getStatus() == EUserStatus.REJECTED)
            throw new BadRequestException("User Already Rejected ");

        if (user.getStatus() == EUserStatus.ACTIVE)
            throw new BadRequestException("User was approved recently");

        user.setStatus(EUserStatus.REJECTED);
        user.setRejectionDescription(rejectionMessage);

        mailService.sendAccountRejectedMail(user);

        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deActivate(UUID id) {
        User user = findById(id);

        user.setStatus(EUserStatus.DEACTIVATED);
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<User> search(EUserStatus status, String name, EGender gender, Pageable pageable) {
        if (gender == null)
            return userRepository.findByStatusAndFullName(status, name, pageable);
        else
            return userRepository.findByStatusAndGenderAndSearchByFullName(status, gender, name, pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<User> search(Role role, EUserStatus status, String name, EGender gender, Pageable pageable) {
        if (role != null) {
            if (gender == null) {
                return userRepository.findByRoleAndStatusAndFullName(role, status, name, pageable);
            } else {
                return userRepository.findByRoleAndStatusAndGenderAndSearchByFullName(role, status, gender, name, pageable);
            }
        } else {
            return search(status, name, gender, pageable);
        }
    }
}
