package aic.gas.sc.gg_bot.replay_parser.model.irl;

import burlap.behavior.policy.BoltzmannQPolicy;
import burlap.behavior.policy.support.ActionProb;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.QValue;
import burlap.datastructures.BoltzmannDistribution;
import burlap.mdp.core.state.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OurBoltzmannQPolicy extends BoltzmannQPolicy {

  private final double temperature;
  private static final Random RANDOM = new Random();
  private final double noise;

  public OurBoltzmannQPolicy(QProvider planner, double temperature, double noise) {
    super(planner, temperature);
    this.temperature = temperature;
    this.noise = noise;
  }

  public List<ActionProb> policyDistribution(State s) {
    List<QValue> qValues = this.qplanner.qValues(s);
    return this.getActionDistributionForQValues(s, qValues);
  }

  private List<ActionProb> getActionDistributionForQValues(State queryState, List<QValue> qValues) {
    List<ActionProb> res = new ArrayList<>();
    double[] rawQs = new double[qValues.size()];

    for (int i = 0; i < qValues.size(); ++i) {
      rawQs[i] = qValues.get(i).q;
    }

    BoltzmannDistribution bd = new BoltzmannDistribution(rawQs, this.temperature);
    double[] probs = bd.getProbabilities();

    //add random noise to current policy
    for (int i = 0; i < probs.length; i++) {
      probs[i] = probs[i] + ((RANDOM.nextBoolean() ? 1 : -1) * (RANDOM.nextDouble() * noise));
    }
    perturbProbabilityDistribution(probs);

    for (int i = 0; i < qValues.size(); ++i) {
      QValue q = qValues.get(i);
      ActionProb ap = new ActionProb(q.a, probs[i]);
      res.add(ap);
    }

    return res;
  }

  private void perturbProbabilityDistribution(double[] probabilities) {

    //make sure all probabilities are non-zero
    if (Arrays.stream(probabilities)
        .anyMatch(value -> value <= 0)) {
      IntStream.range(0, probabilities.length)
          .filter(value -> probabilities[value] <= 0)
          //set value to random noise
          .forEach(value -> probabilities[value] = RANDOM.nextDouble() * noise);
    }

    //lower probability over one (included)
    if (Arrays.stream(probabilities)
        .anyMatch(value -> value >= 1)) {
      IntStream.range(0, probabilities.length)
          .filter(value -> probabilities[value] >= 1)
          //set value to random noise
          .forEach(value -> probabilities[value] = 1.0 - (RANDOM.nextDouble() * noise));
    }

    //change policy to sum to one - split rest between probs.
    if (Arrays.stream(probabilities).sum() != 1.0) {
      double toAdd = 1.0 - Arrays.stream(probabilities).sum();
      Set<Integer> indexesToIgnore = IntStream.range(0, probabilities.length)
          .filter(value -> {
            double newProb = (probabilities[value] + (toAdd / probabilities.length));
            return newProb >= 1.0 || newProb <= 0.0;
          })
          .boxed()
          .collect(Collectors.toSet());
      for (int i = 0; i < probabilities.length; i++) {
        if (!indexesToIgnore.contains(i)) {
          probabilities[i] = probabilities[i] + (toAdd / probabilities.length);
        }
      }

      //correct probability if needed
      if (Arrays.stream(probabilities)
          .anyMatch(value -> value >= 1 || value <= 0)) {
        perturbProbabilityDistribution(probabilities);
      }
    }
  }

}
