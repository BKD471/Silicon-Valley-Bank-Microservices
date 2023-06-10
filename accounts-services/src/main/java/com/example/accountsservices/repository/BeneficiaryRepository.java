package com.example.accountsservices.repository;

import com.example.accountsservices.model.Beneficiary;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface BeneficiaryRepository extends JpaRepository<Beneficiary,Long> {
    void deleteByBeneficiaryId(Long beneficiaryId);
    void deleteAllByAccounts_AccountNumber(Long accountNUmber);
    Optional<Page<Beneficiary>> findAllByAccounts_AccountNumber(Long AccountNumber, Pageable pageable);
}
