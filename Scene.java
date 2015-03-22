package raytrace;
import java.util.ArrayList;
public class Scene {
	public ArrayList<Shape> shapes;
	public ArrayList<LightSource> lightSources;
	public Scene(){
		this.shapes=new ArrayList<Shape>();
		this.lightSources=new ArrayList<LightSource>();
	}
	public Scene add(Shape s){
		this.shapes.add(s);
		return this;
	}
	public Scene rotate(Rotation r){
		for(int i=0;i<shapes.size();i++){
			shapes.get(i).rotate(r);
		}
		for(int i=0;i<lightSources.size();i++){
			lightSources.get(i).rotate(r);
		}
		return this;
	}
	public boolean illuminates(Point lightSource, Point p){
		Line toLightSource=new Line(p, lightSource);
		Point tl=toLightSource.baseVector();
		Point ti;
		Point[] intersections=intersect(toLightSource);
		for(int i=0;i<intersections.length;i++){
			ti=intersections[i].minus(p);
			if(!ti.isZero()&&ti.sameDirection(tl)&&ti.norm()<tl.norm()){
				return false;
			}
		}
		return true;
	}
	public double visibility(LightSource ls, Point from, Point direction){
		if(!illuminates(ls.location, from)){return 0;}
		Point distance=ls.location.minus(from);
		double angle=Math.acos(distance.unit().dot(direction.unit()));
		double maxangle=ls.radius/distance.norm();
		if(angle<=maxangle){return 1-angle/maxangle;}else{return 0;}
	}
	public double illumination(LightSource ls, Point p){
		Line toLightSource=new Line(p, ls.location);
		Point[] intersections=intersect(toLightSource);
		Point[] between=Point.between(intersections, p, ls.location);
		Point toLight=toLightSource.baseVector();
		double initial=Math.max(0, normal(p).dot(toLight.unit()));
		if(between.length==0){
			return initial;
		}else{
			for(int i=0;i<between.length;i++){if(curvature(between[i], ls.location.minus(p))==0){return 0;}}
			if(between.length%2!=0){return 0;}
			Point p1=between[0], n1=normal(between[0]), p2, n2;
			Line c;
			double angle=0, maxAngle=0;
			for(int i=0;i<between.length;i++){
				if(i%2==0){
					p1=between[i];
					n1=normal(p1);
				}else{
					p2=between[i];
					n2=normal(p2);
					if(n1.minus(n2).norm()<0.35){angle=0;}else{
						c=Line.intersectPlanes(p1, n1, p2, n2);
						if(c!=null){
							angle=Line.smallestAngle(p, toLight, c);
						}else{maxAngle=Math.PI/2;}
					}
					if(angle>=maxAngle){maxAngle=angle;}
				}
			}
			double maxLightSourceAngle=ls.radius/toLight.norm();
			if(maxAngle>maxLightSourceAngle){return 0;}else{return initial*Math.max(0, 1-maxAngle/maxLightSourceAngle);}
		}
	}
	public Point getClosest(Point viewPoint, Point direction){
		Line lineOfSight=new Line(viewPoint, viewPoint.plus(direction));
		Point[] intersections=intersect(lineOfSight);
		return Point.getClosest(viewPoint, direction, intersections);
	}
	public MyColor getNextColor(Point viewPoint, Point direction, int depth){
		MyColor c=new MyColor(0,0,0);
		if(depth>0){
			Point closest=getClosest(viewPoint, direction);
			if(closest!=null){
				Point currentDirection=closest.minus(viewPoint);
                MyColor d=new MyColor(0),e=new MyColor(0);
				double reflection=fromOutside(closest, currentDirection)?reflection(closest, currentDirection):0;
				double transparency=transparency(closest, currentDirection);
				Point nextDirection=reflect(closest, currentDirection);
				double roughness=roughness(closest, currentDirection);
                //Point nextDirection3=currentDirection.plus(s.normal(closest).project(currentDirection).scale(-2));
                //smoothness=Math.pow(Math.max(0, nextDirection.unit().dot(nextDirection3.unit())), Math.pow(currentDirection.norm(), 1.2));
				if(reflection>0){
					
                    

					d=getNextColor(closest, nextDirection, depth-1).scale(reflection);
					if(roughness>0){
						MyColor d1;
						for(int i=1;i<=5;i++){
							d1=getNextColor(closest, reflect(closest, currentDirection), depth-1).scale(reflection);
							d=d.scale((double)i/(double)(1+i)).add(d1.scale(1/(double)(i+1)));
						}
						
						
					}
				}
				if(transparency>0){
					Point nextDirection2=refract(closest, currentDirection);
					e=getNextColor(closest, nextDirection2, depth-1).scale(transparency);
                    d=d.limit();
				}
				c=c.add(color(closest).add(d).add(e));
				for(LightSource ls:lightSources){
					double howMuch=illumination(ls, closest);
					MyColor lsc=ls.color;
					c=c.add(lsc.scale(howMuch).scale(diffusion(closest, currentDirection)));
					c=c.add(lsc.scale(visibility(ls, closest, nextDirection)).scale(shininess(closest, currentDirection)));
				}
				
			}
		}
		return c;
	}
	public Point refract(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).refract(p, direction);}
		}
		return Point.origin;
	}
	public boolean fromOutside(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).fromOutside(p, direction);}
		}
		return true;
	}
	public Point reflect(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).reflect(p, direction);}
		}
		return Point.origin;
	}
    public Scene translate(Point p){
        for(int i=0;i<shapes.size();i++){
            shapes.get(i).translate(p);
        }
        for(int i=0;i<lightSources.size();i++){
            lightSources.get(i).translate(p);
        }
        return this;
    }
	public Scene add(LightSource ls){
		this.lightSources.add(ls);
		return this;
	}
	public MyColor color(Point p){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).color(p);}
		}
		return new MyColor(0);
	}
	public double transparency(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).transparency(p, direction);}
		}
		return 1;
	}
    public Line intersectionLine(Point onS1, Point onS2, Point p){
        Shape s1=null, s2=null;
        for(int i=0;i<shapes.size();i++){if(shapes.get(i).contains(onS1)){s1=shapes.get(i);}}
        for(int i=0;i<shapes.size();i++){if(shapes.get(i).contains(onS2)){s2=shapes.get(i);}}
        if(s1==null||s2==null){
            if(!p.minus(onS1).isZero()){
                return new Line(onS1.plus(onS2).scale(0.5), onS1.minus(onS2).cross(p.minus(onS1)));
            }else{
                return new Line(onS1.plus(onS2).scale(0.5), onS1.minus(onS2).cross(Point.unitX));
            }
        }else{
            return Shape.intersectionLine(s1, s2, p);
        }
    }
	public double diffusion(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).diffusion(p, direction);}
		}
		return 1;
	}
    public double roughness(Point p, Point direction){
        for(int i=0;i<shapes.size();i++){
            if(shapes.get(i).contains(p)){return shapes.get(i).roughness(p, direction);}
        }
        return 1;
    }
    public boolean onSameShape(Point p1, Point p2){
        for(int i=0;i<shapes.size();i++){
            if(shapes.get(i).contains(p1)&&shapes.get(i).contains(p2)){return true;}
        }
        return false;
    }
	public double shininess(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).shininess(p, direction);}
		}
		return 1;
	}
	public double reflection(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).reflection(p, direction);}
		}
		return 1;
	}
	public double curvature(Point p, Point direction){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).curvature(p, direction);}
		}
		return 0;
	}
	public boolean contains(Point p){
		for(int i=0;i<shapes.size();i++){
			if(!shapes.get(i).contains(p)){return false;}
		}
		return true;
	}
	public Point normal(Point p){
		for(int i=0;i<shapes.size();i++){
			if(shapes.get(i).contains(p)){return shapes.get(i).normal(p);}
		}
		return null;
	}
	public Point[] intersect(Line l){
		Point[] intersections=new Point[]{};
		for(int i=0;i<shapes.size();i++){
			intersections=Point.together(intersections, shapes.get(i).intersect(l));
		}
		return intersections;
	}
}
