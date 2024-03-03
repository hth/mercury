package com.github.hth.dataviakafka.dto;

import com.github.hth.dataviakafka.enums.ReceiverTagEnum;
import com.github.hth.dataviakafka.enums.TransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@ToString
public class CreditTransactionDTO {
    private ReceiverTagEnum receiverTagEnum;
    private UUID transactionId;
    private String name;
    private String address;
    private String phoneNumber;
    private String country;
    private String countryCode;
    private Integer amount;
    private LocalDateTime localDateTime;
    private TransactionStatusEnum transactionStatus;
}