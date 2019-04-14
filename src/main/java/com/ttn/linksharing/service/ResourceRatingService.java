package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.Resource;
import com.ttn.linksharing.entity.ResourceRating;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.ResourceRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ResourceRatingService {
    @Autowired
    ResourceRatingRepository resourceRatingRepository;

    public void save(ResourceRating resourceRating) {
        resourceRatingRepository.save(resourceRating);
    }

    public ResourceRating getByUserAndResource(User user, Resource resource) {
        return resourceRatingRepository.findByUserAndResource(user, resource);
    }

    @Transactional
    public void deleteByResourceId(Resource resource) {
        resourceRatingRepository.deleteByResource(resource);
    }

    public Integer getRatingValue(User user, Resource resource) {
        return resourceRatingRepository.getRating(user, resource);
    }
}
