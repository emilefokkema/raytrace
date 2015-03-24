package raytrace;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class SceneImageWriter {
	int w,h, d;
	Scene s;
	Viewport v;
	String imagePath;
	public SceneImageWriter(Scene s, Viewport v, String imagePath, int recursionDepth){
		this.w=v.w;
		this.h=v.h;
		this.s=s;
		this.v=v;
		this.d=recursionDepth;
		this.imagePath=imagePath;
	}
	public void write() throws IOException{
		MyImage i=new MyImage(w, h);
		ExecutorService executor = Executors.newFixedThreadPool(4);
		for(int l=1;l<=w;l++){
			for(int t=1;t<=h;t++){
				Runnable pixelCalculator=new PixelCalculator(l, t, i, v, s, d);
				executor.execute(pixelCalculator);
			}
		}
		executor.shutdown();
		while(!executor.isTerminated()){}
		System.out.println("SceneImageWriter: image calculated.");
		BufferedImage bi=i.makeBitmap();
		File f=new File(imagePath);
		ImageIO.write(bi, "bmp", f);
        System.out.println("SceneImageWriter: file written.");
	}
}
