package com.example.rces.spm.services.service;

import com.example.rces.spm.controller.payload.JobComponentPayload;
import com.example.rces.spm.controller.payload.JobStepPayload;
import com.example.rces.spm.controller.payload.PrimaryDemandPayload;
import com.example.rces.spm.models.JobComponent;
import com.example.rces.spm.models.JobStep;
import com.example.rces.spm.models.PrimaryDemand;
import com.example.rces.spm.services.SPMRepository;
import com.example.rces.utils.TreeNode;
import org.apache.commons.collections4.list.TreeList;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.rces.utils.ExcelExporter.exportToExcelTree;

@Service
public class SPMReportService {
    private final SPMRepository spmRepository;
    private final PrimaryDemandService primaryDemandService;
    private final JobComponentService jobComponentService;

    public SPMReportService(SPMRepository spmRepository, PrimaryDemandService primaryDemandService, JobComponentService jobComponentService) {
        this.spmRepository = spmRepository;
        this.primaryDemandService = primaryDemandService;
        this.jobComponentService = jobComponentService;
    }


//    public ByteArrayResource makeCustomerOrderTreeReport(Long customerOrderId) throws Exception {
//        List<PrimaryDemand> primaryDemandList = spmRepository.getEntityManager().createQuery(
//                        """
//                                SELECT e
//                                FROM PrimaryDemand e
//                                WHERE e.customerorder.id = :id ORDER BY e.demandType
//                                """, PrimaryDemand.class)
//                .setParameter("id", customerOrderId).getResultList();
//
//        Map<PrimaryDemand, JobComponent> pdJcMap = primaryDemandService
//                .getMainJobComponentForPrimaryDemand(primaryDemandList);
//        List<PrimaryDemandPayload> primaryDemandPayloadList = primaryDemandList
//                .stream()
//                .map(pd -> new PrimaryDemandPayload(pd.getId(), pd.getStormSingleString(), new JobComponentPayload(pdJcMap.get(pd))))
//                .toList();
//
//        List<TreeNode> rootNodes = new TreeList<>();
//        for (PrimaryDemandPayload pd : primaryDemandPayloadList) {
//            TreeNode pdNode = new TreeNode(pd);
//            Map<JobComponent, List<JobStep>> JobComponentStepMap = jobComponentService.getAllChildJobComponents(pd.jobComponent().id())
//                    .stream()
//                    .collect(Collectors.toMap(Function.identity(), JobComponent::getJobSteps));
//
//            Map<Long, TreeNode> nodeMap = new HashMap<>();
//            JobComponentStepMap.forEach((jc, jsList) -> {
//                JobComponentPayload jcPayload = new JobComponentPayload(jc);
//                nodeMap.put(jc.getId(), new TreeNode(jcPayload, pd.name(), jcPayload.parentId()));
//                jsList.forEach(js -> nodeMap.put(js.getId(), new TreeNode(new JobStepPayload(js), pd.name(), jcPayload.id())));
//            });
//            JobComponentStepMap.forEach((jc, jsList) -> {
//                TreeNode jcCurrentNode = nodeMap.get(jc.getId());
//                Long jcParentId = jc.getParentJobComponent() != null ? jc.getParentJobComponent().getId() : null;
//                if (jcParentId != null && nodeMap.containsKey(jcParentId)) {
//                    nodeMap.get(jcParentId).addChild(jcCurrentNode);
//                } else {
//                    pdNode.addChild(jcCurrentNode);
//                }
//                jsList.forEach(js -> {
//                    TreeNode jsCurrentNode = nodeMap.get(js.getId());
//                    Long jsParentId = jsCurrentNode.parentId;
//
//                    if (jsParentId != null && nodeMap.containsKey(jsParentId)) {
//                        nodeMap.get(jsParentId).addChild(jsCurrentNode);
//                    } else {
//                        pdNode.addChild(jsCurrentNode);
//                    }
//                });
//            });
//
//            spmRepository.findById(JobComponent.class, pdNode.id).getJobSteps()
//                    .forEach(mainJs -> pdNode.addChild(
//                            new TreeNode(new JobStepPayload(mainJs), pd.name(), pdNode.id)
//                    ));
//            rootNodes.add(pdNode);
//        }
//        return exportToExcelTree(rootNodes,
//                Collections.unmodifiableMap(new LinkedHashMap<>() {{
//                    put("Строка ЗК/Спрос", 70 * 256);
//                    put("ДСЕ", 60 * 256);
//                    put("Узел ПЛМ", 35 * 256);
//                    put("Описание(заход)", 40 * 256);
//                    put("План брутто", 12 * 256);
//                    put("Выполнено", 14 * 256);
//                    put("Трудоемкость", 14 * 256);
//                    put("Дата начала", 12 * 256);
//                    put("Дата завершения", 16 * 256);
//                    put("РД начала", 14 * 256);
//                    put("РД завершения", 15 * 256);
//                }}));
//    }

