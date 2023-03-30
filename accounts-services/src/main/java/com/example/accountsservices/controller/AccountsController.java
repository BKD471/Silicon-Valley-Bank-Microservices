package com.example.accountsservices.controller;

import com.example.accountsservices.dto.AccountsDto;
import com.example.accountsservices.repository.AccountsRepository;
import com.example.accountsservices.service.impl.AccountsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountsController {
    private AccountsRepository accountsRepository;
    private AccountsServiceImpl accountsService;

    AccountsController(AccountsRepository accountsRepository, AccountsServiceImpl accountsService) {
        this.accountsRepository = accountsRepository;
        this.accountsService = accountsService;
    }

    /**
     * @param accountsDto
     * @paramType AccountsDto
     * @ReturnType ResponseEntity<AccountsDto>
     */
    @PostMapping
    public ResponseEntity<AccountsDto> createAccounts(@RequestBody AccountsDto accountsDto) {
        AccountsDto createdAccount = accountsService.createAccounts(accountsDto);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }


    /**
     * @param customerId
     * @paramType Long
     * @ReturnType ResponseEntity<AccountsDto>
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountsDto> getAccountsByCustomerId
            (@PathVariable(name = "id") Long customerId) {

        AccountsDto fetchedAcountDto = accountsService.getAccountByCustomerId(customerId);
        return new ResponseEntity<>(fetchedAcountDto, HttpStatus.OK);
    }
}
