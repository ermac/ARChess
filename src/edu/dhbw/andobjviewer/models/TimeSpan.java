package edu.dhbw.andobjviewer.models;

public class TimeSpan {

	private long time = System.currentTimeMillis();
	
	private int seconds;
	
	public TimeSpan(int seconds) {
		// TODO Auto-generated constructor stub
		this.seconds = seconds;
	}
	
	public boolean elapsedSeconds() {
		return time - ((int) (time / 1000) % 60) > seconds; 
	}
}
