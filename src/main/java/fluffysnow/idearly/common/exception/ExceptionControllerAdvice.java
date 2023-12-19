package fluffysnow.idearly.common.exception;


import fluffysnow.idearly.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "fluffysnow.idearly")
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse badRequest(Exception e) {

        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse forbidden(Exception e) {

        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse notFound(Exception e) {

        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse unauthorized(Exception e) {

        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(TeamNameDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse duplicateTeamName(Exception e) {

        return ApiResponse.fail(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse serverError(Exception e) {

        return ApiResponse.fail(e.getMessage());
    }
}
