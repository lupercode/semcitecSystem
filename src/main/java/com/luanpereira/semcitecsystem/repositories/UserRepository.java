package com.luanpereira.semcitecsystem.repositories;

import com.luanpereira.semcitecsystem.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    UserDetails findByUsername(String username);

    @Query("SELECT u FROM app_user u WHERE MONTH(u.birthday) = :month")
    List<UserModel> findByBirthdayMonth(@Param("month") int month);
}
