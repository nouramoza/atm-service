package com.egs.atmservice;

import com.egs.atmservice.enums.RequestTypeEnum;
import com.egs.atmservice.util.ConstantsUtil;
import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.error.ErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.servlet.http.HttpSession;
import java.net.URI;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class AtmserviceApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HttpSession httpSession;

    private static final URI CARD_VARIFICATION_URI = URI.create("/api/v1/atm-service/cardVerification");
    private static final URI CARD_PIN_VARIFICATION_URI = URI.create("/api/v1/atm-service/cardPinVerification");
    private static final URI REQUEST_MANAGEMENT_URI = URI.create("/api/v1/atm-service/requestManagement");

    @Test
    void notValidCardVerification() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber("432432");
        String cardStr = mapToJson(cardDto);
        RequestBuilder req = post(CARD_VARIFICATION_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(cardStr);

        String outputExpectedStr = ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(409))
                .andDo(print())
                .andReturn();
    }

    @Test
    void validCardVerification() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber("6280231451904303");
        String cardStr = mapToJson(cardDto);
        RequestBuilder req = post(CARD_VARIFICATION_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(cardStr);

        String outputExpectedStr = ConstantsUtil.ResponseMessage.CARD_ACCEPTED;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void inActiveCardVerification() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber("6280231451904304");
        String cardStr = mapToJson(cardDto);
        RequestBuilder req = post(CARD_VARIFICATION_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(cardStr);

        String outputExpectedStr = ErrorConstants.CardVerificationMessage.CARD_NOT_VALID_MSG;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(409))
                .andDo(print())
                .andReturn();
    }

    @Test
    void expiredCardVerification() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber("6280231451904305");
        String cardStr = mapToJson(cardDto);
        RequestBuilder req = post(CARD_VARIFICATION_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(cardStr);

        String outputExpectedStr = ErrorConstants.CardVerificationMessage.CARD_EXPIRED_MSG;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(409))
                .andDo(print())
                .andReturn();
    }

    @Test
    void correctPinVerification() throws Exception {
        httpSession.setAttribute(ConstantsUtil.SessionKey.CARD_NUMBER, "6280231451904303");
        CardDto cardDto = new CardDto();
        cardDto.setPin("1234");
        String cardStr = mapToJson(cardDto);
        RequestBuilder req = post(CARD_PIN_VARIFICATION_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(cardStr);

        String outputExpectedStr = ConstantsUtil.ResponseMessage.CARD_ACCEPTED;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void incorrectPinVerification() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber("6280231451904303");
        cardDto.setPin("123");
        String cardStr = mapToJson(cardDto);
        RequestBuilder req = post(CARD_PIN_VARIFICATION_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(cardStr);

        String outputExpectedStr = ErrorConstants.CardVerificationMessage.WRONG_PIN_MSG;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(409))
                .andDo(print())
                .andReturn();
    }

    @Test
    void checkBalance() throws Exception {
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setRequestType(RequestTypeEnum.CHECK_BALANCE);

        String accountRequestStr = mapToJson(accountRequestDto);
        RequestBuilder req = post(REQUEST_MANAGEMENT_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(accountRequestStr);

        String outputExpectedStr = ConstantsUtil.CommonMessage.YOUR_BALANCE + 1000;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void deposit() throws Exception {
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setRequestType(RequestTypeEnum.DEPOSIT);
        accountRequestDto.setAmount(200L);

        String accountRequestStr = mapToJson(accountRequestDto);
        RequestBuilder req = post(REQUEST_MANAGEMENT_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(accountRequestStr);

        String outputExpectedStr = ConstantsUtil.CommonMessage.SUCCESS_DEPOSIT + 1200;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setRequestType(RequestTypeEnum.WITHDRAW);
        accountRequestDto.setAmount(200L);

        accountRequestStr = mapToJson(accountRequestDto);
        req = post(REQUEST_MANAGEMENT_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(accountRequestStr);
        this.mockMvc.perform(req).andReturn();
    }

    @Test
    void withdraw() throws Exception {
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setRequestType(RequestTypeEnum.WITHDRAW);
        accountRequestDto.setAmount(200L);

        String accountRequestStr = mapToJson(accountRequestDto);
        RequestBuilder req = post(REQUEST_MANAGEMENT_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(accountRequestStr);

        String outputExpectedStr = ConstantsUtil.CommonMessage.SUCCESS_WITHDRAW + 800;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setRequestType(RequestTypeEnum.DEPOSIT);
        accountRequestDto.setAmount(200L);

        accountRequestStr = mapToJson(accountRequestDto);
        req = post(REQUEST_MANAGEMENT_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(accountRequestStr);
        mvcResult = this.mockMvc.perform(req).andReturn();
    }

    @Test
    void withdrawNotEnough() throws Exception {
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setRequestType(RequestTypeEnum.WITHDRAW);
        accountRequestDto.setAmount(22200L);

        String accountRequestStr = mapToJson(accountRequestDto);
        RequestBuilder req = post(REQUEST_MANAGEMENT_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(accountRequestStr);

        String outputExpectedStr = ErrorConstants.AccountMessage.NOT_ENOUGH_BALANCE_MSG;

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    protected static String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
