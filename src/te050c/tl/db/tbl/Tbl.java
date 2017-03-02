package te050c.tl.db.tbl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import te050c.tl.TL;
import te050c.tl.Util;
//import te050c.tl.Form.FI;
import te050c.tl.db.DB;

public abstract class Tbl extends te050c.tl.Form{

		static final String StrSsnTbls="TL.DB.Tbl.tbls";
		//public Map<Class<? extends DB.Tbl.Sql>,DB.Tbl.Sql>tbls;
		public static Tbl tbl(Class<? extends Tbl>p){
			TL t=TL.tl();Object o=t.s(StrSsnTbls);
			Map<Class<? extends Tbl>,Tbl>tbls=o instanceof Map?(Map)o:null;
			if(tbls==null)t.s(StrSsnTbls,tbls=new HashMap<Class<? extends Tbl>,Tbl>());
			Tbl r=tbls.get(p);if(r==null)try {tbls.put(p, r=p.newInstance());}
			//catch (InstantiationException ex) {}catch(IllegalAccessException ex){}
			catch(Exception ex){t.error(ex,"TL.DB.Tbl.tbl(Class<TL.DB.Tbl>",p,"):Exception:");}
			return r;}


/**Sql-Column Interface, for enum -items that represent columns in sql-tables
 * the purpose of creating this interface is to centerlize
 * the definition of the names of columns in java source code*/
public interface CI extends FI{
	//**per column, get the primary key column*/public CI pkc();
	//**per column, get the primary key value */public Object pkv();
	/**per column, load from the sql/db table
	 * the value of this column, store the value
	 * in the F field and return the value*/Object load();
	/**per column, save into the db-table
	 * the value from the member field */public void save();
	//public StringBuilder where(StringBuilder b);
	//void save(<T>newVal);
	//String tblNm();
	public Class<? extends Tbl>cls();
	public Tbl tbl();
}//interface CI


		public static CI[]cols(CI...p){return p;}
		public static CI[]orderBy(CI...p){return p;}//static Col[]groupBy(Col...p){return p;}
		public static Object[]where(Object...p){return p;}

		public abstract CI pkc();
		public abstract Object pkv();
		public abstract CI[]columns();

		@Override public FI[]flds(){return columns();}

		/** returns a list of 3 lists,
		 * the 1st is a list for the db-table columns-CI
		 * the 2nd is a list for the db-table-key-indices
		 * the 3rd is a list for row insertion
		 *
		 * the 1st list, the definition of the column is a string
		 * , e.i. varchar(255) not null
		 * or e.i. int(18) primary key auto_increment not null
		 * the 2nd list of the db-table key-indices(optional)
		 * each dbt-key-index can be a CI or a list , if a list
		 * each item has to be a List
		 * ,can start with a prefix, e.i. unique , or key`ix1`
		 * , the items of this list should be a CI
		 * ,	or the item can be a list that has as the 1st item the CI
		 * and the 2nd item the length of the index
		 * the third list is optional, for each item in this list
		 * is a list of values to be inserted into the created table
		 */
		public abstract List<?> creationDBTIndices(TL tl);

