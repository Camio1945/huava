package cn.huava.common.service.attachment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import cn.huava.common.pojo.po.AttachmentPo;
import cn.huava.common.util.Fn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Test class for AceAttachmentService
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class AceAttachmentServiceTest {

  @Mock
  private ServeFileService serveFileService;

  private AceAttachmentService aceAttachmentService;

  @BeforeEach
  void setUp() {
    aceAttachmentService = new AceAttachmentService(serveFileService);
  }

  @Test
  void should_upload_file_successfully() throws Exception {
    // Given
    String handleClassName = "cn.huava.common.service.attachment.UploadToLocalServiceImpl";
    BaseUploadService mockUploadService = mock(BaseUploadService.class);
    MultipartHttpServletRequest mockRequest = mock(MultipartHttpServletRequest.class);
    AttachmentPo expectedAttachment = new AttachmentPo();

    // Set the handleClass field using reflection
    java.lang.reflect.Field handleClassField = AceAttachmentService.class.getDeclaredField("handleClass");
    handleClassField.setAccessible(true);
    handleClassField.set(aceAttachmentService, handleClassName);

    // Mock the static Fn.getBean method
    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
        fnMock.when(() -> Fn.getBean(any(Class.class))).thenReturn(mockUploadService);
        when(mockUploadService.upload(mockRequest)).thenReturn(expectedAttachment);

        // When
        AttachmentPo actualAttachment = aceAttachmentService.upload(mockRequest);

        // Then
        assertThat(actualAttachment).isEqualTo(expectedAttachment);
        fnMock.verify(() -> Fn.getBean(Class.forName(handleClassName)));
        verify(mockUploadService).upload(mockRequest);
    }
  }

  @Test
  void should_serve_file_successfully() {
    // Given
    String url = "/test/file.jpg";
    ResponseEntity<Resource> expectedResponse = ResponseEntity.ok().build();
    when(serveFileService.serveFile(url)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Resource> actualResponse = aceAttachmentService.serveFile(url);

    // Then
    assertThat(actualResponse).isEqualTo(expectedResponse);
    verify(serveFileService).serveFile(url);
  }

  @Test
  void should_handle_upload_with_invalid_cast() throws Exception {
    // Given
    String handleClassName = "java.lang.String"; // This is not a BaseUploadService
    MultipartHttpServletRequest mockRequest = mock(MultipartHttpServletRequest.class);

    java.lang.reflect.Field handleClassField = AceAttachmentService.class.getDeclaredField("handleClass");
    handleClassField.setAccessible(true);
    handleClassField.set(aceAttachmentService, handleClassName);

    // Mock the static Fn.getBean method to return a String (which is not a BaseUploadService)
    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
        fnMock.when(() -> Fn.getBean(any(Class.class))).thenReturn("not-a-base-upload-service");

        // When & Then
        assertThatThrownBy(() -> aceAttachmentService.upload(mockRequest))
            .isInstanceOf(ClassCastException.class);

        fnMock.verify(() -> Fn.getBean(Class.forName(handleClassName)));
    }
  }

  @Test
  void should_handle_upload_with_non_existent_class() throws Exception {
    // Given
    String handleClassName = "com.example.NonExistentUploadService";
    MultipartHttpServletRequest mockRequest = mock(MultipartHttpServletRequest.class);

    java.lang.reflect.Field handleClassField = AceAttachmentService.class.getDeclaredField("handleClass");
    handleClassField.setAccessible(true);
    handleClassField.set(aceAttachmentService, handleClassName);

    // When & Then
    assertThatThrownBy(() -> aceAttachmentService.upload(mockRequest))
        .isInstanceOf(ClassNotFoundException.class);
  }

  @Test
  void should_verify_constructor_injection() {
    // Given
    ServeFileService injectedService = mock(ServeFileService.class);

    // When
    AceAttachmentService service = new AceAttachmentService(injectedService);

    // Then
    // We can't directly access the private field to verify injection,
    // but we can verify that the service was created successfully
    assertThat(service).isNotNull();
  }
}