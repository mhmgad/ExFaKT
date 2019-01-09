package extendedsldnf.viewing;

import extendedsldnf.datastructure.EvidenceNode;

import java.util.List;

public class ExecutionNode {

    String parentId;

    String id;

    EvidenceNode evidenceNode;

    List<ExecutionNode> children;

    int level;

    public ExecutionNode(String parentId, String id, EvidenceNode evidenceNode, int level) {
        this.parentId = parentId;
        this.id = id;
        this.evidenceNode = evidenceNode;
        this.level = level;
    }
}
