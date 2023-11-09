package com.xoso.lottery.repository;

import com.xoso.lottery.model.LotteryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryCategoryRepository extends JpaRepository<LotteryCategory, Long>, JpaSpecificationExecutor<LotteryCategory> {
}