		public void checkDBTCreation(TL tl){
			String dtn=getName();Object o=null;
			try {o=DB.q("desc "+dtn);} catch (SQLException ex) {
				tl.error(ex, "TL.DB.Tbl.checkTableCreation:",dtn);}
			try{if(o==null){
				StringBuilder sql=
						new StringBuilder("CREATE TABLE `")
								.append(dtn).append("` (\n");
				CI[]ci=columns();int an,x=0;
				List<?> a=creationDBTIndices(tl),b=(List<?>)a.get(0);

				for(CI i:ci){
					if(x>0 )
						sql.append("\n,");
					sql.append('`').append(i).append('`')
							.append(String.valueOf(b.get(x)) );
					x++;}
				an=a.size();b=an>1?(List<?>)a.get(1):b;
				if(an>1)for(Object bo:b)
				{sql.append("\n,");x=0;
					if(bo instanceof CI)
						sql.append("KEY(`").append(bo).append("`)");
					else if(bo instanceof List)
					{	List<?> bl=(List<?>)bo;x=0;boolean keyHeadFromList=false;
						for(Object c:bl){
							boolean s=c instanceof String;
							if(x<1 && !s&& !keyHeadFromList)
								sql.append("KEY(");
							if(x>0)
								sql.append(',');//in the list
							if(s){sql.append((String)c);if(x==0){x--;keyHeadFromList=true;}}
							else {List<?> l=c instanceof List?(List<?>)c:null;
								sql.append('`').append(
									l==null?c.toString()
									:String.valueOf(l.get(0))
								).append("`");
								if(l!=null&&l.size()>1)
									sql.append('(').append(l.get(1)).append(')');
							}x++;
						}sql.append(")");
					}else
						sql.append(bo);
				}
				/*"KEY (`"+C.p+"`,`"+C.b+"`,`"+C.f+"`),\n" +
					"KEY (`"+C.jsonRef+"`)\n" +
					"KEY (`"+C.dt+"`)\n" +*/
				sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 ;");
				int r=DB.x(sql.toString());
				tl.log("TL.DB.Tbl.checkTableCreation:",dtn,r,sql);
				b=an>2?(List<?>)a.get(2):b;if(an>2)
					for(Object bo:b){
						List<?> c=(List<?>)bo;
						Object[]p=new Object[c.size()];
						c.toArray(p);
						vals(p);
						try {save();} catch (Exception ex) {
							tl.error(ex, "TL.DB.Tbl.checkTableCreation:insertion",c);}
					}
			}
			} catch (SQLException ex) {
				tl.error(ex, "TL.DB.Tbl.checkTableCreation:",dtn);}
		}//checkTableCreation

		/**where[]={col-name , param}*/
		public int count(Object[]where) throws Exception{
			StringBuilder sql=new StringBuilder(
					"select count(*) from `")
					.append(getName())
					.append("` where `")
					.append(where[0])
					.append("`=").append(Cols.M.m(where[0]).txt);//where[0]instanceof CI?m((CI)where[0]):'?');
			return DB.q1int(sql.toString(),-1,where[0],where[1]);}

		/**where[]={col-name , param}*/public
		int maxPlus1(CI col) throws Exception{
			StringBuilder sql=new StringBuilder(
					"select max(`"+col+"`)+1 from `")
					.append(getName()).append("`");
			return DB.q1int(sql.toString(),1);}

		/**returns one object from the db-query*/
		public Object obj(CI col,Object[]where) throws Exception{
			StringBuilder sql=new StringBuilder("select `")
					.append(col).append("` from `")
					.append(getName()).append('`');
			Cols.where(sql, where);
			return DB.q1Obj(sql.toString(),where);}

		/**returns one string*/
		public String select(CI col,Object[]where) throws Exception{
			StringBuilder sql=new StringBuilder("select `")
					.append(col).append("` from `")
					.append(getName()).append('`');
			Cols.where(sql, where);
			return DB.q1Str(sql.toString(),where);}

		/**returns one column, where:array of two elements:1st is column param, 2nd value of param*/
		Object[]column(CI col,Object...where) throws Exception{
			return DB.q1col("select `"+col+"` from `"+getName()
							+"` where `"+where[0]+"`="
							+Cols.M.m(where[0]).txt//(where[0]instanceof CI?m((CI)where[0]):Cols.M.prm)
					,where[0],where[1]);}//at

		/**returns a table*/
		public Object[][]select(CI[]col,Object[]where)throws Exception{
			StringBuilder sql=new StringBuilder("select ");
			Cols.generate(sql,col);
			sql.append(" from `").append(getName()).append('`');
			Cols.where(sql,where);
			return DB.Q(sql.toString(), where);}

		/**loads one row from the table*/
		Tbl load(ResultSet rs)throws Exception{return load(rs,fields());}

		/**loads one row from the table*/
		Tbl load(ResultSet rs,Field[]a)throws Exception{
			int c=0;for(Field f:a)v(f,rs.getObject(++c));
			return this;}

