package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.Resource;
import com.ttn.linksharing.entity.Topic;
import com.ttn.linksharing.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResourceRepository extends CrudRepository<Resource,Integer> {
    Integer countByTopic(Topic topic);
    Integer countByUser(User user);

    @Query(value = "select r.topic from Resource r where r.topic.visibility='PUBLIC' group by r.topic order by count(r) desc")
    List<Topic> getTrendingTopics();

    List<Resource> findByTopicIn(List<Topic> topics);

    List<Resource> findTop5ByUserIsNotNullOrderByIdDesc();


    List<Resource> findByTopic(Topic topic);
}
