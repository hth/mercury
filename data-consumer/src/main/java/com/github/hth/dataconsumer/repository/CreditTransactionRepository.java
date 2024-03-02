package com.github.hth.dataconsumer.repository;

import com.github.hth.dataconsumer.entity.CreditTransactionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CreditTransactionRepository extends ReactiveMongoRepository<CreditTransactionEntity, String> {
}
