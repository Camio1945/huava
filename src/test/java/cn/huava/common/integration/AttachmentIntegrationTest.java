package cn.huava.common.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.util.Fn;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Camio1945
 */
class AttachmentIntegrationTest extends BaseTest {

  @Test
  void integrationTest() throws IOException {
    login();
    // upload();
    download();
  }

  void upload() throws IOException {
    File file = new File("C:\\Users\\Administrator\\Desktop\\BeforeDeleteValidator.java");
    Map<String, Object> formMap = Map.of("file", file);
    Request req = HttpUtil.createPost(BASE_URL + "/common/attachment/upload").form(formMap);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      assertEquals(200, resp.getStatus());
      String body = resp.bodyStr();
      TypeReference<AttachmentPo> type = new TypeReference<>() {};
      AttachmentPo po = JSONUtil.toBean(body, type);
      assertEquals("BeforeDeleteValidator.java", po.getOriginalName());
      assertEquals(file.length(), po.getSize());
      assertTrue(Fn.isNotBlank(po.getUrl()));
    }
  }
  void download() throws IOException {
    File file = new File("C:\\Users\\Administrator\\Desktop\\BeforeDeleteValidator.java");
    Request req = HttpUtil.createGet(BASE_URL + "/20240824/985d124c52a38fb1985d124c52a38fb1.java");
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      assertEquals(200, resp.getStatus());
      File tempFile = FileUtil.createTempFile();
      FileUtil.writeFromStream(resp.bodyStream(), tempFile);
      assertEquals(tempFile.length(), file.length());
    }
  }
}
