package aic.gas.sc.gg_bot.replay_parser.model.clustering.pso;

import java.util.List;

public interface IFitnessFunctionEvaluationStrategy {
  double evaluate(List<Centroid> centroids);
}
