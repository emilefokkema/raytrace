package raytrace;

public class Intersection {
	public Point i;
	public double angle;
	public Intersection(Point i, double angle){
		this.i=i;
		this.angle=angle;
	}
	public String toString(){
		return "[point: "+i.toString()+", angle: "+angle*180/Math.PI+" degrees]";
	}
}
