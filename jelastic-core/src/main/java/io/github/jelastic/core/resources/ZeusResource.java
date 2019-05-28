package io.github.jelastic.core.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.phonepe.platform.requestinfo.annotations.RequestContext;
import com.phonepe.platform.requestinfo.models.RequestInfo;
import io.github.jelastic.core.config.ZeusConfiguration;
import io.github.jelastic.core.core.client.ZeusClient;
import io.github.jelastic.core.utils.AuthorizationUtils;
import com.phonepe.zeus.models.GenericResponse;
import com.phonepe.zeus.models.PinningRequest;
import com.phonepe.zeus.models.PinningResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author koushik
 */
@Path("/v1/destination")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
@Api(value = "Resource to handle destination pinning related functions")
public class ZeusResource {

    private final ZeusClient zeusClient;

    public ZeusResource(ZeusClient zeusClient){
        this.zeusClient = zeusClient;
    }

    //Possibly integrate this with Gandalf to make sure we have clientId and permissions and not everyone
    //can access everything!
    @POST
    @Path("/details")
    @ExceptionMetered
    @Timed
    @ApiOperation(value = "Get the respective destination details for a given pinning request ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GenericResponse.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse<PinningResponse> initiate(
            @ApiParam(hidden = true) @RequestContext RequestInfo requestInfo,
            @Valid PinningRequest pinningRequest) {
        AuthorizationUtils.validateUserId(
                requestInfo.getAuthorizeForId(),
                pinningRequest.getUserId()
        );

        return GenericResponse.<PinningResponse>builder()
                .success(true)
                .data(
                        zeusClient.getPinningResponse(
                                pinningRequest.getPinningKey()
                        )
                )
                .build();
    }

    @GET
    @Path("/all")
    @ExceptionMetered
    @Timed
    @ApiOperation(value = "Get whatever is the registered zeus config")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ZeusConfiguration.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public ZeusConfiguration getConfiguration(){
        return zeusClient.getConfiguration();
    }
}
