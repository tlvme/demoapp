package com.example.demo.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/restaurant")
	@PreAuthorize("hasRole('RESTAURANT')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/customer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String moderatorAccess(Authentication authentication) {
		return "Moderator Board.";
	}

	@GetMapping("/driver")
	@PreAuthorize("hasRole('DRIVER')")
	public String adminAccess(Authentication authentication) {
		return "username "+authentication.getName();
	}
}
