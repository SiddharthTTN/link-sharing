package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.enums.Visibility;
import com.ttn.linksharing.repositories.SubscriptionRepository;
import com.ttn.linksharing.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    public void save(Topic topic, Subscription subscription) {
        topicRepository.save(topic);
        subscriptionRepository.save(subscription);
    }

    public Integer getTopics(User user) {
        return topicRepository.countByUser(user);
    }

    public List<Topic> getAllPublicTopics() {
        return topicRepository.findByVisibility(Visibility.PUBLIC);
    }

    public List<Topic> getAllPrivateTopics(User user) {
        return topicRepository.findByVisibilityAndUser(Visibility.PRIVATE, user);
    }

    public Topic getTopicBySameName(User user, String name) {
        return topicRepository.findByUserAndName(user, name);
    }

    public List<Topic> getTopicByName(String topic) {
        return topicRepository.findByName(topic);
    }

    public Topic getTopicById(Integer id) {
        return topicRepository.findById(id).get();
    }
}
