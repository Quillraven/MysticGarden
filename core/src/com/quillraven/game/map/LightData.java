package com.quillraven.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.maps.MapProperties;

public class LightData {
    private final String type;
    private final float distance;
    private final Color color;
    private final float coneDegree;
    private final float coneDirection;
    private final float fluctuation;
    private final float fluctuationSpeed;
    private final float offsetX;
    private final float offsetY;

    LightData(final MapProperties properties) {
        type = properties.get("light_type", "", String.class);
        distance = properties.get("light_distance", 0f, Float.class);

        String colorHex = properties.get("light_color", "", String.class);
        if (!colorHex.isEmpty()) {
            final Color cachedColor = Colors.get(colorHex);
            if (cachedColor == null) {
                color = Color.valueOf(colorHex);
                Colors.put(colorHex, color);
            } else {
                color = cachedColor;
            }
        } else {
            color = null;
        }

        coneDegree = properties.get("light_cone_degree", 0f, Float.class);
        coneDirection = properties.get("light_direction", 0f, Float.class);
        fluctuation = properties.get("light_fluctuation", 0f, Float.class);
        fluctuationSpeed = properties.get("light_fluctuation_speed", 0f, Float.class);
        offsetX = properties.get("light_offset_x", 0f, Float.class);
        offsetY = properties.get("light_offset_y", 0f, Float.class);
    }

    public String getType() {
        return type;
    }

    public float getDistance() {
        return distance;
    }

    public Color getColor() {
        return color;
    }

    public float getConeDegree() {
        return coneDegree;
    }

    public float getConeDirection() {
        return coneDirection;
    }

    public float getFluctuation() {
        return fluctuation;
    }

    public float getFluctuationSpeed() {
        return fluctuationSpeed;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }
}
