package com.xoso.giftcode.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gift_code")
public class GiftCode extends AbstractAuditableCustom {

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(name = "number_of_users")
    private int numberOfUsers;

}
