package com.example.accountsservices.controller.Impl;

import com.example.accountsservices.controller.ITransactionsController;
import com.example.accountsservices.dto.outputDtos.OutputDto;
import com.example.accountsservices.dto.baseDtos.TransactionsDto;
import com.example.accountsservices.exception.AccountsException;
import com.example.accountsservices.exception.TransactionException;
import com.example.accountsservices.service.ITransactionsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
public class TransactionsControllerImpl implements ITransactionsController {
    private final ITransactionsService transactionsService;

    TransactionsControllerImpl(@Qualifier("transactionsServicePrimary") ITransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    /**
     * @param transactionsDto
     * @return
     * @throws TransactionException
     * @throws AccountsException
     */
    @Override
    public ResponseEntity<OutputDto> executeTransactions(final TransactionsDto transactionsDto) throws TransactionException, AccountsException {
        final OutputDto responseDto = transactionsService.transactionsExecutor(transactionsDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * @param accountNumber
     * @return
     * @throws AccountsException
     */
    @Override
    public ResponseEntity<OutputDto> getPastSixMonthsTransaction(final String accountNumber) throws AccountsException {
        final OutputDto responseDto = transactionsService.getPastSixMonthsTransactionsForAnAccount(accountNumber);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
