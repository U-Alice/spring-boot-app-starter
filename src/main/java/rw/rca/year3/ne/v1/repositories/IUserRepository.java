package rw.rca.year3.ne.v1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.rca.year3.ne.v1.enums.EGender;
import rw.rca.year3.ne.v1.enums.EUserStatus;
import rw.rca.year3.ne.v1.models.Role;
import rw.rca.year3.ne.v1.models.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByActivationCodeAndEmail(String activationCode, String email);


    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    Optional<User> findByEmailOrPhoneNumberOrNationalId(String email, String phoneNumber,String nationalId);

    @Query("SELECT u FROM User u  WHERE u.status = :status AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE CONCAT('%', LOWER(:fullNames), '%')")
    Page<User> findByStatusAndFullName(EUserStatus status, String fullNames, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (:role MEMBER OF u.roles) AND u.status = :status AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE CONCAT('%', LOWER(:fullNames), '%')")
    Page<User> findByRoleAndStatusAndFullName(Role role, EUserStatus status, String fullNames, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (:role MEMBER OF u.roles)  AND u.status = :status AND u.gender = :gender AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE CONCAT('%', LOWER(:fullNames), '%')")
    Page<User> findByRoleAndStatusAndGenderAndSearchByFullName(Role role, EUserStatus status, EGender gender, String fullNames, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.gender = :gender AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE CONCAT('%', LOWER(:fullNames), '%')")
    Page<User> findByStatusAndGenderAndSearchByFullName(EUserStatus status, EGender gender, String fullNames, Pageable pageable);

}
