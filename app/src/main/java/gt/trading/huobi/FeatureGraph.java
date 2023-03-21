package gt.trading.huobi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FeatureGraph {
  private final InputNode[] inputNodes;
  private final InferenceNode[] orderedInferenceNodes;
  private final int nodeCount;

  public static class Builder {
    private Map<String, FeatureNode> nameToNode = new HashMap<>();
    private List<InputNode> inputNodes = new ArrayList<>();
    private List<InferenceNode> inferenceNodes = new ArrayList<>();
    private int nodeCount = 0;

    public Builder register(InputNode node) {
      nameToNode.put(node.name, node);
      inputNodes.add(node);
      nodeCount += 1;
      return this;
    }

    public Builder register(InferenceNode node) {
      nameToNode.put(node.name, node);
      inferenceNodes.add(node);
      nodeCount += 1;
      return this;
    }

    public FeatureGraph build() throws Exception {
      return new FeatureGraph(this);
    }
  }

  private FeatureGraph(Builder builder) throws Exception {
    // assign all parents
    for (InferenceNode inferenceNode : builder.inferenceNodes) {
      FeatureNode[] parents = new FeatureNode[inferenceNode.numParents];
      for (int i = 0; i < inferenceNode.numParents; i++) {
        String parentName = inferenceNode.parentNames[i];
        if (!builder.nameToNode.containsKey(parentName))
          throw new Exception("Feature name: " + parentName + " not found.");
        parents[i] = builder.nameToNode.get(parentName);
      }
      inferenceNode.setParents(parents);
    }
    this.inputNodes = builder.inputNodes
        .toArray(new InputNode[builder.nodeCount]);
    this.nodeCount = builder.nodeCount;
    // toposort
    orderedInferenceNodes = null;
  }

  public void updateInferenceNodes() {
    for (InferenceNode node : orderedInferenceNodes) {
      node.onUpdate();
    }
  }
}
