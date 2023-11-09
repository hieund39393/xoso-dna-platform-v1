package com.xoso.user.model;


import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification")
public class Verification implements Persistable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "expired_at", nullable = false)
    private Timestamp expiredAt;

    @Column(name = "disabled", nullable = false)
    private Boolean disabled;

    @Column(name = "sent_at")
    private Timestamp sentAt;

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
