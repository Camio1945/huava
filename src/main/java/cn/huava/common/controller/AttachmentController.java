package cn.huava.common.controller;

import cn.huava.common.mapper.AttachmentMapper;
import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.service.attachment.AceAttachmentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 附件控制器
 *
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common/attachment")
public class AttachmentController
    extends BaseController<AceAttachmentService, AttachmentMapper, AttachmentPo> {

  @PostMapping("/upload")
  public ResponseEntity<AttachmentPo> upload(@NonNull final MultipartHttpServletRequest req) {
    return ResponseEntity.ok(service.upload(req));
  }
}
