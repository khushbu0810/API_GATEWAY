package com.example.users.service;

import com.example.users.entity.UserDTO;
import com.example.users.entity.UserEntity;
import com.example.users.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        UserEntity userEntity=new UserEntity();

        //copy userDetails -> entity
        BeanUtils.copyProperties(userDetails, userEntity);

        UserEntity savedUser = userRepo.save(userEntity);

        UserDTO returnValue = new UserDTO();

        //copy UserEntity -> userDTO
        BeanUtils.copyProperties(savedUser, returnValue);

        return returnValue;
    }
}
