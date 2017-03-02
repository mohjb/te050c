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

public class JsonProp extends Tbl{//Json.Props
	enum Typ{Int,dbl,str,bool,dt,jsonRef,javaObjectDataStream,clntFunc,srvrFunc;//,file,function,codeJson

		Object load(ResultSet rs){
			final int col=3;try{switch(this){
				case Int:return rs.getInt(col);
				case dbl:return rs.getDouble(col);
				case str:case clntFunc:case srvrFunc:return rs.getString(col);
				case bool:{byte b=rs.getByte(col);return b==0?false:b==1?true:null;}
				case dt:return rs.getDate(col);
				case jsonRef:Long ref=rs.getLong(col);return Util.mapCreate(Jr,ref);
				case javaObjectDataStream:java.sql.Blob b=rs.getBlob(col);
					java.io.ObjectInputStream s=new ObjectInputStream( b.getBinaryStream());
					Object o=s.readObject();s.close();
					return o;
			}}catch(Exception ex){TL.tl().error(ex, "TL.DB.JsonProp.Typ.get");}
			return null;
		}//get(ResultSet)
		/*
		   int save(PreparedStatement ps,String p,Object v) throws Exception{
			   int x=-1;Typ t=this;try{
			   ps.setString(2, p);
			   ps.setString(3, t.toString());
			   switch(t){
			   case javaObjectDataStream:{
				   java.io.PipedInputStream pi=new PipedInputStream();
				   java.io.ObjectOutputStream s=new java.io.
					   ObjectOutputStream(new PipedOutputStream(pi));
				   s.writeObject(v);s.close();
				   ps.setBinaryStream(4, pi);}break;
			   case bool://ps.setObject(4, v==null?-1: ((Boolean)v).booleanValue() );break;
			   case Int:
			   case dbl:
			   case dt:
			   case str:
			   case jsonRef:
			   default:
					   ps.setObject(4, v);
			   }
			   x=ps.executeUpdate();
			   }catch(Exception ex){
					   TL.tl().error(ex,"TL.DB.Tbl.JsonProp.Typ.save");}
			   return x;
		   }*

		int save(PreparedStatement ps,String p,Object v) throws Exception{
		   int x=-1;Typ t=this;byte[]a=null;ByteBuffer b=null;
		   ps.setString(2, p);
		   ps.setString(3, t.toString());
		   switch(t){
			   case jsonRef:v=v instanceof Map?((Map)v).get(Jr):v;//ps.setObject(4, v);break;
			   case Int:b=ByteBuffer.allocate(8)
					   .putLong(((Number)v).longValue());break;
			   case dbl:b=ByteBuffer.allocate(8)
					   .putDouble(((Number)v).doubleValue());break;
			   case dt:b=ByteBuffer.allocate(8)
					   .putLong(((Date)v).getTime());break;
			   case bool:a=new byte[1];a[0]=(byte)(v==null?
				   -1:((Boolean)v).booleanValue()?1:0);break;
			   case str:a=((String)v).getBytes("utf8");break;
			   case javaObjectDataStream:{
				   java.io.PipedInputStream pi=new PipedInputStream();
				   java.io.ObjectOutputStream s=new java.io.
					   ObjectOutputStream(new PipedOutputStream(pi));
				   s.writeObject(v);s.close();
				   ps.setBinaryStream(4, pi);}break;
		   }//switch
		   if(b!=null)
			   a=b.array();
		   if(a!=null)
			   ps.setBytes(4,a);
		   x=ps.executeUpdate();
		   return x;}

		   Object load(ResultSet rs){
			   final int col=3;try{
			   if(this==javaObjectDataStream){
				   java.sql.Blob b=rs.getBlob(col);
				   ObjectInputStream s=new
					   ObjectInputStream( b.getBinaryStream());
				   Object o=s.readObject();s.close();
				   return o;}
			   byte[]a=rs.getBytes(col);
			   ByteBuffer b=this!=str?ByteBuffer.wrap(a):null;
			   switch(this){
			   case Int:return b.getLong();
			   case dbl:return b.getDouble();
			   case str:return new String(a,"utf8");
			   case bool:return a[0]==0?false:a[0]==1?true:null;
			   case dt:return new Date(b.getLong());
			   case jsonRef:return Util.mapCreate(Jr,b.getLong());
		   }}catch(Exception ex){TL.tl().error(ex, "TL.DB.JsonProp.Typ.get");}
		   return null;
		   }//get(ResultSet)
	   */
		static Typ typ(Object p){
			if(p==null || p instanceof Boolean)return bool;
			if(p instanceof String)return str;
			if(p instanceof Number)return p instanceof Double||p instanceof Float?dbl:Int;
			if(p instanceof Date)return dt;
			if(p instanceof Map && ((Map)p).containsKey(Jr))return jsonRef;
			return javaObjectDataStream;
		}

	} //enum Typ

