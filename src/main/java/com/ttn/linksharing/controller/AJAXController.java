package com.ttn.linksharing.controller;

import com.ttn.linksharing.entity.ResourceRating;
import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.enums.Seriousness;
import com.ttn.linksharing.enums.Visibility;
import com.ttn.linksharing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class AJAXController {
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

    @DeleteMapping("/deleteTopic")
    @ResponseBody
    public void deleteTopic(@RequestParam("id") Integer topicID) {
        subscriptionService.deleteByTopic(topicService.getTopicById(topicID));
    }

    @DeleteMapping("/unsubscribeTopic")
    @ResponseBody
    public void unsubscribeTopic(@RequestParam("id") Integer subscriptionID) {
        subscriptionService.deleteById(subscriptionID);
    }

    @GetMapping("/updateSubscription")
    @ResponseBody
    public Integer updateSubscription(HttpSession httpSession) {
        return subscriptionService.getSubscription((User) httpSession.getAttribute("user"));
    }

    @GetMapping("/updateTopicCount")
    @ResponseBody
    public Integer updateTopic(HttpSession httpSession) {
        return topicService.getTopics((User) httpSession.getAttribute("user"));
    }

    @PutMapping("/updateTopic")
    @ResponseBody
    public void updateTopic(@RequestParam("id") Integer topicID, @RequestParam("name") String updatedName) {
        Topic topic = topicService.getTopicById(topicID);
        topic.setName(updatedName);
        topicService.save(topic);
    }

    @PutMapping("/rateResource")
    @ResponseBody
    public void rateResource(@RequestParam("id") Integer resourceID, @RequestParam("value") Integer rating, HttpSession httpSession) {
        ResourceRating resourceRating = resourceRatingService.getByUserAndResource((User) httpSession.getAttribute("user"), resourceService.getResourceById(resourceID));
        if (resourceRating == null) {
            ResourceRating resourceRating1 = new ResourceRating();
            resourceRating1.setResource(resourceService.getResourceById(resourceID));
            resourceRating1.setUser((User) httpSession.getAttribute("user"));
            resourceRating1.setRating(rating);
            resourceRatingService.save(resourceRating1);
        } else {
            resourceRating.setRating(rating);
            resourceRatingService.save(resourceRating);
        }
    }

    @PutMapping("/updateSeriousness")
    @ResponseBody
    public void updateSeriousness(@RequestParam("id") Integer subscriptionID, @RequestParam("name") Seriousness updatedSeriousness) {
        Subscription subscription = subscriptionService.getById(subscriptionID);
        subscription.setSeriousness(updatedSeriousness);
        subscriptionService.save(subscription);
    }

    @PutMapping("/updateVisibility")
    @ResponseBody
    public void updateVisibility(@RequestParam("id") Integer topicID, @RequestParam("name") Visibility updatedVisibility) {
        Topic topic = topicService.getTopicById(topicID);
        topic.setVisibility(updatedVisibility);
        topicService.save(topic);
    }

    @DeleteMapping("/deleteResource")
    @ResponseBody
    public void deleteResource(@RequestParam("id") Integer resourceId) {
        resourceService.deleteById(resourceId);
    }

    @PutMapping("/deactivateUser")
    @ResponseBody
    public void deactivateUser(@RequestParam("id") Integer userID) {
        User user = userService.getUserByID(userID);
        user.setIsActive(false);
        userService.updateActiveness(user);
    }

    @PutMapping("/activateUser")
    @ResponseBody
    public void activateUser(@RequestParam("id") Integer userID) {
        User user = userService.getUserByID(userID);
        user.setIsActive(true);
        userService.updateActiveness(user);
    }


}
