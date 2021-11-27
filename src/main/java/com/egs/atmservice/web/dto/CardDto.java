package com.egs.atmservice.web.dto;

import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardDto implements Serializable {
    public String cardNumber;
    public int cvv2;
    public Date expireDate;
    public String pin;
    private String accountNumber;
    private Boolean isActive;
}
