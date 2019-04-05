package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic,Integer>
{
}
