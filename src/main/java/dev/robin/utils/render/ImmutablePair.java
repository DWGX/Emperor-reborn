package dev.robin.utils.render;

import dev.robin.utils.render.Pair;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class ImmutablePair<A, B>
extends Pair<A, B> {
    private final A a;
    private final B b;

    ImmutablePair(A a, B b2) {
        this.a = a;
        this.b = b2;
    }

    public static <A, B> ImmutablePair<A, B> of(A a, B b2) {
        return new ImmutablePair<A, B>(a, b2);
    }

    public Pair<A, A> pairOfFirst() {
        return ImmutablePair.of(this.a);
    }

    public Pair<B, B> pairOfSecond() {
        return ImmutablePair.of(this.b);
    }

    @Override
    public A getFirst() {
        return this.a;
    }

    @Override
    public B getSecond() {
        return this.b;
    }

    @Override
    public <R> R apply(BiFunction<? super A, ? super B, ? extends R> func) {
        return func.apply(this.a, this.b);
    }

    @Override
    public void use(BiConsumer<? super A, ? super B> func) {
        func.accept(this.a, this.b);
    }
}

