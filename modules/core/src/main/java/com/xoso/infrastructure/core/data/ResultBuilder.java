package com.xoso.infrastructure.core.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultBuilder {
    private Long entityId;
    private Object resultData;
    private String accountNumber;

    private Map<String, Object> changes;

    public ResultBuilder withEntityId(final Long withEntityId) {
        this.entityId = withEntityId;
        return this;
    }

    public ResultBuilder withResultData(final Object resultArgs) {
        this.resultData = resultArgs;
        return this;
    }

    public ResultBuilder with(final Map<String, Object> withChanges) {
        this.changes = withChanges;
        return this;
    }

    public ResultBuilder build() {
        var commandProcessingResult = ResultBuilder.fromDetails(this.entityId, this.resultData, this.accountNumber, this.changes);
        return commandProcessingResult;
    }

    private static ResultBuilder fromDetails(Long entityId, Object resultData, String accountNumber, Map<String, Object> changes) {
        return new ResultBuilder(entityId, resultData, accountNumber, changes);
    }

    public static ResultBuilder empty() {
        return new ResultBuilder(null, null, null, null);
    }
}
