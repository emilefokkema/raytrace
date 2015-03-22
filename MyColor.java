package raytrace;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MyColor {
	private static Pattern colorStringPattern=Pattern.compile("\\d{1,3},\\s?\\d{1,3},\\s?\\d{1,3}t?");
	public double r;
	public double g;
	public double b;
	public boolean reflectable;
	private MyColor(double r, double g, double b, boolean reflectable){
		this(r,g,b);
		this.reflectable=reflectable;
	}
	public MyColor(double r, double g, double b){
		this.r=r;
		this.g=g;
		this.b=b;
	}
	public MyColor(double brightness){
		this(brightness, brightness, brightness);
	}
	public MyColor(String colorString){
		Matcher m=colorStringPattern.matcher(colorString);
		if(m.find()){
			String[] vals=colorString.split(",");
			this.r=Double.parseDouble(vals[0].trim());
			this.g=Double.parseDouble(vals[1].trim());
			if(colorString.endsWith("t")){
				String trimmed=vals[2].trim();
				this.b=Double.parseDouble(trimmed.substring(0,trimmed.length()-1));
				this.reflectable=true;
			}
			else{
				this.b=Double.parseDouble(vals[2].trim());
			}
		}else{
			this.r=this.g=this.b=0;
		}
	}
	private MyColor(double brightness, boolean reflectable){
		this(brightness, brightness, brightness, reflectable);
		
	}
	public Color toColor(){
		double[] values=new double[]{r, g, b};
		int[] intValues=new int[3];
		for(int i=0;i<3;i++){
			int v=(int)values[i];
			if(v<0){v=0;}
			if(v>255){v=255;}
			intValues[i]=v;
		}
		return new Color(intValues[0], intValues[1], intValues[2]);
	}
    public MyColor limit(double l){
        if(Math.min(this.r, Math.min(this.g, this.b))<l){return new MyColor(0);}
        return this;
    }
    public MyColor limit(){
    	if(this.reflectable){return this;}else{return new MyColor(0);}
    }
	public MyColor add(MyColor c){
		return new MyColor(this.r+c.r, this.g+c.g, this.b+c.b, this.reflectable||c.reflectable);
	}
	public MyColor multiply(MyColor c){
		return new MyColor(this.r*c.r, this.g*c.g, this.b*c.b);
	}
	public MyColor scale(double s){
		return new MyColor(this.r*s, this.g*s, this.b*s, this.reflectable);
	}
	public MyColor setReflectable(){
		this.reflectable=true;
		return this;
	}
}
