package com.xoso.agency.repository;

import com.xoso.agency.model.AgencyRequest;
import com.xoso.bank.model.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRequestRepository extends JpaRepository<AgencyRequest, Long> {
    public AgencyRequest findByUserId(Long userId);
}
