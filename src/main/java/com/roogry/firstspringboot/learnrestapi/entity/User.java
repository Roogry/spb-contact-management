package com.roogry.firstspringboot.learnrestapi.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  private String username;

  private String password;

  private String name;

  private String token;

  @Column(name = "token_expired_at")
  private Long tokenExpiredAt;

  @OneToMany(mappedBy = "user")
  private List<Contact> contacts;
}
