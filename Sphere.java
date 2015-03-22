package raytrace;
import java.util.ArrayList;

abstract class Shape{
    public static Line intersectionLine(Shape s1, Shape s2, Point p){
        Point d1=s1.directionOfCenter(p).scale(s1.distanceFrom(p));
        Point d2=s2.directionOfCenter(p).scale(s2.distanceFrom(p));
        double angle1=Math.min(s1.viewAngle(p), Math.PI-0.1);
        double angle2=Math.min(s2.viewAngle(p), Math.PI - 0.1);
        double dd1=d1.norm();
        double dd2=d2.norm();
        double r1=dd1/(1-Math.sin(angle1/2))-dd1;
        double r2=dd2/(1-Math.sin(angle2/2))-dd2;
        double cosTheta=d1.project(d2).norm()/dd2;
        double theta=Math.acos(cosTheta);
        double angle;
        double d=Math.sqrt(Math.pow(dd1+r1, 2)+Math.pow(dd2+r2, 2)-2*(dd1+r1)*(dd2+r2)*cosTheta);
        double defaultAngle;

        if(d>=r1+r2){
           if(angle1/2>theta){angle=theta-angle2/2;}
            else if(theta-angle2/2<0){angle=angle1/2;}
            else if(theta-angle2/2>=angle1/2){angle=(theta-angle2/2+angle1/2)/2;}
            else{
               if(dd1<=dd2){angle=angle1/2;}else{angle=theta-angle2/2;}
           }

            return new Line(p.plus(d1.unit().scale(Math.cos(angle)).plus(d2.minus(d1.project(d2)).unit().scale(Math.sin(angle))).scale((dd1+dd2)/2)), d1.cross(d2));
        }else{
            double x=(d*d-r1*r1+r2*r2)/(2*d);
            double y=Math.sqrt((-d+r1-r2)*(-d-r1+r2)*(-d+r1+r2)*(d+r1+r2))/(2*d);

        }
        return new Line(p.plus(d1.plus(d2).scale(0.5)), d1.cross(d2));
    }
	public double reflection;
	public double shininess;
	public double diffusion;
	public double transparency=0;
	public double refractionIndex=1;
    public double roughness=0;
    public MyColor color;
    public ArrayList<ColorPattern> patterns;
    public Shape(){
        this.patterns=new ArrayList<ColorPattern>();
    }
	public abstract Point[] intersect(Line line);
	public abstract Point normal(Point p);
	public abstract boolean contains(Point p);
	public MyColor color(Point p){
        double x=x(p);
        double y=y(p);
        MyColor c=this.color, c1;
        double alpha;
        for(int i=0;i<this.patterns.size();i++){
            c1=patterns.get(i).color(x,y);

                alpha=patterns.get(i).alpha(x,y);
                c=c.scale(1-alpha).add(c1.scale(alpha));


        }
        return c;
    };
    public abstract double x(Point p);
    public abstract double y(Point p);
	public abstract double diffusion(Point p, Point direction);
	public abstract double reflection(Point p, Point direction);
	public abstract double refractionIndex(Point p, Point direction);
	public abstract double curvature(Point p, Point direction);
	public abstract double shininess(Point p, Point direction);
    public abstract double roughness(Point p, Point direction);
	public abstract double transparency(Point p, Point direction);
	public abstract boolean fromOutside(Point p, Point direction);
	public abstract Shape rotate(Rotation r);
    public abstract Shape translate(Point p);
    public abstract double distanceFrom(Point p);
    public abstract Point directionOfCenter(Point p);
    public abstract double viewAngle(Point p);
    public Shape add(ColorPattern pattern){
        this.patterns.add(pattern);
        return this;
    }
	public Point reflect(Point p, Point direction){
		Point n=normal(p);
		Point r=direction.plus(n.project(direction).scale(-2));
		if(this.roughness>0){
			Point Right=direction.cross(n).unit();
			Point Down=direction.cross(Right).unit();
			//Point toTheRight=direction.cross(n).unit().scale((2*Math.random()-1)*this.roughness);
			double angleOut=Math.random()*roughness;
			double angleAround=Math.random()*2*Math.PI;
			double R=direction.norm()*Math.tan(angleOut);
			double s1=R*Math.cos(angleAround);
			double s2=R*Math.sin(angleAround);
			r=r.plus(Right.scale(s1).plus(Down.scale(s2)));
			if(r.dot(n)<0){r=r.plus(n.project(r).scale(-2));}
		}
		
		return r;
	}
    public Point refract(Point p, Point direction){
        double dot=direction.unit().dot(normal(p));
        if(fromOutside(p, direction)){
            double angle=Math.acos(-dot);
            double oldSine=direction.minus(normal(p).project(direction)).norm()/direction.norm();
            double newSine=oldSine*this.refractionIndex;
            double newCosine=Math.cos(Math.asin(newSine));
            return normal(p).project(direction).scale(newCosine/(-dot)).plus(direction.minus(normal(p).project(direction)).scale(newSine/oldSine));
            //return direction.plus(direction.minus(normal(p).project(direction)).scale(this.refractionIndex-1));
        }else if(dot>0){
            double oldSine=direction.minus(normal(p).project(direction)).norm()/direction.norm();
            double newSine=oldSine/this.refractionIndex;
            if(newSine>1){return reflect(p, direction);}
            double newCosine=Math.cos(Math.asin(newSine));
            return normal(p).project(direction).scale(newCosine/dot).plus(direction.minus(normal(p).project(direction)).scale(newSine/oldSine));
        }else{
            return direction;
        }
    }
	public Shape setReflection(double r){
		this.reflection=r;
		return this;
	}
    public Shape setRoughness(double r){
        this.roughness=r;
        return this;
    }
	public Shape setTransparency(double r){
		this.transparency=r;
		return this;
	}
	public Shape setRefractionIndex(double r){
		this.refractionIndex=r;
		return this;
	}
	public Shape setShininess(double d){
		this.shininess=d;
		return this;
	}
	public Shape setDiffusion(double d){
		this.diffusion=d;
		return this;
	}
}
class Sphere extends Shape{
	public Point center;
	public double radius;
	public double criticalCosine;
	public Sphere(Point center, double radius, MyColor color){
		this.center=center;
		this.radius=radius;
		this.color=color;
		this.reflection=0.5;
		this.diffusion=0.5;
		this.shininess=0.5;
		this.criticalCosine=Math.sqrt(1-Math.pow(this.refractionIndex, 2));
	}
	public Sphere(Point center, double radius){
		this(center, radius, new MyColor(0,0,0));
	}
	public Sphere rotate(Rotation r){
		this.center=this.center.rotate(r);
		return this;
	}
    public Sphere translate(Point p){
        this.center=this.center.plus(p);
        return this;
    }
	public boolean fromOutside(Point p, Point direction){
		return direction.dot(normal(p))<0;
	}
    public double x(Point p){
        return 0;
    }
    public double y(Point p){
        return 0;
    }
	public Point normal(Point p){
		return p.minus(this.center).unit();
	}
	public boolean contains(Point p){
		return Math.abs(this.radius-p.minus(this.center).norm())<0.01;
	}
	public double curvature(Point p, Point direction){
		if(direction.dot(p.minus(this.center))<=0){return 1/this.radius;}else{return -1/this.radius;}
	}
	public double transparency(Point p, Point direction){
		if(this.reflection>0&&fromOutside(p, direction)){
			double dot=Math.abs(direction.unit().dot(normal(p).unit()));
			return Math.pow(dot, 0.5)*this.transparency;
		}
        return this.transparency;
	}
	public double diffusion(Point p, Point direction){
		return this.diffusion;
	}
    public double roughness(Point p, Point direction){
        return this.roughness;
    }
	public double refractionIndex(Point p, Point direction){
		return this.refractionIndex;
	}
	public double shininess(Point p, Point direction){
		return this.shininess*curvature(p, direction);
	}
	public double reflection(Point p, Point direction){
		/*if(this.transparency>0){
			return (1-Math.abs(direction.unit().dot(normal(p))))*this.reflection;
		}*/
		return this.reflection;
	}
    public double distanceFrom(Point p){
        return Math.abs(this.radius-p.minus(this.center).norm());
    }
    public Point directionOfCenter(Point p){
        if(p.minus(this.center).isZero()){return Point.unitX;}
        if(p.minus(this.center).norm()<this.radius){return p.minus(this.center).unit();}
        return this.center.minus(p).unit();
    }
    public double viewAngle(Point p){
        if(p.minus(this.center).norm()<this.radius){return 2*Math.PI;}
        else if(p.minus(this.center).norm()==this.radius){return Math.PI;}else{
            return 2*Math.asin(this.radius/p.minus(this.center).norm());
        }
    }
	public Point[] intersect(Line line){
		Point l=line.baseVector().unit();
		Point o=line.p1();
		Point c=this.center;
		Point o_c=o.minus(c);
		double det=Math.pow(l.dot(o_c), 2)-Math.pow(l.norm(), 2)*(Math.pow(o_c.norm(), 2)-Math.pow(this.radius, 2));
		if(det<0){return new Point[]{};}
		else if(det==0){
			double d=-l.dot(o_c);
			return new Point[]{o.plus(l.scale(d))};
		}
		else if(det>0){
			double d1=-l.dot(o_c)+Math.sqrt(det);
			double d2=-l.dot(o_c)-Math.sqrt(det);
			return new Point[]{o.plus(l.scale(d1)), o.plus(l.scale(d2))};
		}
		return new Point[]{new Point(0,0,0)};
	}
}
class Plane extends Shape{
	public Point point;
	public Point normal;
	public Point xAxis;
	public Point yAxis;
	public Plane(Point point, Point normal, MyColor color){
		this.point=point;
		this.normal=normal.unit();
		this.color=color;
		this.reflection=0.1;
		this.diffusion=0.5;
		this.shininess=0.5;
		if(this.normal.cross(Point.unitX).norm()!=0){
			this.xAxis=this.point.plus(Point.unitX).projectOnPlane(this.point, this.normal).minus(this.point).unit();
		}
		else if(this.normal.cross(Point.unitY).norm()!=0){
			this.xAxis=this.point.plus(Point.unitY).projectOnPlane(this.point, this.normal).minus(this.point).unit();
		}
		else if(this.normal.cross(Point.unitZ).norm()!=0){
			this.xAxis=this.point.plus(Point.unitZ).projectOnPlane(this.point, this.normal).minus(this.point).unit();
		}
		this.yAxis=this.normal.cross(this.xAxis);
	}
	public Plane(Point point, Point normal){
		this(point, normal, new MyColor(0,0,0));
	}
	public Point refract(Point p, Point direction){
		return direction;
	}
	public boolean fromOutside(Point p, Point direction){
		return true;
	}
	public Plane rotate(Rotation r){
		this.normal=this.point.plus(this.normal);
		this.xAxis=this.point.plus(this.xAxis);
		this.yAxis=this.point.plus(this.yAxis);
		this.point=this.point.rotate(r);
		this.normal=this.normal.rotate(r).minus(this.point);
		this.xAxis=this.xAxis.rotate(r).minus(this.point);
		this.yAxis=this.yAxis.rotate(r).minus(this.point);
		return this;
	}
    public Plane translate(Point p){
        this.point=this.point.plus(p);
        return this;
    }
    public double distanceFrom(Point p){
        return this.normal.project(p.minus(this.point)).norm();
    }
    public Point directionOfCenter(Point p){
        return this.normal.project(this.point.minus(p)).unit();
    }
    public double viewAngle(Point p){
        return Math.PI;
    }
	public Point[] intersect(Line l){
		if(this.contains(l.p1())){return new Point[]{l.p1()};}
		if(l.baseVector().perpendicularTo(this.normal)){return new Point[]{};}
		double t=-this.normal.dot(l.p1().minus(this.point))/this.normal.dot(l.baseVector());
		return new Point[]{l.p1().plus(l.baseVector().scale(t))};
		
	}
	public double transparency(Point p, Point direction){
		return this.transparency;
	}
    public double roughness(Point p, Point direction){
        return this.roughness*Math.pow(Math.abs(direction.unit().dot(normal(p))), 1.7);
        //return this.roughness*Math.abs(p.minus(this.point).unit().dot(normal(p)));
    }
	public double diffusion(Point p, Point direction){
		return this.diffusion;
	}
	public double curvature(Point p, Point direction){
		return 0;
	}
	public double refractionIndex(Point p, Point direction){
		return this.refractionIndex;
	}
	public double reflection(Point p, Point direction){

		return this.reflection;
	}
	public double shininess(Point p, Point direction){
		return this.shininess*curvature(p, direction);
	}
    public double x(Point p){
    	Point v=p.minus(this.point);
    	double x=this.xAxis.project(v).norm();
    	if(v.dot(this.xAxis)<0){x=-x;}
        return x;
    }
    public double y(Point p){
    	Point v=p.minus(this.point);
    	double y=this.yAxis.project(v).norm();
    	if(v.dot(this.yAxis)<0){y=-y;}
        return y;
    }
	public boolean contains(Point p){
		return p.minus(this.point).perpendicularTo(this.normal);
	}
	public Point normal(Point p){
		return this.normal;
	}
}
abstract class ShapeSection<T extends Shape> extends Shape{
    public T s;
    public abstract boolean containsXY(double x, double y);
    public boolean contains(Point p){
        boolean b=s.contains(p);
        if(b){
            double x=x(p);
            double y=y(p);
            return containsXY(x,y);
        }
        return false;
    }
    public Point normal(Point p){return s.normal(p);}
    public Point[] intersect(Line l){
        Point[] ii=s.intersect(l);
        boolean[] contains=new boolean[ii.length];
        int howMany=0;
        for(int i=0;i<ii.length;i++){
            contains[i]=contains(ii[i]);
            if(contains[i]){howMany++;}
        }
        Point[] left=new Point[howMany];
        int count=0;
        for(int i=0;i<ii.length;i++){
            if(contains[i]){
                left[count++]=ii[i];
            }
        }
        return left;
    }
    public MyColor color(Point p){
    	return s.color(p);
    	}
    public double x(Point p){return s.x(p);}
    public double y(Point p){return s.y(p);}
    public double diffusion(Point p, Point direction){return s.diffusion(p, direction);}
    public double reflection(Point p, Point direction){return s.reflection(p, direction);}
    public double refractionIndex(Point p, Point direction){return s.refractionIndex(p, direction);}
    public double curvature(Point p, Point direction){return s.curvature(p, direction);}
    public double shininess(Point p, Point direction){return s.shininess(p, direction);}
    public double roughness(Point p, Point direction){return s.roughness(p, direction);}
    public double transparency(Point p, Point direction){return s.transparency(p, direction);}
    public Shape rotate(Rotation r){return s.rotate(r);}
    public Shape translate(Point p){return s.translate(p);}
    public double distanceFrom(Point p){return s.distanceFrom(p);}
    public Point directionOfCenter(Point p){return s.directionOfCenter(p);}
    public double viewAngle(Point p){return s.viewAngle(p);}
    public Shape setReflection(double r){
		s.setReflection(r);
		return this;
	}
    public Shape setRoughness(double r){
        s.setRoughness(r);
        return this;
    }
	public Shape setTransparency(double r){
		s.setTransparency(r);
		return this;
	}
	public Shape setRefractionIndex(double r){
		s.setRefractionIndex(r);
		return this;
	}
	public Shape setShininess(double d){
		s.setShininess(d);
		return this;
	}
	public Shape setDiffusion(double d){
		s.setDiffusion(d);
		return this;
	}
	public Shape add(ColorPattern pattern){
        s.add(pattern);
        return this;
    }
}
class RectangleSection extends ShapeSection<Plane>{
    public double x1, y1, x2, y2;
    public RectangleSection(Plane p,  double x1, double y1, double x2, double y2){
        this.s=p;
        this.x1=Math.min(x1, x2);
        this.y1=Math.min(y1, y2);
        this.x2=Math.max(x1, x2);
        this.y2=Math.max(y1, y2);;
    }
    public boolean fromOutside(Point p, Point direction){
        return true;
    }
    public boolean containsXY(double x, double y){
        return x>=x1&&x<=x2&&y>=y1&&y<=y2;
    }
}


