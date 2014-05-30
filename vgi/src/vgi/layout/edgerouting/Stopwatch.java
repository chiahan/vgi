/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.edgerouting;

/**
 *
 * @author JLiu
 */
public class Stopwatch {

	protected long elapsedNanoseconds;
	protected Long startTime;

	public Stopwatch() {
		this.elapsedNanoseconds = 0;
		this.startTime = null;
	}

	public double getElapsedMilliseconds() {
		return this.getElapsedNanoseconds() / 1000000d;
	}

	public double getElapsedMicroseconds() {
		return this.getElapsedNanoseconds() / 1000d;
	}

	public long getElapsedNanoseconds() {
		long currentTime = System.nanoTime();
		if (this.isRunning()) {
			return this.elapsedNanoseconds + (currentTime - this.startTime.longValue());
		} else {
			return this.elapsedNanoseconds;
		}
	}  // End public long getElapsedNanoseconds()

	public boolean isRunning() {
		return (this.startTime != null);
	}

	public void reset() {
		this.elapsedNanoseconds = 0;
		this.startTime = null;
	}

	public Stopwatch start() {
		this.startTime = System.nanoTime();
		return this;
	}  // End public Stopwatch start()

	public void stop() {
		long currentTime = System.nanoTime();
		if (!(this.isRunning())) {
			return;
		}
		this.elapsedNanoseconds = this.elapsedNanoseconds + (currentTime - this.startTime.longValue());
		this.startTime = null;
	}  // End public void stop()
}  // End public class Stopwatch
