package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import java.util.Random;

/**
 * DecisionDomainGenerator generates domain describing decision
 */
public class DecisionDomainGenerator implements DomainGenerator {

  private static final Random RANDOM = new Random();
  static final String VAR_STATE = "state";
  private final DecisionModel model;
  private final Configuration configuration;

  public DecisionDomainGenerator(DecisionModel model, Configuration configuration) {
    this.model = model;
    this.configuration = configuration;
  }

  @Override
  public SADomain generateDomain() {
    SADomain domain = new SADomain();
    domain.addActionTypes(new UniversalActionType(NextActionEnumerations.YES.name()),
        new UniversalActionType(NextActionEnumerations.NO.name()));

    //unknown reward
    RewardFunction rf = (state, action, state1) -> getRandomRewardInInterval(configuration);

    //no terminal state
    TerminalFunction tf = state -> false;

    domain.setModel(new FactoredModel(model, rf, tf));

    return domain;
  }

  public static double getRandomRewardInInterval(Configuration configuration) {
    return configuration.getMinReward()
        + (configuration.getMaxReward() - configuration.getMinReward()) * RANDOM.nextDouble();
  }

}
