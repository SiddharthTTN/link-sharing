package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.enums.Visibility;
import com.ttn.linksharing.repositories.SubscriptionRepository;
import com.ttn.linksharing.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class TopicService {

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    public void save(Topic topic, Subscription subscription) {
        topicRepository.save(topic);
        subscriptionRepository.save(subscription);
    }

    public void save(Topic topic) {
        topicRepository.save(topic);
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

    public List<Topic> getTopicByUser(User user) {
        return topicRepository.findByUser(user);
    }

    @Async
    public void sendEmail(User user) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(user.getEmail());
        helper.setText("Hello your password is: " + user.getPassword());
        helper.setSubject("Please Find your password here");
        sender.send(message);
    }

    @Async
    public void sendInvite(User user, Integer selectedTopic, String email) throws Exception {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setText("Hello\nTopic Name: " + getTopicById(selectedTopic).getName() + "\nPlease click on this link to subscribe to this topic: http://localhost:8080/subscribe/" + selectedTopic);
        helper.setSubject("You have got a new Invitation from " + user.getFirstName() + " " + user.getLastName());
        sender.send(message);
    }



/*    @Transactional
    public void deleteTopicByID(Integer id){topicRepository.deleteById(id);}*/
}
