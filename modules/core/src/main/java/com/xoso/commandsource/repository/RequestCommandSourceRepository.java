package com.xoso.commandsource.repository;

import com.xoso.commandsource.model.RequestCommandSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestCommandSourceRepository extends JpaRepository<RequestCommandSource, Long>,
        JpaSpecificationExecutor<RequestCommandSource> {
}