		/**loads one row from the table*/
		public Tbl load(Object pk){
			ResultSet r=null;TL t=TL.tl();
			try{r=DB.r("select * from `"+getName()+"` where `"+pkc()+"`="+Cols.M.prm.txt,pk);
				if(r.next())load(r);
				else{t.error(null,"TL.DB.Tbl(",this,").load(pk=",pk,"):resultset.next=false");nullify();}}
			catch(Exception x){t.error(x,"TL.DB.Tbl(",this,"):",pk);}
			finally{DB.closeRS(r);}
			return this;}

		public Tbl nullify(){return nullify(fields());}
		public Tbl nullify(Field[]a){for(Field f:a)v(f,null);return this;}

		/**loads one row from the table*/
		Tbl load(){return load(pkv());}

		/**loads one object from column CI c ,from one row of primary-key value pkv ,from the table*/
		Object load(CI c){Object pkv=pkv();
			Object o=null;try{o=DB.q1obj("select `"+c+"` from `"
					+getName()+"` where `"+pkc()+"`="+Cols.M.m(c).txt,pkv);
				v(c,o);}
			catch(Exception x){TL.tl().error(x,"TL.DB.Tbl(",this,").load(CI ",c,"):",pkv);}
			return o;}//load

		public Tbl save(CI c){// throws Exception
			CI pkc=pkc();
			Object cv=v(c),pkv=pkv();TL t=TL.tl();
			if(cv instanceof Map)try
			{String j=t.jo().clrSW().o(cv).toString();cv=j;}
			catch (IOException e) {t.error(e,"TL.DB.Tbl.save(CI:",c,"):");}
			try{DB.x("replace into `"+getName()+"` (`"+pkc+
					"`,`"+c+"`) values("+Cols.M.m(pkc).txt
					+","+Cols.M.m(c).txt+")",pkv,cv);
				Integer k=(Integer)pkv;
				Log.log(
						Log.Entity.valueOf(getName())
						, k, Log.Act.Update
						, Util.mapCreate(c,v(c)) );
			}catch(Exception x){TL.tl().error(x
					,"TL.DB.Tbl(",this,").save(",c,"):pkv=",pkv);}
			return this;}//save

		/**store this entity in the dbt , if pkv is null , this method uses the max+1 */
		public Tbl save() throws Exception{
			Object pkv=pkv();CI pkc=pkc();boolean nw=pkv==null;//Map old=asMap();
			if(nw){
				int x=DB.q1int("select max(`"
						+pkc+"`)+1 from `"+getName()+"`",1);
				v(pkc,pkv=x);
				TL.tl().log("TL.DB.Tbl(",toJson(),").save-new:max(",pkc,") + 1:",x);
			}CI[]cols=columns();
			StringBuilder sql=new StringBuilder("replace into`")
					.append(getName()).append("`( ");
			Cols.generate(sql, cols).toString();
			sql.append(")values(").append(Cols.M.m(cols[0]).txt);//Cols.M.prm);
			for(int i=1;i<cols.length;i++)
				sql.append(",").append(Cols.M.m(cols[i]).txt);
			sql.append(")");//int x=
			DB.X( sql.toString(), vals() ); //TODO: investigate vals() for json columns
			log(nw?Log.Act.New:Log.Act.Update);
			return this;}//save

		public Tbl readReq_save() throws Exception{
			Map<?, ?> old=asMap();
			readReq("");
			Map<?, ?> val=asMap();
			for(CI c:columns()){String n=c.f().getName();
				Object o=old.get(n),v=val.get(n);
				if(o==v ||(o!=null && o.equals(v)))
				{val.remove(n);old.remove(n);}
				else save(c);}
			//log(TL.DB.Tbl.Log.Act.Update,old);
			return this;
		}//readReq_save

		@Override public Object[] vals() {
			Object[]r=super.vals();
			for(int i=0;i<r.length;i++)
				if(r[i] instanceof Map)
				{TL t=TL.tl();try {
					r[i]=t.jo().clrSW().o(r[i]).toStrin_();
				} catch (IOException e) {e.printStackTrace();}}
			return r;
		}

