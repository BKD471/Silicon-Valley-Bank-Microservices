package com.example.accountsservices.controller.Impl;

import com.example.accountsservices.dto.baseDtos.CustomerDto;
import com.example.accountsservices.dto.tokenDtos.JwtRequest;
import com.example.accountsservices.dto.tokenDtos.JwtResponse;
import com.example.accountsservices.exception.BadApiRequestException;
import com.example.accountsservices.helpers.JwtHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody final JwtRequest jwtRequest) {
        this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getEmail());
        final String token = jwtHelper.generateToken(userDetails);
        final CustomerDto customerDto = modelMapper.map(userDetails, CustomerDto.class);
        final JwtResponse jwtResponse = JwtResponse.builder()
                .jwtToken(token)
                .customer(customerDto).build();

        return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
    }

    @GetMapping("/current")
    public ResponseEntity<CustomerDto> getCurrentUser(final Principal principal) {
        final String userName = principal.getName();
        return new ResponseEntity<>(modelMapper.map(userDetailsService.loadUserByUsername(userName)
                , CustomerDto.class)
                , HttpStatus.OK);
    }

    private void doAuthenticate(final String email,final String password) {
        final String methodName = "doAuthenticate(String,String) in AuthController";
        final UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(auth);
        } catch (BadCredentialsException e) {
            throw new BadApiRequestException(BadApiRequestException.class, "Invalid Credentials", methodName);
        }
    }
}
