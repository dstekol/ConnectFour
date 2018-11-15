import java.util.*;

public class Point implements Comparable{
    public final int x, y;
    
    public Point(int a, int b){
        x=a;
        y=b;
    }
    
    public int compareTo(Object o){
        if(o==null || !(o instanceof Point)){
            return -1;
        }
        Point p = (Point) o;
        if(this.x==p.x && this.y==p.y){
            return 0;
        }
        else if(this.x>p.x || (this.x==p.x && this.y>p.y)){
            return 1;
        }
        else{
            return -1;
        }
    }
    
    public boolean equals(Object o){
        if(o==null || !(o instanceof Point)){
            return false;
        }
        Point p = (Point) o;
        return this.x==p.x && this.y==p.y;
    }
    
    public String toString(){
        return x + ", " + y;
    }
    
    public static Set<Point> toSet(Point[] p){
        Set<Point> s = new TreeSet<Point>();
        for(int i=0; i<p.length; i++){
            s.add(p[i]);
        }
        return s;
    }
}