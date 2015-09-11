package com.example.luanxinglong_xy.eyeposition.util.cluster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class Utility {

    public static Vector<Point> VisitList = new Vector<Point>();

    public static double getDistance(Point p, Point q) {

        float dx = p.getX() - q.getX();

        float dy = p.getY() - q.getY();

        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance;

    }

    /**
     * neighbourhood points of any point p
     **/

    public static Vector<Point> getNeighbours(Point p) {
        Vector<Point> neigh = new Vector<Point>();
        Iterator<Point> points = Dbscan.pointList.iterator();
        while (points.hasNext()) {
            Point q = points.next();
            if (getDistance(p, q) <= Dbscan.e) {
                neigh.add(q);
            }
        }
        return neigh;
    }

    public static void Visited(Point d) {
        VisitList.add(d);

    }

    public static boolean isVisited(Point c) {
        if (VisitList.contains(c)) {
            return true;
        } else {
            return false;
        }
    }

    public static Vector<Point> Merge(Vector<Point> a, Vector<Point> b) {

        Iterator<Point> it5 = b.iterator();
        while (it5.hasNext()) {
            Point t = it5.next();
            if (!a.contains(t)) {
                a.add(t);
            }
        }
        return a;
    }

    // Returns PointsList to DBscan.java
	/*
	public static Vector<Point> getList() {
		
		Vector<Point> newList = new Vector<Point>();
		newList.clear();
		newList.addAll(Gui.hset);
		return newList;
	}*/

    public static Vector<Point> getList(String inputFile){
        Vector<Point> newList = new Vector<Point>();
        newList.clear();
        try {
            BufferedReader bReader=new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
            String line = "";
            float count = 0;
            while((line = bReader.readLine())!=null){
                String[] parts = line.split(",");
                if(parts.length!=2)
                    continue;
                float x = 0 - Float.parseFloat(parts[1]);
                float y = Float.parseFloat(parts[0]);
                //float x = Float.parseFloat(parts[0]);
                //float y = Float.parseFloat(parts[1]);
                Point tmp = new Point(x, y,count);
                newList.add(tmp);
                count ++;
            }
            return newList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("读取点列表错误");
            e.printStackTrace();
        }
        return newList;

    }

    public static Boolean equalPoints(Point m, Point n) {
        if ((m.getX() == n.getX()) && (m.getY() == n.getY()))
            return true;
        else
            return false;
    }
}
