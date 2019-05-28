package io.github.jelastic.core.core.exception;

import com.phonepe.zeus.models.GenericResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author koushik
 */
public class ZeusExceptionMapper implements ExceptionMapper<ZeusException> {
    @Override
    public Response toResponse(ZeusException exception) {
        return Response.status(exception.getResponseCode())
                .entity(
                        GenericResponse.builder()
                                .success(false)
                                .code(exception.getCode())
                                .data(exception.getContext())
                                .build()
                )
                .build();
    }
}
