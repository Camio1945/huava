package cn.huava.common.service.attachment;

import cn.huava.common.mapper.AttachmentMapper;
import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 附件服务主入口类
 *
 * @author Camio1945
 */
@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class AceAttachmentService extends BaseService<AttachmentMapper, AttachmentPo> {
  private final ServeFileService serveFileService;

  @Value("${project.attachment.handle_class}")
  private String handleClass;

  @SneakyThrows(ClassNotFoundException.class)
  public AttachmentPo upload(final MultipartHttpServletRequest req) {
    Class<?> clazz = Class.forName(handleClass);
    BaseUploadService uploadService = (BaseUploadService) Fn.getBean(clazz);
    return uploadService.upload(req);
  }

  public ResponseEntity<Resource> serveFile(final String url) {
    return serveFileService.serveFile(url);
  }
}
