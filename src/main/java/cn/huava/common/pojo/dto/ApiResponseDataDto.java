package cn.huava.common.pojo.dto;

import lombok.Data;

/**
 * corresponds to the data structure defined in the frontend project <br>
 * in file types/api.d.ts, has something like this: <br>
 *
 * <pre>
 * interface ApiResponseData<T> {
 *   code: number
 *   data: T
 *   message: string
 * }
 * </pre>
 *
 * @author Camio1945
 */
@Data
public class ApiResponseDataDto<T> {
  private int code;
  private T data;
  private String message;

  public ApiResponseDataDto() {
  }

  public ApiResponseDataDto(T data) {
    this.data = data;
  }

  public ApiResponseDataDto(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }
}