	@Override public List creationDBTIndices(TL tl){
		return Util.lst(
				Util.lst("int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
						,"int(18) NOT NULL"//jsonRef
						,"text not null"//path
						,"enum('Int','dbl','str','bool','dt','jsonRef','javaObjectDataStream','clntFunc','srvrFunc') NOT NULL"//typ
						,"blob"//json
				)
				,Util.lst(Util.lst("unique key (",C.jsonRef,Util.lst(C.path,64))
						,Util.lst(C.typ,Util.lst(C.json,64))
						,Util.lst(Util.lst(C.path,64),Util.lst(C.json,64))
				)
		);/*
Create table `jsonHead`(
`jsonRef` int(18) primary key auto_increment,
`parent` int(18) not null,
`proto` int(18) not null,
`meta` text, -- json
Key (`parent`),
Key (`proto`),
);

Create table `JsonProp`(
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

	@F public Integer no,jsonRef;
	@F public String path;
	@F public Typ typ;
	@F public Object json;

	public Map mv(){return json instanceof Map?(Map)json:asMap();}

	public enum C implements CI{no,jsonRef,path,typ,json;
		@Override public Class<? extends Tbl>cls(){return JsonProp.class;}
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

	@Override public CI pkc(){return C.no;}
	@Override public Object pkv(){return no;}
	@Override public CI[]columns(){return C.values();}
	public static final String dbtName="jsonProp",Jr=Typ.jsonRef.toString();

	@Override public String getName(){return dbtName;}//public	Ssn(){super(Name);}

/**param p is the parts of a qualified name of a property path, seperated by dots*/
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
	static Map LoadRef( Map ref ){TL tl=null;
		Object jsonRef=jr(ref);
		for(ItTbl.ItRow row:ItTbl.it(
				"select `"+C.path+"`,`"+C.typ+"`,`"+C.json
						+"` from `"+dbtName+"` where `"+C.jsonRef+"`=?", jsonRef))
			try{	String path=row.nextStr();Typ typ=Typ.valueOf(row.nextStr());
				int i=path.indexOf('.');Object v=typ.load(row.rs);if(i==-1)
				{Object o=ref.get(path);
					if(o==null)
						ref.put(path, v);
					else
						(tl==null?tl=TL.tl():tl).log("TL.DB.Tbl.JsonProp.LoadRef(Map ref):wont overwrite:",path);
				}
				else
					set(ref,path.split("\\."),v,0);
			}catch(Exception ex){
				(tl==null?tl=TL.tl():tl).error(ex,"TL.DB.Tbl.JsonProp.LoadRef:jr=",jsonRef,row);
			}
		ref.put(Jr, jsonRef);
		return ref;
	} // LoadRef

	/**remark(internal):o is parent*/
	static Object set(Object p,String[]path,Object v,int i){
		Object o=p;
		try{
			String s=path[i];int j=Util.parseInt(s, -1)
					,j1=i+1<path.length?Util.parseInt(path[i+1], -1):-1;
			if(o==null)
				o=j==-1&&j1==-1?Util.mapCreate():Util.lst();
			if(o instanceof Map){Map m=(Map)o;
				if(i==path.length-1)
					m.put(s, v);
				else if(i<path.length){
					o=m.get(s);
					if(o==null)
					{
						o=new HashMap();//Util.mapCreate();
						m.put(s,o);}
					o=set(o,path,v,i+1);
				}
			}else if(o instanceof List){List m=(List)o;//TODO: switch List to map in cases j==-1
				if(i==path.length-1)//TODO: check when j==-1 with o instanceof List
					m.set(j, v);
				else if(i<path.length){
					o=m.get(j);
					if(o==null){
						o=j==-1?Util.mapCreate():Util.lst();
						m.set(j,o);}
					o=set(o,path,v,i+1);
				}
			}}catch(Exception ex){
			TL.tl().error(ex,
					"TL.DB.Tbl.JsonProp.set(Object o,String[]path,Object v,int i)"
					, o ,path,v,i);}
		//if(i<path.length)o=set(o,path,v,i+1);
		return o;}

	public static Map LoadRef( Object jsonRef ){
		Map m=jsonRef instanceof Map?(Map)jsonRef:
				//jsonRef instanceof Integer?:
				null;
		if(m!=null && m.get(Jr)==null)
			m.put(Jr, Json.jrmp1());
		if(m==null) m=Util.mapCreate(Jr
				,//jsonRef instanceof Number?jsonRef:
				jsonRef);
		return LoadRef(m);
	} // LoadRef


	/**get from the arg map the property jsonRef as an int, if not found return -1 ,javadoc-draft2016.08.17.11.10*/
	static int jr(Object m){
		Object o=m instanceof Map?((Map)m).get(Jr):null;
		return o==null?-1:((Number)o).intValue();}

	/**same as js but instead of -1 , returns jrmp1 ,javadoc-draft2016.08.17.11.10*/
	public static int jrn(Object m){
		Object o=m instanceof Map?((Map)m).get(Jr):null;
		return o==null?Json.jrmp1():((Number)o).intValue();}

	/**create an instance and set in the db tbl the name-value pairs, uses maPCreate ,javadoc-draft2016.08.17.11.10*/
	static JsonProp create(Object...p) throws SQLException{
		Map m=maPCreate(p==null||p.length<1?null:Util.maPSet(new HashMap(), p));
		JsonProp j=new JsonProp();j.json=m;
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
			int jr=Json.jrMaxPlus1();
			m.put(Jr, jr);
		}save(m);return m;}

