package rw.rca.year3.ne.v1.services;


import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.models.Role;

public interface IRoleService {

    Role findByName(ERole role);

}
