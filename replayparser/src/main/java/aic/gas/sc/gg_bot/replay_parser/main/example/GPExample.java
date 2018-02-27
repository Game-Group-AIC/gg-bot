package aic.gas.sc.gg_bot.replay_parser.main.example;/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */

import static java.lang.Math.abs;
import static java.lang.String.format;

import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.Tree;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
@Slf4j
public class GPExample {

  // Lookup table for 4*x^3 - 3*x^2 + x
  static final double[][] SAMPLES = new double[][]{
      {-1.0, 1, -7.0000},
      {-0.9, 0, -6.2460},
      {-0.8, 0, -4.7680},
      {-0.7, 0, -3.5420},
      {-0.6, 0, -2.5440},
      {-0.5, 0, -1.7500},
      {-0.4, 0, -1.1360},
      {-0.3, 0, -0.6780},
      {-0.2, 0, -0.3520},
      {-0.1, 0, -0.1340},
      {0.0, 0, 0.0000},
      {0.1, -2, -2.0740},
      {0.2, 0, 0.1120},
      {0.3, 0, 0.1380},
      {0.4, 1, 1.1760},
      {0.5, 0, 0.2500},
      {0.6, 0, 0.3840},
      {0.7, 0, 0.6020},
      {0.8, 1, 1.9280},
      {0.9, 0, 1.3860},
      {1.0, -2, 0.0000}
  };

  // The function we want to determine.
  static double f(final double x, double y) {
    return 4 * x * x * x - 3 * x * x + x + y;
  }

  // Definition of the allowed operations.
  static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
      MathOp.ADD,
      MathOp.SUB,
      MathOp.MUL
  );

  // Definition of the terminals.
  static final ISeq<Op<Double>> TERMINALS = ISeq.of(
      Var.of("x", 0),
      Var.of("y", 1)
  );

  static double error(final ProgramGene<Double> program) {
    return Arrays.stream(SAMPLES)
        .mapToDouble(sample ->
            abs(sample[2] - program.eval(sample[0], sample[1])) +
                program.size() * 0.00001)
        .sum();
  }

  static final Codec<ProgramGene<Double>, ProgramGene<Double>> CODEC =
      Codec.of(
          Genotype.of(ProgramChromosome.of(
              5,
              ch -> ch.getRoot().size() <= 50,
              OPERATIONS,
              TERMINALS
          )),
          Genotype::getGene
      );

  public static void main(final String[] args) {
    final Engine<ProgramGene<Double>, Double> engine = Engine
        .builder(GPExample::error, CODEC)
        .minimizing()
        .alterers(
            new SingleNodeCrossover<>(),
            new Mutator<>())
        .build();

    final ProgramGene<Double> program = engine.stream()
        .limit(2000)
        .peek(programGeneDoubleEvolutionResult -> log.info(programGeneDoubleEvolutionResult.getBestFitness()+""))
        .collect(EvolutionResult.toBestGenotype())
        .getGene();

    System.out.println(Tree.toString(program));

    for (int i = 0; i < SAMPLES.length; ++i) {
      final double x = SAMPLES[i][0];
      final double y = SAMPLES[i][1];
      final double result = program.eval(x, 0.0);

      System.out
          .println(format("%2.2f: %2.4f, %2.4f: %2.5f", x, f(x, y), result, abs(f(x, y) - result)
          ));
    }
  }

}