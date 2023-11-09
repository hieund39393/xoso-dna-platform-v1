package com.xoso.lottery.repository;

import com.xoso.lottery.model.Lottery;
import com.xoso.lottery.model.TypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotteryRepository extends JpaRepository<Lottery, Long>, JpaSpecificationExecutor<Lottery> {
    @Query("SELECT l FROM Lottery l WHERE l.active = true")
    List<Lottery> findAllActiveLotteries();

    @Query("SELECT l FROM Lottery l WHERE l.vip = true and l.active = true")
    List<Lottery> findAllHotLotteries();

    @Query("SELECT l FROM Lottery l WHERE l.active = true and l.type = :type and l.category.code = :code")
    Lottery findLotteriesByTypeAndCategoryCode(@Param("type") TypeCode type, @Param("code") String code);
}
