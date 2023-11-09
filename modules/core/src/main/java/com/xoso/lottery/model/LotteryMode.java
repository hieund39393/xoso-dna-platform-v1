package com.xoso.lottery.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.language.model.TranslatedString;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lottery_mode")
public class LotteryMode extends AbstractAuditableCustom {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModeCode code;

    @Column(name = "price")
    private long price;

    @Column(name = "prize_money")
    private long prizeMoney;

}
