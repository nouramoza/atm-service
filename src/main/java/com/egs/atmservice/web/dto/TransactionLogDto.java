package com.egs.atmservice.web.dto;

import com.egs.atmservice.enums.RequestTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionLogDto {
    private String accountNumber;
    private RequestTypeEnum requestTypeEnum;
    private Long newBalance;
    private String response;
    private int status;
    private Date requestDate;
    private String description;
}
