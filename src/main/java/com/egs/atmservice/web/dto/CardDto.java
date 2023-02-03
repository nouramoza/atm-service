package com.egs.atmservice.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDto implements Serializable {
    private String cardNumber;
    private int cvv2;
    private Date expireDate;
    private String pin;
    private String accountNumber;
    private Boolean isActive;
    private LocalDateTime issueDate;

    public CardDto(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
