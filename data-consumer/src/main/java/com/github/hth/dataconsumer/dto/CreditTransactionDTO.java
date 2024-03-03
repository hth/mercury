package com.github.hth.dataconsumer.dto;

import com.github.hth.dataconsumer.enums.ReceiverTagEnum;
import com.github.hth.dataconsumer.enums.TransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@ToString
public class CreditTransactionDTO {
    private ReceiverTagEnum receiverTagEnum;
    private String transactionId;
    private String name;
    private String address;
    private String phoneNumber;
    private String country;
    private String countryCode;
    private Integer amount;
    private LocalDateTime localDateTime;
    private TransactionStatusEnum transactionStatus;
}
