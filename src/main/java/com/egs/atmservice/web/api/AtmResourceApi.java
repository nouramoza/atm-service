package com.egs.atmservice.web.api;

import com.egs.atmservice.service.AtmService;
import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.dto.GenericRestResponse;
import com.egs.atmservice.web.error.BadRequestAlertException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm-service")
@Api(value = "ATM Emulator API")
public class AtmResourceApi {

    Logger log = LoggerFactory.getLogger(AtmResourceApi.class);
    private AtmService atmService;

    public AtmResourceApi(AtmService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/cardVerification")
    @ApiOperation(value = "REST request to Verify CardDto",
            produces = "Application/JSON", response = CardDto.class, httpMethod = "POST")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            allowEmptyValue = false, paramType = "header", dataTypeClass = HttpHeaders.class, example = "Bearer access_token")
    public GenericRestResponse cardVerification(
            @ApiParam(value = "CardDto Number", required = true)
            @RequestBody CardDto cardDto,
            @RequestHeader HttpHeaders httpHeaders) {
        log.info("REST request to Verify CardDto");
        return atmService.getCardVerification(httpHeaders, cardDto);
    }

    @PostMapping("/cardPinVerification")
    @ApiOperation(value = "REST request to Verify CardDto Pin",
            produces = "Application/JSON", response = CardDto.class, httpMethod = "POST")
    public GenericRestResponse getCardPinVerification(
            @ApiParam(value = "pin number", required = true)
            @RequestBody CardDto cardDto) throws BadRequestAlertException {
        log.debug("REST request to Verify CardDto Pin");
        return atmService.getCardPinVerification(cardDto);
    }

    @PostMapping("/requestManagement")
    @ApiOperation(value = "Client REST request",
            produces = "Application/JSON", response = CardDto.class, httpMethod = "POST")
    public GenericRestResponse requestManagement(
            @ApiParam(value = "requestType and amount if needed", required = true)
            @RequestBody AccountRequestDto accountRequestDto) {
        log.debug("Client REST request");
        return atmService.requestManagement(accountRequestDto);
    }

}
