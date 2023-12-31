package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping
    public String adminPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("users", userService.getUserList());
        return "admin";
    }

    @GetMapping("/user")
    public String showUserDetails(Model model, Principal principal) {
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        return "user";
    }


    @PostMapping
    public String createUser(@ModelAttribute("user") @Valid User user,
                             @RequestParam("selectedRole") String selectedRole,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin";
        }

        try {
            userService.saveUser(user, selectedRole);
        } catch (RuntimeException e) {
            model.addAttribute("emailError", e.getMessage());
            return "admin";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }



    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult, @PathVariable("id") long id, Model model, String selectedRole) {

        if (bindingResult.hasErrors())
            return "admin";
        try {
            userService.updateUser(id, user, selectedRole);
        }
        catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin";
        }
        return "redirect:/admin";
    }
}