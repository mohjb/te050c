package te050c.tl.db.tbl;

import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import te050c.tl.TL;
import te050c.tl.db.DB;
import te050c.tl.db.ItTbl;
import te050c.tl.Util;
import te050c.tl.Form;

public class Json extends Tbl{

	@Override public List creationDBTIndices(TL tl){
		return Util.lst(
				Util.lst("int(18) NOT NULL primary key auto_increment"//jsonRef
						,"int(18) not null"//parent
						,"int(18) not null"//proto
						,"text not null"//meta
				)
				,Util.lst(Util.lst(Util.lst(C.parent)
						,Util.lst(C.proto)
				))
		);/*
Create table `json`( #jsonHead
`jsonRef` int(18) primary key auto_increment,
`parent` int(18) not null,
`proto` int(18) not null,
`meta` text, -- json
Key (`parent`),
Key (`proto`),
);

Create table `Json`(
`no` int(24) primary key auto_increment,
`jsonRef` int(18) not null,
`path` text not null,
`typ` enum( 'Int' , 'dbl' , 'str' , 'bool' , 'dt' , 'jsonRef', ‘javaObjectDataStream’	) not null, --	, ‘file’ ,'function','codeJson','javaMap'
`json` blob ,
Unique ( `jsonRef` , `path`(64) ) ,
key(`typ`,`json`(64)) ,
key( `path`(64) , `typ`,`json`(64) )
);

Create table `log`(
`no` int(27) primary key auto_increment ,
`dt` timestamp not null ,
`proto` int(18) ,
`jsonRef`int(18) ,
`path` text ,
`json` blob ,
`act` enum(‘insert’ ,’update’ ,’delete’ ,’login’ ,’logout’) ,
Key (`proto`,`path`(64),`json`(64)),
key(`dt`),
key(`jsonRef`,`path`(64),`act`)
);
*/}

public static Map root;

	@F public Integer jsonRef,parent,proto;
	@F public Object json;

	public Map props;

	//public Map mv(){return json instanceof Map?(Map)json:asMap();}

	public enum C implements CI{jsonRef,parent,proto,meta;
		@Override public Class<? extends Tbl>cls(){return Json.class;}
		@Override public Class<? extends Form>clss(){return cls();}
		@Override public String text(){return name();}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public Tbl tbl(){return Tbl.tbl(cls());}
		@Override public void save(){tbl().save(this);}
		@Override public Object load(){return tbl().load(this);}
		@Override public Object value(){return val(tbl());}
		@Override public Object value(Object v){return val(tbl(),v);}
		@Override public Object val(Form f){return f.v(this);}
		@Override public Object val(Form f,Object v){return f.v(this,v);}
	}//C

	@Override public CI pkc(){return C.jsonRef;}
	@Override public Object pkv(){return jsonRef;}
	@Override public CI[]columns(){return C.values();}
	public static final String dbtName="json",Jr=JsonProp.Typ.jsonRef.toString();

	@Override public String getName(){return dbtName;}//public	Ssn(){super(Name);}

	static Object get(Object o,String[]p,int i){
		if(o instanceof Map){Map m=(Map)o;
			String n=p[i];
			o=m.get(n);
			if(i<p.length)
				o=get(o,p,i+1);
		}else if(o instanceof Object[])
		{int j=Util.parseInt(p[i], -1);
			if(j!=-1){
				Object[]a=(Object[])o;
				o=a[j];
				if(i<p.length)
					o=get(o,p,i+1);
			}
		}else if(o instanceof List)//Collection
		{int j=Util.parseInt(p[i], -1);
			if(j!=-1){
				List a=(List)o;
				o=a.get(j);
				if(i<p.length)
					o=get(o,p,i+1);
			}
		}
		return o;
	}//get

	/**load from dbt json all properties, param ref should have a prop "jsonRef"*/
	static Map LoadRef( Map ref ){
		return JsonProp.LoadRef(ref);} // LoadRef


	public static Map LoadRef( Object jsonRef ){
		return JsonProp.LoadRef(jsonRef);} // LoadRef

	/**return from the db tbl the max value of jsonRef plus 1 ,javadoc-draft2016.08.17.11.10*/
	static int jrMaxPlus1() throws SQLException{
		int no= DB.q1int("select max(`"+C.jsonRef+"`)+1 from `"+dbtName+"`", 1);
		TL.tl().log("TL.DB.Tbl.Json.jrMaxPlus1=",no);
		return no;}

	/**same as jrMaxPlus1 but the method signature doesnt throw anything ,javadoc-draft2016.08.17.11.10*/
	public static int jrmp1(){try {
		return jrMaxPlus1();}catch (SQLException ex){}
		return 1;}

