package com.xoso.campaign.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.lottery.model.LanguageEnum;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;

@Slf4j
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "template_content")
public class TemplateContent extends AbstractAuditableCustom {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    private String category;
    private String html;
    private String banner;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private LanguageEnum language;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
    private boolean active;
}
