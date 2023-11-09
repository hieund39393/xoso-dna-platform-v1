package com.xoso.staff.mapper;

import com.xoso.staff.data.StaffData;
import com.xoso.staff.model.Staff;
import org.mapstruct.Mapper;

@Mapper
public interface StaffMapper {

    StaffData mapToStaff(Staff staff);
}
