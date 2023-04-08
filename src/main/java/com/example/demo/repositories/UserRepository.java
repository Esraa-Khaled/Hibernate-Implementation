package com.example.demo.repositories;

import com.example.demo.config.CrudRepository;
import com.example.demo.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends CrudRepository<User, Long> {


}
