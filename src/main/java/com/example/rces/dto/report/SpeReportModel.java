package com.example.rces.dto.report;

import com.example.rces.models.SPE;

import java.util.ArrayList;
import java.util.List;

public class SpeReportModel {

    private List<Table> lines = new ArrayList<>();

    public List<Table> getLines() {
        return lines;
    }

    public void setLines(List<Table> lines) {
        this.lines = lines;
    }

    public SpeReportModel(List<SPE> speList) {
        for (SPE spe : speList) {
            Table table = new Table();
            table.setName(spe.getName());
            table.setType(spe.getType());
            table.setNumber(String.valueOf(spe.getOutNumber()));
            table.setEmployee(spe.getEmployee().getName());
            this.lines.add(table);
        }
    }

    public static class Table {

        private String name;

        private String type;

        private String number;

        private String employee;

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

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getEmployee() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }
    }

}
