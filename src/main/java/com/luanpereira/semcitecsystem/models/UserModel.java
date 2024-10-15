package com.luanpereira.semcitecsystem.models;

import com.luanpereira.semcitecsystem.roles.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "uuid")
@Entity(name = "app_user")
public class UserModel implements UserDetails { //UserDetails from springSecurity
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    private String name;
    @Column(unique = true, nullable = false, length = 14)
    private String cpf;
    private LocalDate birthday;
    private String gender;
    private String phone1;
    private String phone2;
    private String email;
    private UserRoles role;
    private String position;
    private String work_schedule;
    private String matricula;
    private String street;
    @ColumnDefault(value = "'s/n'")
    @Column(length = 20)
    private String houseNumber;
    private String neighborhood;
    private String city;
    private String state;
    private String zip_code;
    @Column(columnDefinition = "text")
    private String obs;
    @Column(unique = true, nullable = false, length = 100)
    private String username;
    private String password;
    private String dossier;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ATIVO'")
    private Status status;
    private String img;

    public UserModel(String username, String password, UserRoles role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRoles.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}