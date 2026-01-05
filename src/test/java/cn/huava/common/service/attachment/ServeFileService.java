package cn.huava.common.service.attachment;

import cn.huava.common.mapper.AttachmentMapper;
import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.hutool.v7.core.io.file.FileNameUtil;
import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

/**
 * 提供文件（比如文件头像）。<br>
 * 如果访问的文件是图片，则会在浏览器直接显示图片；如果不是图片，则会下载文件。<br>
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class ServeFileService extends BaseService<AttachmentMapper, AttachmentPo> {

  @Value("${project.attachment.path}")
  private String attachmentPath;

  @SneakyThrows(IOException.class)
  public ResponseEntity<Resource> serveFile(@NonNull final String url) {
    convertAttachmentPathToAbsolutePath();
    AttachmentPo attachmentPo = getAttachmentPo(url);
    String filePath = buildFilePath(url);
    Resource resource = new FileSystemResource(filePath);
    String mimeType = Files.probeContentType(resource.getFile().toPath());
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(filePath, attachmentPo))
        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + getCacheTime())
        .contentType(MediaType.parseMediaType(mimeType))
        .lastModified(getLastModified(filePath).toEpochMilli())
        .body(resource);
  }

  private static long getCacheTime() {
    // 缓存 100 年，以秒为单位
    return 100 * 365 * 24 * 60 * 60L;
  }

  private static Instant getLastModified(String filePath) throws IOException {
    BasicFileAttributes attr = Files.readAttributes(Path.of(filePath), BasicFileAttributes.class);
    return attr.lastModifiedTime().toInstant();
  }

  private void convertAttachmentPathToAbsolutePath() {
    if (!Paths.get(attachmentPath).isAbsolute()) {
      attachmentPath = System.getProperty("user.home") + File.separator + attachmentPath;
    }
    attachmentPath = Fn.cleanPath(attachmentPath);
  }

  private AttachmentPo getAttachmentPo(String url) {
    AttachmentPo attachmentPo =
        getOne(new LambdaQueryWrapper<AttachmentPo>().eq(AttachmentPo::getUrl, url));
    Assert.notNull(attachmentPo, "文件不可访问");
    return attachmentPo;
  }

  private String buildFilePath(String url) {
    String filePath = attachmentPath + url;
    Assert.isTrue(FileUtil.exists(filePath), "文件不存在");
    return filePath;
  }

  private String getContentDisposition(String filePath, AttachmentPo attachmentPo) {
    String contentDispositionPrefix =
        isImage(FileNameUtil.extName(filePath)) ? "inline" : "attachment";
    return contentDispositionPrefix + "; filename=\"" + attachmentPo.getOriginalName() + "\"";
  }

  private boolean isImage(String fileExtension) {
    return switch (fileExtension) {
      case "jpg", "jpeg", "png", "gif", "bmp", "tiff" -> true;
      default -> false;
    };
  }
}
