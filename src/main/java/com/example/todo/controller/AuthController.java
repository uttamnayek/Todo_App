package com.example.todo.controller;

import com.example.todo.model.User;

import com.example.todo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String home(HttpSession session) {
		if (session.getAttribute("userId") != null) {
			return "redirect:/todos";
		}
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginPage(HttpSession session) {
		if (session.getAttribute("userId") != null) {
			return "redirect:/todos";
		}
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
			RedirectAttributes redirectAttributes) {

		Optional<User> userOpt = userService.authenticateUser(username, password);

		if (userOpt.isPresent()) {
			session.setAttribute("userId", userOpt.get().getId());
			session.setAttribute("username", userOpt.get().getUsername());
			return "redirect:/todos";
		} else {
			redirectAttributes.addFlashAttribute("error", "Invalid username or password");
			return "redirect:/login";
		}
	}

	@GetMapping("/register")
	public String registerPage(HttpSession session) {
		if (session.getAttribute("userId") != null) {
			return "redirect:/todos";
		}
		return "register";
	}

	@PostMapping("/register")
	public String register(@RequestParam String username, @RequestParam String password,
			@RequestParam String confirmPassword, RedirectAttributes redirectAttributes) {

		if (!password.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("error", "Passwords do not match");
			return "redirect:/register";
		}

		try {
			userService.registerUser(username, password);
			redirectAttributes.addFlashAttribute("success", "Registration successful. Please login.");
			return "redirect:/login";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/register";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
