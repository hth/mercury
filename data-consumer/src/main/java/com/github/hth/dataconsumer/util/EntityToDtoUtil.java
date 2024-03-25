/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.dataconsumer.util;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.entity.CreditTransactionEntity;
import org.springframework.beans.BeanUtils;

public class EntityToDtoUtil {

    public static CreditTransactionDTO convertEntityToDTO(CreditTransactionEntity creditTransactionEntity) {
        CreditTransactionDTO creditTransactionDTO = new CreditTransactionDTO();
        BeanUtils.copyProperties(creditTransactionEntity, creditTransactionDTO);
        return creditTransactionDTO;
    }

    public static CreditTransactionEntity convertDTOToEntity(CreditTransactionDTO creditTransactionDTO) {
        CreditTransactionEntity creditTransactionEntity = new CreditTransactionEntity();
        BeanUtils.copyProperties(creditTransactionDTO, creditTransactionEntity);
        return creditTransactionEntity;
    }
}