	/**return a list of all(objects :jsonRef) that have a ref to this jsonRef with the param- prop path*/
	Integer[]allFK(TL t,String prop){
		List l=null;try {l=DB.q1colList(
				"select `jsonRef` from `JsonProp` where `json`=? and `typ`=? and `path`=?"
				, jsonRef , Typ.jsonRef , prop );
		} catch (SQLException ex) {t.error(ex,"allFK");}
		int n=l==null?0:l.size();
		Integer[]a=new Integer[n];
		for(int i=0;i<n;i++)
			a[i]=((Number)l.get(i)).intValue();
		return a;}

	/**return a list of the Primary keys of all props with this.jsonRef*/
	Integer[]allNo(TL t){
		List l=null;try {l=DB.q1colList(
				"select `no` from `JsonProp` where `jsonRef`=? "
				, jsonRef	);
		} catch (SQLException ex) {t.error(ex,"allNo");}
		int n=l==null?0:l.size();
		Integer[]a=new Integer[n];
		for(int i=0;i<n;i++)
			a[i]=((Number)l.get(i)).intValue();
		return a;}

	static JsonProp load(int jr){
		JsonProp j=new JsonProp();
		j.json=loadProps(j.jsonRef=jr,null);
		return j;}

	/** load from dbTbl `json` all the properties into Map<String,JsonProp>, where the props are tied by dbtColumn `jsonRef`, param jr (short for jsonRef)
	 if jr is negative , then jr is inited from prop jsonRef in param props, if also not found then NOTHING is processed.

	 //return a formal(general-struct) representation of all the properties in the json-obj/array,

	Motivation: the need when updating from ajax .
		must consider the case when the new ajax json(obj or array) has new props
		, or ,
		old-props that are not mention in the new axaj json
	  ,in a nutshell, Structural Change ,javadoc-draft2016.08.17.11.10*/
	static Map<String,JsonProp>loadProps(int jr,Map<String,JsonProp>props){// ,draft2016.08.17.11.10 , this method is the inspiration for db tbls master-detail (dbTbl:`JsonProp` (head) ,and dbTbl:`jsonProp`)
		if(jr<0 && props!=null)
			jr=jr(props);
		if(jr<0)
			return props;
		if(props==null)
			props=new HashMap<String,JsonProp>();
		for(ItTbl.ItRow row:ItTbl.it( "select `"
				+C.no+"`,`"+C.path+"`,`"+C.typ+"`,`"+C.json+"` from `"
				+dbtName+"` where `"+C.jsonRef+"`=?", jr))
		{JsonProp j=new JsonProp();j.jsonRef=jr;
			j.no=row.nextInt();
			j.path=row.nextStr();
			props.put(j.path,j);
			String ty=row.nextStr();
			j.typ=Typ.valueOf(ty);
			j.json=j.typ.load(row.rs);//this is beatiful (tear running down my cheek)
		}//for itrtr
		return props;
	}//loadProps

