package com.example.rces.init;

import com.example.rces.dto.DocumentCreateDTO;
import com.example.rces.models.ExecutedRunOnceScripts;
import com.example.rces.models.SPE;
import com.example.rces.service.EmployeeService;
import com.example.rces.service.ReportService;
import com.example.rces.service.SpeService;
import com.example.rces.utils.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Init {

    @PersistenceContext
    private EntityManager em;
    private final TransactionTemplate transactionTemplate;
    private List<String> runOnceList = new ArrayList<>();
    protected static Logger log = LoggerFactory.getLogger(Init.class);

    private final SpeService speService;
    private final ReportService reportService;
    private final ApiClient apiClient;
    private final EmployeeService employeeService;

    @Autowired
    public Init(TransactionTemplate transactionTemplate, SpeService speService, ReportService reportService, ApiClient apiClient, EmployeeService employeeService) {
        this.transactionTemplate = transactionTemplate;
        this.speService = speService;
        this.reportService = reportService;
        this.apiClient = apiClient;
        this.employeeService = employeeService;
    }

    @PostConstruct
    protected void initialize() {
        this.updateRunOnceListCache();
        try {
            this.employeeService.setSecurityContext(employeeService.loadUserByUsername("system"));
            this.init();
        } catch (Exception e) {
            throw new RuntimeException("Error PostConstruct in Init", e);
        }
    }

    private void init() {
        log.info("Initialization tasks");
        runOnce("#1-Recalculation date metrology", speService::calculateDateVerification, true);
//        runOnce("#2-Install documents in SPE", () -> installDocumentOnSPE(false), true);
        runOnce("#3-Set organization in SPE", this::setOrganizationOnSPE, false);
    }

    private void installDocumentOnSPE(Boolean reinstall) {
        List<SPE> speList = em.createQuery(
                "SELECT e FROM SPE e" + (!reinstall ? " WHERE e.document.id IS NULL" : ""),
                SPE.class
        ).getResultList();
        Map<String, SPE> speByOutNumber = speList.stream()
                .collect(Collectors.toMap(
                        SPE::getOutNumber,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        List<JsonNode> fgisData = apiClient.getFgisData(new ArrayList<>(speByOutNumber.keySet()));
        if (fgisData.isEmpty()) {
            return;
        }
        for (JsonNode data : fgisData) {
            try {
                String outNumber = data.path("miInfo").path("singleMI").path("manufactureNum").asText();
                SPE spe = speByOutNumber.get(outNumber);
                if (spe!=null) {
                    speService.createSpeDocument(spe, new DocumentCreateDTO(), reportService.createSpeFgisReport(data));
                    log.info("Installed document in {}", spe.getOutNumber());
                } else {
                    log.info("SPE is NULL for {}", outNumber);
                }
            } catch (Exception e) {
                throw new ApplicationContextException("Error at initialization installations document in SPE", e);
            }
        }
    }

//    private void test() {
//        List<SPE> speList = em.createQuery(
//                "SELECT e FROM SPE e WHERE e.id = ''",
//                SPE.class
//        ).getResultList();
//    }

    private void setOrganizationOnSPE() {
        List<SPE> speList = em.createQuery(
                "SELECT e FROM SPE e WHERE e.organization IS NULL",
                SPE.class
        ).getResultList();
        Map<String, SPE> speByOutNumber = speList.stream()
                .collect(Collectors.toMap(
                        SPE::getOutNumber,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        List<JsonNode> fgisData = apiClient.getFgisData(new ArrayList<>(speByOutNumber.keySet()));
        if (fgisData.isEmpty()) {
            return;
        }
        for (JsonNode data : fgisData) {
            try {
                String outNumber = data.path("miInfo").path("singleMI").path("manufactureNum").asText();
                SPE spe = speByOutNumber.get(outNumber);
                if (spe!=null) {
                    speService.setOrganizationWithFgis(spe, data);
                    log.info("Organization installed in {}", spe.getOutNumber());
                } else {
                    log.info("SPE is NULL for {}", outNumber);
                }
            } catch (Exception e) {
                throw new ApplicationContextException("Error at initialization set organization in SPE", e);
            }
        }
    }

    private void runOnce(String id, Runnable task, Boolean always) {
        if (always || this.checkRunOnce(id)) {
            transactionTemplate.execute(status -> {
                try {
                    log.info("Execution task {}", id);
                    task.run();
                    this.markRunOnce(id, always);
                } catch (Exception e) {
                    log.error("Error at execution task: {}", id, e);
                    status.setRollbackOnly();
                    throw new RuntimeException("Error at initialization task", e);
                }
                return null;
            });
        }
    }

    private void markRunOnce(String id, Boolean always) {
        ExecutedRunOnceScripts s = new ExecutedRunOnceScripts();
        s.setName(id + (always ? Instant.now() : ""));
        em.persist(s);
    }

    protected boolean checkRunOnce(String id) {
        return !this.runOnceList.contains(id);
    }

    private void updateRunOnceListCache() {
        this.runOnceList = this.em.createNativeQuery("select e.name from rces_history.executed_run_once_scripts e", String.class).getResultList();
    }
}
