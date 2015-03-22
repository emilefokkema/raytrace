package raytrace;

public class Viewport {
	public Point topLeft, unitLeftAxis, unitTopAxis;
	public int w, h;
	public Viewport(Point topLeft, Point leftAxis, Point topAxis){
		this.topLeft=topLeft;
		//this.unitLeftAxis=leftAxis.unit();
		//this.unitTopAxis=topAxis.unit();
		this.unitLeftAxis=leftAxis;
		this.unitTopAxis=topAxis;
	}
	public Viewport setScale(double scale){
		this.unitLeftAxis=this.unitLeftAxis.scale(scale);
		this.unitTopAxis=this.unitTopAxis.scale(scale);
		return this;
	}
	public Viewport setSize(int w, int h){
		this.w=w;
		this.h=h;
		return this;
	}
	public Point getPoint(double l, double t){
		return this.topLeft.plus(unitLeftAxis.scale(l/this.w).plus(unitTopAxis.scale(t/this.h)));
		//return this.topLeft.plus(unitLeftAxis.scale(l).plus(unitTopAxis.scale(t)));
	}
    public Viewport translate(Point p){
        this.topLeft=this.topLeft.plus(p);
        return this;
    }
	public Viewport rotate(Point around, int axis, double angle){
		return this.rotate(new Rotation(around, axis, angle));
	}
	public Viewport rotate(Rotation r){
		this.unitLeftAxis=this.topLeft.plus(this.unitLeftAxis);
		this.unitTopAxis=this.topLeft.plus(this.unitTopAxis);
		this.topLeft=this.topLeft.rotate(r);
		this.unitLeftAxis=this.unitLeftAxis.rotate(r).minus(this.topLeft);
		this.unitTopAxis=this.unitTopAxis.rotate(r).minus(this.topLeft);
		return this;
	}
}
