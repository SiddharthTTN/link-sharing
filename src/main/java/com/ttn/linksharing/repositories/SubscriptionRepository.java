package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    Integer countByUser(User user);

    @Query("select t from Subscription s,Topic t where t.id = s.topic and s.user = :user")
    List<Topic> getSubscribedTopic(@Param("user") User user);

    Subscription findByUserAndTopic(User user, Topic topic);
}
