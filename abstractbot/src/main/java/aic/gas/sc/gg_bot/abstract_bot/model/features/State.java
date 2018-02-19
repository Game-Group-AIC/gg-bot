package aic.gas.sc.gg_bot.abstract_bot.model.features;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(of = "featureVector")
@Getter
public class State {

  private final double[] featureVector;
}
