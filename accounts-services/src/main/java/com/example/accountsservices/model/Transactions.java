package com.example.accountsservices.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transactions extends AuditTransactions{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="trans_id")
    private long transactionId;

    @Column(name="trans-amnt",nullable = false)
    private long transactionAmount;

    @Column(name = "trans-acnt-num", nullable = false)
    private String transactedAccountNumber;

    @Column(name = "trans-type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public enum TransactionType {
        CREDIT, DEBIT
    }

    @Column(name = "desc_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private DescriptionType description;

    public enum DescriptionType{
        //credit types
        SALARY,
        DEPOSIT,
        RECEIVED_FROM_OTHER_ACCOUNT,


        //debit types
        EMI,
        CREDIT_CARD_BILL_PAYMENT,
        DONATION,
        RENT,
        E_SHOPPING,
        BUSINESS,
        INVESTMENT,
        FAMILY,
        ELECTRICITY,
        OTHERS
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Accounts accounts;
}
