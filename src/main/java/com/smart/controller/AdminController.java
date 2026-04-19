package com.smart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "Admin Dashboard");
        model.addAttribute("message", "Welcome Admin!");
        return "admin/dashboard";
    }

    @GetMapping("/view-users")
    public String viewUsers(Model model) {
        List<User> users = userRepository.getAllNormalUsers();
        model.addAttribute("users", users);
        return "admin/view-users";
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userRepository.getAllNormalUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {

        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "danger");

        return "redirect:/admin/manage-users";
    }

    // =========================
    // ✅ STEP 1: OPEN UPDATE FORM
    // =========================
    @GetMapping("/update-user/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "admin/update-user"; // Thymeleaf page
    }

    // =========================
    // ✅ STEP 2: UPDATE USER
    // =========================
    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Safe update (avoid null overwrite)
        if (user.getName() != null && !user.getName().isEmpty()) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }

        // 🔒 Preserve important fields
        existingUser.setRole(existingUser.getRole());
        existingUser.setPassword(existingUser.getPassword());
        existingUser.setEnabled(existingUser.isEnabled());

        userRepository.save(existingUser);

        redirectAttributes.addFlashAttribute("message", "User updated successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/admin/manage-users";
    }
    
    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("title", "Admin Dashboard");

        model.addAttribute("totalUsers", userRepository.countNormalUsers());
        model.addAttribute("activeUsers", userRepository.countActiveNormalUsers(true));
        //model.addAttribute("contactsCount", contactRepository.count());

        model.addAttribute("recentUsers", userRepository.findTop5NormalUsers());

        return "admin/admin-dashboard";
    }
    @GetMapping("/reports")
    public String reports(Model model) {

        long totalUsers = userRepository.countNormalUsers();
        long activeUsers = userRepository.countActiveNormalUsers(true);
        long inactiveUsers = userRepository.countInactiveNormalUsers();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("inactiveUsers", inactiveUsers);
        return "admin/reports";
    }
    
    @GetMapping("/toggle-user-status/{id}")
    public String toggleUserStatus(@PathVariable int id, RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.isEnabled()); // toggle

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
            "message",
            "User " + (user.isEnabled() ? "Activated" : "Deactivated") + " successfully!"
        );
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/admin/manage-users";
    }
}