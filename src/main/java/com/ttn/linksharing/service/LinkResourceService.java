package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.LinkResource;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.LinkResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkResourceService {

    @Autowired
    LinkResourceRepository linkResourceRepository;

    public void addResource(User user, Topic topic, String link, String description) {
        LinkResource linkResource= new LinkResource();
        linkResource.setUrl(link);
        linkResource.setDescription(description);
        linkResource.setUser(user);
        linkResource.setTopic(topic);
        linkResourceRepository.save(linkResource);
    }
}
