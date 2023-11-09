package com.xoso.user.model;

import lombok.Getter;
import lombok.Setter;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.infrastructure.core.model.AbstractPersistableCustom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "permission")
public class Permission extends AbstractAuditableCustom implements Serializable {

    @Column(name = "grouping", nullable = false, length = 45)
    private String grouping;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "entity_name", nullable = true, length = 100)
    private String entityName;

    @Column(name = "action_name", nullable = true, length = 100)
    private String actionName;

    public Permission(final String grouping, final String entityName, final String actionName) {
        this.grouping = grouping;
        this.entityName = entityName;
        this.actionName = actionName;
        this.code = actionName + "_" + entityName;
    }

    protected Permission() {
        this.grouping = null;
        this.entityName = null;
        this.actionName = null;
        this.code = null;
    }

    public boolean hasCode(final String checkCode) {
        return this.code.equalsIgnoreCase(checkCode);
    }

}
