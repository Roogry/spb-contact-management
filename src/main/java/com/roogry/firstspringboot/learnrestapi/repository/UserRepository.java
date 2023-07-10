package com.roogry.firstspringboot.learnrestapi.repository;

import com.roogry.firstspringboot.learnrestapi.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findFirstByToken(String token);
}
