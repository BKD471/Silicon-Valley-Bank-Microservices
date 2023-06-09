package com.example.accountsservices.service;

import com.example.accountsservices.dto.outputDtos.OutputDto;
import com.example.accountsservices.dto.baseDtos.TransactionsDto;
import com.example.accountsservices.exception.AccountsException;
import com.example.accountsservices.exception.TransactionException;

public interface ITransactionsService {
    OutputDto transactionsExecutor(final TransactionsDto transactionsDto) throws TransactionException, AccountsException;
    OutputDto getPastSixMonthsTransactionsForAnAccount(final String accountNumber) throws AccountsException;
}
