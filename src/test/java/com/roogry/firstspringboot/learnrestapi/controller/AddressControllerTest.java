package com.roogry.firstspringboot.learnrestapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roogry.firstspringboot.learnrestapi.entity.Address;
import com.roogry.firstspringboot.learnrestapi.entity.Contact;
import com.roogry.firstspringboot.learnrestapi.entity.User;
import com.roogry.firstspringboot.learnrestapi.model.AddressResponse;
import com.roogry.firstspringboot.learnrestapi.model.CreateAddressRequest;
import com.roogry.firstspringboot.learnrestapi.model.UpdateAddressRequest;
import com.roogry.firstspringboot.learnrestapi.model.WebResponse;
import com.roogry.firstspringboot.learnrestapi.repository.AddressRepository;
import com.roogry.firstspringboot.learnrestapi.repository.ContactRepository;
import com.roogry.firstspringboot.learnrestapi.repository.UserRepository;
import com.roogry.firstspringboot.learnrestapi.security.BCrypt;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
    userRepository.save(user);

    Contact contact = new Contact();
    contact.setId("test");
    contact.setUser(user);
    contact.setFirstName("Eko");
    contact.setLastName("Khanedy");
    contact.setEmail("eko@example.com");
    contact.setPhone("9238423432");
    contactRepository.save(contact);
  }

  @Test
  void createAddressBadRequest() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setCountry("");

    mockMvc
      .perform(
        post("/api/contacts/test/addresses")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isBadRequest())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void createAddressSuccess() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setStreet("Jalan");
    request.setCity("Jakarta");
    request.setProvince("DKI");
    request.setCountry("Indonesia");
    request.setPostalCode("123123");

    mockMvc
      .perform(
        post("/api/contacts/test/addresses")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isOk())
      .andDo(result -> {
        WebResponse<AddressResponse> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNull(response.getErrors());
        assertEquals(request.getStreet(), response.getData().getStreet());
        assertEquals(request.getCity(), response.getData().getCity());
        assertEquals(request.getProvince(), response.getData().getProvince());
        assertEquals(request.getCountry(), response.getData().getCountry());
        assertEquals(
          request.getPostalCode(),
          response.getData().getPostalCode()
        );

        assertTrue(addressRepository.existsById(response.getData().getId()));
      });
  }

  @Test
  void getAddressNotFound() throws Exception {
    mockMvc
      .perform(
        get("/api/contacts/test/addresses/test")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isNotFound())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void getAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Jalan");
    address.setCity("Jakarta");
    address.setProvince("DKI");
    address.setCountry("Indonesia");
    address.setPostalCode("123123");
    addressRepository.save(address);

    mockMvc
      .perform(
        get("/api/contacts/test/addresses/test")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isOk())
      .andDo(result -> {
        WebResponse<AddressResponse> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNull(response.getErrors());
        assertEquals(address.getId(), response.getData().getId());
        assertEquals(address.getStreet(), response.getData().getStreet());
        assertEquals(address.getCity(), response.getData().getCity());
        assertEquals(address.getProvince(), response.getData().getProvince());
        assertEquals(address.getCountry(), response.getData().getCountry());
        assertEquals(
          address.getPostalCode(),
          response.getData().getPostalCode()
        );
      });
  }

  @Test
  void updateAddressBadRequest() throws Exception {
    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCountry("");

    mockMvc
      .perform(
        put("/api/contacts/test/addresses/test")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isBadRequest())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void updateAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Lama");
    address.setCity("Lama");
    address.setProvince("Lama");
    address.setCountry("Lama");
    address.setPostalCode("43535");
    addressRepository.save(address);

    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setStreet("Jalan");
    request.setCity("Jakarta");
    request.setProvince("DKI");
    request.setCountry("Indonesia");
    request.setPostalCode("123123");

    mockMvc
      .perform(
        put("/api/contacts/test/addresses/test")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isOk())
      .andDo(result -> {
        WebResponse<AddressResponse> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNull(response.getErrors());
        assertEquals(request.getStreet(), response.getData().getStreet());
        assertEquals(request.getCity(), response.getData().getCity());
        assertEquals(request.getProvince(), response.getData().getProvince());
        assertEquals(request.getCountry(), response.getData().getCountry());
        assertEquals(
          request.getPostalCode(),
          response.getData().getPostalCode()
        );

        assertTrue(addressRepository.existsById(response.getData().getId()));
      });
  }

  @Test
  void deleteAddressNotFound() throws Exception {
    mockMvc
      .perform(
        delete("/api/contacts/test/addresses/test")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isNotFound())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void deleteAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Jalan");
    address.setCity("Jakarta");
    address.setProvince("DKI");
    address.setCountry("Indonesia");
    address.setPostalCode("123123");
    addressRepository.save(address);

    mockMvc
      .perform(
        delete("/api/contacts/test/addresses/test")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
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

        assertFalse(addressRepository.existsById("test"));
      });
  }

  @Test
  void listAddressNotFound() throws Exception {
    mockMvc
      .perform(
        get("/api/contacts/salah/addresses")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isNotFound())
      .andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNotNull(response.getErrors());
      });
  }

  @Test
  void listAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    for (int i = 0; i < 5; i++) {
      Address address = new Address();
      address.setId("test-" + i);
      address.setContact(contact);
      address.setStreet("Jalan");
      address.setCity("Jakarta");
      address.setProvince("DKI");
      address.setCountry("Indonesia");
      address.setPostalCode("123123");
      addressRepository.save(address);
    }

    mockMvc
      .perform(
        get("/api/contacts/test/addresses")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .header("X-API-TOKEN", "test")
      )
      .andExpectAll(status().isOk())
      .andDo(result -> {
        WebResponse<List<AddressResponse>> response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          new TypeReference<>() {}
        );
        assertNull(response.getErrors());
        assertEquals(5, response.getData().size());
      });
  }
}
