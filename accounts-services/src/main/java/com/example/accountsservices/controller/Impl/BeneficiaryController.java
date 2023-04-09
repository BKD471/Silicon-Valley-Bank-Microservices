package com.example.accountsservices.controller.Impl;

import com.example.accountsservices.controller.AbstractParentController;
import com.example.accountsservices.dto.BeneficiaryDto;
import com.example.accountsservices.exception.AccountsException;
import com.example.accountsservices.exception.BeneficiaryException;
import com.example.accountsservices.service.impl.BeneficiaryAccountsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BeneficiaryController extends AbstractParentController {

    private  final BeneficiaryAccountsServiceImpl beneficiaryAccountsService;

    BeneficiaryController(BeneficiaryAccountsServiceImpl beneficiaryAccountsService){
        this.beneficiaryAccountsService=beneficiaryAccountsService;
    }

    /**
     * @param accountNumber
     * @param beneficiaryDto
     * @return
     * @throws AccountsException
     */
    @Override
    public ResponseEntity<BeneficiaryDto> addBeneficiary(Long accountNumber, BeneficiaryDto beneficiaryDto) throws AccountsException {
        return null;
    }

    /**
     * @param accountNumber
     * @return
     * @throws AccountsException
     */
    @Override
    public ResponseEntity<List<BeneficiaryDto>> getAllBeneficiariesOfAnAccount(Long accountNumber) throws AccountsException {
        return null;
    }

    /**
     * @param accountNumber
     * @param beneficiaryDto
     * @return
     * @throws AccountsException
     * @throws BeneficiaryException
     */
    @Override
    public ResponseEntity<BeneficiaryDto> updateBeneficiaryDetails(Long accountNumber, BeneficiaryDto beneficiaryDto) throws AccountsException, BeneficiaryException {
        return null;
    }

    /**
     * @param beneficiaryDto
     * @return
     * @throws AccountsException
     * @throws BeneficiaryException
     */
    @Override
    public ResponseEntity<String> deleteBeneficiaryForAnAccount(BeneficiaryDto beneficiaryDto) throws AccountsException, BeneficiaryException {
        return null;
    }

    /**
     * @param accountNumber
     * @return
     * @throws AccountsException
     */
    @Override
    public ResponseEntity<String> deleteAllBeneficiaries(Long accountNumber) throws AccountsException {
        return null;
    }
}
