package aic.gas.sc.gg_bot.replay_parser.model.irl_rl;

import aic.gas.sc.gg_bot.abstract_bot.model.decision.OurProbabilisticPolicy;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.FunctionGradient;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OurGradientDescentSARSA extends GradientDescentSarsaLam {

  private final List<Action> actions;
  private final double exploration;

  public OurGradientDescentSARSA(double gamma, DifferentiableStateActionValue vfa, double learningRate, double lambda,
      List<Action> actions, double exploration) {
    //null domain - we don ot need it for our use case
    super(null, gamma, vfa, learningRate, lambda);
    this.actions = actions;
    this.exploration = exploration;
  }

  public OurProbabilisticPolicy getCurrentPolicy() {
    return new OurProbabilisticPolicy(vfa, exploration, actions);
  }

  public void learnFromEpisode(Episode episode, RewardFunction rewardFunction) {
    if (episode.maxTimeStep() <= 0) {
      throw new IllegalArgumentException("Provided episode is not valid.");
    }

    State initialState = episode.state(0);
    maxWeightChangeInLastEpisode = 0.;

    State curState = initialState;
    eStepCounter = 0;
    Map<Integer, EligibilityTraceVector> traces = new HashMap<>();

    Action action = episode.action(0);
    for (int i = 0; i < episode.maxTimeStep() - 1; i++) {

      //get Q-value and gradient
      double curQ = this.vfa.evaluate(curState, action);
      FunctionGradient gradient = this.vfa.gradient(curState, action);

      State nextState = episode.state(i + 1);

      //determine next Q-value for outcome state
      Action nextAction = episode.action(i + 1);
      double nextQV = 0.;
      if (i + 1 == episode.maxTimeStep()) {
        nextQV = this.vfa.evaluate(nextState, nextAction);
      }

      //manage option specifics
      double r = rewardFunction.reward(curState, action, nextState);
      double discount = this.gamma;
      int stepInc = 1;
      eStepCounter += stepInc;

      //compute function delta
      double delta = r + (discount * nextQV) - curQ;

      //manage replacing traces by zeroing out features for actions
      //also zero out selected action, since it will be put back in later code
      if (this.useReplacingTraces) {
        List<Action> allActions = this.applicableActions(curState);
        for (Action oa : allActions) {

          //get non-zero parameters and zero them
          this.vfa.evaluate(curState, oa);
          FunctionGradient ofg = this.vfa.gradient(curState, oa);
          for (FunctionGradient.PartialDerivative pds : ofg.getNonZeroPartialDerivatives()) {
            EligibilityTraceVector et = traces.get(pds.parameterId);
            if (et != null) {
              et.eligibilityValue = 0.;
            } else {
              //no trace for this yet, so add it
              et = new EligibilityTraceVector(pds.parameterId,
                  this.vfa.getParameter(pds.parameterId), 0.);
              traces.put(pds.parameterId, et);
            }
          }

        }
      } else {
        //if not using replacing traces, then add any new parameters whose traces need to be set, but set initially
        //at zero since it will be updated in the next loop
        for (FunctionGradient.PartialDerivative pds : gradient.getNonZeroPartialDerivatives()) {
          if (!traces.containsKey(pds.parameterId)) {
            traces.put(pds.parameterId,
                new EligibilityTraceVector(pds.parameterId, this.vfa.getParameter(pds.parameterId),
                    0.));
          }
        }

      }

      //scan through trace elements, update them, and update parameter
      double learningRate = 0.;
      if (!this.useFeatureWiseLearningRate) {
        learningRate = this.learningRate
            .pollLearningRate(this.totalNumberOfSteps, curState, action);
      }

      Set<Integer> deletedSet = new HashSet<>();
      for (EligibilityTraceVector et : traces.values()) {
        if (this.useFeatureWiseLearningRate) {
          learningRate = this.learningRate.pollLearningRate(this.totalNumberOfSteps, et.weight);
        }

        et.eligibilityValue += gradient.getPartialDerivative(et.weight);
        double newParam = vfa.getParameter(et.weight) + learningRate * delta * et.eligibilityValue;
        this.vfa.setParameter(et.weight, newParam);

        double deltaW = Math.abs(et.initialWeightValue - newParam);
        if (deltaW > maxWeightChangeInLastEpisode) {
          maxWeightChangeInLastEpisode = deltaW;
        }

        //now decay and delete from tracking if too small
        et.eligibilityValue *= this.lambda * discount;
        if (et.eligibilityValue < this.minEligibityForUpdate) {
          deletedSet.add(et.weight);
        }
      }

      //delete traces marked for deletion
      for (Integer t : deletedSet) {
        traces.remove(t);
      }

      //move on
      curState = nextState;
      action = nextAction;

      this.totalNumberOfSteps++;
    }
  }


}
