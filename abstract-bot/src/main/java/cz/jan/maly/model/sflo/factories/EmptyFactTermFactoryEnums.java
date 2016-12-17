package cz.jan.maly.model.sflo.factories;

import cz.jan.maly.model.Fact;
import cz.jan.maly.model.sflo.EmptyFactTerm;

/**
 * Enumeration of possible strategies how to evaluate truth of relationship of fact and his content
 * Created by Jan on 17-Dec-16.
 */
public enum EmptyFactTermFactoryEnums {
    IS_EMPTY {
        @Override
        public <T extends Fact> EmptyFactTerm<T> createExpression(T firstFact) {
            return new EmptyFactTerm<T>(firstFact) {
                @Override
                public boolean evaluate() {
                    return firstFact.getContent() == null;
                }
            };
        }
    };

    /**
     * This expression implements strategy to evaluate truth of fact related to its value assignment
     *
     * @param firstFact
     * @return
     */
    public abstract <T extends Fact> EmptyFactTerm createExpression(T firstFact);
}
