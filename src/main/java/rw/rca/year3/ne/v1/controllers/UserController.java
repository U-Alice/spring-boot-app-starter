package rw.rca.year3.ne.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.rca.year3.ne.v1.dtos.AdminRegisterDTO;
import rw.rca.year3.ne.v1.enums.EGender;
import rw.rca.year3.ne.v1.enums.ERole;
import rw.rca.year3.ne.v1.enums.EUserStatus;
import rw.rca.year3.ne.v1.models.Role;
import rw.rca.year3.ne.v1.payload.ApiResponse;
import rw.rca.year3.ne.v1.services.IRoleService;
import rw.rca.year3.ne.v1.services.IUserService;
import rw.rca.year3.ne.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
@CrossOrigin
public class UserController {

    private final IUserService userService;
    private final IRoleService roleService;

    @Autowired
    public UserController(IUserService userService, IRoleService iRoleService) {
        this.userService = userService;
        this.roleService = iRoleService;
    }
    @PostMapping(path = "/admin/register")
    public ResponseEntity<ApiResponse> registerAdmin(@Valid @RequestBody AdminRegisterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userService.registerAdmin(dto)));
    }


    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approveAUser(@PathVariable UUID id) {

        userService.approve(id);

        return ResponseEntity.ok(ApiResponse.success("Approved User Successfully"));
    }

    @PutMapping("/{id}/de-activate")
    public ResponseEntity<ApiResponse> deActivateAnAccount(@PathVariable UUID id) {

        userService.deActivate(id);

        return ResponseEntity.ok(ApiResponse.success("De-Activate User Successfully"));
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "status") EUserStatus status,
            @RequestParam(value = "gender", required = false) EGender gender,
            @RequestParam(value = "role", required = false) ERole role

    ){
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "id");

        if(role != null) {
            Role roleSearch = roleService.findByName(role);
            return ResponseEntity.ok(ApiResponse.success(userService.search(roleSearch, status, name, gender, pageable)));
        }else{
            return ResponseEntity.ok(ApiResponse.success(userService.search(status, name, gender, pageable)));
        }

    }

}