package raytrace;

/**
 * Created by Emile on 7-3-2015.
 */
public abstract class ColorPattern {
    public abstract double alpha(double x, double y);
    public abstract MyColor color(double x, double y);
}
class Rectangle extends ColorPattern{
    private double x1, y1, x2, y2;
    private double alpha;
    private MyColor color;
    private double thickness=-1;
    public Rectangle(double x1, double y1, double x2, double y2, MyColor c, double alpha, double thickness){
    	this(x1, y1, x2, y2, c, alpha);
    	this.thickness=thickness;
    }
    public Rectangle(double x1, double y1, double x2, double y2, MyColor c, double alpha){
        this.x1=Math.min(x1, x2);
        this.y1=Math.min(y1, y2);
        this.x2=Math.max(x1, x2);
        this.y2=Math.max(y1, y2);
        this.alpha=alpha;
        this.color=c;
    }
    public double alpha(double x, double y){
    	if(thickness>=0){
    		if((Math.abs(x-x1)<thickness||Math.abs(x-x2)<thickness||Math.abs(y-y1)<thickness||Math.abs(y-y2)<thickness)&&(x>=x1-thickness)&&(x<=x2+thickness)&&(y>=y1-thickness)&&(y<=y2+thickness)){return this.alpha;}
    	}else{
    		if(x>=this.x1&&x<=this.x2&&y>=this.y1&&y<=this.y2){return this.alpha;}
    	}
        
        return 0;
    }
    public MyColor color(double x, double y){
        return this.color;
    }
}
class Circles extends ColorPattern{
	private double r, thickness, offsetX, offsetY;
	private double alpha;
	private MyColor color;
	private int interval;
	public Circles(double r, double thickness, MyColor c, double alpha, double offsetX, double offsetY, int interval){
		this.r=r;
		this.thickness=thickness;
		this.color=c;
		this.alpha=alpha;
		this.offsetX=offsetX;
		this.offsetY=offsetY;
		this.interval=interval;
	}
	public double alpha(double x, double y){
		x=x-this.offsetX;
		y=y-this.offsetY;
		x=Math.abs(x-2*r*(int)(x/(2*r)));
		y=Math.abs(y-2*r*(int)(y/(2*r)));
		double r1=Math.sqrt(Math.pow((x-r), 2)+Math.pow(y-r, 2));
		if((int)(r1/this.thickness)%interval==0){return this.alpha;}
		return 0;
	}
	public MyColor color(double x, double y){
		return this.color;
	}
}
