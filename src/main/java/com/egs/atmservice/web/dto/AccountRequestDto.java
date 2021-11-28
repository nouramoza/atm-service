package com.egs.atmservice.web.dto;

import com.egs.atmservice.enums.RequestTypeEnum;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class AccountRequestDto {

    @NonNull
    public RequestTypeEnum requestType;

    public String cardNumber;

    public String description;

    public Long amount;

    public Date fromDate;

    public Date toDate;
}
