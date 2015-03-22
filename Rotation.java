package raytrace;
import java.util.ArrayList;
public class Rotation {
	public ArrayList<Point> around;
	public ArrayList<Integer> axes;
	public ArrayList<Double> angles;
	private Rotation(){
		this.around=new ArrayList<Point>();
		this.axes=new ArrayList<Integer>();
		this.angles=new ArrayList<Double>();
	}
	private Rotation(ArrayList<Point> around, ArrayList<Integer> axes, ArrayList<Double> angles){
		this.around=around;
		this.axes=axes;
		this.angles=angles;
	}
	public Rotation(Point around, int axis, double angle){
		this();
		this.around.add(around);
		this.axes.add(axis);
		this.angles.add(angle);
	}
	public Rotation before(Point around, int axis, double angle){
		return this.before(new Rotation(around, axis, angle));
	}
	public Rotation before(Rotation r){
		ArrayList<Point> around=(ArrayList<Point>)this.around.clone();
		around.addAll(r.around);
		ArrayList<Integer> axes=(ArrayList<Integer>)this.axes.clone();
		axes.addAll(r.axes);
		ArrayList<Double> angles=(ArrayList<Double>)this.angles.clone();
		angles.addAll(r.angles);
		return new Rotation(around, axes, angles);
		
	}
}
