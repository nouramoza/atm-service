package com.egs.atmservice.service.impl;

import com.egs.atmservice.enums.RequestTypeEnum;
import com.egs.atmservice.service.AtmService;
import com.egs.atmservice.util.ConstantsUtil;
import com.egs.atmservice.util.ObjectMapperUtils;
import com.egs.atmservice.util.externalserviceclient.BankServiceClient;
import com.egs.atmservice.web.dto.AccountDto;
import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.dto.GenericRestResponse;
import com.egs.atmservice.web.dto.external.response.BankRestResponse;
import com.egs.atmservice.web.error.BadRequestAlertException;
import com.egs.atmservice.web.error.CardNotFoundException;
import com.egs.atmservice.web.error.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Service Implementation for ATM-Service .
 */

@Service
public class AtmServiceImpl implements AtmService {
    Logger log = LoggerFactory.getLogger(AtmServiceImpl.class);
    private static final String CARD_DTO = "cardDto";

    BankServiceClient bankServiceClient;
    HttpSession httpSession;

    public AtmServiceImpl(HttpSession httpSession, BankServiceClient bankServiceClient) {
        this.bankServiceClient = bankServiceClient;
        this.httpSession = httpSession;
    }

    /**
     * Card Number Validation
     *
     * @param cardDto CardDto with cardDto number
     * @return GenericRestResponse Bank Service response
     */
    @Override
    public GenericRestResponse<CardDto> getCardVerification(HttpHeaders httpHeaders, CardDto cardDto) {

        try {
            httpSession.setAttribute(ConstantsUtil.SessionKey.JWT, bankServiceClient.loginToBankService(httpHeaders));
                ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                    bankServiceClient.validateCardNumber(cardDto.getCardNumber(), httpSession.getAttribute(ConstantsUtil.SessionKey.JWT).toString());
            httpSession.setAttribute(ConstantsUtil.SessionKey.CARD_NUMBER, cardDto.getCardNumber());

            BankRestResponse body = getBodyFromBankResponse(bankRestResponseResponseEntity);
            CardDto resultCardDto = getCardFromBankResponse(body);

            log.error("Response: {}, for cardNumber: {}, ResultDto: {}", body.getMessage(), resultCardDto.getCardNumber(), resultCardDto);
            return new GenericRestResponse<>(body.getStatus(), body.getMessage(), ObjectMapperUtils.map(resultCardDto, CardDto.class));
        } catch (CardNotFoundException e) {
            log.error("Response: {}, for cardNumber: {}, Cause: {}", ErrorConstants.CardVerificationMessage.CARD_NOT_FOUND_MSG, cardDto.getCardNumber(), e.getMessage());
            return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE, e.getMessage(), cardDto);
        } catch (Exception e) {
            log.error("Response: {}, for cardNumber: {}, Cause: {}", ErrorConstants.CardVerificationMessage.EXCEPTION, cardDto.getCardNumber(), e.getMessage());
            return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE,
                    e.getMessage() != null ? e.getMessage() : Arrays.toString(e.getStackTrace()), cardDto);
        }
    }
    @Override
    public GenericRestResponse getCardPinVerification(CardDto cardDto) throws BadRequestAlertException {
        try {
            if (httpSession.getAttribute(ConstantsUtil.SessionKey.CARD_NUMBER) == null) {
                return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE, ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG);
            }
            cardDto.setCardNumber(String.valueOf(httpSession.getAttribute(ConstantsUtil.SessionKey.CARD_NUMBER)));
            ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                    bankServiceClient.validateCardPinNumber(cardDto, httpSession.getAttribute(ConstantsUtil.SessionKey.JWT).toString());

            BankRestResponse body = getBodyFromBankResponse(bankRestResponseResponseEntity);
            CardDto resultCardDto = getCardFromBankResponse(body);

            if (body.getStatus() == BankRestResponse.STATUS.FAILURE) {
                throw new BadRequestAlertException(body.getMessage(), CARD_DTO, ErrorConstants.CardVerificationMessage.WRONG_PIN_KEY);
            }
            httpSession.setAttribute(ConstantsUtil.SessionKey.PIN_ACCEPTED, ResponseEntity.ok().body(bankRestResponseResponseEntity).getStatusCodeValue() == HttpStatus.OK.value());
            return new GenericRestResponse<>(body.getStatus(), body.getMessage(), resultCardDto);

        } catch (CardNotFoundException e) {
            log.error(e.getMessage());
            return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE, e.getMessage(), cardDto);
        } catch (Exception e) {
            log.error(e.getMessage() != null ? e.getMessage() : Arrays.toString(e.getStackTrace()));
            return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE,
                    e.getMessage() != null ? e.getMessage() : Arrays.toString(e.getStackTrace()), cardDto);
        }
    }

    @Override
    public GenericRestResponse requestManagement(AccountRequestDto accountRequestDto) {
        try {
            if (httpSession.getAttribute(ConstantsUtil.SessionKey.CARD_NUMBER) == null) {
                return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE, ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG);
            }
            if (Objects.equals(Boolean.FALSE, httpSession.getAttribute(ConstantsUtil.SessionKey.PIN_ACCEPTED))) {
                return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE, ErrorConstants.CardVerificationMessage.WRONG_PIN_MSG);
            }
            requestInputValidation(accountRequestDto);
            setInputValues(accountRequestDto);

            ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                    bankServiceClient.requestManagement(accountRequestDto, httpSession.getAttribute(ConstantsUtil.SessionKey.JWT).toString());
            BankRestResponse body = getBodyFromBankResponse(bankRestResponseResponseEntity);
            AccountDto resultAccountDto = getAccountFromBankResponse(body);
            return new GenericRestResponse<>(body.getStatus(), body.getMessage(), resultAccountDto);
        } catch (CardNotFoundException e) {
            log.error(e.getMessage());
            return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE, e.getMessage(), accountRequestDto);
        } catch (Exception e) {
            log.error(e.getMessage() != null ? e.getMessage() : Arrays.toString(e.getStackTrace()));
            return new GenericRestResponse<>(BankRestResponse.STATUS.FAILURE,
                    e.getMessage() != null ? e.getMessage() : Arrays.toString(e.getStackTrace()), accountRequestDto);
        }
    }

    private BankRestResponse getBodyFromBankResponse(ResponseEntity<BankRestResponse> bankRestResponseResponseEntity) throws CardNotFoundException {
        BankRestResponse body = bankRestResponseResponseEntity.getBody();
        if (body == null) {
            throw new CardNotFoundException(ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG,
                    "Card", ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_KEY);
        }
        return body;
    }
    private CardDto getCardFromBankResponse(BankRestResponse body) throws CardNotFoundException {
        if (body.getData() == null) {
            throw new CardNotFoundException(ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG,
                    "Card", ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_KEY);
        }
        return ObjectMapperUtils.map(body.getData(), CardDto.class);
    }

    private AccountDto getAccountFromBankResponse(BankRestResponse body) throws CardNotFoundException {
        if (body.getData() == null) {
            throw new CardNotFoundException(ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG,
                    "Card", ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_KEY);
        }
        return ObjectMapperUtils.map(body.getData(), AccountDto.class);
    }

    private void requestInputValidation(AccountRequestDto accountRequestDto) throws BadRequestAlertException {
        if ((accountRequestDto.getRequestType().equals(RequestTypeEnum.WITHDRAW) ||
                accountRequestDto.getRequestType().equals(RequestTypeEnum.DEPOSIT)) &&
                accountRequestDto.getAmount() == null) {
            throw new BadRequestAlertException(ErrorConstants.ReceiptMessage.INVALID_AMOUNT_MSG, CARD_DTO, ErrorConstants.ReceiptMessage.INVALID_AMOUNT_KEY);
        }

        if (accountRequestDto.getRequestType().equals(RequestTypeEnum.GET_RECEIPT) &&
                (accountRequestDto.getFromDate() == null ||
                        accountRequestDto.getToDate() == null ||
                        accountRequestDto.getFromDate().after(accountRequestDto.getToDate()) ||
                        accountRequestDto.getToDate().after(new Date()))) {
            throw new BadRequestAlertException(ErrorConstants.ReceiptMessage.DATE_NOT_VALID_MSG, CARD_DTO, ErrorConstants.ReceiptMessage.DATE_NOT_VALID_KEY);
        }
    }

    private void setInputValues(AccountRequestDto accountRequestDto) throws ParseException {
        accountRequestDto.setCardNumber(String.valueOf(httpSession.getAttribute(ConstantsUtil.SessionKey.CARD_NUMBER)));

        if (accountRequestDto.getRequestType().equals(RequestTypeEnum.GET_RECEIPT)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd");
            accountRequestDto.setFromDate(simpleDateFormat.parse(simpleDateFormat.format(accountRequestDto.getFromDate())));
            Calendar c = Calendar.getInstance();
            c.setTime(accountRequestDto.getToDate());
            c.add(Calendar.DATE, 1);
            accountRequestDto.setToDate(c.getTime());
        }
    }


}