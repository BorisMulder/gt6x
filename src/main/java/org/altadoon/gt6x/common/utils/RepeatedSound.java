package org.altadoon.gt6x.common.utils;

public class RepeatedSound extends Sound implements Runnable {
	protected float volume = 0, pitch = 0;
	protected int x = 0, y = 0, z = 0;
	protected boolean playing = false;

	public RepeatedSound(String resource, int duration) {
		super(resource, duration);
	}

	public void start(float volume, int x, int y, int z) {
		start(volume, 1.0f, x, y, z);
	}

	public void start(float volume, float pitch, int x, int y, int z) {
		playing = true;
		this.pitch = pitch;
		this.volume = volume;
		this.x = x;
		this.y = y;
		this.z = z;
		play(volume, pitch, x, y, z);
	}

	public void stop() {
		playing = false;
	}

	public void unstop() {
		playing = true;
	}

	@Override
	public void run() {
		super.run();
		if (finished() && playing) {
			play(volume, pitch, x, y, z);
		}
	}
}
