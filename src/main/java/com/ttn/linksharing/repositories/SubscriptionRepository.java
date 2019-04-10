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

    @Query("select s.topic from Subscription s where s.user = :user")
    List<Topic> getSubscribedTopic(@Param("user") User user);

    Subscription findByUserAndTopic(User user, Topic topic);

    List<Subscription> findByUser(User user);
    List<Subscription> findByTopic(Topic topic);

    Integer countByTopic(Topic topic);


}
