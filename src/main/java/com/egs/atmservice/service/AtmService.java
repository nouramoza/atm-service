package com.egs.atmservice.service;

import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.dto.GenericRestResponse;
import com.egs.atmservice.web.error.BadRequestAlertException;
import org.springframework.stereotype.Service;

@Service
public interface AtmService {

    GenericRestResponse getCardVerification(CardDto cardDto) throws BadRequestAlertException;

    GenericRestResponse getCardPinVerification(CardDto cardDto) throws BadRequestAlertException;

    GenericRestResponse balanceManagement(AccountRequestDto accountRequestDto);

}
