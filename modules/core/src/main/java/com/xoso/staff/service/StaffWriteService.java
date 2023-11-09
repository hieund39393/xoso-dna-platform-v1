package com.xoso.staff.service;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.staff.data.StaffData;
import com.xoso.staff.wsdto.StaffCreateRequestWsDTO;
import com.xoso.staff.wsdto.StaffUpdateRequestWsDTO;

public interface StaffWriteService {
    StaffData createStaff(StaffCreateRequestWsDTO request);

    ResultBuilder updateStaff(Long staffId, StaffUpdateRequestWsDTO request);
}
