package rw.rca.year3.ne.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.exceptions.ResourceNotFoundException;
import rw.rca.year3.ne.v1.models.Role;
import rw.rca.year3.ne.v1.repositories.IRoleRepository;
import rw.rca.year3.ne.v1.services.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {
    private final IRoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(IRoleRepository iRoleRepository){
        this.roleRepository = iRoleRepository;
    }

    @Override
    public Role findByName(ERole role) {
        return roleRepository.findByName(role).orElseThrow(() -> new ResourceNotFoundException("Role", "name", role.toString()));
    }
}
