package raytrace;

public class Point{
	public static Point getClosest(Point viewPoint, Point direction, Point[] points){
		Point closest=null;
		double distance=-1;
		Point fromViewPoint;
		for(int i=0;i<points.length;i++){
			fromViewPoint=points[i].minus(viewPoint);
			if(fromViewPoint.sameDirection(direction)){
				double d=fromViewPoint.norm();
				if(d<=distance||distance==-1){
					distance=d;
					closest=points[i];
				}
				
			}
		}
		return closest;
	}
	public static Point[] between(Point[] points, Point p1, Point p2){
		int howMany=0;
		boolean[] between=new boolean[points.length];
		Point tl=p2.minus(p1);
		Point ti;
		for(int i=0;i<points.length;i++){
			ti=points[i].minus(p1);
			if(!ti.isZero()&&ti.sameDirection(tl)&&ti.norm()<tl.norm()){
				between[i]=true;
				howMany++;
			}
		}
		Point[] pointsBetween=new Point[howMany];
		int j=0;
		for(int i=0;i<points.length;i++){
			if(between[i]){pointsBetween[j++]=points[i];}
		}
		return pointsBetween;
	}
	public static Point[] together(Point[] t1, Point[] t2){
		Point[] together=new Point[t1.length+t2.length];
		for(int i=0;i<t1.length;i++){
			together[i]=t1[i];
		}
		for(int i=t1.length;i<together.length;i++){
			together[i]=t2[i-t1.length];
		}
		return together;
	}
    public static Point random(){
        return new Point(Math.random(), Math.random(), Math.random());
    }
	public static Point projection(Point p1, Point point, Point normal){
		return point.plus(p1.minus(point).minus(projection(p1.minus(point), normal)));
	}
	public static Point origin=new Point(0,0,0);
	public static Point unitX=new Point(1,0,0);
	public static Point unitY=new Point(0,1,0);
	public static Point unitZ=new Point(0,0,1);
	public static Point crossProduct(Point p1, Point p2){
		return new Point(p1.y*p2.z-p1.z*p2.y, p1.z*p2.x-p1.x*p2.z, p1.x*p2.y-p1.y*p2.x);
	}
	public static Point projection(Point p1, Point p2){
		Point p2u=p2.unit();
		return p2u.scale(p2u.dot(p1));
	}
	public static boolean sameDirection(Point p1, Point p2){
		return !p1.isZero()&&!p2.isZero()&&Math.abs(p1.dot(p2)-p1.norm()*p2.norm())<0.02;
		//return p1.dot(p2)>=0;
	}
	public static boolean perpendicular(Point p1, Point p2){
		return Math.abs(p1.dot(p2))<0.0005;
	}
	public static Point plus(Point p1, Point p2){
		return new Point(p1.x+p2.x, p1.y+p2.y, p1.z+p2.z);
	}
	public static Point minus(Point p1, Point p2){
		return new Point(p1.x-p2.x, p1.y-p2.y, p1.z-p2.z);
	}
	public static double dotProduct(Point p1, Point p2){
		return p1.x*p2.x+p1.y*p2.y+p1.z*p2.z;
	}
	public static double angle(Point p1, Point p2){
		return Math.acos(p1.dot(p2)/(p1.norm()*p2.norm()));
	}
	public static boolean isOn(Line l, Point p){
		return l.baseVector().cross(p.minus(l.p1())).isZero();
	}
	public double x;
	public double y;
	public double z;
	public Point(double x, double y, double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Point plus(Point p1){
		return plus(this, p1);
	}
	public Point minus(Point p1){
		return minus(this, p1);
	}
	public Point cross(Point p1){
		return crossProduct(this, p1);
	}
	public Point project(Point p1){
		return projection(p1, this);
	}
	public double dot(Point p1){
		return dotProduct(this, p1);
	}
	public double norm(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	public Point projectOnPlane(Point point, Point normal){
		return projection(this, point, normal);
	}
	public Point rotate(Rotation r){
		Point p=new Point(this.x, this.y, this.z);
		for(int i=0;i<r.around.size();i++){
			p=p.rotate(r.around.get(i), r.axes.get(i), r.angles.get(i));
		}
		return p;
	}
	public Point rotate(Point around, int axis, double angle){
		Point op=this.minus(around);
		if(axis==0){
			double npy=op.y*Math.cos(angle)-op.z*Math.sin(angle);
			double npz=op.z*Math.cos(angle)+op.y*Math.sin(angle);
			return around.plus(new Point(op.x, npy, npz));
		}
		if(axis==1){
			double npz=op.z*Math.cos(angle)-op.x*Math.sin(angle);
			double npx=op.x*Math.cos(angle)+op.z*Math.sin(angle);
			return around.plus(new Point(npx, op.y, npz));
		}
		if(axis==2){
			double npx=op.x*Math.cos(angle)-op.y*Math.sin(angle);
			double npy=op.y*Math.cos(angle)+op.x*Math.sin(angle);
			return around.plus(new Point(npx, npy, op.z));
		}
		return null;
	}
	public Point scale(double r){
		return new Point(r*x, r*y, r*z);
	}
	public Point unit(){
		if(this.norm()==0){return this;}
		return this.scale(1/this.norm());
	}
	public boolean equals(Point p1){
		return x==p1.x&&y==p1.y&&z==p1.z;
	}
	public boolean sameDirection(Point p1){
		return sameDirection(this, p1);
	}
	public boolean perpendicularTo(Point p1){
		return perpendicular(this, p1);
	}
	public boolean isOn(Line l){
		return isOn(l, this);
	}
	public boolean isZero(){
		return Math.abs(x)<0.01&&Math.abs(y)<0.01&&Math.abs(z)<0.01;
	}
	public String toString(){
		return "("+x+","+y+","+z+")";
	}
}
