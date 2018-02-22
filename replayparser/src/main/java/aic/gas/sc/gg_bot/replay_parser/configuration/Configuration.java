package aic.gas.sc.gg_bot.replay_parser.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Configuration {

  @Builder.Default
  private int batchSize = 100;

  @Builder.Default
  private int iterations = 20;

  @Builder.Default
  private int clusters = 300;

  //set last "dummy state" to large negative number as we do not want to go there
  @Builder.Default
  private int minReward = -1;

  @Builder.Default
  private int maxReward = 1;

  @Builder.Default
  private int irlNoChangeStopCondition = 10;

  @Builder.Default
  private double initialLearningRate = 0.1;

  @Builder.Default
  private boolean staticLearningRate = false;

  @Builder.Default
  private double dropLearningRate = 0.5;

  @Builder.Default
  private int dropAfterIterations = 100;

  @Builder.Default
  private double noiseForLearningReward = 0.001;

  @Builder.Default
  private double maxLikelihoodChange = 0.01;

  @Builder.Default
  private double gamma = 0.99;

  @Builder.Default
  private double beta = 10;

  @Builder.Default
  private int steps = 50;

  @Builder.Default
  private long timeBudget = 1000 * 60 * 5;

  @Builder.Default
  private int countOfTrajectoriesPerIRLBatch = 20;

  @Builder.Default
  private int multiplierOfRewardForDeadEnd = 4;

  @Builder.Default
  private PSOParameters psoParameters = PSOParameters.builder().build();

  @Builder.Default
  private boolean usePSOForClustering = true;

  @Override
  public String toString() {
    return "Utils{" +
        "batchSize=" + batchSize +
        ", iterations=" + iterations +
        ", clusters=" + clusters +
        ", minReward=" + minReward +
        ", maxReward=" + maxReward +
        ", initialLearningRate=" + initialLearningRate +
        ", staticLearningRate=" + staticLearningRate +
        ", dropLearningRate=" + dropLearningRate +
        ", dropAfterIterations=" + dropAfterIterations +
        ", noiseForLearningReward=" + noiseForLearningReward +
        ", maxLikelihoodChange=" + maxLikelihoodChange +
        ", gamma=" + gamma +
        ", beta=" + beta +
        ", steps=" + steps +
        ", timeBudget=" + timeBudget +
        ", countOfTrajectoriesPerIRLBatch=" + countOfTrajectoriesPerIRLBatch +
        ", multiplierOfRewardForDeadEnd=" + multiplierOfRewardForDeadEnd +
        ", psoParameters=" + psoParameters +
        ", usePSOForClustering=" + usePSOForClustering +
        '}';
  }
}
