package aic.gas.sc.gg_bot.replay_parser.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PSOParameters {

  @Builder.Default
  private final double learningRateLoc = 2;

  @Builder.Default
  private final double learningRateGlo = 2;

  @Builder.Default
  private final int countOfParticles = 50;

  @Builder.Default
  private final int neighborhoodSize = 5;

  @Builder.Default
  private final int noImprovmentTerminationCondition = 2;

  @Builder.Default
  private final int clusteringRunsForInitialization = 5;

  @Override
  public String toString() {
    return "PSOParameters{" +
        "learningRateLoc=" + learningRateLoc +
        ", learningRateGlo=" + learningRateGlo +
        ", countOfParticles=" + countOfParticles +
        ", neighborhoodSize=" + neighborhoodSize +
        ", noImprovmentTerminationCondition=" + noImprovmentTerminationCondition +
        ", clusteringRunsForInitialization=" + clusteringRunsForInitialization +
        '}';
  }
}