	/**get member-var json as Map<String,JsonProp>, if necessary load from db, calls loadProps */
	Map<String,JsonProp>prps(){
		Map<String,JsonProp>m=null;
		if(json instanceof Map)//<String,JsonProp>)
		{	m=(Map<String,JsonProp>)json;
			//for(Object k:m.keySet())if()
			Object[]a=m.getClass().getTypeParameters();
			if(a==null|| a.length<2 || a[0]!=String.class || a[1]!=JsonProp.class)
				m=loadProps(jsonRef,null);
		}
		else m=loadProps(jsonRef,null);
		return m;}//prps

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
		else if(json instanceof List)
			savePropList(TL.tl(),"",(List)json,prps());
		else
			saveProp(TL.tl(),"",Typ.typ(json),json,prps());
		return this;}

	void saveProps(TL tl,Map p){
		if(jsonRef==null)
			jsonRef=jrn(p);
		Map<String,JsonProp>props=prps();
		try{for(Object k:p.keySet()){
			Object v=p.get(k);
			Typ typ=Typ.typ(v);
			if( v instanceof Map )
			{	int j=jr(v);if(j==-1)
				savePropMap(tl,k+".",(Map)v,props);
			else saveProp(tl,k.toString(),Typ.jsonRef,j,props);
			}
			else if(v instanceof List)
				savePropList(tl,k+".",(List)v,props);
			else saveProp(tl,k.toString(),typ,v,props);
		}//for
		}catch(Exception ex){}}//sav

	void savePropMap(TL tl,String path,Map p,Map<String,JsonProp>props){
		if(jsonRef==null)
			jsonRef=jrn(p);
		try{for(Object k:p.keySet()){
			Object v=p.get(k);
			Typ typ=Typ.typ(v);
			if( v instanceof Map )
			{	int j=jr(v);if(j==-1)
				savePropMap(tl,path+k+'.',(Map)v,props);
			else saveProp(tl,path+k,Typ.jsonRef,j,props);
			}
			else if(v instanceof List)
				savePropList(tl,path+k+".",(List)v,props);
			else saveProp(tl,path+k,typ,v,props);
		}//for
		}catch(Exception ex){}}//sav

	void savePropList(TL tl,String path,List p,Map<String,JsonProp>props){
		if(jsonRef==null)
			jsonRef=jrn(p);int k=0,n=p.size();
		try{for(;k<n;k++){
			Object v=p.get(k);
			Typ typ=Typ.typ(v);
			if( v instanceof Map )
			{	int j=jr(v);if(j==-1)
				savePropMap(tl,path+k+'.',(Map)v,props);
			else saveProp(tl,path+k,Typ.jsonRef,j,props);
			}
			else if(v instanceof List)
				savePropList(tl,path+k+".",(List)v,props);
			else saveProp(tl,path+k,typ,v,props);
		}//for
		}catch(Exception ex){}}//sav

	static boolean propNotEq(Object a,Object b)
	{if((a==null && b==null)
			|| a==b || (a!=null && a.equals(b)))
		return false;
		if(a==null || b==null )
			return true;
		Class ac=a.getClass(),bc=b.getClass();
		if(ac!=bc)
			return true;
		if(a instanceof Map)
		{	Map am=(Map)a,bm=(Map)b;
			if(am.size()!=bm.size())
				return true;
			for(Object k:am.keySet())
			{Object ax=am.get(k),bx=bm.get(k);
				if(propNotEq(ax,bx))
					return true;
			}
		}//if Map
		if(a instanceof List)
		{List am=(List)a,bm=(List)b;int k=0,n=am.size();
			if(n!=bm.size())
				return true;
			for(;k<n;k++)
			{Object ax=am.get(k),bx=bm.get(k);
				if(propNotEq(ax,bx))
					return true;
			}
		}//if List
		return false;
	}

	static final String SqlSv="replace into `"+dbtName
			+"` (`"+C.no+"`,`"+C.jsonRef+"`,`"+
			C.path+"`,`"+C.typ+"`,`"+C.json+"`)values(?,?,?,?,?)";

	JsonProp saveProp(TL tl,String path,Typ t,Object v, Map<String,JsonProp>props ){
		JsonProp j=props==null?null:props.get(path);
		try{boolean New=j==null||j.no==null;
			if(New){if(j==null){
				j=new JsonProp();
				j.path=path==null?"":path;
				if(props!=null)
					props.put(j.path,j);}
				j.no=DB.q1int("select max(`"+C.no+"`)+1 from `"+dbtName+"`", 1);
			}
			if(New || propNotEq(v,j.json)){//int x=
				DB.x(SqlSv,j.no,j.jsonRef=jsonRef,path,(j.typ=t).toString(),j.json=v);
				Log.log_(tl,Log.Entity.json, j.no, New?Log.Act.New:Log.Act.Update, j.json);
			}
		}catch(Exception ex){tl.error(ex,"TL.DB.Tbl.JsonProp.saveProp:ex:path=",path,": json=",v);}
		return j;}

	void remOldPropsByPathStart(TL tl,String
			pathStarts,Map<String,JsonProp>props){
		for(String k:props.keySet())try{
			if(k.startsWith(pathStarts))
			{	props.get(k).delete();
				props.remove(k);
			}
		}catch(Exception ex){tl.error(ex,
				"TL.DB.Tbl.JsonProp.remOldPropsByPathStart");}
	}//remOldPropsByPathStart

	void removeOldProps(TL tl,Map p,
						String[]a,int i,int n,JsonProp j,
						Map<String,JsonProp>props)
			throws SQLException
	{	if(i>=n)return;
		Object o=p.get(a[i]);
		if(o==null)
		{j.delete();
			props.remove(j.path);
			return;
		}else if(o instanceof Map)
			removeOldProps(tl,(Map)o,a,i+1,n,j,props);
		else if(o instanceof List)
			removeOldProps(tl,(List)o,a,i+1,n,j,props);
	}//removeOldProps

	void removeOldProps(TL tl,List p,
						String[]a,int i,int n,JsonProp j,
						Map<String,JsonProp>props)
			throws SQLException{
		if(i>=n)return;
		int k=Util.parseInt(a[i], -1);
		Object o=p.get(k);
		if(o==null)
		{j.delete();
			props.remove(j.path);
			return;
		}else if(o instanceof Map)
			removeOldProps(tl,(Map)o,a,i+1,n,j,props);
		else if(o instanceof List)
			removeOldProps(tl,(List)o,a,i+1,n,j,props);
	}

	void removeOldProps(TL tl,Map p,Map<String,JsonProp>props){
		for(String k:props.keySet())try{
			int di=k.indexOf(".");
			if(di!=-1){String[]a=k.split(".");int n=a.length;
				Object o=p.get(a[0]);
				if(o==null)
					remOldPropsByPathStart(tl,a[0]+".",props);
				if(o instanceof Map)
					removeOldProps(tl,(Map)o,a,1,n,props.get(k),props);
				else if(o instanceof List)
					removeOldProps(tl,(List)o,a,1,n,props.get(k),props);
			}else{
				Object o=p.get(k);
				if(o==null)try{
					JsonProp j=props.get(k);
					//DB.x("delete from json where no=",j.no);
					//Tbl.Log.log_(tl,Log.Entity.json, j.no, Log.Act.Delete, j.jsonRef);
					j.delete();
				}catch(Exception ex){tl.error(ex,"removeOldProps");}
			}//else
		}//for k
		catch(Exception ex){tl.error(ex,"TL.DB.Tbl.JsonProp.removeOldProps");}
	}//removeOldProps


	public static Map save(Map p){
		//Object o=p.get(Jr);int i=o instanceof Number?((Number)o).intValue():jrmp1();
		save(p,jrn(p));
		return p;
	}

	static Map save(Map p,int jr){
		JsonProp j=new JsonProp();
		j.jsonRef=jr;j.json=p;
		try{j.save();}catch(Exception ex){TL.tl().
				error(ex, "TL.DB.Tbl.JsonProp.save:map,jr");}
		return p;}

}// class TL$DB$JsonProp
