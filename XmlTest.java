package raytrace;

import java.util.ArrayList;

public class XmlTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SceneXmlFactory sf=SceneXmlFactory.getInstance();
		SceneXml sx=new SceneXml(sf.getSceneXml("c:\\Users\\efokkema\\Desktop\\raytrace\\scene.xml"));
		
		ArrayList<Rotation> rotations=sx.getGroupRotations();
		System.out.println(rotations.size()+", "+(rotations.get(1)==null));
		
		ArrayList<ArrayList<LightSource>> lll=sx.getLightSourcesInGroups();
		System.out.println(lll.get(1).size());
		
		Scene s=sx.getScene();
		System.out.println(s.shapes.size());
	}

}
