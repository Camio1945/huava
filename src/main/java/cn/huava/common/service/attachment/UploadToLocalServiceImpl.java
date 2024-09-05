package cn.huava.common.service.attachment;

import static cn.huava.common.constant.CommonConstant.MULTIPART_PARAM_NAME;

import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.util.Fn;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.date.DatePattern;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.io.file.FileNameUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 将附件上传到本地磁盘的文件夹中，文件夹的路径在 application.yml 中定义，见 attachment 相关配置<br>
 * 这个类没有被显式地调用，而是在反射中用到。
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadToLocalServiceImpl extends BaseUploadService {

  @Value("${project.attachment.path}")
  private String attachmentPath;

  @Override
  protected AttachmentPo upload(@NonNull MultipartHttpServletRequest req) {
    MultipartFile file = getMultipartFile(req);
    AttachmentPo attachmentPo = buildAttachmentPo(file);
    String url = saveFile(file);
    attachmentPo.setUrl(url);
    save(attachmentPo);
    return attachmentPo;
  }

  @SuppressWarnings("java:S2637") // SonarLint 检测不到 Assert 的语义，实际上代码没有问题，不会返回 null，所以这里直接忽略
  private static @NonNull MultipartFile getMultipartFile(final MultipartHttpServletRequest req) {
    MultipartFile file = req.getFile(MULTIPART_PARAM_NAME);
    Assert.isTrue(file != null && !file.isEmpty(), "解析不到上传的文件");
    return file;
  }

  private static AttachmentPo buildAttachmentPo(MultipartFile file) {
    return new AttachmentPo()
        .setOriginalName(file.getOriginalFilename())
        .setSize(file.getSize())
        .setHumanSize(FileUtil.readableFileSize(file.getSize()))
        .setCreateTime(new Date());
  }

  private static String getExt(String fileName) {
    String ext = FileNameUtil.extName(fileName);
    ext = Fn.isBlank(ext) ? "" : "." + ext;
    ext = ext.toLowerCase();
    return ext;
  }

  @SneakyThrows(IOException.class)
  private String saveFile(MultipartFile file) {
    String destFilePath = buildFilePath(file.getOriginalFilename());
    file.transferTo(new File(destFilePath));
    File destFile = new File(destFilePath);
    Assert.isTrue(destFile.exists(), "文件保存失败");
    Assert.isTrue(destFile.length() == file.getSize(), "文件保存失败，文件大小不匹配");
    return destFilePath.replace(attachmentPath, "");
  }

  private String buildFilePath(String fileName) {
    if (!Paths.get(attachmentPath).isAbsolute()) {
      attachmentPath = System.getProperty("user.home") + File.separator + attachmentPath;
    }
    attachmentPath = Fn.cleanPath(attachmentPath);
    String ext = getExt(fileName);
    String date = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
    // e.g. : C:/Users/Administrator/.huava/attachment/20240824/39da49c481234228a70bf18d41b3e8ee.jpg
    String folderPath = attachmentPath + File.separator + date;
    FileUtil.mkdir(folderPath);
    return Fn.cleanPath(folderPath + File.separator + IdUtil.fastSimpleUUID() + ext);
  }
}
