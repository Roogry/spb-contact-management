package com.roogry.firstspringboot.learnrestapi.service;

import com.roogry.firstspringboot.learnrestapi.entity.User;
import com.roogry.firstspringboot.learnrestapi.model.LoginUserRequest;
import com.roogry.firstspringboot.learnrestapi.model.TokenResponse;
import com.roogry.firstspringboot.learnrestapi.repository.UserRepository;
import com.roogry.firstspringboot.learnrestapi.security.BCrypt;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidationService validationService;

  @Transactional
  public TokenResponse login(LoginUserRequest request) {
    validationService.validate(request);

    User user = userRepository
      .findById(request.getUsername())
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "Username or password wrong"
        )
      );

    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(next30Days());
      userRepository.save(user);

      return TokenResponse
        .builder()
        .token(user.getToken())
        .expiredAt(user.getTokenExpiredAt())
        .build();
    } else {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Username or password wrong"
      );
    }
  }

  private Long next30Days() {
    return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
  }

  public void logout(User user) {
    user.setToken(null);
    user.setTokenExpiredAt(null);
    userRepository.save(user);
  }
}
