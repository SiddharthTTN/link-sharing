package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    public void subscribe(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public Integer getSubscription(User user) {
        return subscriptionRepository.countByUser(user);
    }

    public List<Topic> getSubscribedTopics(User user) {
        return subscriptionRepository.getSubscribedTopic(user);
    }

    public Subscription confirmSubscription(User user, Topic topic) {
        return subscriptionRepository.findByUserAndTopic(user, topic);
    }
}

