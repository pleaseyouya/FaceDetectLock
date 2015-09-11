package com.example.luanxinglong_xy.eyeposition.util.cluster;


import java.util.*;


public class Dbscan
{
	public static int e;
	public static int minpt;
	
	public static Vector<List> resultList = new Vector<List>();
	
	public static Vector<Point> pointList = null;
	 	    
    public static Vector<Point> Neighbours ;
	
	
	public static Vector<List> applyDbscan(PointList input_pointList, int input_e, int input_minpt){
		Vector<Point> pointList = new Vector<Point>(input_pointList.getPointList());
		return applyDbscan(pointList, input_e, input_minpt);
	}

	
	public static Vector<List> applyDbscan(Vector<Point> input_pointList, int input_e, int input_minpt)
	{
		e = input_e;
		minpt = input_minpt;
		resultList.clear();
		//pointList.clear();
		Utility.VisitList.clear();
		//pointList=Utility.getList();
		pointList = new Vector<Point>(input_pointList);
		
		int index2 =0;
		
					
		while (pointList.size()>index2){
			Point p =pointList.get(index2);
			 if(!Utility.isVisited(p)){			
				Utility.Visited(p);				
				Neighbours = Utility.getNeighbours(p);						
				if (Neighbours.size()>=minpt){									
					int ind=0;
					while(Neighbours.size()>ind){				
						Point r = Neighbours.get(ind);
						if(!Utility.isVisited(r)){
							Utility.Visited(r);
							Vector<Point> Neighbours2 = Utility.getNeighbours(r);
							if (Neighbours2.size() >= minpt){
								Neighbours=Utility.Merge(Neighbours, Neighbours2);
							}
						} ind++;
					}	
					//System.out.println("N"+Neighbours.size());
					resultList.add(Neighbours);
				}
			 }
			 index2++;
		}
		return resultList;	
	}		
}
		

			






				