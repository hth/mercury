package com.github.hth.datacsv.dto;

import com.github.hth.datacsv.enums.ReceiverTagEnum;
import com.github.hth.datacsv.enums.TransactionStatusEnum;
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

    public String asRecordForCSV() {
        return receiverTagEnum + ";" +
                transactionId + ";" +
                name + ";" +
                address.replaceAll(",", "") + ";" +
                phoneNumber + ";" +
                country + ";" +
                countryCode + ";" +
                amount + ";" +
                localDateTime + ";" +
                transactionStatus;
    }
}
