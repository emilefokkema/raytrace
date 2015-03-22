package raytrace;

public class LightSource {
	public Point location;
	public double radius;
	public double brightness;
	public MyColor color;
	public LightSource(Point location, MyColor color, double radius){
		this.location=location;
		this.color=color;
		this.radius=radius;
	}
	public LightSource(Point location, double brightness, double radius){
		this(location, new MyColor(brightness), radius);
	}
	public LightSource rotate(Rotation r){
		this.location=this.location.rotate(r);
		return this;
	}
    public LightSource translate(Point p){
        this.location=this.location.plus(p);
        return this;
    }
	public LightSource(Point location, double brightness){
		this(location, brightness, 0.1);
		
	}
}
