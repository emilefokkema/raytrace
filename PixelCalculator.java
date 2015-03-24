package raytrace;

public class PixelCalculator implements Runnable{
	private int l, t, depth;
	private MyImage i;
	private Point viewPoint;
	private Viewport v;
	private Scene s;
	public PixelCalculator(int l, int t, MyImage i, Viewport v, Scene s, int depth){
		this.l=l;
		this.t=t;
		this.i=i;
		this.depth=depth;
		this.viewPoint=v.viewPoint;
		this.v=v;
		this.s=s;
	}
	public void run(){
		MyColor c1=s.getNextColor(viewPoint, v.getPoint(l+0.25, t+0.25).minus(viewPoint), depth);
        MyColor c2=s.getNextColor(viewPoint, v.getPoint(l+0.25, t+0.75).minus(viewPoint), depth);
        MyColor c3=s.getNextColor(viewPoint, v.getPoint(l+0.75, t+0.25).minus(viewPoint), depth);
        MyColor c4=s.getNextColor(viewPoint, v.getPoint(l+0.75, t+0.75).minus(viewPoint), depth);
		i.setColor(l, t, c1.scale(0.25).add(c2.scale(0.25)).add(c3.scale(0.25)).add(c4.scale(0.25)));
	}
}
