package edu.dhbw.andobjviewer.models;

public class TimeSpan {

	private long time = System.currentTimeMillis();
	
	private int seconds;
	
	public TimeSpan(int seconds) {
		// TODO Auto-generated constructor stub
		this.seconds = seconds;
	}
	
	public void reset() {
		time = System.currentTimeMillis();
	}
	
	public boolean elapsedSeconds() {
		return ((int) ((System.currentTimeMillis() - time) / 1000) % 60) > seconds; 
	}
}
