package cn.huava.sys.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.service.perm.AcePermService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class PermControllerTest {
  @Autowired private MockMvc mockMvc;

  @InjectMocks private PermController permController;

  @Mock private AcePermService acePermService;

  @BeforeEach
  void setUp() {
    // No specific setup needed for MockHttpServletRequest/Response as getAll doesn't directly use
    // them
  }

  @Test
  void getAllReturnsListOfPermDtos() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/sys/perm/getAll"))
        .andExpect(status().isOk());

//    assertThat(repository.findAll()).anyMatch(item -> item.getContent().equals("foo"));
  }
}
