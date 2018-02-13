package aic.gas.sc.gg_bot.replay_parser.service;

import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureNormalizer;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import java.util.List;

public interface IFeatureNormalizerService {

  List<FeatureNormalizer> computeFeatureNormalizersBasedOnStates(List<State> states,
      String[] headers);

}
