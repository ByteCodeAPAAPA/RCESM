package com.example.rces.dto.report;

import com.example.rces.models.SPE;
import com.example.rces.models.enums.OrganizationSPE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.rces.utils.DateUtil.formatedDate;

public class SpeScheduleReportModel {

    private String organization;

    private String position;

    private String verifier;

    private String now;

    private List<SpeScheduleReportModel.Table> lines = new ArrayList<>();

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public List<Table> getLines() {
        return lines;
    }

    public void setLines(List<Table> lines) {
        this.lines = lines;
    }

    public SpeScheduleReportModel(List<SPE> data) {
        OrganizationSPE organization = data.get(0).getOrganization();
        setOrganization(organization.getName());
        setPosition(organization.getPosition());
        setVerifier(organization.getVerifier());
        setNow(formatedDate(LocalDate.now()));

        for (SPE spe : data) {
            SpeScheduleReportModel.Table table = new SpeScheduleReportModel.Table();
            table.setSubDivision(spe.getSubDivision().getName());
            table.setName(spe.getName());
            table.setType(spe.getType());
            table.setOutNumber(spe.getOutNumber());
            table.setAccuracyClass(spe.getAccuracyClass());
            table.setLimitMeasurement(spe.getLimitMeasurement());
            table.setPeriodicity(spe.getPeriodicity());
            table.setDateVerification(formatedDate(spe.getDateVerification()));
            this.lines.add(table);
        }
    }

    public static class Table {

        private String subDivision;

        private String name;

        private String type;

        private String outNumber;

        private String accuracyClass;

        private String limitMeasurement;

        private Integer periodicity;

        private String organization;

        private String dateVerification;

        public String getSubDivision() {
            return subDivision;
        }

        public void setSubDivision(String subDivision) {
            this.subDivision = subDivision;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOutNumber() {
            return outNumber;
        }

        public void setOutNumber(String outNumber) {
            this.outNumber = outNumber;
        }

        public String getAccuracyClass() {
            return accuracyClass;
        }

        public void setAccuracyClass(String accuracyClass) {
            this.accuracyClass = accuracyClass;
        }

        public String getLimitMeasurement() {
            return limitMeasurement;
        }

        public void setLimitMeasurement(String limitMeasurement) {
            this.limitMeasurement = limitMeasurement;
        }

        public Integer getPeriodicity() {
            return periodicity;
        }

        public void setPeriodicity(Integer periodicity) {
            this.periodicity = periodicity;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getDateVerification() {
            return dateVerification;
        }

        public void setDateVerification(String dateVerification) {
            this.dateVerification = dateVerification;
        }
    }

}
