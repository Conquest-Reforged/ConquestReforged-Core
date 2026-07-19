package com.conquestrefabricated.core.client.render.type;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Replace the first call to submitCustomGeometry's RenderType with our own.
 * Subsequent calls pass through unmodified
 */
public class ReplaceFirstInjector extends RenderTypeInjector {

    private final RenderType type;
    private volatile boolean first = true;

    public ReplaceFirstInjector(SubmitNodeCollector delegate, RenderType type) {
        super(delegate);
        this.type = type;
    }

    @Override
    protected RenderType getRenderType(RenderType type) {
        if (first) {
            first = false;
            return this.type;
        }
        return type;
    }
}