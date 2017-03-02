package te050c.tl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Form {
	@Override public String toString(){return toJson();}
	public abstract String getName();
	public String toJson(){te050c.tl.json.Output o= TL.tl().jo().clrSW();
		try {o.oForm(this, "", "");}
		catch (IOException ex) {}return o.toString();}

	//public Writer toJson(Writer w){Json.Output o= new Json.Output(w);try {o.oForm(this, "", "").toString();}catch (IOException ex) {}return w;}

	public String[]prmsReq(String prefix){return prmsReq(prefix,fields());}

	public static String[]prmsReq (String prefix,Field[]a){
		String[]r=new String[a.length];int i=-1;TL t=TL.tl();
		for(Object e:a)r[++i]=t.req(prefix+e);
		return r;}

	public static Object parse(String p,Class<?> c){
		TL t=TL.tl();if(c.isEnum()){
			for(Object i:c.getEnumConstants())
				if(i!=null&&i.toString().equals(p))
					return i;}else
		if(c.isAssignableFrom(Float.class))return Float.parseFloat(p);else
		if(c.isAssignableFrom(Double.class))return Double.parseDouble(p);else
		if(c.isAssignableFrom(Integer.class))return Integer.parseInt(p);else
		if(c.isAssignableFrom(URL.class))try {return new URL("file:" +t.getServletContext().getContextPath()+'/'+p);}
		catch (Exception ex) {t.error(ex,"TL.Form.parse:URL:p=",p," ,c=",c);}else
		if(c.isAssignableFrom(String.class))return p;else
		if(c.isAssignableFrom(Date.class))try {return Util.parseDate(p);}//Util.dateFormat.parse(p);}
		catch (Exception ex) {t.error(ex,"TL.Form.parse:Date:p=",p," ,c=",c);}
		else//if(c.isAssignableFrom(Map.class))
			try{return te050c.tl.json.Parser.parse(p);}
			catch (Exception ex) {t.error(ex,"TL.Form.parse:Json.Parser.parse:p=",p," ,c=",c);}
		return null;}

	public Form readReq(String prefix){
		TL t=TL.tl();FI[]a=flds();for(FI f:a){
			String s=t.req(prefix+f);
			Class <?>c=s==null?null:f.f().getType();
			Object v=null;try {
				if(s!=null)v=parse(s,c);
				v(f,v);//f.set(this, v);
			}catch (Exception ex) {// IllegalArgumentException,IllegalAccessException
				t.error(ex,"TL.Form.readReq:t=",this," ,field="
						,f+" ,c=",c," ,s=",s," ,v=",v);}}
		return this;}

	public abstract FI[]flds();

	public Object[]vals(){
		Field[]a=fields();
		Object[]r=new Object[a.length];
		int i=-1;
		for(Field f:a){i++;
			r[i]=v(a[i]);
		}return r;}

	public Form vals (Object[]p){
		Field[]a=fields();int i=-1;
		for(Field f:a)
			v(f,p[++i]);
		return this;}

	public Map asMap(){
		return asMap(null);}

	public Map asMap(Map r){
		Field[]a=fields();
		if(r==null)r=new HashMap();
		int i=-1;
		for(Field f:a){i++;
			r.put(f.getName(),v(a[i]));
		}return r;}

	public Form fromMap (Map p){
		Field[]a=fields();
		for(Field f:a)
			v(f,p.get(f.getName()));
		return this;}

	public Field[]fields(){return fields(getClass());}

	public static Field[]fields(Class<?> c){//this is beautiful(tear running down cheek)
		Field[]a=c.getDeclaredFields();
		List<Field>l=new LinkedList<Field>();
		for(Field f:a){F i=f.getAnnotation(F.class);//getDeclaredAnnotation
			if(i!=null)l.add(f);}
		//if(this instanceof Sql){};//<enum>.values() =:= c.getEnumConstants()
		Field[]r=new Field[l.size()];
		l.toArray(r);
		return r;}

	public Form v(FI p,Object v){return v(p.f(),v);}//this is beautiful(tear running down cheek)
	public Object v(FI p){return v(p.f());}//this is beautiful(tear running down cheek)

	public Form v(Field p,Object v){//this is beautiful(tear running down cheek)
		try{Class <?>t=p.getType();
			//boolean b=v!=null&&p.isEnumConstant();
			if(v!=null && !t.isAssignableFrom( v.getClass() ))//t.isEnum()||t.isAssignableFrom(URL.class))
				v=parse(v instanceof String?(String)v:String.valueOf(v),t);
			p.set(this,v);
		}catch (Exception ex) {TL.tl().error(ex,"TL.Form.v(",this,",",p,",",v,")");}
		return this;}

	public Object v(Field p){//this is beautiful(tear running down cheek)
		try{return p.get(this);}
		catch (Exception ex) {//IllegalArgumentException,IllegalAccessException
			TL.tl().error(ex,"TL.Form.v(",this,",",p,")");return null;}}


	/**Field annotation to designate a java member for use in a Html-Form-field/parameter*/
	@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	public @interface F{	boolean prmPw() default false;boolean json() default false; }


	/**Interface for enum-items from different forms and sql-tables ,
	 * the enum items represent a reference Column Fields for identifing the column and selection.*/
	public interface FI{//<T>
		public String text();
		//public String tblNm();
		public Class<? extends Form>clss();
		public Field f();
		public Object value();//<T>
		public Object value(Object p);//<T>
		public Object val(Form f);
		public Object val(Form f,Object p);
	}//interface I

/*public static Form form(String varName){
Object o=TL.s(varName);
if(o==null){Form r=new Form(varName);return r;}
 return null;}

public static Form form(Class<? extends DB.Tbl.Form>p){
	final String T="DB.Tbl.Sql.forms";Object o=TL.s(T);
	Map<Class<? extends DB.Tbl.Sql>,DB.Tbl.Sql>tbls=o instanceof Map?(Map)o:null;
	if(tbls==null)TL.s(T,tbls=new HashMap<Class<? extends DB.Tbl.Sql>,DB.Tbl.Sql>());
	DB.Tbl.Sql r=tbls.get(p);if(r==null)try {tbls.put(p, r=p.newInstance());}
	//catch (InstantiationException ex) {}catch(IllegalAccessException ex){}
	catch(Exception ex){TL.error("TL.form(Class<DB.Tbl.Sql>"+p+"):Exception:", ex);}
	return r;}*/

}//public abstract static class Form