package aic.gas.sc.gg_bot.mas.model.planing.heap;

/**
 * Interface for each element of heapOfTrees to accept heapOfTrees visitor instance
 */
public interface IVisitorAcceptor {

  /**
   * Accept instance of heapOfTrees visitor
   */
  void accept(ITreeVisitor treeVisitor);
}
