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

	public static final int[][] SIDE_RIGHT_ROTATED = { // indexed by primary and secondary rotations
			{SIDE_INVALID, SIDE_INVALID, SIDE_X_NEG  , SIDE_X_POS  , SIDE_Z_POS  , SIDE_Z_NEG  },
			{SIDE_INVALID, SIDE_INVALID, SIDE_X_POS  , SIDE_X_NEG  , SIDE_Z_NEG  , SIDE_Z_POS  },
			{SIDE_X_NEG  , SIDE_X_POS  , SIDE_INVALID, SIDE_INVALID, SIDE_Y_POS  , SIDE_Y_NEG  },
			{SIDE_X_POS  , SIDE_X_NEG  , SIDE_INVALID, SIDE_INVALID, SIDE_Y_NEG  , SIDE_Y_POS  },
			{SIDE_Z_NEG  , SIDE_Z_POS  , SIDE_Y_POS  , SIDE_Y_NEG  , SIDE_INVALID, SIDE_INVALID},
			{SIDE_Z_POS  , SIDE_Z_NEG  , SIDE_Y_NEG  , SIDE_Y_POS  , SIDE_INVALID, SIDE_INVALID}
	};
	public static final int[][] SIDE_LEFT_ROTATED = { // indexed by primary and secondary rotations
			{SIDE_INVALID, SIDE_INVALID, SIDE_X_POS  , SIDE_X_NEG  , SIDE_Z_NEG  , SIDE_Z_POS  },
			{SIDE_INVALID, SIDE_INVALID, SIDE_X_NEG  , SIDE_X_POS  , SIDE_Z_POS  , SIDE_Z_NEG  },
			{SIDE_X_POS  , SIDE_X_NEG  , SIDE_INVALID, SIDE_INVALID, SIDE_Y_NEG  , SIDE_Y_POS  },
			{SIDE_X_NEG  , SIDE_X_POS  , SIDE_INVALID, SIDE_INVALID, SIDE_Y_POS  , SIDE_Y_NEG  },
			{SIDE_Z_POS  , SIDE_Z_NEG  , SIDE_Y_NEG  , SIDE_Y_POS  , SIDE_INVALID, SIDE_INVALID},
			{SIDE_Z_NEG  , SIDE_Z_POS  , SIDE_Y_POS  , SIDE_Y_NEG  , SIDE_INVALID, SIDE_INVALID}
	};

	public static int[][][] ROTATIONS = {
			{ // Y neg
					{1, 0, 0},
					{0, 0, 1},
					{0,-1, 0},
			},{ // Y pos
					{1, 0, 0},
					{0, 0,-1},
					{0, 1, 0},
			},{ // Z neg
					{1, 0, 0},
					{0, 1, 0},
					{0, 0, 1},
			},{ // Z pos
					{-1, 0, 0},
					{ 0, 1, 0},
					{ 0, 0,-1},
			},{ // X neg
					{ 0, 0, 1},
					{ 0, 1, 0},
					{-1, 0, 0},
			},{ // X pos
					{ 0, 0,-1},
					{ 0, 1, 0},
					{ 1, 0, 0},
			}
	};

	public static final int[][][] ROLLS = {
			{ // 0 deg seen from front face
					{1, 0, 0},
					{0, 1, 0},
					{0, 0, 1},
			},{ // 90 deg CW
					{ 0, 1, 0},
					{-1, 0, 0},
					{ 0, 0, 1},
			},{ // 180 deg
					{-1, 0, 0},
					{ 0,-1, 0},
					{ 0, 0, 1},
			},{ // 270 deg
					{0,-1, 0},
					{1, 0, 0},
					{0, 0, 1},
			}
	};

	// All rotations are clockwise
	public static final int ROT_0 = 0;
	public static final int ROT_NONE = ROT_0;
	public static final int ROT_90 = 1;
	public static final int ROT_180 = 2;
	public static final int ROT_270 = 3;

	public static final int[][] ROLL_INDEXES = {
			{ROT_NONE, ROT_NONE, ROT_180 , ROT_0   , ROT_90  , ROT_270 }, // facing1 = Y neg
			{ROT_NONE, ROT_NONE, ROT_0   , ROT_180 , ROT_90  , ROT_270 }, // Y pos
			{ROT_0   , ROT_180 , ROT_NONE, ROT_NONE, ROT_90  , ROT_270 }, // Z neg
			{ROT_0   , ROT_180 , ROT_NONE, ROT_NONE, ROT_270 , ROT_90  }, // Z pos
			{ROT_0   , ROT_180 , ROT_270 , ROT_90  , ROT_NONE, ROT_NONE}, // X neg
			{ROT_0   , ROT_180 , ROT_90  , ROT_270 , ROT_NONE, ROT_NONE}, // X pos
	};

	public static int textureRotationForSide(int side, int facing1, int facing2) {
		if (ALONG_AXIS[side][facing1]) { // front/back
			if (SIDES_AXIS_Y[facing2])
				return ROLL_INDEXES[side][facing2]; // works?
			//TODO
			return 0;
		} else if (ALONG_AXIS[side][facing2]) { // bottom/top
			return ROLL_INDEXES[side][facing1]; // for some reason this works, idk why lol
		} else { // side
			if (SIDES_AXIS_Y[facing1]) {
				if (side == SIDE_LEFT_ROTATED[facing1][facing2]) {
					if (facing1 == SIDE_Y_NEG)
						return ROT_270;
					else
						return ROT_90;
				} else {
					if (facing1 == SIDE_Y_NEG)
						return ROT_90;
					else
						return ROT_270;
				}
			} else {
				return switch (facing2) {
					case SIDE_Y_NEG -> ROT_0;
					case SIDE_Y_POS -> ROT_180;
					// we are bottom/top side
					case SIDE_Z_POS -> ROT_0;
					case SIDE_X_NEG -> ROT_90;
					case SIDE_Z_NEG -> ROT_180;
					case SIDE_X_POS -> ROT_270;
					default -> -1; // unreachable
				};
			}
		}
	}

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

	public static void roll(float[] coords, int facing1, int facing2) {
		float[] copy = Arrays.copyOf(coords, coords.length);
		for (int i = 0; i < coords.length; i++) {
			coords[i] = 0;
			int offset = i / 3;
			for (int j = 0; j < 3; j++) {
				coords[i] += (copy[3 * offset + j] * ROLLS[ROLL_INDEXES[facing1][facing2]][i % 3][j]);
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
	 * Rotate box within block depending on primary and secondary facings, when a player is looking from the first facing direction.
	 * The default directions are Z-neg (facing1) and Y-neg (facing2)
	 */
	public static float[] rotateTwice(int facing1, int facing2, float... coords) {
		if (ALONG_AXIS[facing1][facing2]) throw new IllegalStateException("Double rotation: second side cannot be along first side's axis");
		translate(coords, -0.5f);
		roll(coords, facing1, facing2);
		rotate(coords, facing1);
		reorder(coords);
		translate(coords, 0.5f);
		return coords;
	}
}
