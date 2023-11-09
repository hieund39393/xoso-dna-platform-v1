package com.xoso.staff.service.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.staff.data.StaffData;
import com.xoso.staff.mapper.StaffMapper;
import com.xoso.staff.model.Staff;
import com.xoso.staff.repository.StaffRepository;
import com.xoso.staff.service.StaffWriteService;
import com.xoso.staff.wsdto.StaffCreateRequestWsDTO;
import com.xoso.staff.wsdto.StaffUpdateRequestWsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;

@Service
public class StaffWriteServiceImpl implements StaffWriteService {

    private final static Logger logger = LoggerFactory.getLogger(StaffWriteServiceImpl.class);

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Autowired
    public StaffWriteServiceImpl(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    @Override
    @Transactional
    public StaffData createStaff(StaffCreateRequestWsDTO request) {
        try {
            final Staff staff = Staff.fromRequest(request);

            this.staffRepository.save(staff);

            return this.staffMapper.mapToStaff(staff);
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(dve.getMostSpecificCause(), dve);
            return null;
        }catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause()) ;
            handleStaffDataIntegrityIssues(throwable, dve);
            return null;
        }
    }

    @Override
    public ResultBuilder updateStaff(Long staffId, StaffUpdateRequestWsDTO request) {
        return null;
    }

    private void handleStaffDataIntegrityIssues(final Throwable realCause, final Exception dve) {

        logger.error("Error occured.", dve);
        throw new AbstractPlatformException("error.msg.staff.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}
