package com.conquestrefabricated.client.gui.palette.shape;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class Bounds {

    public static final Bounds NONE = new Bounds();

    private final List<List<Point>> bounds = new LinkedList<>();
    private List<Point> points = Collections.emptyList();

    public Bounds startNew() {
        bounds.add(points = new LinkedList<>());
        return this;
    }

    public Bounds add(Point point) {
        points.add(point);
        return this;
    }

//    public void draw(float red, float green, float blue, float opacity, float ticks) {
//        RenderSystem.setShaderColor(red, green, blue, opacity);
//        Tesselator tessellator = Tesselator.getInstance();
//        for (List<Point> points : bounds) {
//            BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
//            for (Point point : points) {
//                buffer.addVertex(point.x, point.y, 0);
//            }
//            BufferUploader.drawWithShader(buffer.buildOrThrow());
//        }
//    }
}
