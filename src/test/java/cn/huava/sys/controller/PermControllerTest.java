package cn.huava.sys.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.sys.pojo.po.PermPo;
import cn.huava.sys.service.perm.AcePermService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class PermControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private PermController permController;

  @Autowired private AcePermService acePermService;

  @BeforeEach
  void setUp() {
    // No specific setup needed for MockHttpServletRequest/Response as getAll doesn't directly use
    // them
  }

  @Test
  void getAllReturnsListOfPermDtos() throws Exception {
//    PermPo entity = new PermPo().;
//    permController.create(entity);

//    mockMvc.perform(MockMvcRequestBuilders.get("/sys/perm/getAll")).andExpect(status().isOk());

    //    assertThat(repository.findAll()).anyMatch(item -> item.getContent().equals("foo"));
  }
}
