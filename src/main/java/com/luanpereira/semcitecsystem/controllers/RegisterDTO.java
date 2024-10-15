package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.roles.UserRoles;

public record RegisterDTO(String username, String password, UserRoles role) {
}
