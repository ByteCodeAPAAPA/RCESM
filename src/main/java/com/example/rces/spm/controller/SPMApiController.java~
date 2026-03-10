package com.example.rces.spm.controller;

import com.example.rces.spm.controller.payload.JobComponentPayload;
import com.example.rces.spm.controller.payload.JobStepPayload;
import com.example.rces.spm.controller.payload.PrimaryDemandPayload;
import com.example.rces.spm.controller.payload.SPMCustomerOrderPayload;
import com.example.rces.spm.models.JobComponent;
import com.example.rces.spm.models.JobStep;
import com.example.rces.spm.models.PrimaryDemand;
import com.example.rces.spm.models.SPMCustomerOrder;
import com.example.rces.spm.services.SPMService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spm-api")
public class SPMApiController {

    private final SPMService service;

    @Autowired
    public SPMApiController(SPMService service) {
        this.service = service;
    }

    @GetMapping("/getBurningAndAllCustomerOrder")
    @ResponseBody
    public ResponseEntity<Map<String, List<SPMCustomerOrderPayload>>> getBurningTenCustomerOrders() {
        Map<List<SPMCustomerOrder>, List<SPMCustomerOrder>> rawData =
                service.getCustomerOrderService().getFirst10BurningCustomerOrderAndAll();
        List<SPMCustomerOrder> burning = rawData.keySet().iterator().next();
        List<SPMCustomerOrder> all = rawData.values().iterator().next();
        return ResponseEntity.ok(Map.of(
                "burning", burning.stream().map(SPMCustomerOrderPayload::new).toList(),
                "all", all.stream().map(SPMCustomerOrderPayload::new).toList()
        ));
    }

    @GetMapping("/getPrimaryDemandForCustomerOrderId")
    @ResponseBody
    public ResponseEntity<List<PrimaryDemandPayload>> getPrimarydemandForCustomerOrderId(
            @RequestParam Long customerOrderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "85") int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "demandType, stormSingleString");
        Page<PrimaryDemand> primaryDemandsPage = service.getPage(PrimaryDemand.class, page, size, sort,
                String.format("JOIN e.customerorder co WHERE co.id = %d", customerOrderId));
        Map<PrimaryDemand, JobComponent> pdJcMap = service.getPrimaryDemandService().getMainJobComponentForPrimaryDemand(primaryDemandsPage.getContent());
        List<PrimaryDemandPayload> result = pdJcMap.keySet()
                .stream()
                .map(pd -> new PrimaryDemandPayload(pd.getId(), pd.getStormSingleString(), new JobComponentPayload(pdJcMap.get(pd))))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(primaryDemandsPage.getTotalElements()))
                .header("X-Page", String.valueOf(page))
                .header("X-Page-Size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping("/jobComponentHasChildOrHasJobSteps")
    @ResponseBody
    public ResponseEntity<List<Boolean>> getJobComponentHasChildOrHasJobStepsList(@RequestParam List<Long> jobComponentIdList) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.getJobComponentService().jobComponentHasChildOrHasJobSteps(jobComponentIdList));
    }

    @GetMapping("/getChildJobComponentAndJobStepsForJobcomponentId")
    public ResponseEntity<Map<String, Object>> getChildJobComponentAndJobStepsForJobcomponentId(@RequestParam("jobComponentId") Long jobComponentId) {
        Pair<Map<JobComponent, Boolean>, List<JobStep>> jcHasChildMapJobStepList =
                service.getJobComponentService()
                        .getChildJobComponentAndHasChildOrJobStepsAndCurrentJobSteps(jobComponentId);
        List<JobComponentPayload> jobComponentPayloadList = jcHasChildMapJobStepList.getLeft().entrySet()
                .stream()
                .map(entry -> new JobComponentPayload(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        List<JobStepPayload> jobStepPayloadList = jcHasChildMapJobStepList.getRight()
                .stream()
                .map(JobStepPayload::new)
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("left", jobComponentPayloadList);
        result.put("right", jobStepPayloadList);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping("/getJobStepsForJobComponentId")
    @ResponseBody
    public ResponseEntity<List<JobStepPayload>> getJobStepsForJobComponentId(Long jobComponentId) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findById(JobComponent.class, jobComponentId).getJobSteps()
                        .stream()
                        .map(JobStepPayload::new)
                        .toList()
                );
    }

}
