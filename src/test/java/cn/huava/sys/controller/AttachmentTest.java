package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.MULTIPART_PARAM_NAME;
import static cn.huava.common.util.ApiTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.controller.AttachmentController;
import cn.huava.common.controller.AttachmentServingController;
import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.util.Fn;
import cn.hutool.v7.json.JSONUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

/**
 * Test the apis in {@link AttachmentController} and {@link AttachmentServingController}. <br>
 * 测试 {@link AttachmentController} 和 {@link AttachmentServingController} 中的接口。<br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S2187")
public class AttachmentTest {

  private static String uploadedUrl = null;

  public static void testAll() throws Exception {
    upload();
    download();
  }

  private static void upload() throws Exception {
    MockMultipartHttpServletRequestBuilder req =
        (MockMultipartHttpServletRequestBuilder)
            initReq().multipart("/common/attachment/upload").build();
    Resource[] resources =
        new PathMatchingResourcePatternResolver().getResources("classpath:static_captcha/*");
    Resource resource = resources[0];
    byte[] bytes = Fn.resourceToBytes(resource);
    MockMultipartFile file =
        new MockMultipartFile(MULTIPART_PARAM_NAME, "head.jpg", "image/jpeg", bytes);
    req.file(file);
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    AttachmentPo attachmentPo = JSONUtil.toBean(resJsonStr, AttachmentPo.class);
    assertNotNull(attachmentPo);
    assertNotNull(attachmentPo.getUrl());
    uploadedUrl = attachmentPo.getUrl();
  }

  private static void download() throws Exception {
    RequestBuilder req =
        initReq().get(uploadedUrl).contentTypeText().acceptType("image/jpeg").build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    assertTrue(res.getResponse().getContentLength() > 0);
  }
}
