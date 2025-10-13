package com.barsik.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.LogInRequest;
import com.barsik.backend.api.DTO.request.RegistrationRequestShort;
import com.barsik.backend.api.DTO.response.FullProfileResponse;
import com.barsik.backend.entity.User;




@Service
public class RegisterService {

    @Autowired
    private UserService userService;

    private User requestToEntity(RegistrationRequestShort request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        return user;
    }

    public FullProfileResponse register(RegistrationRequestShort request) {
        User user = requestToEntity(request);
        User savedUser = userService.saveUser(user);

        return new FullProfileResponse(
            savedUser.getEmail(),
            savedUser.getCreatedAt(),
            savedUser.getUpdatedAt()
        );
    }

    public HttpStatus login(LogInRequest request){
       User user = userService.findByEmail(request.getEmail());
       if(user.getPasswordHash() == request.getPassword()) return HttpStatus.OK;
       return HttpStatus.NOT_FOUND;
    }
}