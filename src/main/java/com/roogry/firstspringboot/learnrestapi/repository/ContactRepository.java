package com.roogry.firstspringboot.learnrestapi.repository;

import com.roogry.firstspringboot.learnrestapi.entity.Contact;
import com.roogry.firstspringboot.learnrestapi.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository
  extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {
  Optional<Contact> findFirstByUserAndId(User user, String id);
}
