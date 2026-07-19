package com.conquestrefabricated.core.data;

import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.PropertyValueList;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class SextupleProperty<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>, T6 extends Comparable<T6>> extends PropertyDispatch<V> {
    private final Property<T1> property1;
    private final Property<T2> property2;
    private final Property<T3> property3;
    private final Property<T4> property4;
    private final Property<T5> property5;
    private final Property<T6> property6;

    private SextupleProperty(Property<T1> property1, Property<T2> property2, Property<T3> property3,
                             Property<T4> property4, Property<T5> property5, Property<T6> property6) {
        this.property1 = property1;
        this.property2 = property2;
        this.property3 = property3;
        this.property4 = property4;
        this.property5 = property5;
        this.property6 = property6;
    }

    @Override
    public List<Property<?>> getDefinedProperties() {
        return List.of(property1, property2, property3, property4, property5, property6);
    }

    public SextupleProperty<V, T1, T2, T3, T4, T5, T6> select(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, V variant) {
        PropertyValueList key = PropertyValueList.of(new Property.Value[]{
                property1.value(value1), property2.value(value2), property3.value(value3),
                property4.value(value4), property5.value(value5), property6.value(value6)
        });
        this.putValue(key, variant);
        return this;
    }

    public static <V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>, T6 extends Comparable<T6>>
    SextupleProperty<V, T1, T2, T3, T4, T5, T6> initial(
            Property<T1> property1, Property<T2> property2, Property<T3> property3,
            Property<T4> property4, Property<T5> property5, Property<T6> property6) {
        return new SextupleProperty<>(property1, property2, property3, property4, property5, property6);
    }
}