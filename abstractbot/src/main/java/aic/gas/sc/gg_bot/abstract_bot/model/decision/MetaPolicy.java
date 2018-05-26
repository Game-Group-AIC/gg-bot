package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.mdp.core.action.Action;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

public class MetaPolicy extends StateBuilder implements Serializable {

  //always same set of actions
  public static final transient List<Action> ACTIONS = Arrays
      .stream(NextActionEnumerations.values())
      .collect(Collectors.toList());

  //weights of features
  private final Map<Integer, Double> weights;
  private final int nTilings;
  private final double resolution;
  private final double exploration;
  private final double defaultQ;

  @Builder
  public MetaPolicy(StateBuilder stateBuilder, Map<Integer, Double> weights, int nTilings,
      double resolution, double exploration, double defaultQ) {
    super(stateBuilder.normalizers);
    this.weights = weights;
    this.nTilings = nTilings;
    this.resolution = resolution;
    this.exploration = exploration;
    this.defaultQ = defaultQ;
  }

  //create a VFA
  public static DifferentiableStateActionValue initializeVFA(StateBuilder stateBuilder,
      int nTilings, double resolution, double defaultQ) {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
        .addObjectVectorizion(OurState.NAME, new NumericVariableFeatures());
    TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);

    double[] dimensions = Arrays.stream(stateBuilder.normalizers)
        .mapToDouble(value -> value.range() / resolution)
        .toArray();
    tilecoding.addTilingsForAllDimensionsWithWidths(dimensions, nTilings,
        TilingArrangement.RANDOM_JITTER);

    return tilecoding.generateVFA(defaultQ / nTilings);
  }

  public OurProbabilisticPolicy createPolicy() {
    DifferentiableStateActionValue vfa = initializeVFA(this, nTilings, resolution, defaultQ);
    weights.forEach(vfa::setParameter);
    return new OurProbabilisticPolicy(vfa, exploration, ACTIONS);
  }

}
