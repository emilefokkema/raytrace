package raytrace;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class SceneXml {
	private static Rotation getRotation(Element e){
		int axis=Integer.parseInt(e.getAttribute("axis"));
		double angle=Double.parseDouble(e.getAttribute("angle"));
		Point point=getPoint((Element)e.getElementsByTagName("point").item(0));
		return new Rotation(point, axis, angle);
	}
	private static Point getPoint(Element e){
		double x=Double.parseDouble(e.getAttribute("x"));
		double y=Double.parseDouble(e.getAttribute("y"));
		double z=Double.parseDouble(e.getAttribute("z"));
		return new Point(x,y,z);
	}
	private static Shape setShapeAttributes(Shape s, Element e){
		String att;
		if(!(att=e.getAttribute("reflection")).equals("")){s.setReflection(Double.parseDouble(att));}
		if(!(att=e.getAttribute("diffusion")).equals("")){s.setDiffusion(Double.parseDouble(att));}
		if(!(att=e.getAttribute("shininess")).equals("")){s.setShininess(Double.parseDouble(att));}
		if(!(att=e.getAttribute("transparency")).equals("")){s.setTransparency(Double.parseDouble(att));}
		if(!(att=e.getAttribute("refractionIndex")).equals("")){s.setRefractionIndex(Double.parseDouble(att));}
		if(!(att=e.getAttribute("roughness")).equals("")){s.setRoughness(Double.parseDouble(att));}
		//s.setDiffusion(0.4);
		return s;
	}
	private static Plane getPlane(Element e){
		MyColor c=new MyColor(e.getAttribute("color"));
		Point point=getPoint((Element)e.getElementsByTagName("point").item(0));
		Point normal=getPoint((Element)e.getElementsByTagName("normal").item(0));
		return (Plane)setShapeAttributes(new Plane(point, normal, c), e);
		//return new Plane(point, normal, c);
	}
	private static Sphere getSphere(Element e){
		MyColor c=new MyColor(e.getAttribute("color"));
		Point center=getPoint((Element)e.getElementsByTagName("center").item(0));
		double radius=Double.parseDouble(e.getAttribute("radius"));
		return (Sphere)setShapeAttributes(new Sphere(center, radius, c), e);
		//return new Sphere(center, radius, c);
	}
	private static LightSource getLightSource(Element e){
		Point location=getPoint((Element)e.getElementsByTagName("location").item(0));
		MyColor c=new MyColor(e.getAttribute("color"));
		double radius=Double.parseDouble(e.getAttribute("radius"));
		return new LightSource(location, c, radius);
	}
	private static ArrayList<LightSource> getLightSources(Element group){
		NodeList lList=group.getElementsByTagName("lightsource");
		ArrayList<LightSource> l=new ArrayList<LightSource>();
		for(int i=0; i<lList.getLength();i++){
			Element ls=(Element)lList.item(i);
			l.add(getLightSource(ls));
		}
		return l;
	}
	private static ArrayList<Shape> getShapes(Element group){
		NodeList planeList=group.getElementsByTagName("plane");
		NodeList sphereList=group.getElementsByTagName("sphere");
		ArrayList<Shape> s=new ArrayList<Shape>();
		for(int i=0;i<planeList.getLength();i++){
			Element plane=(Element)planeList.item(i);
			System.out.println("[getShapes] adding plane");
			s.add(getPlane(plane));
		}
		for(int i=0;i<sphereList.getLength();i++){
			Element sphere=(Element)sphereList.item(i);
			System.out.println("[getShapes] adding sphere");
			s.add(getSphere(sphere));
		}
		System.out.println("[getShapes] how many: "+s.size());
		//System.out.println(s.get(1));
		return s;
	}
	private static Rotation getGroupRotation(Element group){
		NodeList rList=group.getElementsByTagName("rotation");
		Rotation r=null;
		for(int i=0;i<rList.getLength();i++){
			Element rot=(Element)rList.item(i);
			int axis=Integer.parseInt(rot.getAttribute("axis"));
			double angle=Double.parseDouble(rot.getAttribute("angle"));
			Point point=getPoint((Element)rot.getElementsByTagName("point").item(0));
			if(i==0){
				r=new Rotation(point, axis, angle);
			}else{
				r=r.before(point, axis, angle);
			}
		}
		return r;
	}
	private Document xml;
	public SceneXml(Document xml){
		this.xml=xml;
	}
	private Element getElementById(String id){
		return xml.getElementById(id);
	}
	public ArrayList<Rotation> getGroupRotations(){
		NodeList groups=((Element)xml.getElementsByTagName("scene").item(0)).getElementsByTagName("group");
		ArrayList<Rotation> r=new ArrayList<Rotation>();
		for(int i=0;i<groups.getLength();i++){
			r.add(getGroupRotation((Element)groups.item(i)));
		}
		return r;
	}
	public ArrayList<ArrayList<LightSource>> getLightSourcesInGroups(){
		ArrayList<ArrayList<LightSource>> lll=new ArrayList<ArrayList<LightSource>>();
		NodeList groups=((Element)xml.getElementsByTagName("scene").item(0)).getElementsByTagName("group");
		for(int i=0;i<groups.getLength();i++){
			lll.add(getLightSources((Element)groups.item(i)));
		}
		return lll;
	}
	public Scene getScene(){
		Scene s=new Scene();
		NodeList groups=((Element)xml.getElementsByTagName("scene").item(0)).getElementsByTagName("group");
		for(int i=0;i<groups.getLength();i++){
			Element group=(Element)groups.item(i);
			Rotation r=getGroupRotation(group);
			ArrayList<LightSource> lightSources=getLightSources(group);
			ArrayList<Shape> shapes=getShapes(group);
			System.out.println("[getScene] size of shapes: "+shapes.size());
			//System.out.println(shapes.get(1));
			for(int j=0;j<lightSources.size();j++){
				s.add(lightSources.get(j));
			}
			for(int j=0;j<shapes.size();j++){
				System.out.println("[getScene] adding shape");
				s.add(shapes.get(j));
			}
			if(r!=null){s.rotate(r);}
		}
		//System.out.println(s.shapes.get(1));
		return s;
	}
	public Viewport getViewPort(){
		Element viewPort=(Element)((Element)xml.getElementsByTagName("scene").item(0)).getElementsByTagName("viewport").item(0);
		double x,y,z;
		Element leftTop_=(Element)viewPort.getElementsByTagName("lefttop").item(0);
			x=Double.parseDouble(leftTop_.getAttribute("x"));
			y=Double.parseDouble(leftTop_.getAttribute("y"));
			z=Double.parseDouble(leftTop_.getAttribute("z"));
			Point leftTop=new Point(x,y,z);
		Element leftAxis_=(Element)viewPort.getElementsByTagName("leftaxis").item(0);
			x=Double.parseDouble(leftAxis_.getAttribute("x"));
			y=Double.parseDouble(leftAxis_.getAttribute("y"));
			z=Double.parseDouble(leftAxis_.getAttribute("z"));
			Point leftAxis=new Point(x,y,z);
		Element topAxis_=(Element)viewPort.getElementsByTagName("topaxis").item(0);
			x=Double.parseDouble(topAxis_.getAttribute("x"));
			y=Double.parseDouble(topAxis_.getAttribute("y"));
			z=Double.parseDouble(topAxis_.getAttribute("z"));
			Point topAxis=new Point(x,y,z);
		Element viewPoint_=(Element)viewPort.getElementsByTagName("viewpoint").item(0);
			x=Double.parseDouble(viewPoint_.getAttribute("x"));
			y=Double.parseDouble(viewPoint_.getAttribute("y"));
			z=Double.parseDouble(viewPoint_.getAttribute("z"));
			Point viewPoint=new Point(x,y,z);
		int w=Integer.parseInt(viewPort.getAttribute("w"));
		int h=Integer.parseInt(viewPort.getAttribute("h"));
		return new Viewport(leftTop, leftAxis, topAxis, viewPoint).setSize(w, h);
	}
	public Rotation getRotation(String id){
		Element e=getElementById(id);
		if(e!=null){
			return getRotation(e);
		}
		
		return null;
	}
	
}
