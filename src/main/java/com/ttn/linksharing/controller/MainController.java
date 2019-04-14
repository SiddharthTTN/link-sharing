package com.ttn.linksharing.controller;

import com.ttn.linksharing.entity.*;
import com.ttn.linksharing.enums.Visibility;
import com.ttn.linksharing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

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

    @Autowired
    ResourceRatingService resourceRatingService;

    private Boolean status = false;
    private Boolean register = false;
    private Boolean errorlogin = false;
    boolean docOrLink;

    public List<com.ttn.linksharing.entity.Resource> getTopPosts() {
        List<com.ttn.linksharing.entity.Resource> topPosts = resourceService.getRecentShares().stream()
                .filter(e -> e.getTopic().getVisibility() == Visibility.PUBLIC)
                .collect(Collectors.toList());
        return topPosts;
    }

    @GetMapping("/home")
    public String homePage(Model model, HttpSession httpSession) {
        if (httpSession.getAttribute("user") != null)
            return "redirect:/dashboard";
        else {
            model.addAttribute("user", new User());
            model.addAttribute("status", status);
            model.addAttribute("register", register);
            model.addAttribute("errorlogin", errorlogin);
            List<com.ttn.linksharing.entity.Resource> recentShares = resourceService.getRecentShares().stream()
                    .filter(e -> e.getTopic().getVisibility() == Visibility.PUBLIC)
                    .collect(Collectors.toList());
            model.addAttribute("recentShares", recentShares);
            List<com.ttn.linksharing.entity.Resource> topPosts = getTopPosts();
            model.addAttribute("topPosts", topPosts);
            status = false;
            register = false;
            errorlogin = false;
            return "home";
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
                topicService.sendEmail(user);
                redirectAttributes.addFlashAttribute("message", "Email with password has been sent successfully");
                register = true;
                return "redirect:/home";
            }
        }

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
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("message", "Passwords Do not match");
            status = true;
            return "redirect:/home";
        } else if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty() || user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getConfirmPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please Fill in all the fields");
            status = true;
            return "redirect:/home";
        } else if (!user.getUserImage().isEmpty() && !imgVal.equals(user.getUserImage().getContentType().substring(0, 5))) {
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
            } else if (!user2.getIsActive()) {
                redirectAttributes.addFlashAttribute("message", "Your account has been disabled. Please contact the admin.");
                errorlogin = true;
                return "redirect:/home";
            } else {
                request.getSession().setAttribute("user", user2);
                return "redirect:/dashboard";
            }
        }
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

    @GetMapping("/document/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment");
        Resource file = loadAsDocument(filename);
        if (file != null)
            return ResponseEntity.ok().body(file);
        return ResponseEntity.notFound().build();
    }

    private Resource loadAsDocument(String filename) {
        try {
            Path file = Paths.get("/home/ttn/linksharing-attatchments/linkdocument/").resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) {
                return resource;
            }
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
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

    @PostMapping("/editLinkResource")
    public String editLinkResource(String link, String description, Integer resourceId) {
        LinkResource linkResource = linkResourceService.editResource(resourceId);
        linkResource.setUrl(link);
        linkResource.setDescription(description);
        linkResourceService.saveLink(linkResource);
        return "redirect:/resource/" + resourceId;
    }

    @PostMapping("/editDocumentResource")
    public String editDocumentResource(String description, MultipartFile document, Integer resourceId) {
        DocumentResource documentResource = documentResourceService.getDocumentByID(resourceId);
        documentResource.setDocumentResource(document);
        documentResource.setDescription(description);
        documentResourceService.shareDocument(documentResource);
        return "redirect:/resource/" + resourceId;

    }

    @GetMapping("/users/{pageNumber}")
    public String showUsers(Model model,@PathVariable("pageNumber")Integer pagenumber) {
        model.addAttribute("users", userService.getNonAdminUsers(pagenumber).getContent());
        List<Integer> pages=new ArrayList<>();
        Integer pageCount=userService.getNonAdminUsers(pagenumber).getTotalPages();
        for(int i=0;i<pageCount;i++){
            pages.add(i);
        }
        model.addAttribute("pages",pages);
        return "adminProfile";
    }

    @GetMapping("/topic/{topicID}")
    public String getTopic(HttpSession httpSession, @PathVariable("topicID") Integer id, Model model) {
        topicresource(httpSession, model);
        model.addAttribute("topic", topicService.getTopicById(id));
        model.addAttribute("topicSubscribers", subscriptionService.getSubscriptions(topicService.getTopicById(id)));
        model.addAttribute("subscribedResources", resourceService.getResourcesOfTopic(topicService.getTopicById(id)));
        return "topic";
    }

    @GetMapping("/resource/{resourceID}")
    public String getResource(HttpSession httpSession, @PathVariable("resourceID") Integer id, Model model) {
        topicresource(httpSession, model);
        List<Topic> topics = resourceService.getTrendingTopics().stream().limit(5).collect(Collectors.toList());
        model.addAttribute("trendingTopics", topics);
        model.addAttribute("resource", resourceService.getResourceById(id));
        if (httpSession.getAttribute("user") != null) {
            Integer rating = resourceRatingService.getRatingValue((User) httpSession.getAttribute("user"), resourceService.getResourceById(id));
            model.addAttribute("rating", rating);

        } else
            model.addAttribute("rating", 0);
        return "resource";
    }

    private void topicresource(HttpSession httpSession, Model model) {
        boolean sessionExists;
        sessionExists = httpSession.getAttribute("user") != null;
        if (sessionExists)
            model.addAttribute("user", httpSession.getAttribute("user"));
        else
            model.addAttribute("user", new User());
        model.addAttribute("sessionExists", sessionExists);
    }

    @GetMapping("/search/{search}")
    public String search(Model model, @PathVariable("search") String searchParam, HttpSession httpSession) {
        topicresource(httpSession, model);
        model.addAttribute("searchParam", searchParam);
        List<com.ttn.linksharing.entity.Resource> topPosts = getTopPosts();
        model.addAttribute("topPosts", topPosts);
        List<Topic> topics = resourceService.getTrendingTopics().stream().limit(5).collect(Collectors.toList());
        model.addAttribute("trendingTopics", topics);
        List<com.ttn.linksharing.entity.Resource> resources = resourceService.getAllResources();
        List<com.ttn.linksharing.entity.Resource> publicResources = resources.stream()
                .filter(e -> e.getTopic().getVisibility() == Visibility.PUBLIC)
                .filter(e -> e.getDescription().toLowerCase().contains(searchParam.toLowerCase()) || e.getTopic().getName().toLowerCase().contains(searchParam.toLowerCase()))
                .collect(Collectors.toList());
        model.addAttribute("allResources", publicResources);
        return "search";
    }

    @PostMapping("/search")
    public String search(String searchParam) {
        return "redirect:/search/" + searchParam;
    }

}
