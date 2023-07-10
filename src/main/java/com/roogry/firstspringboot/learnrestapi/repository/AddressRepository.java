package com.roogry.firstspringboot.learnrestapi.repository;

import com.roogry.firstspringboot.learnrestapi.entity.Address;
import com.roogry.firstspringboot.learnrestapi.entity.Contact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
  Optional<Address> findFirstByContactAndId(Contact contact, String id);

  List<Address> findAllByContact(Contact contact);
}
