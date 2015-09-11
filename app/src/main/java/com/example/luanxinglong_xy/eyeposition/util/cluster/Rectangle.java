package com.example.luanxinglong_xy.eyeposition.util.cluster;

import javax.security.auth.x500.X500Principal;

public class Rectangle {
	int top;
	int bottom;
	int left;
	int right;
	Rectangle(int _top,int _bottom, int _left, int _right) {
		// TODO Auto-generated constructor stub
		top = _top;
		bottom = _bottom;
		left = _left;
		right = _right;
	}
	public boolean containPoint(Point point){
		float x = point.getX();
		float y = point.getY();
		if(x >= left && x <= right && y >= bottom && y <= top)
			return true;
		else 
			return false;
	}
}
