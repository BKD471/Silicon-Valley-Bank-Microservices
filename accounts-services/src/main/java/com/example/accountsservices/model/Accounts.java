package com.example.accountsservices.model;

import com.example.accountsservices.helpers.AllConstantHelpers;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Accounts extends Audit {
    @Id
    @Column(nullable = false, unique = true, name = "accnt_num")
    private String accountNumber;

    @Column(name = "cust_balnc")
    private long balance;

    @Column(name = "accnt_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private AllConstantHelpers.AccountType accountType;

    @Column(name="branch_code")
    private String branchCode;

    @Column( name = "branch_addr",nullable = false)
    @Enumerated(EnumType.STRING)
    private AllConstantHelpers.Branch homeBranch;

    @Column(name = "trnsfr_lmt_pr_d")
    private long transferLimitPerDay;

    @Column(name = "crdt_scr")
    private int creditScore;

    @Column(name="apprvd_loan_bso_crdt_scr")
    private long approvedLoanLimitBasedOnCreditScore;

    @Column(name="is_ln_actv")
    private Boolean anyActiveLoans;

    @Column(name="tot_loan_issued_sf")
    private long totLoanIssuedSoFar;

    @Column(name="tot_out_amnt")
    private long totalOutStandingAmountPayableToBank;

    @Column(name="acc_stts")
    @Enumerated(EnumType.STRING)
    private AllConstantHelpers.AccountStatus accountStatus;

    @OneToMany(mappedBy = "accounts",cascade = CascadeType.ALL)
    private List<Beneficiary> listOfBeneficiary=new ArrayList<>();

    @OneToMany(mappedBy = "accounts",cascade = CascadeType.ALL)
    private List<Transactions> listOfTransactions=new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customer_id",nullable = false)
    private Customer customer;
}
