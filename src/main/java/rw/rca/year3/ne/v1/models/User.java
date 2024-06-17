package rw.rca.year3.ne.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import rw.rca.year3.ne.v1.audits.InitiatorAudit;
import rw.rca.year3.ne.v1.enums.EGender;
import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.enums.EUserStatus;
import rw.rca.year3.ne.v1.utils.Utility;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}), @UniqueConstraint(columnNames = {"phone_number"}), @UniqueConstraint(columnNames = "national_id")})
@OnDelete(action = OnDeleteAction.CASCADE)
public class User extends InitiatorAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "national_id")
    private String nationalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private EGender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EUserStatus status = EUserStatus.WAIT_EMAIL_VERIFICATION;

    @JsonIgnore
    @Column(name = "activation_code")
        private String activationCode = Utility.randomUUID(6, 0, 'N');

    @Column(name = "rejection_description")
    private String rejectionDescription;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @NotBlank
    @Column(name = "password")
    private String password;

    public User(String firstName, String lastName, String phoneNumber, String email, String nationalId, EGender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nationalId = nationalId;
        this.gender = gender;
    }

    public User(String firstName, String lastName, String phoneNumber, String email, String nationalId, EGender gender, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nationalId = nationalId;
        this.gender = gender;
        this.roles = roles;
    }

    public User(String firstName, String lastName, String phoneNumber, String email, String nationalId, EGender gender, EUserStatus status, Set<Role> roles, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nationalId = nationalId;
        this.gender = gender;
        this.status = status;
        this.roles = roles;
        this.password = password;
    }

    public ERole getRole() {
        Optional<Role> role = this.getRoles().stream().findFirst();
        ERole theRole = null;

        if (role.isPresent())
            theRole = role.get().getName();

        return theRole;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
