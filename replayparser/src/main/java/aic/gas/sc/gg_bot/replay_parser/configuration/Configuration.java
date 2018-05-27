package aic.gas.sc.gg_bot.replay_parser.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Configuration {

  @Builder.Default
  private int nTilings = 5;

  @Builder.Default
  private double resolution = 10;

  @Builder.Default
  private int heightOfTree = 3;

  @Builder.Default
  private int treeMaximalSize = 50;

  @Builder.Default
  private int treeMinimalSize = 5;

  @Builder.Default
  private double defaultQ = 0.5;

  @Builder.Default
  private int folds = 5;

  @Builder.Default
  private double gamma = 0.99;

  @Builder.Default
  private double learningRate = 0.02;

  @Builder.Default
  private double lambda = 0.5;

  @Builder.Default
  private double exploration = 0.05;

  @Builder.Default
  private int numberOfIterations = 100;

}
