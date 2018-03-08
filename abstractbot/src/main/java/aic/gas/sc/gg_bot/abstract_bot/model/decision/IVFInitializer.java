package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;

public interface IVFInitializer {

  DifferentiableStateActionValue initialize();
}
