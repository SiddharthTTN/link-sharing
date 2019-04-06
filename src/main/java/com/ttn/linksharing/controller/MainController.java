package com.ttn.linksharing.controller;

import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;


@Controller
@SessionAttributes
public class MainController {

    @Autowired
    UserService userService;

    private Boolean status = false;
    private Boolean register = false;
    private Boolean errorlogin = false;

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("status", status);
        model.addAttribute("register", register);
        model.addAttribute("errorlogin", errorlogin);
        status = false;
        register = false;
        errorlogin = false;
        return "home";
    }


    @GetMapping("/reset")
    public String resetPassword(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("status", status);
        status = false;
        return "resetPassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(User user, RedirectAttributes redirectAttributes) throws Exception {

        if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getConfirmPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            status = true;
            return "redirect:/reset";
        } else if (!user.getPassword().equals(user.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("message", "Passwords Do not match");
            status = true;
            return "redirect:/reset";
        } else {
            userService.reset(user);
            redirectAttributes.addFlashAttribute("message", "Your Password has been changed Sucessfully");
            register = true;
            return "redirect:/home";
        }
    }

    @PostMapping("/register")
    public String register(User user, RedirectAttributes redirectAttributes) {
        String imgVal = "image";
        if (user.getUserImage().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Upload your image");
            status = true;
            return "redirect:/home";
        } else if (!user.getPassword().equals(user.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("message", "Passwords Do not match");
            status = true;
            return "redirect:/home";
        } else if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty() || user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getConfirmPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            status = true;
            return "redirect:/home";
        } else if (!imgVal.equals((Objects.requireNonNull(user.getUserImage().getContentType())).substring(0, 5))) {
            redirectAttributes.addFlashAttribute("message", "Please Upload an image file only");
            status = true;
            return "redirect:/home";
        } else if (!userService.validateEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("message", "Please enter a proper E-mail Address");
            status = true;
            return "redirect:/home";
        } else {
            User user2 = userService.usernameExists(user.getUsername());
            User user3 = userService.emailExists(user.getEmail());
            if (user2 == null && user3 == null) {
                userService.register(user);
                redirectAttributes.addFlashAttribute("message", "You have been registered Successfully");
                register = true;
                return "redirect:/home";
            } else {
                if (user3 == null) {
                    redirectAttributes.addFlashAttribute("message", "Username already taken");
                    status = true;
                    return "redirect:/home";
                } else {
                    redirectAttributes.addFlashAttribute("message", "This email is Already registered");
                    status = true;
                    return "redirect:/home";
                }
            }
        }

    }

    @PostMapping("/login")
    public String login(User user, RedirectAttributes redirectAttributes) throws Exception {
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            errorlogin = true;
            return "redirect:/home";
        } else {
            User user2 = userService.getUser(user);
            if (user2 == null) {
                redirectAttributes.addFlashAttribute("message", "Username or password does not exist");
                errorlogin = true;
                return "redirect:/home";
            } else
                return "/dashboard";
        }
    }

    @GetMapping("/dashboard")
    public String dash() {
        return "dashboard";
    }
}
