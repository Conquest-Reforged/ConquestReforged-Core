package com.conquestrefabricated.core.block.factory;

import com.conquestrefabricated.core.util.OptimizedList;
import com.conquestrefabricated.core.util.cache.Cache;
import java.util.*;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;

public class TypeList implements Iterable<Class<? extends Block>>, Comparator<Block> {

    public static final TypeList EMPTY = new TypeList(Collections.emptyList());
    private static final Cache<TypeKey, TypeList> cache = new TypeListCache();

    private final List<Class<? extends Block>> types;

    private TypeList(List<Class<? extends Block>> types) {
        this.types = types;
    }

    public boolean isEmpty() {
        return types.isEmpty();
    }

    public Class<? extends Block> first() {
        if (types.size() > 0) {
            return types.get(0);
        }
        return AirBlock.class;
    }

    public static TypeList of(Collection<Class<? extends Block>> types) {
        return new TypeList(new ArrayList<>(types));
    }

    @SafeVarargs
    public static TypeList of(Class<? extends Block>... types) {
        if (types.length == 0) {
            throw new RuntimeException("No Types provided!");
        }
        return cache.get(new TypeKey(types));
    }

    /**
     * Creates a new TypeList with the specified type added
     * @param type The block type to add
     * @return A new TypeList instance with the additional type
     */
    public TypeList add(Class<? extends Block> type) {
        if (type == null) {
            throw new IllegalArgumentException("Cannot add null type to TypeList");
        }

        List<Class<? extends Block>> newTypes = new ArrayList<>(this.types);
        newTypes.add(type);
        return new TypeList(newTypes);
    }

    /**
     * Creates a new TypeList with the specified types added
     * @param types The block types to add
     * @return A new TypeList instance with the additional types
     */
    public TypeList addAll(Collection<Class<? extends Block>> types) {
        if (types == null || types.isEmpty()) {
            return this;
        }

        List<Class<? extends Block>> newTypes = new ArrayList<>(this.types);
        newTypes.addAll(types);
        return new TypeList(newTypes);
    }

    /**
     * Creates a new TypeList with all specified types added
     * @param types The block types to add
     * @return A new TypeList instance with the specified types
     */
    @SafeVarargs
    public final TypeList addAll(Class<? extends Block>... types) {
        if (types == null || types.length == 0) {
            return this;
        }

        return addAll(Arrays.asList(types));
    }

    /**
     * Creates a new TypeList with the specified type removed
     * @param type The block type to remove
     * @return A new TypeList instance without the specified type
     */
    public TypeList remove(Class<? extends Block> type) {
        if (type == null || !this.types.contains(type)) {
            return this;
        }

        List<Class<? extends Block>> newTypes = new ArrayList<>(this.types);
        newTypes.remove(type);

        if (newTypes.isEmpty()) {
            return EMPTY;
        }

        return new TypeList(newTypes);
    }

    /**
     * Creates a new TypeList with all specified types removed
     * @param typesToRemove The collection of block types to remove
     * @return A new TypeList instance without any of the specified types
     */
    public TypeList removeAll(Collection<Class<? extends Block>> typesToRemove) {
        if (typesToRemove == null || typesToRemove.isEmpty()) {
            return this;
        }

        List<Class<? extends Block>> newTypes = new ArrayList<>(this.types);
        newTypes.removeAll(typesToRemove);

        if (newTypes.isEmpty()) {
            return EMPTY;
        }

        return new TypeList(newTypes);
    }

    /**
     * Creates a new TypeList with all specified types removed
     * @param types The block types to remove
     * @return A new TypeList instance without any of the specified types
     */
    @SafeVarargs
    public final TypeList removeAll(Class<? extends Block>... types) {
        if (types == null || types.length == 0) {
            return this;
        }

        return removeAll(Arrays.asList(types));
    }

    /**
     * Creates a new TypeList where the oldType is replaced with newType
     * This maintains the exact order of elements in the list
     *
     * @param oldType The block type to be replaced
     * @param newType The block type to replace with
     * @return A new TypeList with oldType replaced by newType, or this TypeList if oldType not found
     */
    public TypeList replace(Class<? extends Block> oldType, Class<? extends Block> newType) {
        if (oldType == null || newType == null || !this.types.contains(oldType)) {
            return this;
        }

        List<Class<? extends Block>> newTypes = new ArrayList<>(this.types);
        int index = newTypes.indexOf(oldType);
        newTypes.set(index, newType);

        return new TypeList(newTypes);
    }

    @Override
    public Iterator<Class<? extends Block>> iterator() {
        return types.iterator();
    }

    @Override
    public int compare(Block b1, Block b2) {
        return getIndex(b1) - getIndex(b2);
    }

    private int getIndex(Object o) {
        int max = -1;
        for (int i = 0; i < types.size(); i++) {
            Class<?> type = types.get(i);
            if (type.isInstance(o)) {
                max = Math.max(max, i);
            }
        }
        return max == -1 ? types.size() : max;
    }

    private static class TypeListCache extends Cache<TypeKey, TypeList> {

        @Override
        public TypeList compute(TypeKey typeKey) {
            OptimizedList<Class<? extends Block>> list = OptimizedList.of(typeKey.types);
            list.trim();
            return new TypeList(list);
        }
    }

    private static class TypeKey {

        private final Class<? extends Block>[] types;

        private TypeKey(Class<? extends Block>[] types) {
            this.types = types;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeKey typeKey = (TypeKey) o;
            return Arrays.equals(types, typeKey.types);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(types);
        }
    }
}
