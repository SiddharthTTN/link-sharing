package com.ttn.linksharing.controller;

import com.ttn.linksharing.entity.*;
import com.ttn.linksharing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {


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
    boolean docOrLink;

    @GetMapping("/dashboard")
    public String dash(Model model, HttpSession httpSession) {
        model.addAttribute("user", httpSession.getAttribute("user"));
        model.addAttribute("topic", new Topic());
        model.addAttribute("topicCount", topicService.getTopics((User) httpSession.getAttribute("user")));
        model.addAttribute("subscriptionCount", subscriptionService.getSubscription((User) httpSession.getAttribute("user")));
        model.addAttribute("subscibedTopics", subscriptionService.getSubscribedTopics((User) httpSession.getAttribute("user")));
        model.addAttribute("register", register);
        model.addAttribute("errorlogin", errorlogin);
        List<Subscription> subscriptions = subscriptionService.getSubscriptions((User) httpSession.getAttribute("user"));
        List<Subscription> ownSubscription = subscriptions.stream()
                .filter(e -> e.getTopic().getUser().getId().equals(((User) httpSession.getAttribute("user")).getId()))
                .collect(Collectors.toList());
        List<Subscription> otherSubscription = subscriptions.stream().filter(e -> !e.getTopic().getUser().getId().equals(((User) httpSession.getAttribute("user")).getId())).collect(Collectors.toList());
        List<Subscription> allSubscriptions = subscriptionService.getAllSubscription();
        if (((User) httpSession.getAttribute("user")).getIsAdmin())
            model.addAttribute("ownSubscriptions", allSubscriptions);
        else
            model.addAttribute("ownSubscriptions", ownSubscription);
        model.addAttribute("otherSubscription", otherSubscription);
        List<Topic> topics = resourceService.getTrendingTopics().stream().limit(5).collect(Collectors.toList());
        model.addAttribute("trendingTopics", topics);
        List<com.ttn.linksharing.entity.Resource> subscribedResources = resourceService.getSubscribedResources(subscriptionService.getSubscribedTopics((User) httpSession.getAttribute("user")));
        List<Resource> allResources = resourceService.getAllResources();
        if (((User) httpSession.getAttribute("user")).getIsAdmin())
            model.addAttribute("subscribedResources", allResources);
        else
            model.addAttribute("subscribedResources", subscribedResources);
        model.addAttribute("docOrLink", docOrLink);
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
            topicService.sendInvite((User) httpSession.getAttribute("user"), selectedTopic, email);
            redirectAttributes.addFlashAttribute("message", "Invitation send Successfully");
            register = true;
            return "redirect:/dashboard";
        }
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
