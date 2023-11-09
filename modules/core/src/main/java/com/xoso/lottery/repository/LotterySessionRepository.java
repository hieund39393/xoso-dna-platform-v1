package com.xoso.lottery.repository;

import com.xoso.client.model.Client;
import com.xoso.lottery.model.Lottery;
import com.xoso.lottery.model.LotterySession;
import com.xoso.lottery.model.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotterySessionRepository extends JpaRepository<LotterySession, Long>, JpaSpecificationExecutor<LotterySession> {
    //lay session moi nhat cua lottery
    LotterySession findFirstByLotteryIdOrderByIdDesc(Long lotteryId);
    //lay session DONE moi nhat cua lottery theo thoi gian thuc
    LotterySession findFirstByLotteryIdAndStatusOrderByIdDesc(Long lotteryId, SessionStatus status);
    @Query("SELECT session FROM LotterySession session WHERE session.lotteryId = :lotteryId and session.status = 'DONE' ORDER BY session.id DESC")
    Page<LotterySession> getLotterySessionsByLotteryId(@Param("lotteryId") Long lotteryId, Pageable pageable);

}
