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
	private static Plane getPlane(Element e){
		MyColor c=new MyColor(e.getAttribute("color"));
		Point point=getPoint((Element)e.getElementsByTagName("point").item(0));
		Point normal=getPoint((Element)e.getElementsByTagName("normal").item(0));
		return new Plane(point, normal, c);
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
		ArrayList<Shape> s=new ArrayList<Shape>();
		for(int i=0;i<planeList.getLength();i++){
			Element plane=(Element)planeList.item(i);
			s.add(getPlane(plane));
		}
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
			for(int j=0;j<lightSources.size();j++){
				s.add(lightSources.get(j));
			}
			for(int j=0;j<shapes.size();j++){
				s.add(shapes.get(i));
			}
			if(r!=null){s.rotate(r);}
		}
		return s;
	}
	public Rotation getRotation(String id){
		Element e=getElementById(id);
		if(e!=null){
			return getRotation(e);
		}
		
		return null;
	}
	
}
