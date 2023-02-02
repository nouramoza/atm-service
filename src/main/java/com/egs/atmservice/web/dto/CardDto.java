package com.egs.atmservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardDto {
    private String cardNumber;
    private int cvv2;
    private Date expireDate;
    private String pin;
    private String accountNumber;
    private Boolean isActive;

    public CardDto(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
