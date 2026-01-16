package cn.huava.common.service.attachment;

import static cn.huava.common.constant.CommonConstant.MULTIPART_PARAM_NAME;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import cn.huava.common.pojo.po.AttachmentPo;
import cn.hutool.v7.core.io.file.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Test class for UploadToLocalServiceImpl
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class UploadToLocalServiceImplTest {

  @Mock private HttpServletRequest mockRequest;

  private UploadToLocalServiceImpl uploadService;

  @BeforeEach
  void setUp() throws Exception {
    uploadService = new UploadToLocalServiceImpl();
    // Use reflection to set the attachment path for testing
    Field attachmentPathField = UploadToLocalServiceImpl.class.getDeclaredField("attachmentPath");
    attachmentPathField.setAccessible(true);
    attachmentPathField.set(uploadService, "/tmp/test_attachments");
  }

  @Test
  void should_upload_file_successfully() throws Exception {
    // Given
    MockMultipartFile mockFile =
        new MockMultipartFile(
            MULTIPART_PARAM_NAME, "test.txt", "text/plain", "test content".getBytes());

    MultipartHttpServletRequest multipartRequest = mock(MultipartHttpServletRequest.class);
    when(multipartRequest.getFile(MULTIPART_PARAM_NAME)).thenReturn(mockFile);

    // Mock the save method using spy
    UploadToLocalServiceImpl spyUploadService = spy(uploadService);
    doReturn(true).when(spyUploadService).save(any(AttachmentPo.class)); // save returns boolean

    // When
    AttachmentPo result = spyUploadService.upload(multipartRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getOriginalName()).isEqualTo("test.txt");
    assertThat(result.getSize()).isEqualTo(12L); // Length of "test content"
    assertThat(result.getHumanSize()).isEqualTo(FileUtil.readableFileSize(12L));
    assertThat(result.getCreateTime()).isNotNull();
    assertThat(result.getUrl()).isNotEmpty();
  }

  @Test
  void should_get_multipart_file_successfully() throws Exception {
    // Given
    MockMultipartFile mockFile =
        new MockMultipartFile(
            MULTIPART_PARAM_NAME, "test.txt", "text/plain", "test content".getBytes());

    MultipartHttpServletRequest multipartRequest = mock(MultipartHttpServletRequest.class);
    when(multipartRequest.getFile(MULTIPART_PARAM_NAME)).thenReturn(mockFile);

    // Access the private method using reflection
    Method getMultipartFileMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod(
            "getMultipartFile", MultipartHttpServletRequest.class);
    getMultipartFileMethod.setAccessible(true);

    // When
    MultipartFile result = (MultipartFile) getMultipartFileMethod.invoke(null, multipartRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getOriginalFilename()).isEqualTo("test.txt");
    assertThat(result.getSize()).isEqualTo(12L);
  }

  @Test
  void should_throw_exception_when_file_is_null() throws Exception {
    // Given
    MultipartHttpServletRequest multipartRequest = mock(MultipartHttpServletRequest.class);
    when(multipartRequest.getFile(MULTIPART_PARAM_NAME)).thenReturn(null);

    // Access the private method using reflection
    Method getMultipartFileMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod(
            "getMultipartFile", MultipartHttpServletRequest.class);
    getMultipartFileMethod.setAccessible(true);

    // When & Then
    assertThatThrownBy(() -> getMultipartFileMethod.invoke(null, multipartRequest))
        .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
        .cause()
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("解析不到上传的文件");
  }

  @Test
  void should_throw_exception_when_file_is_empty() throws Exception {
    // Given
    MockMultipartFile emptyFile =
        new MockMultipartFile(MULTIPART_PARAM_NAME, "empty.txt", "text/plain", new byte[0]);

    MultipartHttpServletRequest multipartRequest = mock(MultipartHttpServletRequest.class);
    when(multipartRequest.getFile(MULTIPART_PARAM_NAME)).thenReturn(emptyFile);

    // Access the private method using reflection
    Method getMultipartFileMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod(
            "getMultipartFile", MultipartHttpServletRequest.class);
    getMultipartFileMethod.setAccessible(true);

    // When & Then
    assertThatThrownBy(() -> getMultipartFileMethod.invoke(null, multipartRequest))
        .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
        .cause()
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("解析不到上传的文件");
  }

  @Test
  void should_build_attachment_po_correctly() throws Exception {
    // Given
    MockMultipartFile mockFile =
        new MockMultipartFile(
            MULTIPART_PARAM_NAME, "test.txt", "text/plain", "test content".getBytes());

    // Access the private method using reflection
    Method buildAttachmentPoMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod("buildAttachmentPo", MultipartFile.class);
    buildAttachmentPoMethod.setAccessible(true);

    // When
    AttachmentPo result = (AttachmentPo) buildAttachmentPoMethod.invoke(null, mockFile);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getOriginalName()).isEqualTo("test.txt");
    assertThat(result.getSize()).isEqualTo(12L);
    assertThat(result.getHumanSize()).isEqualTo(FileUtil.readableFileSize(12L));
    assertThat(result.getCreateTime()).isNotNull();
  }

  @Test
  void should_save_file_successfully() throws Exception {
    // Given
    MockMultipartFile mockFile =
        new MockMultipartFile(
            MULTIPART_PARAM_NAME, "test.txt", "text/plain", "test content".getBytes());

    // Create a temporary directory to simulate saving
    File tempDir = File.createTempFile("test_dir_", "");
    tempDir.delete(); // Delete the file to make it a directory
    tempDir.mkdirs(); // Create as directory
    tempDir.deleteOnExit();

    // Access the private method using reflection
    Method saveFileMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod("saveFile", MultipartFile.class);
    saveFileMethod.setAccessible(true);

    // Access the attachmentPath field using reflection
    Field attachmentPathField = UploadToLocalServiceImpl.class.getDeclaredField("attachmentPath");
    attachmentPathField.setAccessible(true);

    try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
      mockedFileUtil
          .when(() -> FileUtil.mkdir(anyString()))
          .thenAnswer(
              invocation -> {
                // Return a File object
                String path = invocation.getArgument(0);
                File dir = new File(path);
                dir.mkdirs(); // Ensure directory exists
                return dir;
              });

      // Set the attachment path to the temp directory
      attachmentPathField.set(uploadService, tempDir.getAbsolutePath());

      // When
      String resultPath = (String) saveFileMethod.invoke(uploadService, mockFile);

      // Then
      assertThat(resultPath).isNotNull();
      // Since the method executed without throwing an exception, it was successful
    } finally {
      // Clean up the temp directory
      tempDir.delete();
    }
  }

  @Test
  void should_build_file_path_with_absolute_path() throws Exception {
    // Given
    String fileName = "test.txt";

    // Access the private method using reflection
    Method buildFilePathMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod("buildFilePath", String.class);
    buildFilePathMethod.setAccessible(true);

    // Access the attachmentPath field using reflection
    Field attachmentPathField = UploadToLocalServiceImpl.class.getDeclaredField("attachmentPath");
    attachmentPathField.setAccessible(true);
    attachmentPathField.set(uploadService, "/absolute/path");

    try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
        MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {

      java.nio.file.Path mockPath = mock(java.nio.file.Path.class);
      mockedPaths.when(() -> Paths.get("/absolute/path")).thenReturn(mockPath);
      when(mockPath.isAbsolute()).thenReturn(true);
      mockedFileUtil
          .when(() -> FileUtil.mkdir(anyString()))
          .thenAnswer(
              invocation -> {
                // Return a File object
                String path = invocation.getArgument(0);
                return new File(path);
              });

      // When
      String resultPath = (String) buildFilePathMethod.invoke(uploadService, fileName);

      // Then
      assertThat(resultPath).isNotNull();
      assertThat(resultPath).contains("/absolute/path");
      assertThat(resultPath).endsWith(".txt"); // Extension preserved
    }
  }

  @Test
  void should_build_file_path_with_relative_path() throws Exception {
    // Given
    String fileName = "test.txt";

    // Access the private method using reflection
    Method buildFilePathMethod =
        UploadToLocalServiceImpl.class.getDeclaredMethod("buildFilePath", String.class);
    buildFilePathMethod.setAccessible(true);

    // Access the attachmentPath field using reflection
    Field attachmentPathField = UploadToLocalServiceImpl.class.getDeclaredField("attachmentPath");
    attachmentPathField.setAccessible(true);
    attachmentPathField.set(uploadService, "relative/path");

    // Temporarily set the user.home system property for this test
    String originalHome = System.getProperty("user.home");
    System.setProperty("user.home", "/home/user");

    try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
        MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {

      java.nio.file.Path mockPath = mock(java.nio.file.Path.class);
      mockedPaths.when(() -> Paths.get("relative/path")).thenReturn(mockPath);
      when(mockPath.isAbsolute()).thenReturn(false);
      mockedFileUtil
          .when(() -> FileUtil.mkdir(anyString()))
          .thenAnswer(
              invocation -> {
                // Return a File object
                String path = invocation.getArgument(0);
                return new File(path);
              });

      // When
      String resultPath = (String) buildFilePathMethod.invoke(uploadService, fileName);

      // Then
      assertThat(resultPath).isNotNull();
      assertThat(resultPath).contains("/home/user/relative/path");
      assertThat(resultPath).endsWith(".txt"); // Extension preserved
    } finally {
      // Restore the original user.home property
      if (originalHome != null) {
        System.setProperty("user.home", originalHome);
      } else {
        System.clearProperty("user.home");
      }
    }
  }

  @Test
  void should_handle_file_with_no_extension() throws Exception {
    // Given
    String fileName = "testfile"; // No extension

    // Access the private method using reflection
    Method getExtMethod = UploadToLocalServiceImpl.class.getDeclaredMethod("getExt", String.class);
    getExtMethod.setAccessible(true);

    // When
    String result = (String) getExtMethod.invoke(uploadService, fileName);

    // Then
    assertThat(result).isEqualTo("");
  }

  @Test
  void should_handle_file_with_extension() throws Exception {
    // Given
    String fileName = "test.txt";

    // Access the private method using reflection
    Method getExtMethod = UploadToLocalServiceImpl.class.getDeclaredMethod("getExt", String.class);
    getExtMethod.setAccessible(true);

    // When
    String result = (String) getExtMethod.invoke(uploadService, fileName);

    // Then
    assertThat(result).isEqualTo(".txt");
  }

  @Test
  void should_convert_extension_to_lowercase() throws Exception {
    // Given
    String fileName = "TEST.TXT";

    // Access the private method using reflection
    Method getExtMethod = UploadToLocalServiceImpl.class.getDeclaredMethod("getExt", String.class);
    getExtMethod.setAccessible(true);

    // When
    String result = (String) getExtMethod.invoke(uploadService, fileName);

    // Then
    assertThat(result).isEqualTo(".txt"); // Lowercase conversion
  }

  @Test
  void should_handle_null_filename() throws Exception {
    // Given
    String fileName = null;

    // Access the private method using reflection
    Method getExtMethod = UploadToLocalServiceImpl.class.getDeclaredMethod("getExt", String.class);
    getExtMethod.setAccessible(true);

    // When
    String result = (String) getExtMethod.invoke(uploadService, fileName);

    // Then
    assertThat(result).isEqualTo("");
  }

  @Test
  void should_handle_empty_filename() throws Exception {
    // Given
    String fileName = "";

    // Access the private method using reflection
    Method getExtMethod = UploadToLocalServiceImpl.class.getDeclaredMethod("getExt", String.class);
    getExtMethod.setAccessible(true);

    // When
    String result = (String) getExtMethod.invoke(uploadService, fileName);

    // Then
    assertThat(result).isEqualTo("");
  }

  @Test
  void should_throw_ioexception_when_file_transfer_fails() throws Exception {
    // Given
    MockMultipartFile mockFile = new MockMultipartFile(
        MULTIPART_PARAM_NAME, "test.txt", "text/plain", "test content".getBytes());

    // Create a temporary directory to simulate saving
    File tempDir = File.createTempFile("test_dir_", "");
    tempDir.delete(); // Delete the file to make it a directory
    tempDir.mkdirs(); // Create as directory
    tempDir.deleteOnExit();

    // Access the private method using reflection
    Method saveFileMethod = UploadToLocalServiceImpl.class.getDeclaredMethod("saveFile", MultipartFile.class);
    saveFileMethod.setAccessible(true);

    // Access the attachmentPath field using reflection
    Field attachmentPathField = UploadToLocalServiceImpl.class.getDeclaredField("attachmentPath");
    attachmentPathField.setAccessible(true);

    // Create a spy on the mock file to throw an IOException during transferTo
    MockMultipartFile spyFile = spy(mockFile);
    doThrow(new IOException("Simulated IO error")).when(spyFile).transferTo(any(File.class));

    try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
      mockedFileUtil.when(() -> FileUtil.mkdir(anyString())).thenAnswer(invocation -> {
        // Return a File object
        String path = invocation.getArgument(0);
        File dir = new File(path);
        dir.mkdirs(); // Ensure directory exists
        return dir;
      });

      // Set the attachment path to the temp directory
      attachmentPathField.set(uploadService, tempDir.getAbsolutePath());

      // When & Then
      assertThatThrownBy(() -> saveFileMethod.invoke(uploadService, spyFile))
          .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
          .cause()
          .isInstanceOf(IOException.class)
          .hasMessageContaining("Simulated IO error");
    } finally {
      // Clean up the temp directory
      tempDir.delete();
    }
  }
}
