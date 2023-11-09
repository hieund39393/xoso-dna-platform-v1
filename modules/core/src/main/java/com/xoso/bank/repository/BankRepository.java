package com.xoso.bank.repository;

import com.xoso.bank.model.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findBankByCode(String code);

    @Query(value = "select b from Bank b where b.enabled = true " +
            "and (:search is null or :search = '' " +
            "or UPPER(b.code) like upper(concat('%',:search,'%')) " +
            "or UPPER(b.name) like upper(concat('%',:search,'%'))" +
            "or UPPER(b.description) like upper(concat('%',:search,'%')) )")
    Page<Bank> findBank(@Param("search") String searchValue, Pageable pageable);
}
