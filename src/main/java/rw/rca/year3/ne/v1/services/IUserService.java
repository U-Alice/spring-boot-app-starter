package rw.rca.year3.ne.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.rca.year3.ne.v1.dtos.AdminRegisterDTO;
import rw.rca.year3.ne.v1.enums.EGender;
import rw.rca.year3.ne.v1.enums.EUserStatus;
import rw.rca.year3.ne.v1.models.Role;
import rw.rca.year3.ne.v1.models.User;
import rw.rca.year3.ne.v1.utils.Profile;


import java.util.UUID;


public interface IUserService {

    User findById(UUID id);

    User getLoggedInUser();

    Profile getLoggedInProfile();

    User getByEmail(String email);

    boolean isCodeValid(String email, String activationCode);

    User save(User user);

    User registerAdmin(AdminRegisterDTO dto);

    void validateNewRegistration(User user);

    boolean isNotUnique(User user);

    void verifyEmail(String email, String activationCode);

    User approve(UUID id);

    User reject(UUID id, String rejectionMessage);

    void deActivate(UUID id);

    Page<User> search(EUserStatus status, String name, EGender gender, Pageable pageable);

    Page<User> search(Role role, EUserStatus status, String name, EGender gender, Pageable pageable);
}