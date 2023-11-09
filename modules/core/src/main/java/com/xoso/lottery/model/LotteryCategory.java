package com.xoso.lottery.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.language.model.TranslatedString;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lottery_category")
public class LotteryCategory extends AbstractAuditableCustom {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

   @Column(name = "active",nullable = false)
   private boolean active;

    @Column(name = "total_master_win", nullable = false)
    private BigDecimal totalMasterWin;

    @Column(name = "enable_campaign",nullable = false)
    private boolean enable_campaign;
}
