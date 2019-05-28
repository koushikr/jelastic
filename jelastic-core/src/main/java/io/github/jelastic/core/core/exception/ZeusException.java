package io.github.jelastic.core.core.exception;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author koushik
 */
@Getter
public class ZeusException extends RuntimeException{

    private final int responseCode;
    private final String code;
    private final Map<String, Serializable> context;

    @Builder
    public ZeusException(ErrorCode errorCode, Map<String, Serializable> context) {
        super();

        this.responseCode = errorCode.getResponseCode();
        this.code = errorCode.getErrorTag();
        this.context = context;
    }

    public static ZeusException error(ErrorCode errorCode) {
        return new ZeusException(errorCode, new HashMap<>());
    }

    public enum ErrorCode {
        BAD_REQUEST(400, "Z000"),
        UNAUTHORIZED(401, "Z001");

        @Getter
        int responseCode;
        @Getter
        String errorTag;

        ErrorCode(int responseCode, String errorTag) {
            this.responseCode = responseCode;
            this.errorTag = errorTag;
        }
    }

}
