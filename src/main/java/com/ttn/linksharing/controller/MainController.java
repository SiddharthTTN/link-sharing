package com.ttn.linksharing.controller;

import com.ttn.linksharing.entity.DocumentResource;
import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Controller
public class MainController {

    @Autowired
    UserService userService;

    @Autowired
    TopicService topicService;

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    DocumentResourceService documentResourceService;

    @Autowired
    LinkResourceService linkResourceService;

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
    public String login(User user, RedirectAttributes redirectAttributes, HttpServletRequest request) throws Exception {
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
            } else {
                request.getSession().setAttribute("user", user2);
                return "redirect:/dashboard";
            }
        }
    }

    @GetMapping("/dashboard")
    public String dash(Model model, HttpSession httpSession) {
        model.addAttribute("user", httpSession.getAttribute("user"));
        model.addAttribute("topic", new Topic());
        model.addAttribute("topicCount", topicService.getTopics((User) httpSession.getAttribute("user")));
        model.addAttribute("subscriptionCount", subscriptionService.getSubscription((User) httpSession.getAttribute("user")));
        model.addAttribute("subscibedTopics", subscriptionService.getSubscribedTopics((User) httpSession.getAttribute("user")));
        model.addAttribute("register", register);
        model.addAttribute("errorlogin", errorlogin);
        register = false;
        errorlogin = false;
        return "dashboard";

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        httpServletRequest.getSession().invalidate();
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        errorlogin = true;
        return "redirect:/home";
    }

    @GetMapping("/profile")
    public String editProfile(Model model, HttpSession httpSession, HttpServletRequest httpServletRequest) {
/*        if (httpServletRequest.getSession().getAttribute("user") == null) {
            return "redirect:/home";
        } else*/
        model.addAttribute("user", httpSession.getAttribute("user"));
        model.addAttribute("register", register);
        model.addAttribute("errorlogin", errorlogin);
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

    @RequestMapping("/sendemail")
    public String home(String username, RedirectAttributes redirectAttributes) throws Exception {
        if (username.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill the Username");
            status = true;
            return "redirect:/reset";
        } else {
            User user = userService.userExists(username);
            if (user == null) {
                redirectAttributes.addFlashAttribute("message", "Username/Email does not exists");
                status = true;
                return "redirect:/reset";
            } else {
                sendEmail(user);
                redirectAttributes.addFlashAttribute("message", "Email with password has been sent successfully");
                register = true;
                return "redirect:/home";
            }
        }

    }

    private void sendEmail(User user) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(user.getEmail());
        helper.setText("Hello your password is: " + user.getPassword());
        helper.setSubject("Please Find your password here");
        sender.send(message);
    }

    @GetMapping("/checkerror")
    public String checkError() {
        userService.checkError("xxx");
        return "none";
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = loadAsResource(filename);
        if (file != null)
            return ResponseEntity.ok().body(file);
        return ResponseEntity.notFound().build();
    }

    private Resource loadAsResource(String filename) {
        try {
            Path file = Paths.get("/home/ttn/linksharing-attatchments/userimage").resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) {
                return resource;
            }
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @PostMapping("/createTopic")
    public String createTopic(Topic topic, HttpSession httpSession, Subscription subscription, RedirectAttributes redirectAttributes) {
        Topic topic1 = topicService.getTopicBySameName((User) httpSession.getAttribute("user"), topic.getName());
        if (topic1 == null) {
            topic.setUser((User) httpSession.getAttribute("user"));
            subscription.setUser((User) httpSession.getAttribute("user"));
            subscription.setTopic(topic);
            topicService.save(topic, subscription);
            redirectAttributes.addFlashAttribute("message", "Topic successfully created     ");
            register = true;
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("message", "Topic already created by you");
            errorlogin = true;
            return "redirect:/dashboard";
        }
    }

    @PostMapping("sendInvite")
    public String sendInvite(HttpSession httpSession, RedirectAttributes redirectAttributes, String email, Integer selectedTopic) throws Exception {
        if (email.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please enter the email");
            errorlogin = true;
            return "redirect:/dashboard";
        } else if (!userService.validateEmail(email)) {
            redirectAttributes.addFlashAttribute("message", "Please enter a valid email address");
            errorlogin = true;
            return "redirect:/dashboard";
        } else if (userService.emailExists(email) == null) {
            redirectAttributes.addFlashAttribute("message", "Email ID Does not exists");
            errorlogin = true;
            return "redirect:/dashboard";
        } else if (subscriptionService.confirmSubscription(userService.emailExists(email), topicService.getTopicById(selectedTopic)) != null) {
            redirectAttributes.addFlashAttribute("message", "User with this email ID already subscribed to this topic");
            errorlogin = true;
            return "redirect:/dashboard";
        } else {
            sendInvite((User) httpSession.getAttribute("user"), selectedTopic, email);
            redirectAttributes.addFlashAttribute("message", "Invitation send Successfully");
            register = true;
            return "redirect:/dashboard";
        }
    }

    private void sendInvite(User user, Integer selectedTopic, String email) throws Exception {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setText("Hello\nTopic Name: " + topicService.getTopicById(selectedTopic).getName() + "\nPlease click on this link to subscribe to this topic: http://localhost:8080/subscribe/" + selectedTopic);
        helper.setSubject("You have got a new Invitation from " + user.getFirstName() + " " + user.getLastName());
        sender.send(message);
    }

    @GetMapping("/subscribe/{selectedTopic}")
    public String subscribe(RedirectAttributes redirectAttributes, HttpSession httpSession, @PathVariable("selectedTopic") Integer selectedTopic, Subscription subscription) {
        subscription.setUser((User) httpSession.getAttribute("user"));
        subscription.setTopic(topicService.getTopicById(selectedTopic));
        subscriptionService.subscribe(subscription);
        redirectAttributes.addFlashAttribute("message", "You have been subscribed successfully");
        register = true;
        return "redirect:/dashboard";
    }

    @PostMapping("/createLinkResource")
    public String createLinkResource(HttpSession httpSession, RedirectAttributes redirectAttributes, String link, String description, Integer topicId) {

        if (link.isEmpty() || description.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            errorlogin = true;
            return "redirect:/dashboard";
        } else if (description.length() < 100) {
            redirectAttributes.addFlashAttribute("message", "Description should be of minimum 100 characters");
            errorlogin = true;
            return "redirect:/dashboard";
        } else {
            linkResourceService.addResource((User) httpSession.getAttribute("user"), topicService.getTopicById(topicId), link, description);
            redirectAttributes.addFlashAttribute("message", "Your Link resource have been added successfully");
            register = true;
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/createDocumentResource")
    public String createDocumentResource(RedirectAttributes redirectAttributes, HttpSession httpSession, String description, MultipartFile document, Integer topicId) {
        if (description.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            errorlogin = true;
            return "redirect:/dashboard";
        } else if (document.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please upload a document");
            errorlogin = true;
            return "redirect:/dashboard";
        } else if (description.length() < 100) {
            redirectAttributes.addFlashAttribute("message", "Description should be of minimum 100 characters");
            errorlogin = true;
            return "redirect:/dashboard";
        } else {
            DocumentResource documentResource = new DocumentResource();
            documentResource.setDocumentResource(document);
            documentResource.setUser((User) httpSession.getAttribute("user"));
            documentResource.setDescription(description);
            documentResource.setTopic(topicService.getTopicById(topicId));
            documentResourceService.shareDocument(documentResource);
            redirectAttributes.addFlashAttribute("message", "Document resource have been shared successfully");
            register = true;
            return "redirect:/dashboard";
        }
    }
}