		void log(Log.Act act)//,Map old)
		{	Map<?, ?> val=asMap();
			Integer k=(Integer)pkv();
			Log.log(Log.Entity.valueOf(getName())
					, k, act, val);}

		public boolean delete() throws SQLException{
			Object pkv=pkv();
			if(pkv==null)return false;
			//Map old=asMap();int x=
			DB.x("delete from `"+getName()+"` where `"+pkc()+"`=?", pkv);
			log(Log.Act.Delete);
			return true;}

		Object prevVal(CI c){
			TL t=TL.tl();ResultSet r=null;
			boolean isMap=Map.class.isAssignableFrom(c.f().getType());
			try {r=DB.r(
					"select `val` from `log` where `entity`=? and `pk`=? order by `dt`"
					,getName(), pkv());
				while(r.next()){
					Object o=isMap?r.getString(1):r.getObject(1);
					if(!isMap)return o;
					else{Map<?, ?> m=null;
						try{m=(Map<?, ?>)te050c.tl.json.Parser.parse((String)o);}
						catch(Exception e){}
						String n= c.f().getName();
						o=m!=null ? m.get(n):null;
						if(o!=null)
							return o;
					}
				}
			}catch (SQLException e){t.error(e
					,"TL.DB.Tbl.prevVal(",c,"):");}
			finally{if(r!=null)DB.closeRS(r);}
			return null;}

		/**retrieve from the db table all the rows that match
		 * the conditions in < where > , create an iterator
		 * , e.g.<code>for(Tbl row:query(
		 * 		Tbl.where( CI , < val > ) ))</code>*/
		public Itrtr query(Object[]where){
			Itrtr r=new Itrtr(where);
			return r;}

		public class Itrtr implements Iterator<Tbl>,Iterable<Tbl>{
			public ResultSet rs=null;public int i=0;Field[]a;
			public Itrtr(Object[]where){a=fields();
				StringBuilder sql=new StringBuilder("select * from `"+getName()+"`");
				if(where!=null&&where.length>0)
					Cols.where(sql, where);
				try{rs=DB.R(sql.toString(), where);}
				catch(Exception x){TL.tl().error(x,"TL.DB.Tbl(",this,").Itrtr.<init>:where=",where);}}

			@Override public Iterator<Tbl>iterator(){return this;}

			@Override public boolean hasNext(){boolean b=false;
				try {b = rs!=null&&rs.next();} catch (SQLException x)
				{TL.tl().error(x,"TL.DB.Tbl(",this,").Itrtr.hasNext:i=",i,",rs=",rs);}
				if(!b&&rs!=null){DB.closeRS(rs);rs=null;}
				return b;}

			@Override public Tbl next(){i++;/*
	try {int c=0;for(Field f:fields())try{v(f,rs.getObject(++c));}catch(Exception x)
	{TL.error("DB.Tbl.Sql("+this+").I2.next:i="+i+",c="+c+",rs="+rs,x);}}catch(Exception x)
	{TL.error("DB.Tbl.Sql("+this+").I2.next:i="+i+":"+rs, x);rs=null;}*/
				try{load(rs,a);}catch(Exception x){TL.tl().error(x,"TL.DB.Tbl("
						,this,").Itrtr.next:i=",i,":",rs);rs=null;}
				return Tbl.this;}

			@Override public void remove(){throw new UnsupportedOperationException();}

		}//Itrtr

		/**output to jspOut one row of json of this row*/
		public void outputJson(){try{TL.tl().getOut().o(this);}catch(IOException x){TL.tl().error(x,"te050c.TL.DB.Tbl.outputJson:IOEx:");}}

		/**output to jspOut rows of json that meet the 'where' conditions*/
		public void outputJson(Object...where){try{
			te050c.tl.json.Output o=TL.tl().getOut();
			o.w('[');boolean comma=false;
			for(Tbl i:query(where)){
				if(comma)o.w(',');else comma=true;
				i.outputJson();}
			o.w(']');
		} catch (IOException e){TL.tl().error(e,"TL.DB.Tbl.outputJson:");}
		}//outputJson(Object...where)

}//class Tbl