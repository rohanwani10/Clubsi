package com.clubsi.clubsi.Repository;

import com.clubsi.clubsi.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
     User findByEmail(String email);
     User findByUsername(String username);
}
