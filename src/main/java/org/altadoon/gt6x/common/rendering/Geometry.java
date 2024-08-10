package org.altadoon.gt6x.common.rendering;

import java.util.ArrayList;

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
                    {0, 0,-1},
                    {0, 1, 0},
            },
            { // Y pos
                    {1, 0, 0},
                    {0, 0, 1},
                    {0,-1, 0},
            },
            { // Z neg
                    {1, 0, 0},
                    {0, 1, 0},
                    {0, 0, 1},
            },
            { // Z pos
                    {-1, 0, 0},
                    { 0, 1, 0},
                    { 0, 0,-1}
            },
            { // X neg
                    {0, 0, 1},
                    {0, 1, 0},
                    {1, 0, 0},
            },
            { // X pos
                    { 0, 0, 1},
                    { 0, 1, 0},
                    {-1, 0, 0},
            }
    };

    public static void translate(float[] src, float[] trans) {
        for (int i = 0; i < src.length; i++) src[i] += trans[i];
    }

    public static void translateConst(float[] src, float c) {
        for (int i = 0; i < src.length; i++) src[i] += c;
    }

    /**
     * Set rotation from z-neg towards facing, along origin
     */
    public static void rotateXYZ(int facing, float[] src, float[] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = 0;
            for (int j = 0; j < 3; j++) {
                dst[i] += (src[i] * ROTATIONS[facing][i % 3][j]);
            }
        }
    }

    /**
     * Rotate box within block depending on facings, when a player is looking from the facing direction
     */
    public static float[] rotateOnce(int facing, float... coords) {
        float[] result = new float[coords.length];
        translateConst(coords, -0.5f);
        rotateXYZ(facing, coords, result);
        translateConst(result, 0.5f);
        return result;
    }

    /**
     * Rotate box within block depending on primary and secondary facings, when a player is looking from the first facing direction
     */
    public static float[] rotateTwice(int facing1, int facing2, float... coords) {
        if (ALONG_AXIS[facing1][facing2]) throw new IllegalStateException("Double rotation: second side cannot be along first side's axis");

        float[] result = new float[coords.length];
        translateConst(coords, -0.5f);
        rotateXYZ(facing1, coords, result);
        //TODO
        translateConst(result, 0.5f);
        return result;
    }
}
