package org.example.service.impl;

import org.example.dto.request.LoginRequestDto;
import org.example.dto.request.UserRegisterRequestDto;
import org.example.dto.response.LoginResponseDto;
import org.example.dto.response.UserResponseDto;
import org.example.entity.UserEntity;
import org.example.exception.InValidCredentialsException;
import org.example.exception.UserAlreadyExistsException;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.JwtToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtToken jwtToken;

    public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtToken jwtToken){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtToken = jwtToken;
    }

    @Override
    public UserResponseDto register(UserRegisterRequestDto registerrequestdto){

        if(userRepository.findByEmail(registerrequestdto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("Email already registered");
        }
        UserEntity user = new UserEntity();
        user.setFirstName(registerrequestdto.getFirstName());
        user.setEmail(registerrequestdto.getEmail());
        user.setPassword(passwordEncoder.encode(registerrequestdto.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginrequestdto){
        UserEntity user = userRepository.findByEmail(loginrequestdto.getEmail()).orElseThrow(() -> new InvalidParameterException("User not found"));

        if(!passwordEncoder.matches(loginrequestdto.getPassword(),user.getPassword())){
            throw new InValidCredentialsException("Password is wrong for the user");
        }
        return new LoginResponseDto(jwtToken.generateToken(user.getId()),"Login successfull");
    }

}
