package com.example.loansservices.service.impl;

import com.example.loansservices.dto.LoansDto;
import com.example.loansservices.dto.PaymentDto;
import com.example.loansservices.exception.*;
import com.example.loansservices.mapper.LoansMapper;
import com.example.loansservices.model.Loans;
import com.example.loansservices.repository.ILoansRepository;
import com.example.loansservices.service.ILoansService;
import com.example.loansservices.service.IValidationService;
import com.example.loansservices.utils.AllConstantsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.loansservices.mapper.LoansMapper.mapToLoansDto;
import static com.example.loansservices.mapper.LoansMapper.mapToPaymentDto;
import static com.example.loansservices.utils.AllConstantsHelper.*;
import static com.example.loansservices.utils.RateOfInterestHelper.getRateOfInterest;


/**
 * @parent: LoansService
 * @class: LoanServiceImpl
 * @fields: loansRepository, MONTHS_IN_YEAR, PERCENTAGE
 * @fieldTypes: LoansRepository, int, int
 * @overridenMethods: borrowLoan, getInfoAboutLoanByCustomerIdAndLoanNumber,
 * payInstallments,getAllLoansForCustomerById
 * @specializedMethods: None
 */
@Service
@Slf4j
public class LoanServiceImpl implements ILoansService {
    private final IValidationService validationService;
    private final ILoansRepository loansRepository;


    /**
     * @param: loansRepository
     * @paramType: LoansRepository
     * @returnType: NA
     **/
    LoanServiceImpl(ILoansRepository loansRepository, IValidationService validationService) {
        this.loansRepository = loansRepository;
        this.validationService = validationService;
    }

    /**
     * Method to calculate monthly emi
     */
    private long calculateEmi(final long loanAmount, final int tenure) throws TenureException {
        final String methodName = "calculateEmi(Long,int) in LoanServiceImpl";
        final Double rate_of_interest = getRateOfInterest(tenure);
        if (Objects.isNull(rate_of_interest)) throw new TenureException(TenureException.class,
                String.format("Tenure %s is not available", tenure), methodName);

        double magic_co_eff = ((rate_of_interest / 100) / 12);
        long interest = (long) (loanAmount * magic_co_eff);

        double numerator = Math.pow(1 + magic_co_eff, tenure * 12);
        double denominator = numerator - 1;
        double emi_co_eff = (numerator / denominator);
        return (long) (interest * emi_co_eff);
    }

    /**
     * Method to process critical information like  no of installments,
     * Maturity Date ,emi amount
     */
    private Loans processLoanInformationAndCreateLoan(final Loans loans) throws TenureException {
        final Loans loan = loansRepository.save(loans);

        final String loanNumber = UUID.randomUUID().toString();
        final int tenure = loan.getLoanTenureInYears();
        LocalDate endDate = loan.getStartDate().plusYears(tenure);
        final long loanAmount = loan.getTotalLoan();
        final long emiAmount = calculateEmi(loanAmount, tenure);
        final double rate_of_interest = getRateOfInterest(tenure);

        loan.setLoanNumber(loanNumber);
        loan.setLoanTenureInYears(tenure);
        loan.setEndDt(endDate);
        loan.setEmiAmount(emiAmount);
        loan.setRate_Of_Interest(rate_of_interest);
        loan.setOutstandingAmount(loan.getTotalLoan());
        loan.setAmountPaid(0L);
        loan.setTotalInstallmentsInNumber(tenure * 12);
        loan.setInstallmentsPaidInNumber(0);
        loan.setInstallmentsRemainingInNumber(tenure * 12);
        loan.setLoanActive(true);
        return loan;
    }

    /**
     * @param loansDto
     * @paramType LoansDto
     * @returnType LoansDto
     */
    @Override
    public LoansDto borrowLoan(final LoansDto loansDto) throws TenureException, ValidationException, PaymentException, InstallmentsException, LoansException {
        final Loans loan = LoansMapper.mapToLoans(loansDto);
        validationService.validator(loan, loansDto, ISSUE_LOAN, Optional.empty());
        final Loans processedLoan = processLoanInformationAndCreateLoan(loan);
        final Loans savedLoan = loansRepository.save(processedLoan);
        return mapToLoansDto(savedLoan);
    }

    /**
     * @param paymentDto
     * @paramType PaymentDto
     * @returnType PaymentDto
     */
    @Override
    public PaymentDto payInstallments(final PaymentDto paymentDto) throws LoansException, PaymentException, InstallmentsException, ValidationException {
        final Optional<Loans> loan = loansRepository.findByCustomerIdAndLoanNumber
                (paymentDto.getCustomerId(), paymentDto.getLoanNumber());
        validationService.validator(loan.get(), mapToLoansDto(loan.get()), PAY_EMI,Optional.of(List.of(loan.get())));

        final Loans currentLoan = loan.get();
        long emi = currentLoan.getEmiAmount();
        int paidInstallments = currentLoan.getInstallmentsPaidInNumber();
        int remainingInstallments = currentLoan.getInstallmentsRemainingInNumber();
        long payment = paymentDto.getPaymentAmount();

        //updating the installments data
        int NoOfInstallments = (int) (payment / emi);
        currentLoan.setInstallmentsPaidInNumber(NoOfInstallments + paidInstallments);
        currentLoan.setInstallmentsRemainingInNumber(remainingInstallments - NoOfInstallments);
        if(currentLoan.getInstallmentsRemainingInNumber()==0) currentLoan.setLoanActive(false);
        //updating the outstanding amount,amount paid
        long outstandingAmount = currentLoan.getOutstandingAmount();
        long amountPaid = currentLoan.getAmountPaid();
        currentLoan.setOutstandingAmount(outstandingAmount - payment);
        currentLoan.setAmountPaid(amountPaid + payment);

        Loans paidEmi = loansRepository.save(currentLoan);
        return mapToPaymentDto(paidEmi, payment);
    }

    /**
     * @param customerId
     * @paramType Long
     * @returnType LoansDto
     */
    @Override
    public LoansDto getInfoAboutAParticularLoan(final String customerId, final String loanNumber) throws LoansException, ValidationException, PaymentException, InstallmentsException {
        final Optional<Loans> loan = loansRepository.findByCustomerIdAndLoanNumber(customerId, loanNumber);
        validationService.validator(loan.get(),null,LoansValidateType.GET_INFO_LOAN, Optional.of(Arrays.asList(loan.get())));
        return mapToLoansDto(loan.get());
    }


    /**
     * @param customerId
     * @paramType Long
     * @returnType List<LoansDto>
     */
    @Override
    public List<LoansDto> getAllLoansForACustomer(final String customerId) throws LoansException, ValidationException, PaymentException, InstallmentsException {
        final Optional<List<Loans>> allLoans = loansRepository.findAllByCustomerId(customerId);
        LoansDto loansDto=LoansDto.builder().customerId(customerId).build();
        validationService.validator(null, loansDto, GET_ALL_LOAN,allLoans);
        return allLoans.get().stream().map(LoansMapper::mapToLoansDto).
                collect(Collectors.toList());
    }
}
