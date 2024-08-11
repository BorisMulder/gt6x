package org.altadoon.gt6x.common.rendering;

import java.util.Arrays;

import static gregapi.data.CS.*;

public class Geometry {
    public static final boolean[][] NOT_ALONG_AXIS = {
            {false,false,true ,true ,true ,true ,false,false},
            {false,false,true ,true ,true ,true ,false,false},
            {true ,true ,false,false,true ,true ,false,false},
            {true ,true ,false,false,true ,true ,false,false},
            {true ,true ,true ,true ,false,false,false,false},
            {true ,true ,true ,true ,false,false,false,false},
            {true ,true ,true ,true ,true ,true ,false,false},
            {true ,true ,true ,true ,true ,true ,false,false}
    };

    public static int[][][] ROTATIONS = {
            { // Y neg
                    {1, 0, 0},
                    {0, 0, 1},
                    {0,-1, 0},
            },
            { // Y pos
                    {1, 0, 0},
                    {0, 0,-1},
                    {0, 1, 0},
            },
            { // Z neg
                    {1, 0, 0},
                    {0, 1, 0},
                    {0, 0, 1},
            },
            { // Z pos
                    {-1, 0, 0},
                    { 0, 1, 0},
                    { 0, 0,-1},
            },
            { // X neg
                    { 0, 0, 1},
                    { 0, 1, 0},
                    {-1, 0, 0},
            },
            { // X pos
                    { 0, 0,-1},
                    { 0, 1, 0},
                    { 1, 0, 0},
            }
    };

    public static void translate(float[] src, float[] trans) {
        for (int i = 0; i < src.length; i++) src[i] += trans[i];
    }

    public static void translate(float[] src, float c) {
        for (int i = 0; i < src.length; i++) src[i] += c;
    }

    /**
     * Ensure that min <= max for each coordinate to undo turning it inside out
     */
    protected static void reorder(float[] src) {
        if (src.length != 6) throw new IllegalArgumentException("Reorder: array should have length 6");

        for(int i = 0; i < 3; i++) {
            if (src[i] > src[i+3]) {
                float tmp = src[i+3];
                src[i+3] = src[i];
                src[i] = tmp;
            }
        }
    }

    /**
     * Set rotation from z-neg towards facing, along origin
     */
    public static void rotate(float[] coords, int facing) {
        float[] copy = Arrays.copyOf(coords, coords.length);
        for (int i = 0; i < coords.length; i++) {
            coords[i] = 0;
            int offset = i / 3;
            for (int j = 0; j < 3; j++) {
                coords[i] += (copy[3 * offset + j] * ROTATIONS[facing][i % 3][j]);
            }
        }
    }

    /**
     * Rotate box within block depending on facings, when a player is looking from the facing direction
     */
    public static float[] rotateOnce(int facing, float... coords) {
        translate(coords, -0.5f);
        rotate(coords, facing);
        reorder(coords);
        translate(coords, 0.5f);
        return coords;
    }

    /**
     * Rotate box within block depending on primary and secondary facings, when a player is looking from the first facing direction
     */
    public static float[] rotateTwice(int facing1, int facing2, float... coords) {
        if (ALONG_AXIS[facing1][facing2]) throw new IllegalStateException("Double rotation: second side cannot be along first side's axis");

        translate(coords, -0.5f);
        rotate(coords, facing1);
        //TODO
        reorder(coords);
        translate(coords, 0.5f);
        return coords;
    }
}