    public ByteArrayResource makeCustomerOrderTreeReport(Long customerOrderId) throws Exception {
        List<PrimaryDemand> primaryDemandList = spmRepository.getEntityManager().createQuery(
                        """
                                SELECT e
                                FROM PrimaryDemand e
                                WHERE e.customerorder.id = :id ORDER BY e.demandType
                                """, PrimaryDemand.class)
                .setParameter("id", customerOrderId).getResultList();
        List<PrimaryDemandPayload> pdPayloadList = primaryDemandService
                .getMainJobComponentForPrimaryDemand(primaryDemandList)
                .entrySet().stream()
                .map(entry ->
                        new PrimaryDemandPayload(entry.getKey().getId(), entry.getKey().getStormSingleString(),
                                new JobComponentPayload(entry.getValue())))
                .toList();

        List<TreeNode> rootNodes = new TreeList<>();
        for (PrimaryDemandPayload pd : pdPayloadList) {
            TreeNode pdNode = new TreeNode(pd);
            Map<List<JobComponent>, List<JobStep>> map = jobComponentService
                    .getAllChildJobComponentsAndCurrentJobSteps(pd.jobComponent().id());
            List<JobComponent> allChildJobComponentList = map.keySet()
                    .stream()
                    .flatMap(List::stream).toList();
            List<JobStep> currentJobSteps = map.values()
                    .stream()
                    .flatMap(List::stream).toList();

            Map<Long, TreeNode> nodeMap = new HashMap<>();
            allChildJobComponentList.forEach((jc) -> {
                JobComponentPayload jcPayload = new JobComponentPayload(jc);
                nodeMap.put(jc.getId(), new TreeNode(jcPayload, jcPayload.parentId()));
                jc.getJobSteps().forEach(js -> nodeMap.put(js.getId(), new TreeNode(new JobStepPayload(js), jcPayload.pdName(), jcPayload.id())));
            });
            allChildJobComponentList.forEach((jc) -> {
                TreeNode jcCurrentNode = nodeMap.get(jc.getId());
                Long jcParentId = jc.getParentJobComponent() != null ? jc.getParentJobComponent().getId() : null;
                if (jcParentId != null && nodeMap.containsKey(jcParentId)) {
                    nodeMap.get(jcParentId).addChild(jcCurrentNode);
                } else {
                    pdNode.addChild(jcCurrentNode);
                }
                jc.getJobSteps().forEach(js -> {
                    TreeNode jsCurrentNode = nodeMap.get(js.getId());
                    Long jsParentId = jsCurrentNode.parentId;

                    if (jsParentId != null && nodeMap.containsKey(jsParentId)) {
                        nodeMap.get(jsParentId).addChild(jsCurrentNode);
                    } else {
                        pdNode.addChild(jsCurrentNode);
                    }
                });
            });
            currentJobSteps.forEach(mainJs -> pdNode.addChild(
                    new TreeNode(new JobStepPayload(mainJs), pd.name(), pdNode.id)
            ));
            rootNodes.add(pdNode);
        }

        return exportToExcelTree(rootNodes,
                Collections.unmodifiableMap(new LinkedHashMap<>() {{
                    put("Строка ЗК/Спрос", 70 * 256);
                    put("ДСЕ", 60 * 256);
                    put("Узел ПЛМ", 35 * 256);
                    put("Описание(заход)", 40 * 256);
                    put("План брутто", 12 * 256);
                    put("Выполнено", 14 * 256);
                    put("Трудоемкость", 14 * 256);
                    put("Дата начала", 12 * 256);
                    put("Дата завершения", 16 * 256);
                    put("РД начала", 14 * 256);
                    put("РД завершения", 15 * 256);
                }}));
    }

}
