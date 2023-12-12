package fluffysnow.idearly.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> implements Serializable {

    private ApiResponseStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> ApiResponse<T> ok(T result) {
        return new ApiResponse<>(ApiResponseStatus.SUCCESS, null, result);
    }

    public static ApiResponse fail(String message) {
        return new ApiResponse(ApiResponseStatus.FAIL, message, null);
    }

}
