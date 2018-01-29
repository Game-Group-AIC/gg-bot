package aic.gas.sc.gg_bot.replay_parser.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Configuration {

  @Builder.Default
  private boolean removeOutliersWhenNormalisingFeatures = true;

  @Builder.Default
  private int sampleStates = 20000;

  @Builder.Default
  private int batchSize = 2000;

  @Builder.Default
  private int iterations = 250;

  @Builder.Default
  private int clusters = 1500;

  @Builder.Default
  private int minReward = -1;

  @Builder.Default
  private int maxReward = 1;

  @Builder.Default
  private double learningRate = 0.01;

  @Builder.Default
  private double maxLikelihoodChange = 0.1;

  @Builder.Default
  private double gamma = 0.99;

  @Builder.Default
  private double beta = 10;

  @Builder.Default
  private int steps = 1000;

  @Builder.Default
  private long timeBudget = 1000 * 60 * 30;

}
