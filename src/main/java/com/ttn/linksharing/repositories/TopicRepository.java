package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.enums.Visibility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    Integer countByUser(User user);

    List<Topic> findByVisibility(Visibility visibility);

    List<Topic> findByVisibilityAndUser(Visibility visibility, User user);

    Topic findByUserAndName(User user, String name);

    List<Topic> findByName(String name);

    List<Topic> findByUser(User user);


}
