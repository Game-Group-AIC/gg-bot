package aic.gas.sc.gg_bot.replay_parser.model.irl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;

/**
 * DecisionDomainGenerator generates domain describing decision
 */
public class DecisionDomainGenerator implements DomainGenerator {

  public static final int defaultReward = 0;
  static final String VAR_STATE = "state";
  private final DecisionModel model;

  public DecisionDomainGenerator(DecisionModel model) {
    this.model = model;
  }

  @Override
  public SADomain generateDomain() {
    SADomain domain = new SADomain();
    domain.addActionTypes(
        new UniversalActionType(NextActionEnumerations.YES.name()),
        new UniversalActionType(NextActionEnumerations.NO.name()));

    //unknown reward
    RewardFunction rf = (state, action, state1) -> defaultReward;

    //no terminal state
    TerminalFunction tf = state -> false;

    domain.setModel(new FactoredModel(model, rf, tf));

    return domain;
  }
}
