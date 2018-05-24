package aic.gas.sc.gg_bot.abstract_bot.model.decision;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;

public interface IVFInitializer {

  DifferentiableStateActionValue initialize();
}
