package com.egs.atmservice.util.externalServiceClient;

import com.egs.atmservice.web.dto.AccountRequestDto;
import com.egs.atmservice.web.dto.CardDto;
import com.egs.atmservice.web.dto.externalService.response.BankRestResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

@Component
public class BankServiceClient {

    private static final Logger log = LoggerFactory.getLogger(BankServiceClient.class);
    private static final int RETRY_COUNT = 2;

    @Value("${bankService.url}")
    private String bankServiceTarget;
    @Value("${bankService.connection.timeout.ms}")
    private int bankServiceConnectionTimeout;
    @Value("${bankService.socket.timeout.ms}")
    private int bankServiceSocketTimeout;
    @Value("${bankService.auth.key}")
    private String bankServiceAuthKey;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        this.restTemplate = new RestTemplateBuilder()
                .rootUri(bankServiceTarget)
//                .setReadTimeout(Duration.ofMillis(bankServiceSocketTimeout))
//                .setConnectTimeout(Duration.ofMillis(bankServiceConnectionTimeout))
                .build();
    }

    @Retryable(
            maxAttempts = RETRY_COUNT,
            include = RestClientException.class,
            backoff = @Backoff(delay = 1000)
    )
    public String loginToBankService() throws RestClientException {
        String requestPath = "/authenticate";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
            byte[] encodedAuth = Base64.encodeBase64(
                    "atm:atm".getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            headers.set("Authorization",authHeader);
            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            ResponseEntity<Object> results = restTemplate.exchange(requestPath , HttpMethod.GET, request,
                    Object.class);
            log.debug("called bank-service. path: '{}', status: '{}', response: '{}'", requestPath, results.getStatusCodeValue(),
                    results.getBody());
            return results.getHeaders().get("Authorization").toString();

        } catch (RestClientException e) {
            Throwable cause = e.getMostSpecificCause();
            log.warn("Error while GET `{}`. error: '{}', desc: '{}'", requestPath, cause.getClass().getName(), cause.getMessage());
            System.out.println(e);
            throw e;
        }
    }

    @Retryable(
            maxAttempts = RETRY_COUNT,
            include = RestClientException.class,
            backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<BankRestResponse> validateCardNumber(String cardNUmber, String jwt) throws RestClientException {
        String requestPath = "/v1/bank-service/validateCardNumber";
        return generateRequest(new CardDto(cardNUmber), requestPath, jwt);
    }

    @Retryable(
            maxAttempts = RETRY_COUNT,
            include = RestClientException.class,
            backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<BankRestResponse> validateCardPinNumber(CardDto cardDto, String jwt) throws RestClientException {
        String requestPath = "/v1/bank-service/validateCardPinNumber";
        return generateRequest(cardDto, requestPath, jwt);
    }

    @Retryable(
            maxAttempts = RETRY_COUNT,
            include = RestClientException.class,
            backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<BankRestResponse> requestManagement(AccountRequestDto accountRequestDto, String jwt) throws RestClientException {
        String requestPath = "/v1/account-service/requestManagement";
        return generateRequest(accountRequestDto, requestPath, jwt);
    }

    private ResponseEntity<BankRestResponse> generateRequest(Object obj, String requestPath, String jwt) throws RestClientException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
            headers.set("Authorization", jwt.substring(1, jwt.length()-1));
            HttpEntity<Object> request = new HttpEntity<>(obj, headers);
            ResponseEntity<BankRestResponse> results = restTemplate.exchange(requestPath, HttpMethod.POST, request,
                    BankRestResponse.class);
            log.debug("called bank-service. path: '{}', status: '{}', response: '{}'", requestPath, results.getStatusCodeValue(),
                    results.getBody());
            return results;
        } catch (RestClientException e) {
            Throwable cause = e.getMostSpecificCause();
            log.warn("Error while GET `{}`. error: '{}', desc: '{}'", requestPath, cause.getClass().getName(), cause.getMessage());
            System.out.println(e);
            throw e;
        }
    }
}
