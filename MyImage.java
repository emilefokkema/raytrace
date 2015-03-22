package raytrace;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
public class MyImage {
	public int w;
	public int h;
	double[][] brightness;
	MyColor[][] color;
	public MyImage(int w, int h){
		this.w=w;
		this.h=h;
		this.brightness=new double[w][h];
		this.color=new MyColor[w][h];
	}
	public MyImage setBrightness(int l, int t, double b){
		this.brightness[l-1][t-1]=b;
		return setColor(l, t, new MyColor(b, b, b));
	}
	public MyImage setColor(int l, int t, MyColor c){
		this.color[l-1][t-1]=c;
		return this;
	}
	public BufferedImage makeBitmap(){
		BufferedImage bi=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g=bi.createGraphics();
		MyColor c;
		for(int l=1;l<=this.w;l++){
			for(int t=1;t<=this.h; t++){
				c=color[l-1][t-1];
				if(c!=null){
					g.setColor(c.toColor());
				}else{
					g.setColor(new Color(0,0,0));
				}
				
				g.fillRect(l, t, 1, 1);
			}
		}
		return bi;
	}
	public String toString(){
		String s="";
		for(int t=1;t<=this.h;t++){
			String s1="[";
			for(int l=1;l<=this.w;l++){
				s1+=this.brightness[l-1][t-1]+" ";
			}
			s+=s1+"]\n";
		}
		return s;
	}
}
