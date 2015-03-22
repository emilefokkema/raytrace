package raytrace;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Test {

	public static void main(String[] args) throws IOException {


		SceneXmlFactory sf=SceneXmlFactory.getInstance();
		SceneXml sx=new SceneXml(sf.getSceneXml("c:\\Users\\efokkema\\Desktop\\raytrace\\scene.xml"));
		Scene s1=sx.getScene();

		System.out.println(Line.intersectPlanes(new Point(1,1,0), new Point(1,1,0), new Point(-1,1,0), new Point(-1,1,0)));
		int h=600;
		double ratio=(double)279/(double)498;
		Point leftTop=new Point(1.6, 1.6*ratio, -1);
		int w=(int)((double)h*(double)leftTop.x/(double)leftTop.y);
		Point leftAxis=new Point(-3.2, 0, 0);
		Point topAxis=new Point(0, -3.2, 0).scale(ratio);
		Rotation r=new Rotation(new Point(0,0,-0.5), 0, Math.PI*0.4).before(new Point(0,0,-1), 0, -0.38);
        Rotation r3=new Rotation(Point.origin, 0, -0.3).before(new Point(0, 13, 0), 0, Math.PI / 6);
		Rotation r2=new Rotation(Point.origin, 1, -Math.PI/4+0);
		Point translation=new Point(0,0,0);
		Viewport v=new Viewport(leftTop, leftAxis, topAxis).setSize(w, h).rotate(r);
		//Viewport v=new Viewport(leftTop, leftAxis, topAxis).setScale((double)2*leftTop.y/(double)h).rotate(r);
		
		Point viewPoint=new Point(0,0,-19).rotate(r);
		double radius=0.5;
		double distance=1.1;
		Shape sh=new Sphere(Point.origin, radius, new MyColor(0,0,0)).setReflection(1).setDiffusion(0).setShininess(0);
		Shape sh2=new Sphere(new Point(distance, 0, 0),radius, new MyColor(0,0,0)).setDiffusion(0).setReflection(0.6).setTransparency(1).setRefractionIndex(0.5).setShininess(0);
		Shape sh3=new Sphere(new Point(-distance, 0, 0),radius, new MyColor(20,20,20)).setDiffusion(1.3).setReflection(0).setShininess(0);
		double corner=10;
		double back=18;
        MyColor wallColor=new MyColor(17, 17, 17);
        double windowIntensity=230;
        double x=-8.86, y=10.47;

        ColorPattern circles=new Circles(0.5,0.0003,new MyColor(60,60,60), 1, x-0.5,y-0.5, 30);
		Shape table=new Plane(new Point(10,-radius,10), new Point(0,1,0),new MyColor(0,0,0)).setReflection(0.8).setDiffusion(0).setRoughness(0.2).add(circles);
        Shape p=new Plane(new Point(-1.66,-radius+0.02,-1.19), Point.unitY, new MyColor(160,160,160));
        Shape paper=new RectangleSection((Plane)p,0,0,3.33,2).setReflection(0);
        ColorPattern drawing=new Rectangle(0.2,0.2,3.13,1.8,new MyColor(0), 0.5);
        paper.add(drawing);

		Shape ceiling=new Plane(new Point(0,16,0), new Point(0,-1,0),new MyColor(30, 30, 30)).setReflection(0).setDiffusion(1);
		ceiling.add(new Rectangle(-27,-16,9,9,new MyColor(0), 1,0.2));
		Shape wall1=new Plane(new Point(10, 0, 10), new Point(-1, 0, 0),new MyColor(windowIntensity).setReflectable()).setReflection(0);
        Shape p1=new Plane(new Point(9.75, 0, 10), new Point(-1, 0, 0),wallColor).setDiffusion(0.2).setReflection(0);
        Shape partition=new RectangleSection((Plane)p1, 0,10,18,11.5);
		Shape wall2=new Plane(new Point(0, 0, 10), new Point(0,0,-1),wallColor).setReflection(0).setDiffusion(0.2);
		Shape wall3=new Plane(new Point(-18, 0, -17), new Point(0,0,1),wallColor).setReflection(0).setDiffusion(0.2);
		Shape wall4=new Plane(new Point(-28, 0, -18), new Point(1, 0, 0),wallColor).setReflection(0).setDiffusion(0.2);
		double lightSourceIntensity=180;
		LightSource ls=new LightSource(new Point(0, 9, -4), new MyColor(lightSourceIntensity), 15);
		Scene s=new Scene().add(table).add(wall1).add(partition).add(wall2).add(wall3).add(wall4).add(ceiling).add(ls).rotate(r2).translate(translation).add(sh).add(sh2).add(sh3).add(paper);
		
		
		MyImage i=new MyImage(w, h);
		int d=7;
		ExecutorService executor = Executors.newFixedThreadPool(4);
		for(int l=1;l<=w;l++){
			for(int t=1;t<=h;t++){
				Runnable pixelCalculator=new PixelCalculator(l, t, i, viewPoint, v, s, d);
				executor.execute(pixelCalculator);
			}
		}
		executor.shutdown();
		while(!executor.isTerminated()){}
		System.out.println("a");
		/*for(int l=1;l<=w;l++){
			for(int t=1;t<=h;t++){
                MyColor c1=s.getNextColor(viewPoint, v.getPoint(l+0.25, t+0.25).minus(viewPoint), d);
                MyColor c2=s.getNextColor(viewPoint, v.getPoint(l+0.25, t+0.75).minus(viewPoint), d);
                MyColor c3=s.getNextColor(viewPoint, v.getPoint(l+0.75, t+0.25).minus(viewPoint), d);
                MyColor c4=s.getNextColor(viewPoint, v.getPoint(l+0.75, t+0.75).minus(viewPoint), d);
				i.setColor(l, t, c1.scale(0.25).add(c2.scale(0.25)).add(c3.scale(0.25)).add(c4.scale(0.25)));
			}
		}*/
		
		BufferedImage bi=i.makeBitmap();
		File f=new File("c:\\Users\\efokkema\\Desktop\\raytrace\\image.bmp");
		ImageIO.write(bi, "bmp", f);
        System.out.println("b");
		
	}
	
	
}
