package gt.trading.huobi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Deque;
import java.util.LinkedList;

public class FeatureGraph {
  private final InputNode[] inputNodes;
  private final InferenceNode[] orderedInferenceNodes;
  private final int inputNodeCount;
  private final int inferenceNodeCount;

  public static class Builder {
    // Builder Variables
    private Map<String, FeatureNode> nameToNode = new HashMap<>();
    private List<InferenceNode> inferenceNodes = new ArrayList<>();

    // TopoSort Variables
    private final Map<String, List<String>> adjList = new HashMap<>();
    private Map<String, Integer> state = new HashMap<>();

    // FeatureGraph Variables
    private List<InputNode> inputNodes = new ArrayList<>();
    private Deque<InferenceNode> nodesList = new LinkedList<>(); // no idea how
                                                                 // to name
    private int inputNodeCount = 0;
    private int inferenceNodeCount = 0;

    public Builder register(InputNode node) {
      nameToNode.put(node.name, node);
      inputNodes.add(node);
      inputNodeCount += 1;
      return this;
    }

    public Builder register(InferenceNode node) {
      nameToNode.put(node.name, node);
      inferenceNodes.add(node);
      inferenceNodeCount += 1;
      adjList.put(node.name, new ArrayList<>());
      return this;
    }

    public FeatureGraph build() throws Exception {
      // assign parents
      for (InferenceNode node : inferenceNodes) {
        FeatureNode[] parents = new FeatureNode[node.numParents];
        for (int i = 0; i < node.numParents; i++) {
          String parentName = node.parentNames[i];
          if (!nameToNode.containsKey(parentName))
            throw new Exception("Feature name: " + parentName + " not found.");

          if (nameToNode.get(parentName) instanceof InferenceNode) {
            if (!adjList.containsKey(parentName))
              throw new Exception(
                  "Encountered inference node without adj list.");
            adjList.get(parentName).add(node.name);
          }
          parents[i] = nameToNode.get(parentName);
        }
        node.setParents(parents);
      }

      // topological sort
      for (InferenceNode node : inferenceNodes) {
        if (!state.containsKey(node.name)) {
          dfs(node.name);
        } // check for state==1 errors?
      }

      return new FeatureGraph(this);
    }

    private void dfs(String name) throws Exception {
      if (!nameToNode.containsKey(name))
        throw new Exception("Node not found.");
      if (nameToNode.get(name) instanceof InputNode)
        throw new Exception("InferenceNode parent of InputNode.");
      if (!state.containsKey(name)) {
        state.put(name, 1);
      } else if (state.get(name) == 1) {
        throw new Exception("Cycle detected in feature graph.");
      } else if (state.get(name) == 2) {
        return;
      } else {
        throw new Exception("Unrecognized state in dfs.");
      }
      for (String succName : adjList.get(name)) {
        dfs(succName);
      }
      nodesList.addFirst((InferenceNode) nameToNode.get(name));
      state.put(name, 2);
    }
  }

  private FeatureGraph(Builder builder) throws Exception {
    this.orderedInferenceNodes = builder.nodesList
        .toArray(new InferenceNode[builder.inferenceNodeCount]);
    this.inputNodes = builder.inputNodes
        .toArray(new InputNode[builder.inputNodeCount]);
    this.inferenceNodeCount = builder.inferenceNodeCount;
    this.inputNodeCount = builder.inputNodeCount;
  }

  public void updateInferenceNodes() {
    for (InferenceNode node : orderedInferenceNodes) {
      node.onUpdate();
    }
  }

  // ! Could change way of passing values
  public void updateInputNodes(double[] newValues) {
    assert newValues.length == inputNodeCount;
    for (int i = 0; i < inputNodeCount; i++) {
      inputNodes[i].onUpdate(newValues[i]);
    }
  }

  public void printGraph() {
    System.out.println("----------------");
    for (InputNode node : inputNodes) {
      node.printNode();
    }
    System.out.println("---");
    for (InferenceNode node : orderedInferenceNodes) {
      node.printNode();
    }
    System.out.println("----------------");
  }

  public void printInputNodeNames() {
    for (int i = 0; i < inputNodeCount; i++) {
      System.out.println(i + ": " + inputNodes[i].name);
    }
  }
}
