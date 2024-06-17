package rw.rca.year3.ne.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rw.rca.year3.ne.v1.*;

import rw.rca.year3.ne.v1.dtos.*;
import rw.rca.year3.ne.v1.exceptions.AppException;
import rw.rca.year3.ne.v1.models.User;
import rw.rca.year3.ne.v1.payload.ApiResponse;
import rw.rca.year3.ne.v1.payload.JWTAuthenticationResponse;
import rw.rca.year3.ne.v1.security.JwtTokenProvider;
import rw.rca.year3.ne.v1.services.IUserService;
import rw.rca.year3.ne.v1.services.MailService;
import rw.rca.year3.ne.v1.utils.Profile;
import rw.rca.year3.ne.v1.utils.Utility;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private final IUserService userService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(IUserService userService, MailService mailService, JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.mailService = mailService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody SignInDTO signInDTO){
        String jwt = null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getEmail(),signInDTO.getPassword()));
        try{
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = jwtTokenProvider.generateToken(authentication);
        }
        catch (Exception e){
        }
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.success(new JWTAuthenticationResponse(jwt)));
    }

    @PostMapping(path ="/initiate-password-reset")
    public ResponseEntity<ApiResponse> initiatePasswordReset(@Valid @RequestBody InitiatePasswordResetDto dto){
        User user = this.userService.getByEmail(dto.getEmail());
        user.setActivationCode(Utility.randomUUID(6,0,'N'));
        this.userService.save(user);

        mailService.sendResetPasswordMail(user);

        return ResponseEntity.ok(new ApiResponse(true,"Password Reset Email Sent successfully"));
    }

    @PostMapping(path="/check-code")
    public ResponseEntity<ApiResponse> checkActivationCode(@Valid @RequestBody CheckActivationCodeDto dto){
        return ResponseEntity.ok(ApiResponse.success(userService.isCodeValid(dto.getEmail(), dto.getActivationCode())));
    }

    @PostMapping(path="/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordDto dto){
        User user = this.userService.getByEmail(dto.getEmail());

        if(Utility.isCodeValid(user.getActivationCode(),dto.getActivationCode())){
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            user.setActivationCode(Utility.randomUUID(6,0,'N'));
            this.userService.save(user);
        }
        else{
            throw new AppException("Invalid code");
        }
        return ResponseEntity.ok(new ApiResponse(true,"Password successfully reset"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(){
        Profile profile = userService.getLoggedInProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@Valid @RequestBody VerifyEmailDTO dto) {
        userService.verifyEmail(dto.getEmail(), dto.getActivationCode());
        return ResponseEntity.ok(ApiResponse.success("Your email is verified ..."));
    }

}
