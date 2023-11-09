package com.xoso.giftcode.repository;

import com.xoso.giftcode.model.GiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiftCodeRepository extends JpaRepository<GiftCode, Long> {
    @Query
    Optional<GiftCode> findByUserIdAndCode(@Param("userId")Long userId, @Param("code")String giftCode);
}
