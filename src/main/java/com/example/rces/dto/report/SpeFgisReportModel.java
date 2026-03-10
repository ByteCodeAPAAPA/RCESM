package com.example.rces.dto.report;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.example.rces.utils.DateUtil.parseLocalDate;

public class SpeFgisReportModel {

    private String miTypeNumber;

    private String miTypeType;

    private String miTypeTitle;

    private String miNumber;

    private String miModification;

    private String organization;

    private String signCipher;

    private String miOwner;

    private String vrfDate;

    private String validDate;

    private String docTitle;

    private String suitable;

    private String certNum;

    private String signPass;

    private String signMi;

    private String mieta;

    private String mis;

    private String structure;

    private String briefIndicator;

    public SpeFgisReportModel(JsonNode data) {
        var singleMI = data.path("miInfo").path("singleMI");
        var vriInfo = data.path("vriInfo");
        var applicable = vriInfo.path("applicable");
        var means = data.path("means");
        var info = data.path("info");

        setMiTypeNumber(singleMI.path("mitypeNumber").asText());
        setMiTypeType(singleMI.path("mitypeType").asText());
        setMiTypeTitle(singleMI.path("mitypeTitle").asText());
        setMiNumber(singleMI.path("manufactureNum").asText());
        setMiModification(singleMI.path("modification").asText());
        setOrganization(vriInfo.path("organization").asText());
        setSignCipher(vriInfo.path("signCipher").asText());
        setMiOwner(vriInfo.path("miOwner").asText());
        setVrfDate(vriInfo.path("vrfDate").asText());
        setValidDate(vriInfo.path("validDate").asText());
        setDocTitle(vriInfo.path("docTitle").asText());
        setSuitable(
                !vrfDate.isEmpty() ?
                        !parseLocalDate(vrfDate).isBefore(LocalDate.now()) ? "Да" : "Нет"
                        : "Нет"
        );
        setCertNum(applicable.path("certNum").asText());
        setSignPass(applicable.path("signPass").asBoolean() ? "Да" : "Нет");
        setSignMi(applicable.path("signMi").asBoolean() ? "Да" : "Нет");
        setMieta(StreamSupport.stream(means.path("mieta").spliterator(), false)
                .map(item -> Stream.of(
                                "regNumber", "mietaURL", "mitypeNumber", "mitypeURL", "mitypeTitle",
                                "notation", "modification", "manufactureNum", "manufactureYear",
                                "rankCode", "rankTitle", "schemaTitle"
                        )
                        .map(field -> item.path(field).asText())
                        .collect(Collectors.joining(";")))
                .collect(Collectors.joining("]\n")));
        setMis(StreamSupport.stream(means.path("mis").spliterator(), false)
                .map(item ->
                        item.path("mitypeNumber").asText() + "\n" +
                                item.path("mitypeURL").asText() + "\n" +
                                item.path("mitypeTitle").asText() + "\n" +
                                item.path("number").asText()
                )
                .collect(Collectors.joining("\n\n")));
        setStructure(info.path("structure").asText());
        setBriefIndicator(info.path("briefIndicator").asBoolean() ? "Да" : "Нет");
    }

    public String getMiTypeNumber() {
        return miTypeNumber;
    }

    public void setMiTypeNumber(String miTypeNumber) {
        this.miTypeNumber = miTypeNumber;
    }

    public String getMiTypeType() {
        return miTypeType;
    }

    public void setMiTypeType(String miTypeType) {
        this.miTypeType = miTypeType;
    }

    public String getMiTypeTitle() {
        return miTypeTitle;
    }

    public void setMiTypeTitle(String miTypeTitle) {
        this.miTypeTitle = miTypeTitle;
    }

    public String getMiNumber() {
        return miNumber;
    }

    public void setMiNumber(String miNumber) {
        this.miNumber = miNumber;
    }

    public String getMiModification() {
        return miModification;
    }

    public void setMiModification(String miModification) {
        this.miModification = miModification;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSignCipher() {
        return signCipher;
    }

    public void setSignCipher(String signCipher) {
        this.signCipher = signCipher;
    }

    public String getMiOwner() {
        return miOwner;
    }

    public void setMiOwner(String miOwner) {
        this.miOwner = miOwner;
    }

    public String getVrfDate() {
        return vrfDate;
    }

    public void setVrfDate(String vrfDate) {
        this.vrfDate = vrfDate;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getSuitable() {
        return suitable;
    }

    public void setSuitable(String suitable) {
        this.suitable = suitable;
    }

    public String getCertNum() {
        return certNum;
    }

    public void setCertNum(String certNum) {
        this.certNum = certNum;
    }

    public String getSignPass() {
        return signPass;
    }

    public void setSignPass(String signPass) {
        this.signPass = signPass;
    }

    public String getSignMi() {
        return signMi;
    }

    public void setSignMi(String signMi) {
        this.signMi = signMi;
    }

    public String getMieta() {
        return mieta;
    }

    public void setMieta(String mieta) {
        this.mieta = mieta;
    }

    public String getMis() {
        return mis;
    }

    public void setMis(String mis) {
        this.mis = mis;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getBriefIndicator() {
        return briefIndicator;
    }

    public void setBriefIndicator(String briefIndicator) {
        this.briefIndicator = briefIndicator;
    }
}
