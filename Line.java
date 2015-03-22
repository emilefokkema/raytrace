package raytrace;

public class Line {
	public static double smallestAngle(Point p, Point direction, Line l){
		Point lineDirection=l.baseVector();
		if(lineDirection.sameDirection(direction)){return Math.PI/2;}
		if(lineDirection.dot(direction)<0){lineDirection=lineDirection.scale(-1);}
		double alpha=Math.acos(lineDirection.dot(direction)/(lineDirection.norm()*direction.norm()));
		Point linePoint=l.p1();
		Point yAxis=direction.cross(lineDirection);
		Point zAxis=direction.cross(yAxis);
		double y0=yAxis.project(linePoint.minus(p)).norm();
		double linePointZ=zAxis.project(linePoint.minus(p)).norm();
		double linePointX=direction.project(linePoint.minus(p)).norm();
		double x0=linePointX+linePointZ/Math.tan(alpha);
		if(Math.abs(x0)<0.05){return Math.PI/2;}
		double thingSquare=Math.pow(y0*Math.cos(alpha)/(x0*Math.sin(alpha)),2);
		double cosineSquare=Math.pow(x0*(1-thingSquare),2)/(Math.pow(x0*(1-thingSquare), 2)+y0*y0+thingSquare);
		return Math.acos(Math.sqrt(cosineSquare));
	}
	public static Point intersection(Line l1, Line l2){
		Point a=l1.baseVector();
		Point b=l2.baseVector();
		Point c=l2.p1().minus(l1.p1());
		if(c.dot(a.cross(b))!=0){return null;}else{
			Point d=a.cross(b);
			double nd=d.norm();
			if(nd==0){return null;}
			return l1.p1().plus(a.scale(c.cross(b).dot(d)/Math.pow(nd, 2)));
		}
		
	}
    public static Point intersection(Line l1, Point p, Point n){
        if(l1.baseVector().perpendicularTo(n)){return null;}
        double t=-n.dot(l1.p1().minus(p))/n.dot(l1.baseVector());
        return l1.p1().plus(l1.baseVector().scale(t));
    }
	public static Line intersectPlanes(Point p1, Point n1, Point p2, Point n2){
		if(n1.sameDirection(n2)||n1.scale(-1).sameDirection(n2)){return null;}
		Point base=n1.cross(n2);
		double p1dn1=p1.dot(n1);
		double p2dn2=p2.dot(n2);
		double x=0, y=0, z=0;
		if(base.x!=0){
			x=0;
			y=(n2.z*p1dn1-n1.z*p2dn2)/base.x;
			z=(-n2.y*p1dn1+n1.y*p2dn2)/base.x;
		}
		else if(base.y!=0){
			x=-(n2.z*p1dn1-n1.z*p2dn2)/base.y;
			y=0;
			z=-(-n2.x*p1dn1+n1.x*p2dn2)/base.y;
		}
		else if(base.z!=0){
			x=(n2.y*p1dn1-n1.y*p2dn2)/base.z;
			y=(-n2.x*p1dn1+n1.x*p2dn2)/base.z;
			z=0;
		}
		Point basePoint=new Point(x, y, z);
		return new Line(basePoint, basePoint.plus(base));
	}
	public static Line perpendicular(Line l, Point p){
		if(p.isOn(l)){return null;}
		Point a=l.baseVector();
		Point b=p.minus(l.p1);
		Point p2=l.p1().plus(a.scale(a.dot(b)/Math.pow(a.norm(), 2)));
		return new Line(p, p2);
	}
	private Point p1, p2;
	public Line(Point p1, Point p2){
		this.p1=p1;
		this.p2=p2;
	}
	public Point p1(){return p1;}
	public Point p2(){return p2;}
	public Point baseVector(){
		return p2.minus(p1);
	}
	public Intersection intersect(Line l1){
		Point i=intersection(this, l1);
		if(i==null){return null;}
		else{return new Intersection(i, Point.angle(this.baseVector(), l1.baseVector()));}
	}
	public Line normal(Point p){
		return perpendicular(this, p);
	}
	public String toString(){
		return "[line through "+p1+" and "+p2+"]";
	}
}
