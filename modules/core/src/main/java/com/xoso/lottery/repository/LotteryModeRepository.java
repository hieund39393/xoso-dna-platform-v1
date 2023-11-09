package com.xoso.lottery.repository;

import com.xoso.lottery.model.LotteryMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryModeRepository extends JpaRepository<LotteryMode, Long>, JpaSpecificationExecutor<LotteryMode> {
}
