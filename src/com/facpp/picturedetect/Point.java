package com.facpp.picturedetect;

public class Point {
	public float x;
	public float y;
	public double dis;
	
	public Point() {
		this.dis = 0;
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
		this.dis = Math.sqrt(x * x + y * y);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

}
