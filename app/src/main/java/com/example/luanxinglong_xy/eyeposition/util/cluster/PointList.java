package com.example.luanxinglong_xy.eyeposition.util.cluster;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class PointList {
    private Vector<Point> pointList = null;
    private int top,bottom,left,right;
    private int width;
    private int height;
    private Vector<List> clusterResult = null;
    private Vector<Point> centerList = null;
    private Vector<Rectangle> splitParts = null;
    private Vector<Integer> moveRoute = null;

    //用于聚类的两个参数
    public int e = 20;
    public int minpt = 50;


    /**
     * 用Point的vector来初始化
     * @param list
     */
    PointList(Vector<Point> list) {
        // TODO Auto-generated constructor stub
        pointList = new Vector<Point>(list);
        //得到矩形边界
        this.getBoundary();
        //System.err.println(bottom + "," + top + "," + left +"," + right);
        normalizePoint();
        //System.err.println(pointList);
    }
    /**
     * 用坐标vector来初始化点序列
     * @param xList
     * @param yList
     */
    public PointList(Vector<Float> xList,Vector<Float> yList) {
        // TODO Auto-generated constructor stub
        pointList = new Vector<Point>();
        int len = xList.size();
        for(int i = 0; i < len; i++){
            Point tmp = new Point(xList.get(i), yList.get(i), i);
            pointList.add(tmp);
        }
        //得到矩形边界
        this.getBoundary();
        normalizePoint();
    }

    /**
     * 分析点序列
     */
    public boolean analyse(){
        //得到矩形边界
        this.getBoundary();
        //把矩形划分为四块
        this.getSplitPart();
        //进行聚类
        this.clusterResult = Dbscan.applyDbscan(this.pointList, e, minpt);
        //计算每个簇的中心点
        Vector<Float> array1 = new Vector<>();
        Vector<Float> array2 = new Vector<>();
        centerList = new Vector<Point>();
        for (List list : clusterResult) {
            Point center = findCenterOfCluster(list);
            centerList.add(center);
            array1.add(center.getID());
        }
        Collections.sort(centerList);

        //按照中心点序号顺序重新排列簇的顺序
        for (Point point : centerList) {
            array2.add(point.getID());
        }
        Vector<List> tmpClusterResult = new Vector<>(clusterResult);
        clusterResult.clear();
        for (Float f : array2) {
            int index = array1.indexOf(f);
            clusterResult.add(tmpClusterResult.get(index));
        }


        return patternMatch();
    }



    /**
     * 得到矩形边界、宽高
     */
    private void getBoundary(){
        System.err.println("进入矩形边界获取");
        int tag = 0;
        for (Point point : pointList) {
            if( tag == 0){
                this.left = (int) point.getX();
                this.right = (int) point.getX();
                this.top = (int) point.getY();
                this.bottom = (int) point.getY();
                tag = 1;
            }
            else{
                if(this.left>point.getX())
                    this.left = (int) point.getX();
                if(this.right<point.getX())
                    this.right = (int) point.getX();
                if(this.top<point.getY())
                    this.top = (int) point.getY();
                if(this.bottom>point.getY())
                    this.bottom = (int) point.getY();
            }
        }
        this.width = this.right - this.left;
        this.height = this.top - this.bottom;
    }

    /**
     * 得到矩形的四个分割部分
     */
    private void getSplitPart(){
        int midX = (left + right )/2;
        int midY = (top + bottom)/2;
        Rectangle topLeft = new Rectangle(top, midY, left, midX);
        Rectangle topRight = new Rectangle(top, midY, midX+1, right);
        Rectangle botLeft = new Rectangle(midY+1, bottom, left, midX);
        Rectangle botRight = new Rectangle(midY+1, bottom, midX+1, right);
        splitParts = new Vector<Rectangle>();
        splitParts.add(topLeft);
        splitParts.add(topRight);
        splitParts.add(botRight);
        splitParts.add(botLeft);
    }

    /**
     * 找到簇的中心点
     * @param cluster
     * @return
     */
    private static Point findCenterOfCluster(List<Point> cluster){
        float x = 0;
        float y = 0;
        float id = 0;
        for (Point point : cluster) {
            x += point.getX();
            y += point.getY();
            id += point.getID();
        }
        int size = cluster.size();
        x /= size;
        y /= size;
        id /= size;
        Point center = new Point(x, y, id);
        return center;


    }

    /**
     * 动作序列匹配，具体实现是各簇中心所处的区域编号是否递增
     * @return
     */
    private boolean patternMatch(){
        if(centerList.size()<=1){
            System.err.println("只有一个类簇，验证失败");
            return false;
        }
        moveRoute = new Vector<>(centerList.size());
        int size = splitParts.size();
        for (Point point : centerList) {
            for(int i = 0; i < size;i ++){
                if(splitParts.get(i).containPoint(point)){
                    moveRoute.add(i);
                    break;
                }
            }
        }
        Vector<Integer> compactRoute = new Vector<>();
        for (Integer integer : moveRoute) {
            if(compactRoute.size()==0)
                compactRoute.add(integer);
            else{
                if(compactRoute.get(compactRoute.size()-1)==integer)
                    continue;
                else
                    compactRoute.add(integer);
            }
        }

//		System.err.println("簇中心路径压缩前");
//		System.err.println(moveRoute);
//		System.err.println("簇中心路径压缩后");
//		System.err.println(compactRoute);
        StringBuffer routeBuilder = new StringBuffer();
        for (Integer integer : compactRoute) {
            routeBuilder.append(String.valueOf(integer));
        }

        //路径补上一段，判断是否有环路
        String route = routeBuilder.toString();
        if(route.charAt(0) == route.charAt(route.length()-1))
            route = route + route.substring(1);
        else
            route += route;
        System.err.println("簇中心路径字符串为：" + route);
        if(route.contains("0123") || route.contains("1230") || route.contains("2301")||route.contains("3012")){
            System.err.println("动作序列匹配，验证成功");
            return true;
        }
        else{
            System.err.println("动作序列不匹配，验证失败");
            return false;
        }
		/*for(int i =1; i < moveRoute.size();i++){
			if(moveRoute.get(i)<moveRoute.get(i-1)){
				System.err.println("动作序列不匹配类型1，验证失败");
				return false;
			}
		}
		if(moveRoute.get(0)!=0 || moveRoute.get(moveRoute.size()-1)!=3){
			System.err.println("动作序列不匹配类型2，验证失败");
			return false;
		}*/

    }

    /**
     * 参数设定，e为半径，minpt为簇最小点数
     * @param _e
     * @param _minpt
     */
    public void setE_Minpt(int _e, int _minpt){
        e = _e;
        minpt = _minpt;
    }

    /**
     * 将原始的点列表归一化成非负数
     */
    private void normalizePoint(){
        Vector<Point> tmpPointList = new Vector<>(pointList);
        pointList.clear();
        for (Point point : tmpPointList) {
            float id = point.getID();
            float x = point.getX() - left;
            float y = point.getY() - bottom;
            pointList.add(new Point(x, y, id));
        }
    }

    /**
     * 返回初始的点列表
     * @return
     */
    public Vector<Point> getPointList() {
        return pointList;
    }
    /**
     * 返回聚类后的类簇
     * @return
     */
    public Vector<List> getClusterResult() {
        return this.clusterResult;
    }
    /**
     * 返回类簇的中心点列表
     * @return
     */
    public Vector<Point> getCenterList() {
        return this.centerList;
    }
    /**
     * 返回类簇中心点所处的区域序号列表
     * @return
     */
    public Vector<Integer> getMoveRoute() {
        return moveRoute;
    }

    public int getLeft() {
        return left;
    }
    public int getRight() {
        return right;
    }
    public int getTop(){
        return top;
    }
    public int getBottom() {
        return bottom;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

}
