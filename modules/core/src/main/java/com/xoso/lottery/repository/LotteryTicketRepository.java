package com.xoso.lottery.repository;

import com.xoso.lottery.model.LotteryTicket;
import com.xoso.lottery.model.ModeCode;
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
public interface LotteryTicketRepository extends JpaRepository<LotteryTicket, Long>, JpaSpecificationExecutor<LotteryTicket> {
    //lay session moi nhat cua lottery
    @Query("select ticket from LotteryTicket ticket where ticket.session.status = 'DONE' and ticket.status = 0 ORDER BY ticket.id DESC")
    List<LotteryTicket> getTicketsPay( Pageable pageable);
    @Query("select ticket from LotteryTicket ticket where ticket.session.id = :sessionId and ticket.userId = :userId ORDER BY ticket.id DESC")
    List<LotteryTicket> getPurchasedTicketsBySessionId(@Param("userId")long userId, @Param("sessionId") long sessionId);
    @Query("select sum(ticket.price) from LotteryTicket ticket where ticket.session.id = :sessionId")
    Optional<Long> getTotalBet( @Param("sessionId") long sessionId);
    @Query("select SUM(ticket.price) from LotteryTicket ticket where " +
            "ticket.session.id = :sessionId " +
            "and ticket.code = :code")
    Optional<Long> getTotalBetByCode(@Param("code") ModeCode code, @Param("sessionId")long sessionId);

    @Query("select ticket from LotteryTicket ticket where " +
            "(ticket.session.lotteryId = :lotteryId or :lotteryId = 0) " +
            "and ticket.userId = :userId ORDER BY ticket.id DESC")
    Page<LotteryTicket> getTicketByLotteryId(@Param("userId")long userId,@Param("lotteryId")long lotteryId, Pageable pageable);
    @Query("select ticket from LotteryTicket ticket where " +
            "(ticket.session.lotteryId = :lotteryId or :lotteryId = 0) " +
            "and ticket.status = :status " +
            "and ticket.userId = :userId ORDER BY ticket.id DESC")
    Page<LotteryTicket> getTicketByLotteryIdAndStatus(@Param("userId")long userId,@Param("lotteryId")long lotteryId, @Param("status")int status, Pageable pageable);
    @Query("select SUM(ticket.prizeMoney) from LotteryTicket ticket where " +
            "ticket.session.id = :sessionId " +
            "and ticket.numbers = :number " +
            "and ticket.code = 'DDB'")
    Optional<Long> getTotalWinDDB(@Param("number")String number, @Param("sessionId")long sessionId);

    @Query("select SUM(ticket.prizeMoney) from LotteryTicket ticket where " +
            "ticket.session.id = :sessionId " +
            "and ticket.numbers = :number " +
            "and ticket.code = :code")
    Optional<Long> getTotalWinByCode(@Param("code")ModeCode code,@Param("number")String number,@Param("sessionId")long sessionId);

    @Query("select COUNT(ticket.userId) from LotteryTicket ticket where " +
            "ticket.session.id = :sessionId " +
            "and ticket.numbers = :number " +
            "and ticket.code = :code group by ticket.userId")
    Optional<Long> getTotalUserWinByCode(@Param("code")ModeCode code,@Param("number")String number,@Param("sessionId")long sessionId);
}
