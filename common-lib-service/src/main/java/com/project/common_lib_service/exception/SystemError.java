package com.project.common_lib_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SystemError implements AbstractError {
    ERROR_001(400, "ERROR-001", HttpStatus.BAD_REQUEST), // Field must not be blank
    ERROR_002(400, "ERROR-002", HttpStatus.BAD_REQUEST), // Field must not be empty
    ERROR_003(400, "ERROR-003", HttpStatus.BAD_REQUEST), // Field must be null
    ERROR_004(400, "ERROR-004", HttpStatus.BAD_REQUEST), // Field cannot be null

    ERROR_005(401, "ERROR-005", HttpStatus.UNAUTHORIZED), // LOGIN EXPIRED
    ERROR_006(401, "ERROR-006", HttpStatus.UNAUTHORIZED), // UNAUTHORIZED
    ERROR_007(401, "ERROR-007", HttpStatus.UNAUTHORIZED), // Invalid Username or Password
    ERROR_008(403, "ERROR-008", HttpStatus.FORBIDDEN),    // Permission denied

    ERROR_009(400, "ERROR-009", HttpStatus.BAD_REQUEST), // Data has many errors
    ERROR_010(404, "ERROR-010", HttpStatus.NOT_FOUND),   // Data not found
    ERROR_011(404, "ERROR-011", HttpStatus.NOT_FOUND),   // Username not registered
    ERROR_012(401, "ERROR-012", HttpStatus.UNAUTHORIZED),// Incorrect username or password
    ERROR_013(403, "ERROR-013", HttpStatus.FORBIDDEN),   // Account deactivated

    ERROR_014(400, "ERROR-014", HttpStatus.BAD_REQUEST), // Field must be between {min} and {max}
    ERROR_015(409, "ERROR-015", HttpStatus.CONFLICT),    // Username existed
    ERROR_016(409, "ERROR-016", HttpStatus.CONFLICT),    // Email existed
    ERROR_017(400, "ERROR-017", HttpStatus.BAD_REQUEST), // Password and confirm password do not match
    ERROR_018(401, "ERROR-018", HttpStatus.UNAUTHORIZED),// Incorrect password
    ERROR_500(500, "ERROR-500", HttpStatus.INTERNAL_SERVER_ERROR),//Internal server error. Please try again later or contact support.
    ERROR_019(401, "ERROR-019", HttpStatus.UNAUTHORIZED), // Token expired
    ERROR_020(400,"ERROR-020", HttpStatus.NOT_FOUND),
    ERROR_021(400,"ERROR-021", HttpStatus.BAD_REQUEST),
    ERROR_022(400,"ERROR-022", HttpStatus.NOT_FOUND),
    ERROR_023(400,"ERROR-023", HttpStatus.NOT_FOUND),
    ERROR_024(400,"ERROR-024", HttpStatus.NOT_FOUND),
    ERROR_025(400,"ERROR-025", HttpStatus.NOT_FOUND),
    ERROR_026(400,"ERROR-026", HttpStatus.NOT_FOUND),
    ERROR_027(400,"ERROR-027", HttpStatus.NOT_FOUND),
    ERROR_028(400,"ERROR-028", HttpStatus.NOT_FOUND),
    ERROR_029(400,"ERROR-029", HttpStatus.NOT_FOUND),
    ERROR_030(400,"ERROR-030", HttpStatus.NOT_FOUND);



    private final int code;
    private final String errorCode;
    private final HttpStatus httpStatus;


    SystemError(int code, String errorCode, HttpStatus httpStatus) {
        this.code = code;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
