package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.MULTIPART_PARAM_NAME;
import static cn.huava.common.util.ApiTestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.controller.AttachmentController;
import cn.huava.common.controller.AttachmentServingController;
import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.util.ApiTestUtil;
import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.json.JSONUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test the apis in {@link AttachmentController} and {@link AttachmentServingController}. <br>
 * 测试 {@link AttachmentController} 和 {@link AttachmentServingController} 中的接口。<br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@Slf4j
@Rollback
@Transactional
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class AttachmentTest extends WithSpringBootTestAnnotation {

  @Autowired MockMvc mockMvcAutowired;

  @AfterAll
  @SneakyThrows
  static void afterAll() {
    logout();
  }

  @BeforeEach
  @SneakyThrows
  void beforeEach() {
    if (ApiTestUtil.mockMvc == null) {
      ApiTestUtil.mockMvc = mockMvcAutowired;
      loginByAdmin();
    }
  }

  @Test
  @SneakyThrows
  void should_upload_attachment_successfully() {
    // Create a temporary file instead of using head.jpg
    File tempFile = File.createTempFile("test_attachment_", ".jpg");
    Files.write(tempFile.toPath(), "test image content".getBytes(), StandardOpenOption.WRITE);

    MockMultipartHttpServletRequestBuilder req =
        (MockMultipartHttpServletRequestBuilder)
            initReq().multipart("/common/attachment/upload").build();

    byte[] bytes = Files.readAllBytes(tempFile.toPath());
    MockMultipartFile file =
        new MockMultipartFile(MULTIPART_PARAM_NAME, tempFile.getName(), "image/jpeg", bytes);
    req.file(file);
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    AttachmentPo attachmentPo = JSONUtil.toBean(resJsonStr, AttachmentPo.class);
    assertThat(attachmentPo).isNotNull();
    assertThat(attachmentPo.getUrl()).isNotNull();

    // Clean up temp file
    FileUtil.del(tempFile);
  }

  @Test
  @SneakyThrows
  void should_download_attachment_successfully() {
    // First upload a file to get a URL
    File tempFile = File.createTempFile("test_attachment_", ".jpg");
    Files.write(tempFile.toPath(), "test image content".getBytes(), StandardOpenOption.WRITE);

    MockMultipartHttpServletRequestBuilder req =
        (MockMultipartHttpServletRequestBuilder)
            initReq().multipart("/common/attachment/upload").build();

    byte[] bytes = Files.readAllBytes(tempFile.toPath());
    MockMultipartFile file =
        new MockMultipartFile(MULTIPART_PARAM_NAME, tempFile.getName(), "image/jpeg", bytes);
    req.file(file);
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    AttachmentPo attachmentPo = JSONUtil.toBean(resJsonStr, AttachmentPo.class);
    assertThat(attachmentPo).isNotNull();
    assertThat(attachmentPo.getUrl()).isNotNull();

    String uploadedUrl = attachmentPo.getUrl();

    // Now test downloading
    RequestBuilder downloadReq =
        initReq().get(uploadedUrl).contentTypeText().acceptType("image/jpeg").build();
    MvcResult downloadRes = mockMvc.perform(downloadReq).andExpect(status().isOk()).andReturn();
    assertThat(downloadRes.getResponse().getContentLength()).isGreaterThan(0);

    // Clean up temp file
    FileUtil.del(tempFile);
  }
}
