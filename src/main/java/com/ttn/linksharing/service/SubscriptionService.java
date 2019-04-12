package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.ResourceRepository;
import com.ttn.linksharing.repositories.SubscriptionRepository;
import com.ttn.linksharing.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    ResourceRepository resourceRepository;

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

    public Integer confirmSubscriptionCount(User user, Topic topic) {
        return subscriptionRepository.countByUserAndAndTopic(user, topic);
    }
    public List<Subscription> getSubscriptions(User user) {
        return subscriptionRepository.findByUser(user);
    }

    public Integer getSubscriptionCount(Topic topic) {
        return subscriptionRepository.countByTopic(topic);
    }

    public List<Subscription> getSubscriptions(Topic topic) {
        return subscriptionRepository.findByTopic(topic);
    }

    @Transactional
    public void deleteByTopic(Topic topic) {
        subscriptionRepository.deleteByTopic(topic);
        resourceRepository.deleteByTopic(topic);
        topicRepository.deleteById(topic.getId());
    }

    public Subscription getById(Integer id){
        return subscriptionRepository.findById(id).get();
    }

    public void save(Subscription subscription){
        subscriptionRepository.save(subscription);
    }

    public void deleteById(Integer subscriptionID) {
        subscriptionRepository.deleteById(subscriptionID);
    }

    public void subscribeUser(User user,Topic topic){
        Subscription subscription= new Subscription();
        subscription.setUser(user);
        subscription.setTopic(topic);
        subscriptionRepository.save(subscription);
    }

}

