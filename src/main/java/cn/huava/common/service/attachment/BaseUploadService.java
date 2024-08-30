package cn.huava.common.service.attachment;

import cn.huava.common.mapper.AttachmentMapper;
import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.service.BaseService;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author Camio1945
 */
public abstract class BaseUploadService extends BaseService<AttachmentMapper, AttachmentPo> {

  /**
   * upload file
   *
   * @param req request (must be multipart request)
   * @return
   */
  protected abstract AttachmentPo upload(@NonNull final MultipartHttpServletRequest req);
}
