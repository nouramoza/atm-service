package com.egs.atmservice.service.impl;

import com.egs.atmservice.enums.RequestTypeEnum;
import com.egs.atmservice.service.AtmService;
import com.egs.atmservice.util.ConstantsUtil;
import com.egs.atmservice.util.externalServiceClient.BankServiceClient;
import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.dto.GenericRestResponse;
import com.egs.atmservice.web.dto.externalService.response.BankRestResponse;
import com.egs.atmservice.web.error.BadRequestAlertException;
import com.egs.atmservice.web.error.ErrorConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Service Implementation for ATM-Service .
 */

@Service
public class AtmServiceImpl implements AtmService {
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
    public GenericRestResponse getCardVerification(CardDto cardDto) {

        try {
            httpSession.setAttribute(ConstantsUtil.SessionKey.JWT, bankServiceClient.loginToBankService());
            ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                    bankServiceClient.validateCardNumber(cardDto.getCardNumber(), httpSession.getAttribute(ConstantsUtil.SessionKey.JWT).toString());
            httpSession.setAttribute(ConstantsUtil.SessionKey.CARD_NUMBER, cardDto.getCardNumber());
            return new GenericRestResponse(bankRestResponseResponseEntity.getStatusCodeValue(),
                    bankRestResponseResponseEntity.getBody().getMessage());
        } catch (Exception e) {
            return new GenericRestResponse(BankRestResponse.STATUS.FAILURE,
                    e.getMessage() != null ? e.getMessage() : e.getStackTrace().toString());
        }
    }

    @Override
    public GenericRestResponse getCardPinVerification(CardDto cardDto) throws BadRequestAlertException {
        cardDto.setCardNumber(String.valueOf(httpSession.getAttribute(ConstantsUtil.SessionKey.CARD_NUMBER)));
        ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                bankServiceClient.validateCardPinNumber(cardDto, httpSession.getAttribute(ConstantsUtil.SessionKey.JWT).toString());
        if (bankRestResponseResponseEntity.getBody().getStatus() == BankRestResponse.STATUS.FAILURE) {
            throw new BadRequestAlertException(bankRestResponseResponseEntity.getBody().getMessage(), CARD_DTO, ErrorConstants.CardVerificationMessage.WRONG_PIN_KEY);
        }
        return new GenericRestResponse(bankRestResponseResponseEntity.getStatusCodeValue(), bankRestResponseResponseEntity.getBody().getMessage());
    }

    @Override
    public GenericRestResponse requestManagement(AccountRequestDto accountRequestDto) {
        try {
            requestInputValidation(accountRequestDto);
            setInputValues(accountRequestDto);

            ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                    bankServiceClient.requestManagement(accountRequestDto, httpSession.getAttribute(ConstantsUtil.SessionKey.JWT).toString());
            return new GenericRestResponse(bankRestResponseResponseEntity.getStatusCodeValue(), bankRestResponseResponseEntity.getBody().getMessage());
        } catch (Exception e) {
            return new GenericRestResponse(BankRestResponse.STATUS.FAILURE,
                    e.getMessage() != null ? e.getMessage() : e.getStackTrace().toString());
        }
    }

    private void requestInputValidation(AccountRequestDto accountRequestDto) throws BadRequestAlertException {
        if (accountRequestDto.getRequestType() == null) {
            throw new BadRequestAlertException(ErrorConstants.ReceiptMessage.INVALID_REQ_TYPE_MSG, CARD_DTO, ErrorConstants.ReceiptMessage.INVALID_REQ_TYPE_KEY);
        }
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