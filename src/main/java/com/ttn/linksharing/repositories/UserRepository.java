package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
