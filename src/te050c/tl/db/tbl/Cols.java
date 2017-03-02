package te050c.tl.db.tbl;

import java.lang.reflect.Field;

import te050c.tl.TL;
import te050c.tl.Form;
import te050c.tl.db.tbl.Tbl.CI;

/**Class for Utility methods on set-of-columns, opposed to operations on a single column*/
public class Cols {//Marker ,sql-preparedStatement-parameter

	public enum M implements CI{
		uuid("uuid()")
		,now("now()")
		,count("count(*)")
		,all("*")
		,prm("?")
		,password("password(?)")
		,Null("null")
		;String txt;
		private M(String p){txt=p;}
		public String text(){return txt;}
		public Class<? extends Tbl>cls(){return Tbl.class;}
		public Class<? extends Form>clss(){return cls();}
		public Tbl tbl(){return null;}
		public Field f(){return null;}
		public Object value(){return null;}
		public Object value(Object p){return null;}
		public Object val(Form f){return null;}
		public Object val(Form f,Object p){return null;}
		public Object load(){return null;}
		public void save(){}
		public static M m(Object p){return p instanceof CI?m((CI)p):p instanceof Field?m((Field)p):prm;}
		public static M m(CI p){return m(p.f());}
		public static M m(Field p){
			return p.getAnnotation(Form.F.class).prmPw()?password:prm;}

	}//enum M

	//public static StringBuilder where(StringBuilder b,Field f){M m=m(f);b.append("`").append(f.getName()).append("`=").append(m);return b;}

	public static Field f(String name,Class<? extends Tbl>c){
		//for(Field f:fields(c))if(name.equals(f.getName()))return f;return null;
		Field r=null;try{r=c.getField(name);}catch(Exception x)
		{TL.tl().error(x,"TL.DB.Tbl.f(",name,c,"):");}
		return r;}

	/**generate Sql into the StringBuilder*/
	public static StringBuilder generate(StringBuilder b,CI[]col){
		return generate(b,col,",");}

	static StringBuilder generate(
			StringBuilder b,CI[]col,String separator){
		if(separator==null)separator=",";
		for(int n=col.length,i=0;i<n;i++){
			if(i>0)b.append(separator);
			if(col[i] instanceof Cols.M)
				//b.append(((Col)col[i]).name);
				b.append(col[i]);
				//else if(col[i] instanceof Tbl)b.append('\'').append(col[i]).append('\'');
				//else if(col[i] instanceof String)b.append(col[i]);
			else b.append("`").append(col[i]).append("`");}
		return b;}

	static StringBuilder where(
			StringBuilder b,Object[]where){b.append(" where ");
		for(int n=where.length,i=0;i<n;i++){Object o=where[i];
			if(i>0)b.append(" and ");
			if(o instanceof Cols.M)b.append(o);else
			if(o instanceof CI)//((CI)where[i]).where(b);
				b.append('`').append(o).append("`=")
						.append(Cols.M.m(o).txt);
			else TL.tl().error(null,"TL.DB.Tbl.Col.where:for:",o);
			i++;
		}//for
		return b;}

}//class Cols
