package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.Resource;
import com.ttn.linksharing.entity.ResourceRating;
import com.ttn.linksharing.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ResourceRatingRepository extends CrudRepository<ResourceRating, Integer> {
    ResourceRating findByUserAndResource(User user, Resource resource);

    void deleteByResource(Resource resource);

    @Transactional
    void deleteAllByResourceIn(List<Resource> resources);

    @Query("select r.rating from ResourceRating r where r.user=:user and r.resource=:resource")
    Integer getRating(@Param("user") User user, @Param("resource") Resource resource);

}
