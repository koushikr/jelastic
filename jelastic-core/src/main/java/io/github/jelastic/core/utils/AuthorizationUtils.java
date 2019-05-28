package io.github.jelastic.core.utils;

import io.github.jelastic.core.core.exception.ZeusException;

/**
 * @author koushik
 */
public interface AuthorizationUtils {

    static void validateUserId(final String xUserId, final String requestedUserId) {
        if (xUserId == null || !xUserId.equalsIgnoreCase(requestedUserId)) {
            throw ZeusException.error(ZeusException.ErrorCode.UNAUTHORIZED);
        }
    }

}
