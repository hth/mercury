/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.dataconsumer.entity;

import com.github.hth.dataconsumer.enums.ReceiverTagEnum;
import com.github.hth.dataconsumer.enums.TransactionStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Expected record of transaction from various data source.
 */
@Document("CREDIT_TX")
@Data
@NoArgsConstructor
@ToString
@CompoundIndex(name = "country_amount", def = "{'COUNTRY': 1, 'AMOUNT': 1}")
@CompoundIndex(name = "country_date", def = "{'COUNTRY': 1, 'DATE': 1}")
public class CreditTransactionEntity {

    @Id
    private String id;

    @Field("TAG")
    private ReceiverTagEnum receiverTagEnum;

    @Field("TX")
    private String transactionId;

    @Field("NAME")
    private String name;

    @Field("ADDRESS")
    private String address;

    @Field("PHONE_NUMBER")
    private String phoneNumber;

    @Field("COUNTRY")
    private String country;

    @Field("COUNTRY_CODE")
    private String countryCode;

    @Field("AMOUNT")
    private Integer amount;

    @Field("DATE")
    private LocalDateTime localDateTime;

    @Field("STATUS")
    private TransactionStatusEnum transactionStatus;

    public CreditTransactionEntity(
            ReceiverTagEnum receiverTagEnum,
            String transactionId,
            String name,
            String address,
            String phoneNumber,
            String country,
            String countryCode,
            Integer amount,
            LocalDateTime localDateTime,
            TransactionStatusEnum transactionStatus
    ) {
        this.receiverTagEnum = receiverTagEnum;
        this.transactionId = transactionId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.countryCode = countryCode;
        this.amount = amount;
        this.localDateTime = localDateTime;
        this.transactionStatus = transactionStatus;
    }

}
