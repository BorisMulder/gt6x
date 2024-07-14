package org.altadoon.gt6x.common.utils;

import gregapi.util.UT;

public class Sound implements Runnable {
	protected String resource;
	public int duration;
	public int currentDuration = 0;

	public Sound(String resource, int duration) {
		this.resource = resource;
		this.duration = duration;
	}

	public void play(float volume, float pitch, int x, int y, int z) {
		float modulatedDuration = (float)duration / pitch;
		UT.Sounds.play(resource, (int)modulatedDuration, volume, pitch, x, y, z);
		currentDuration = 0;
	}

	public void play(float volume, int x, int y, int z) {
		UT.Sounds.play(resource, duration, volume, 1.0f, x, y, z);
		currentDuration = 0;
	}

	@Override
	public void run() {
		if (!finished()) currentDuration++;
	}

	public boolean finished() {
		return currentDuration >= duration;
	}
}
