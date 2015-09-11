package com.example.luanxinglong_xy.eyeposition.util.cluster;


public class Point implements Comparable<Point>{
    private Float id;
    private float x;
    private float y;

    public Point(float _x, float _y, float _id) {
        x = _x;
        y = _y;
        id = _id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    public float getID() {
        return id;
    }
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(Float.toString(id));
        s.append("#");
        s.append(Float.toString(x));
        s.append("#");
        s.append(Float.toString(y));
        return s.toString();
    }
    @Override
    public int compareTo(Point o) {
        // TODO Auto-generated method stub
        return this.id.compareTo(o.id);
    }


}
