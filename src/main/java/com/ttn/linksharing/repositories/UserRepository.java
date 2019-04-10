package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);

    User findByEmail(String username);

    User findByUsernameOrEmail(String username, String email);

    User findByUsernameAndPassword(String username, String password);

    User findByEmailAndPassword(String username, String password);

    List<User> findByIsAdminFalse();
}

