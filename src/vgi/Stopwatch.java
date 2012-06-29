/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

/**
 *
 * @author JLiu
 */
public class Stopwatch {

	protected Long startTime;
	protected Long stopTime;

	public Stopwatch() {
		this.startTime = null;
		this.stopTime = null;
	}

	public double getElapsedMilliseconds() {
		return this.getElapsedNanoseconds() / 1000000d;
	}

	public double getElapsedMicroseconds() {
		return this.getElapsedNanoseconds() / 1000d;
	}

	public long getElapsedNanoseconds() {
		if (this.startTime == null) {
			throw new IllegalStateException("The stopwatch did not start so there is no elapsed time.");
		}
		if (this.stopTime == null) {
			return System.nanoTime() - this.startTime;
		} else {
			return this.stopTime - this.startTime;
		}
	}  // End public long getElapsedNanoseconds()

	public boolean isRunning() {
		return (this.startTime != null);
	}

	public Stopwatch start() {
		this.startTime = System.nanoTime();
		return this;
	}  // End public Stopwatch start()

	public void stop() {
		this.stopTime = System.nanoTime();
	}

}  // End public class Stopwatch
