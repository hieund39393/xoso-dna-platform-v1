package com.xoso.lottery.repository;

import com.xoso.lottery.model.Lottery;
import com.xoso.lottery.model.LotteryVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryVideoRepository extends JpaRepository<LotteryVideo, Long>, JpaSpecificationExecutor<LotteryVideo> {

}
