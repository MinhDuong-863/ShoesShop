package com.project.shopapp.services;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        //User register
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new RuntimeException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        if (role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("Cannot create admin account");
        }
        //convert userDTO to user
        User user = User.builder()
                .fullName(userDTO.getFullname())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .password(userDTO.getPassword())
                .dateOfBirth(userDTO.getDateOfBirth())
                .gender(userDTO.getGender())
                .fbAccountId(userDTO.getFacebookAccountId())
                .ggAccountId(userDTO.getGoogleAccountId())
                .build();
        user.setRole(role);
        //Check accountID --> Ko yeu cau mk
        if(user.getFbAccountId() == 0 && user.getGgAccountId() == 0){
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    @Override
    public String login(String phoneNumber, String password, int roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid phone number or password");
        }
        //return JWT Token
        User user = optionalUser.get();
        //Check password
        if(user.getFbAccountId() == 0 && user.getGgAccountId() == 0){
            if(!passwordEncoder.matches(password, user.getPassword())){
                throw new BadCredentialsException("Wrong phone number or password" + password + " " + user.getPassword());
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(phoneNumber, password, user.getAuthorities());
        //Authenticate with Java Spring Security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(user);
    }
    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)){
            throw new RuntimeException("Token expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if(user.isPresent()) return user.get();
        else throw new DataNotFoundException("User not found");
    }

    @Override
    @Transactional
    public User updateUser(int id, UpdateUserDTO updateUserDTO) throws Exception {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if(!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
            userRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new RuntimeException("Phone number already exists");
        }

        if (updateUserDTO.getFullname() != null) {
            existingUser.setFullName(updateUserDTO.getFullname());
        }
        if (updateUserDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if (updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if (updateUserDTO.getGender() != 0) {
            existingUser.setGender(updateUserDTO.getGender());
        }
        if (updateUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFbAccountId(updateUserDTO.getFacebookAccountId());
        }
        if (updateUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGgAccountId(updateUserDTO.getGoogleAccountId());
        }
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            String password = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            existingUser.setPassword(encodedPassword);
        }
        return userRepository.save(existingUser);
    }

}
