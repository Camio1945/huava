package cn.huava.common.pojo;

import static cn.huava.common.constant.CommonConstant.AUTHORIZATION_HEADER;
import static cn.huava.common.constant.CommonConstant.BEARER_PREFIX;
import static cn.huava.common.util.ApiTestUtil.accessToken;

import cn.huava.common.util.ApiTestUtil;
import cn.hutool.v7.json.JSONUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ReqBuilder {
  private MockHttpSession session;
  private boolean needToken = true;
  private String contentType;
  private String acceptType;
  private String content;
  private AbstractMockHttpServletRequestBuilder<?> req;

  // =================== 第 1 步，调用以下几个方法之一 =====================

  public ReqBuilder get(@NonNull String urlTemplate, Object... uriVariables) {
    req = MockMvcRequestBuilders.get(urlTemplate, uriVariables);
    return this;
  }

  public ReqBuilder post(@NonNull String urlTemplate, Object... uriVariables) {
    req = MockMvcRequestBuilders.post(urlTemplate, uriVariables);
    return this;
  }

  public ReqBuilder put(@NonNull String urlTemplate, Object... uriVariables) {
    req = MockMvcRequestBuilders.put(urlTemplate, uriVariables);
    return this;
  }

  public ReqBuilder patch(@NonNull String urlTemplate, Object... uriVariables) {
    req = MockMvcRequestBuilders.patch(urlTemplate, uriVariables);
    return this;
  }

  public ReqBuilder delete(@NonNull String urlTemplate, Object... uriVariables) {
    req = MockMvcRequestBuilders.delete(urlTemplate, uriVariables);
    return this;
  }

  /**
   * 参考用法：
   *
   * <pre>
   * MockMultipartHttpServletRequestBuilder req =
   *   (MockMultipartHttpServletRequestBuilder)
   *       initReq().multipart("/common/attachment/upload").build();
   * </pre>
   *
   * @param urlTemplate url
   * @param uriVariables 参数
   * @return MockMultipartHttpServletRequestBuilder
   */
  public ReqBuilder multipart(@NonNull String urlTemplate, Object... uriVariables) {
    req = MockMvcRequestBuilders.multipart(urlTemplate, uriVariables);
    return this;
  }

  // =================== 第 2 步（可选），设置 session，未设置则使用默认值 =====================

  public ReqBuilder session(@NonNull MockHttpSession session) {
    this.session = session;
    return this;
  }

  // =================== 第 3 步（可选），设置是否需要 token，默认是需要的 =====================

  public ReqBuilder needToken(boolean needToken) {
    this.needToken = needToken;
    return this;
  }

  // =================== 第 4 步（可选），设置请求的 Content-Type，默认是 application/json;charset=UTF-8
  // ==========

  public ReqBuilder contentType(@NonNull String contentType) {
    this.contentType = contentType;
    return this;
  }

  public ReqBuilder contentTypeText() {
    this.contentType = "text/plain;charset=UTF-8";
    return this;
  }

  // =================== 第 5 步（可选），设置接受什么类型的返回数据 默认是 application/json;charset=UTF-8 ==========

  public ReqBuilder acceptType(@NonNull String acceptType) {
    this.acceptType = acceptType;
    return this;
  }

  // =================== 第 6 步（可选），设置请求体内容 ==========

  public ReqBuilder content(String content) {
    this.content = content;
    return this;
  }

  public ReqBuilder contentJson(Object obj) {
    this.content = JSONUtil.toJsonStr(obj);
    return this;
  }

  // =================== 第 7 步，执行构建 ==========
  public AbstractMockHttpServletRequestBuilder<?> build() {
    if (this.session == null) {
      req.session(ApiTestUtil.session);
    }
    if (this.needToken) {
      req.header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
    }
    if (this.contentType == null) {
      req.contentType("application/json;charset=UTF-8");
    }
    if (this.acceptType == null) {
      req.accept("application/json;charset=UTF-8");
    }
    if (this.content != null) {
      req.content(this.content);
    }
    return req;
  }
}