	/**get from the arg map the property jsonRef as an int, if not found return -1 ,javadoc-draft2016.08.17.11.10*/
	static int jr(Object m){
		Object o=m instanceof Map?((Map)m).get(Jr):null;
		return o==null?-1:((Number)o).intValue();}

	/**same as js but instead of -1 , returns jrmp1 ,javadoc-draft2016.08.17.11.10*/
	public static int jrn(Object m){
		Object o=m instanceof Map?((Map)m).get(Jr):null;
		return o==null?jrmp1():((Number)o).intValue();}

	/**create an instance and set in the db tbl the name-value pairs, uses maPCreate ,javadoc-draft2016.08.17.11.10*/
	static Json create(Object...p) throws SQLException{
		Map m=maPCreate(p==null||p.length<1?null:Util.maPSet(new HashMap(), p));
		Json j=new Json();j.json=m;
		j.jsonRef=jr(m);
		return j;}

	/**convenience method with the variable length-arguments, uses maPCreate ,javadoc-draft2016.08.17.11.10*/
	static Map mapCreate(Object...p) throws SQLException{
		return maPCreate(p==null||p.length<1?null:Util.maPSet(new HashMap(), p));}

	/**create an instance and set in the db tbl the name-value pairs, if there is no prop jsonRef then uses jrMaxPlaus1 , uses Tbl.save ,javadoc-draft2016.08.17.11.10*/
	static Map maPCreate(Map m) throws SQLException{
		if(m==null)m=new HashMap();
		Object o= m.get(Jr) ;
		if(o ==null){
			int jr=jrMaxPlus1();
			m.put(Jr, jr);
		}save(m);return m;}

	/**return a list of all(objects :jsonRef) that have a ref to this jsonRef with the param- prop path*/
	Integer[]allFK(TL t,String prop){
		List l=null;try {l=DB.q1colList(
				"select `jsonRef` from `Json` where `json`=? and `typ`=? and `path`=?"
				, jsonRef , JsonProp.Typ.jsonRef , prop );
		} catch (SQLException ex) {t.error(ex,"allFK");}
		int n=l==null?0:l.size();
		Integer[]a=new Integer[n];
		for(int i=0;i<n;i++)
			a[i]=((Number)l.get(i)).intValue();
		return a;}

	static Json load(int jr){
		Json j=new Json();
		j.json=JsonProp.loadProps(j.jsonRef=jr,null);
		return j;}

	/** as can be clearly read from the source-code of the method body
	 , overwrites the super-class method.
	 according to the run-time type of what is stored in the member-variable(mmbrVrbl) `json`,
	 if mmbrVrbl-json is a Map then call(delegate the work) saveProps(Map) ,
	 if mmbrVrbl-json is a List then call(delegate the work) saveProps(List),
	 otherwise call(delegate the work) saveProp
	,javadoc-draft2016.08.17.11.10*/
	@Override public Tbl save() throws Exception{
		if(json instanceof Map)
			saveProps(TL.tl(),(Map)json);
		else {
			JsonProp j=new JsonProp();
			if(json instanceof List)
			j.savePropList(TL.tl(),"",(List)json,j.prps());
		else
			j.saveProp(TL.tl(),"",JsonProp.Typ.typ(json),json,j.prps());}
		return this;}

	void saveProps(TL tl,Map p){
		if(jsonRef==null)
			jsonRef=jrn(p);JsonProp J=new JsonProp();
		Map<String,JsonProp>props=J.prps();
		try{for(Object k:p.keySet()){
			Object v=p.get(k);
			JsonProp.Typ typ=JsonProp.Typ.typ(v);
			if( v instanceof Map )
			{	int j=jr(v);if(j==-1)
				J.savePropMap(tl,k+".",(Map)v,props);
			else J.saveProp(tl,k.toString(),JsonProp.Typ.jsonRef,j,props);
			}
			else if(v instanceof List)
				J.savePropList(tl,k+".",(List)v,props);
			else J.saveProp(tl,k.toString(),typ,v,props);
		}//for
		}catch(Exception ex){}}//sav




	public static Map save(Map p){
		//Object o=p.get(Jr);int i=o instanceof Number?((Number)o).intValue():jrmp1();
		save(p,jrn(p));
		return p;
	}

	static Map save(Map p,int jr){
		Json j=new Json();
		j.jsonRef=jr;j.json=p;
		try{j.save();}catch(Exception ex){TL.tl().
				error(ex, "TL.DB.Tbl.Json.save:map,jr");}
		return p;}

	//public abstract class Props extends Tbl {}

}// class TL$DB$Json
