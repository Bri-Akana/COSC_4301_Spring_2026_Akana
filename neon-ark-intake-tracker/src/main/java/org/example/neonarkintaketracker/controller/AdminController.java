package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.dto.UserResponse;
import org.example.neonarkintaketracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 8. Admin: GET /api/admin/users — list all system users
// Note: authentication/authorization not implemented in this project.
//       In production this would require an ADMIN role check.
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
