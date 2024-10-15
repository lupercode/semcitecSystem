package com.luanpereira.semcitecsystem.services;

import com.luanpereira.semcitecsystem.models.UserModel;
import com.luanpereira.semcitecsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Long countItem() {
        return userRepository.count();
    }

    public List<UserModel> birthdaysOfTheMonth(int month) {
        return userRepository.findByBirthdayMonth(month);
    }

    public Optional<UserModel> findById(UUID uuid) {
        return this.userRepository.findById(uuid);
    }

    public List<UserModel> findAll() {
        return this.userRepository.findAll();
    }

    public UserModel save(UserModel userData) {
        return this.userRepository.save(userData);
    }
}
