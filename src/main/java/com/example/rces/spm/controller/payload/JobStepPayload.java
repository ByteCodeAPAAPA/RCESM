package com.example.rces.spm.controller.payload;

import com.example.rces.spm.models.JobStep;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record JobStepPayload(Long id, String name, String mlmNode, String description, BigDecimal qty, BigDecimal qtyFinished, BigDecimal resourceTime,
                                  LocalDate dateStart, LocalDate dateEnd, LocalDate dateCalcStart, LocalDate dateCalcEnd) {

    public JobStepPayload(JobStep jobStep) {
        this(
                jobStep.getId(),
                String.format("%s %s: %s",
                        jobStep.getJobcomponent().getItem().getName(),
                        jobStep.getJobcomponent().getItem().getDescription()!=null ? jobStep.getJobcomponent().getItem().getDescription() : "",
                        jobStep.getNumber()),
                jobStep.getMlmNode().getName(),
                String.format("%s (%d)",jobStep.getDescription()!=null ? jobStep.getDescription() : "Нет описания захода",jobStep.getNumber()),
                jobStep.getQtyProduction().setScale(3, RoundingMode.DOWN),
                jobStep.getQtyFinished().setScale(3, RoundingMode.DOWN),
                jobStep.getResourceTime().setScale(3, RoundingMode.DOWN),
                jobStep.getDateStart().toLocalDate(),
                jobStep.getDateEnd().toLocalDate(),
                jobStep.getDateCalcStart().toLocalDate(),
                jobStep.getDateCalcEnd().toLocalDate()
        );
    }
}
