package com.egs.atmservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardDto implements Serializable {

    public CardDto(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String cardNumber;
    public int cvv2;
    public Date expireDate;
    public String pin;
    private String accountNumber;
    private Boolean isActive;
}
