package com.example.loansservices.service;

import com.example.loansservices.dto.LoansDto;
import com.example.loansservices.exception.InstallmentsException;
import com.example.loansservices.exception.LoansException;
import com.example.loansservices.exception.PaymentException;
import com.example.loansservices.exception.ValidationException;
import com.example.loansservices.model.Loans;
import com.example.loansservices.utils.AllConstantsHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface IValidationService {
    void validator(Loans loans, LoansDto loansDto, AllConstantsHelper.LoansValidateType loansValidateType, Optional<List<Loans>> e) throws ValidationException, PaymentException, InstallmentsException, LoansException;
}
