package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.Resource;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    @Autowired
    ResourceRepository resourceRepository;

    public Integer getResourceCount(Topic topic){return resourceRepository.countByTopic(topic);}
    public Integer getResourceCount(User user){return resourceRepository.countByUser(user);}

    public List<Topic> getTrendingTopics(){return resourceRepository.getTrendingTopics();}

    public List<Resource> getSubscribedResources(List<Topic> topics){return resourceRepository.findByTopicIn(topics);}

    public List<Resource> getRecentShares(){return resourceRepository.findTop5ByUserIsNotNullOrderByIdDesc();}

    public List<Resource> getResourcesOfTopic(Topic topic){return resourceRepository.findByTopic(topic);}

    public Resource getResourceById(Integer id){return resourceRepository.findById(id).get();}


}
