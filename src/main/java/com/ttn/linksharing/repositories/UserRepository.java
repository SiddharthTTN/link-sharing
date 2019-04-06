package com.ttn.linksharing.repositories;

import com.ttn.linksharing.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT * from user where (username=:username || email=:username) AND password=:password", nativeQuery = true)
    User userLogin(@Param("username") String username, @Param("password") String password);

    User findByUsername(String username);
    User findByEmail(String username);

    User findByUsernameOrEmail(String username, String email);

    User findByUsernameOrEmailAndPassword(String username, String email, String password);

    User findByUsernameAndPassword(String username, String password);

    User findByEmailAndPassword(String username, String password);
}

