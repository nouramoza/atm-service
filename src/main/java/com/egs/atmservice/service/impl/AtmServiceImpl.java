package com.egs.atmservice.service.impl;

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
     * CardDto Number Validation
     *
     * @param cardDto CardDto with cardDto number
     * @return GenericRestResponse Bank Service response
     */

    @Override
    public GenericRestResponse getCardVerification(CardDto cardDto) throws BadRequestAlertException {
        ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                bankServiceClient.validateCardNumber(cardDto.getCardNumber());
        httpSession.setAttribute(ConstantsUtil.SessionKey.CARD_NUMBER, cardDto.getCardNumber());
        if (bankRestResponseResponseEntity.getBody().getStatus() == BankRestResponse.STATUS.FAILURE) {
            throw new BadRequestAlertException(bankRestResponseResponseEntity.getBody().getMessage(), CARD_DTO, ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_KEY);
        }

        return new GenericRestResponse(bankRestResponseResponseEntity.getStatusCodeValue(),
                bankRestResponseResponseEntity.getBody().getMessage());
    }

    @Override
    public GenericRestResponse getCardPinVerification(CardDto cardDto) throws BadRequestAlertException {
        cardDto.setCardNumber(String.valueOf(httpSession.getAttribute(ConstantsUtil.SessionKey.CARD_NUMBER)));
        ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                bankServiceClient.validateCardPinNumber(cardDto);
        if (bankRestResponseResponseEntity.getBody().getStatus() == BankRestResponse.STATUS.FAILURE) {
            throw new BadRequestAlertException(bankRestResponseResponseEntity.getBody().getMessage(), CARD_DTO, ErrorConstants.CardVerificationMessage.WRONG_PIN_KEY);
        }
        return new GenericRestResponse(bankRestResponseResponseEntity.getStatusCodeValue(), bankRestResponseResponseEntity.getBody().getMessage());
    }

    @Override
    public GenericRestResponse balanceManagement(AccountRequestDto accountRequestDto) {
        try {
            ResponseEntity<BankRestResponse> bankRestResponseResponseEntity =
                    bankServiceClient.balanceManagement(accountRequestDto);
            return new GenericRestResponse(bankRestResponseResponseEntity.getStatusCodeValue(), bankRestResponseResponseEntity.getBody().getMessage());
        } catch (Exception e) {
        }
        return null;

    }
}