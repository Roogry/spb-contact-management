package com.roogry.firstspringboot.learnrestapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roogry.firstspringboot.learnrestapi.entity.User;
import com.roogry.firstspringboot.learnrestapi.model.LoginUserRequest;
import com.roogry.firstspringboot.learnrestapi.model.TokenResponse;
import com.roogry.firstspringboot.learnrestapi.model.WebResponse;
import com.roogry.firstspringboot.learnrestapi.repository.UserRepository;
import com.roogry.firstspringboot.learnrestapi.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void loginFailedUserNotFound() throws Exception {
    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc
      .perform(
        post("/api/auth/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpectAll(status().isUnauthorized())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void loginFailedWrongPassword() throws Exception {
    User user = new User();
    user.setName("Test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("salah");

    mockMvc
      .perform(
        post("/api/auth/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpectAll(status().isUnauthorized())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void loginSuccess() throws Exception {
    User user = new User();
    user.setName("Test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc
      .perform(
        post("/api/auth/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpectAll(status().isOk())
      .andDo(result -> {
        WebResponse<TokenResponse> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNull(response.getErrors());
        assertNotNull(response.getData().getToken());
        assertNotNull(response.getData().getExpiredAt());

        User userDb = userRepository.findById("test").orElse(null);
        assertNotNull(userDb);
        assertEquals(userDb.getToken(), response.getData().getToken());
        assertEquals(
          userDb.getTokenExpiredAt(),
          response.getData().getExpiredAt()
        );
      });
  }

  @Test
  void logoutFailed() throws Exception {
    mockMvc
      .perform(delete("/api/auth/logout").accept(MediaType.APPLICATION_JSON))
      .andExpectAll(status().isUnauthorized())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void logoutSuccess() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
    userRepository.save(user);

    mockMvc
      .perform(
        delete("/api/auth/logout")
          .accept(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isOk())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNull(response.getErrors());
        assertEquals("OK", response.getData());

        User userDb = userRepository.findById("test").orElse(null);
        assertNotNull(userDb);
        assertNull(userDb.getTokenExpiredAt());
        assertNull(userDb.getToken());
      });
  }
}