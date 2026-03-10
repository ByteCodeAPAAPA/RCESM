package com.example.rces.spm.controller.payload;

import com.example.rces.spm.models.JobComponent;
import com.example.rces.spm.models.JobStep;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public record JobComponentPayload(Long id, Long parentId, String name, String pdName, BigDecimal qty,
                                  BigDecimal qtyFinished, LocalDate dateStart, LocalDate dateEnd, LocalDate dateCalcEnd, List<JobStepPayload> jobSteps, Boolean hasChildOrJobSteps) {
    public JobComponentPayload(JobComponent jobComponent, Boolean hasChildOrJobSteps) {
        this(jobComponent.getId(), jobComponent.getParentJobComponent()!=null ? jobComponent.getParentJobComponent().getId() : null,
                String.format("%s %s", jobComponent.getItem().getName(),
                        jobComponent.getItem().getDescription()!=null ? jobComponent.getItem().getDescription() : ""),
                jobComponent.getPrimarydemand().getStormSingleString(),
                jobComponent.getQtyDemand().setScale(3, RoundingMode.DOWN), jobComponent.getQtyFinished().setScale(3, RoundingMode.DOWN),
                jobComponent.getDateStart().toLocalDate(),
                jobComponent.getDateEnd()!=null ? jobComponent.getDateEnd().toLocalDate() : null,
                jobComponent.getDateCalcEnd()!=null ? jobComponent.getDateCalcEnd().toLocalDate() : null,
                jobComponent.getJobSteps().stream().map(JobStepPayload::new).toList(),
                hasChildOrJobSteps
        );
    }

    public JobComponentPayload(JobComponent jobComponent) {
        this(jobComponent.getId(), jobComponent.getParentJobComponent()!=null ? jobComponent.getParentJobComponent().getId() : null,
                String.format("%s %s", jobComponent.getItem().getName(),
                        jobComponent.getItem().getDescription()!=null ? jobComponent.getItem().getDescription() : ""),
                jobComponent.getPrimarydemand().getStormSingleString(),
                jobComponent.getQtyDemand().setScale(3, RoundingMode.DOWN), jobComponent.getQtyFinished().setScale(3, RoundingMode.DOWN),
                jobComponent.getDateStart().toLocalDate(),
                jobComponent.getDateEnd()!=null ? jobComponent.getDateEnd().toLocalDate() : null,
                jobComponent.getDateCalcEnd()!=null ? jobComponent.getDateCalcEnd().toLocalDate() : null,
                null,null
        );
    }
}
