package com.ttn.linksharing.controller;

import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
public class ProfileController {
    @Autowired
    UserService userService;

    @Autowired
    TopicService topicService;

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    DocumentResourceService documentResourceService;

    @Autowired
    LinkResourceService linkResourceService;

    private Boolean register = false;
    private Boolean errorlogin = false;

    @GetMapping("/profile")
    public String editProfile(Model model, HttpSession httpSession) {
/*        if (httpServletRequest.getSession().getAttribute("user") == null) {
            return "redirect:/home";
        } else*/
        model.addAttribute("user", httpSession.getAttribute("user"));
        model.addAttribute("topicCount", topicService.getTopics((User) httpSession.getAttribute("user")));
        model.addAttribute("subscriptionCount", subscriptionService.getSubscription((User) httpSession.getAttribute("user")));
        model.addAttribute("register", register);
        model.addAttribute("errorlogin", errorlogin);
        model.addAttribute("ownTopics", topicService.getTopicByUser((User) httpSession.getAttribute("user")));
        register = false;
        errorlogin = false;
        return "editprofile";

    }

    @PostMapping("/updateProfile")
    public String updateProfile(RedirectAttributes redirectAttributes, User user, HttpSession httpSession) {
        String imgVal = "image";
        if (user.getUserImage().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Upload your image");
            errorlogin = true;
            return "redirect:/profile";
        } else if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getUsername().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            errorlogin = true;
            return "redirect:/profile";
        } else if (!imgVal.equals((Objects.requireNonNull(user.getUserImage().getContentType())).substring(0, 5))) {
            redirectAttributes.addFlashAttribute("message", "Please Upload an image file only");
            errorlogin = true;
            return "redirect:/profile";
        } else {
            User user2 = userService.usernameExists(user.getUsername());
            if (user2 == null) {
                User user3 = (User) httpSession.getAttribute("user");
                user3.setUsername(user.getUsername());
                user3.setFirstName(user.getFirstName());
                user3.setLastName(user.getLastName());
                user3.setUserImage(user.getUserImage());
                userService.register(user3);
                redirectAttributes.addFlashAttribute("message", "Your Profile has been Updated Successfully");
                register = true;
                return "redirect:/profile";
            } else {
                redirectAttributes.addFlashAttribute("message", "Username already taken");
                errorlogin = true;
                return "redirect:/profile";
            }

        }
    }

    @PostMapping("/changePasswordFromProfile")
    public String changePasswordFromProfile(RedirectAttributes redirectAttributes, User user, HttpSession httpSession) throws Exception {
        if (user.getPassword().isEmpty() || user.getConfirmPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            errorlogin = true;
            return "redirect:/profile";
        } else if (!user.getPassword().equals(user.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("message", "Passwords Do not match");
            errorlogin = true;
            return "redirect:/profile";
        } else {
            user.setUsername(((User) httpSession.getAttribute("user")).getUsername());
            userService.reset(user);
            redirectAttributes.addFlashAttribute("message", "Your Password has been changed successfully");
            register = true;
            return "redirect:/profile";
        }
    }


}
