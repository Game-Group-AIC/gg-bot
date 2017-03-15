package cz.jan.maly.model.metadata;

import cz.jan.maly.model.knowledge.Fact;
import cz.jan.maly.model.knowledge.FactSet;
import lombok.Getter;

/**
 * Simple class describing metadata for fact - used for identification. It contains factory method do instantiate new
 * fact of this type.
 * Created by Jan on 10-Feb-17.
 */
public abstract class FactKey<V> extends Key {

    @Getter
    private final int howLongStayInMemoryWithoutUpdate;

    @Getter
    private final boolean isFading;

    public FactKey(String name, int howLongStayInMemoryWithoutUpdate) {
        super(name, FactKey.class);
        this.howLongStayInMemoryWithoutUpdate = howLongStayInMemoryWithoutUpdate;
        this.isFading = true;
    }

    public FactKey(String name) {
        super(name, FactKey.class);
        this.howLongStayInMemoryWithoutUpdate = Integer.MAX_VALUE;
        this.isFading = false;
    }


    public abstract V getInitValue();

    /**
     * Returns new instance of fact of this type with initialization value
     *
     * @return
     */
    public Fact<V> returnEmptyFact() {
        return new Fact<>(getInitValue(), this);
    }

    /**
     * Returns new instance of fact set of this type with initialization value
     *
     * @return
     */
    public FactSet<V> returnEmptyFactSet() {
        return new FactSet<>(this);
    }

}
