package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Override
    public User createUser(UserDTO userDTO) throws DataNotFoundException {
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new RuntimeException("Phone number already exists");
        }
        //convert userDTO to user
        User user = User.builder()
                .fullName(userDTO.getFullname())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .password(userDTO.getPassword())
                .dateOfBirth(userDTO.getDateOfBirth())
                .fbAccountId(userDTO.getFacebookAccountId())
                .ggAccountId(userDTO.getGoogleAccountId())
                .build();
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        user.setRole(role);
        //Check accountID --> Ko yeu cau mk
        if(user.getFbAccountId() == 0 && user.getGgAccountId() == 0){
            String password = user.getPassword();
            /*String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);*/
        }
        return userRepository.save(user);
    }

    @Override
    public String login(String phoneNumber, String password) {
        return null;
    }
}
