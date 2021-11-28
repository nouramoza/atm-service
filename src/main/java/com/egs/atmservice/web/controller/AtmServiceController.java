package com.egs.atmservice.web.controller;

import com.egs.atmservice.enums.RequestTypeEnum;
import com.egs.atmservice.service.AtmService;
import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.dto.GenericRestResponse;
import com.egs.atmservice.web.error.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/atm-service")
@Api(value = "ATM Emulator API")
public class AtmServiceController {

    Logger log = LoggerFactory.getLogger(AtmServiceController.class);
    private AtmService atmService;

    public AtmServiceController(AtmService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/cardVerification")
    @ApiOperation(value = "REST request to Verify CardDto",
            produces = "Application/JSON", response = CardDto.class, httpMethod = "POST")
    public GenericRestResponse cardVerification(
            @ApiParam(value = "CardDto Number", required = true)
            @RequestBody CardDto cardDto) {
        log.debug("REST request to Verify CardDto");
        return atmService.getCardVerification(cardDto);
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

    @GetMapping("/getRequestDtoJson")
    @ApiOperation(value = "Client REST request",
            produces = "Application/JSON", response = CardDto.class, httpMethod = "GET")
    public String getRequestDtoJson() throws JsonProcessingException {
        AccountRequestDto accountRequestDto = new AccountRequestDto(RequestTypeEnum.GET_RECEIPT,null,
                "hi", 200L, new Date(), new Date());
        return mapToJson(accountRequestDto);
    }

    protected static String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
