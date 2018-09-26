package com.quillraven.game.map;

import com.badlogic.gdx.math.Vector2;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

class CollisionArea {
    private final Vector2 startLocation;
    private final float[] vertices;
    private final boolean isWater;

    public CollisionArea(final float x, final float y, final float[] vertices, final boolean isWater) {
        this.startLocation = new Vector2(x * UNIT_SCALE, y * UNIT_SCALE);
        this.vertices = vertices;
        for (int i = 0; i < vertices.length; i += 2) {
            vertices[i] = vertices[i] * UNIT_SCALE;
            vertices[i + 1] = vertices[i + 1] * UNIT_SCALE;
        }
        this.isWater = isWater;
    }

    public Vector2 getStartLocation() {
        return startLocation;
    }

    public float[] getVertices() {
        return vertices;
    }

    public boolean isWater() {
        return isWater;
    }
}
