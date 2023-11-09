package com.xoso.agency.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyState {
    public int state;
    public String reason;
}
