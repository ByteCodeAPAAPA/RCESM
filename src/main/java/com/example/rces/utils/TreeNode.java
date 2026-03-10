package com.example.rces.utils;

import com.example.rces.spm.controller.payload.JobComponentPayload;
import com.example.rces.spm.controller.payload.JobStepPayload;
import com.example.rces.spm.controller.payload.PrimaryDemandPayload;
import org.apache.commons.collections4.list.TreeList;

import java.util.Comparator;
import java.util.List;

import static com.example.rces.utils.DateUtil.formatedDate;

public class TreeNode {
    public Long id;
    public Long parentId;
    public String primaryDemand;
    public String item;
    public String mlmNode;
    public String description;
    public String qty;
    public String qtyFinished;
    public String resourceTime;
    public String dateStart;
    public String dateEnd;
    public String dateCalcStart;
    public String dateCalcEnd;
    public String type;
    public List<TreeNode> children = new TreeList<>();

    public void addChild(TreeNode child) {
        this.children.add(child);
        this.children.sort(Comparator.comparing(node -> node.type));
    }


    public TreeNode(PrimaryDemandPayload pd) {
        this.primaryDemand = pd.name();
        this.item = pd.jobComponent().name();
        this.mlmNode = "";
        this.description = "";
        this.qty = pd.jobComponent().qty().toString();
        this.qtyFinished = pd.jobComponent().qtyFinished().toString();
        this.resourceTime = "";
        this.dateStart = formatedDate(pd.jobComponent().dateStart());
        this.dateEnd = formatedDate(pd.jobComponent().dateEnd());
        this.dateCalcStart = "";
        this.dateCalcEnd = "";
        this.id = pd.jobComponent().id();
        this.parentId = -1L;
        this.type = "pd";
    }

    public TreeNode(JobComponentPayload jc, Long parentId) {
        this.primaryDemand = jc.pdName();
        this.item = jc.name();
        this.mlmNode = "";
        this.description = "";
        this.qty = jc.qty().toString();
        this.qtyFinished = jc.qtyFinished().toString();
        this.resourceTime = "";
        this.dateStart = formatedDate(jc.dateStart());
        this.dateEnd = formatedDate(jc.dateEnd());
        this.dateCalcStart = "";
        this.dateCalcEnd = formatedDate(jc.dateCalcEnd());
        this.id = jc.id();
        this.parentId = parentId;
        this.type = "jc";
    }

    public TreeNode(JobStepPayload js, String pdName, Long parentId) {
        this.primaryDemand = pdName;
        this.item = js.name();
        this.mlmNode = js.mlmNode();
        this.description = js.description();
        this.qty = js.qty().toString();
        this.qtyFinished = js.qtyFinished().toString();
        this.resourceTime = String.valueOf(js.resourceTime());
        this.dateStart = formatedDate(js.dateStart());
        this.dateEnd = formatedDate(js.dateEnd());
        this.dateCalcStart = formatedDate(js.dateCalcStart());
        this.dateCalcEnd = formatedDate(js.dateCalcEnd());
        this.id = js.id();
        this.parentId = parentId;
        this.type = "js";
    }

}
