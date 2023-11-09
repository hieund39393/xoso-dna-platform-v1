package com.xoso.commandsource.model;

import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request_command_source")
public class RequestCommandSource extends AbstractAuditableCustom implements Serializable {

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Column(name = "action_name", nullable = false)
    private String actionName;

    @Column(name = "command_as_json", nullable = false)
    private String commandAsJson;

    @Column(name = "checker", nullable = false)
    private boolean checker;

    @Column(name = "checker_on_date")
    private LocalDateTime checkerOnDate;

    @Column(name = "resource_id")
    private Long resourceId;
}
