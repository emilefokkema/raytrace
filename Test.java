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


//		SceneXmlFactory sf=SceneXmlFactory.getInstance();
//		SceneXml sx=new SceneXml(sf.getSceneXml("c:\\Users\\efokkema\\Desktop\\raytrace\\scene.xml"));
//		Scene s1=sx.getScene();
//		Viewport v1=sx.getViewPort();
//		
//		SceneImageWriter w2=new SceneImageWriter(s1, v1, "c:\\Users\\efokkema\\Desktop\\raytrace\\image2.bmp", 3);
//		w2.write();
		new RaytraceServer().run();
		
		
	}
	
	
}
