package te050c.tl.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import te050c.tl.TL;
import te050c.tl.Context;

public class DB {
	/**returns a jdbc pooled Connection.
	 uses MysqlConnectionPoolDataSource with a database from the enum context.DB.url.str,
	 sets the pool as an application-scope attribute named context.DB.pool.str
	 when first time called, all next calls uses this context.DB.pool.str*/
	public static synchronized Connection c()throws SQLException
	{ TL t=TL.tl();Connection r=(Connection)t.r(Context.DB.reqCon.str);if(r!=null)return r;
		MysqlConnectionPoolDataSource d=(MysqlConnectionPoolDataSource)t.a(Context.DB.pool.str);
		r=d==null?null:d.getPooledConnection().getConnection();
		if(r!=null)//changed 2016.07.18
			t.r(Context.DB.reqCon.str,r);
		else try
		{String s="",ss=null;
			Context.DB db=Context.DB.dbName,sr=Context.DB.server,un=Context.DB.un,pw=Context.DB.pw;
			String[]dba=db.a,sra=sr.a,una=un.a,pwa=pw.a;//CHANGED: 2016.02.18.10.32
			for(int idb=0;r==null&&idb<dba.length;idb++)
				for(int iun=0;r==null&&iun<una.length;iun++)
					for(int ipw=0;r==null&&ipw<pwa.length;ipw++)//n=Context.DB.len()
						for(int isr=0;r==null&&isr<sra.length;isr++)try
						{	d=new MysqlConnectionPoolDataSource();
							s=dba[Math.min(dba.length-1,idb)];if(t.logOut)ss="\ndb:"+s;
							d.setDatabaseName(s);d.setPort(3306);
							s=sra[Math.min(sra.length-1,isr)];if(t.logOut)ss+="\nsrvr:"+s;
							d.setServerName(s);
							s=una[Math.min(una.length-1,iun)];if(t.logOut)ss+="user:"+s;
							d.setUser(s);
							s=pwa[Math.min(pwa.length-1,ipw)];if(t.logOut)ss+="\npw:"+s;
							d.setPassword(s);
							r=d.getPooledConnection().getConnection();
							t.a(Context.DB.pool.str,d);
							t.r(Context.DB.reqCon.str,r);
							if(t.logOut)t.log("new "+Context.DB.pool.str+":"+d);
						}catch(Exception e){t.log("TL.DB.MysqlConnectionPoolDataSource:",idb,",",isr,",",iun,ipw,t.logOut?ss:"",",",e);}
		}catch(Throwable e){t.error(e,"TL.DB.MysqlConnectionPoolDataSource:throwable:");}//ClassNotFoundException
		if(t.logOut)t.log(Context.DB.pool.str+":"+d);
		if(r==null)try
		{r=java.sql.DriverManager.getConnection
				("jdbc:mysql://"+Context.DB.server.str
								+"/"+Context.DB.dbName.str
						,Context.DB.un.str,Context.DB.pw.str
				);
			t.r(Context.DB.reqCon.str,r);
		}catch(Throwable e){t.error(e,"TL.DB.DriverManager:");}
		return r;
	}

	/**returns a jdbc-PreparedStatement, setting the variable-length-arguments parameters-p, calls dbP()*/
	public static PreparedStatement p(String sql,Object...p)throws SQLException{return P(sql,p);}

	/**returns a jdbc-PreparedStatement, setting the values array-parameters-p, calls TL.dbc() and log()*/
	public static PreparedStatement P(String sql,Object[]p)throws SQLException{return P(sql,p,true);}//tl().dbParamsOddIndex

	public static PreparedStatement P(String sql,Object[]p,boolean odd)throws SQLException
	{TL t=TL.tl();Connection c=t.dbc();
		PreparedStatement r=c.prepareStatement(sql);if(t.logOut)
		t.log("TL("+t+").DB.P(sql="+sql+",p="+p+",odd="+odd+")");
		if(odd){if(p.length==1)
			r.setObject(1,p[0]);else
			for(int i=1;p!=null&&i<p.length;i+=2)
				r.setObject(i/2+1,p[i]);//if(t.logOut)TL.log("dbP:"+i+":"+p[i]);
		}else
			for(int i=0;p!=null&&i<p.length;i++)
			{r.setObject(i+1,p[i]);if(t.logOut)t.log("dbP:"+i+":"+p[i]);}
		if(t.logOut)t.log("dbP:sql="+sql+":n="+(p==null?-1:p.length)+":"+r);return r;}

	/**@return a jdbc-PreparedStatement stored in the application-scope, when first time called,this function creates the PreparedStatement and set it in the application scope with the attribute-name nm parameter*/
	public static PreparedStatement Ps(String nm,String sql)throws Exception
	{TL tl=TL.tl();Object[]a=(Object[])tl.a(nm);
		if(a==null){a=new Object[2];a[0]=c().prepareStatement(sql);tl.a(nm,a);}
		else {tl.log("Ps:1:else:tl.now="+tl.now+", a[1]="+a[1]);
			if( (tl.now.getTime()-((java.util.Date)a[1]).getTime()) >1000*60*5)
			{try{((PreparedStatement)a[0]).getConnection().close();}
			catch(Exception ex){tl.log("",ex);}
				a[0]=c().prepareStatement(sql);}}
		a[1]=tl.now;return(PreparedStatement)a[0];}

	/**returns a jdbc-ResultSet, setting the variable-length-arguments parameters-p, calls dbP()*/
	public static ResultSet r(String sql,Object...p)throws SQLException{return P(sql,p,true).executeQuery();}

	/**returns a jdbc-ResultSet, setting the values array-parameters-p, calls dbP()*/
	public static ResultSet R(String sql,Object[]p)throws SQLException{
		PreparedStatement x=P(sql,p,true);
		ResultSet r=x.executeQuery();
		return r;}

	/**closes the resultSet-r and the statement, but DOES-NOT close the connection*/
	public static void closeRS(ResultSet r)//,Connection c
	{if(r!=null)try{Statement s=r.getStatement();r.close();s.close();}catch(Exception e){e.printStackTrace();}}
	public void close(ResultSet r){if(r!=null)try{Statement s=r.getStatement();r.close();close(s);}catch(Exception e){e.printStackTrace();}}
	public static void close(Statement s){try{Connection c=s.getConnection();s.close();close(c);}catch(Exception e){e.printStackTrace();}}
	public static void close(Connection c){
		try{if(c!=null){
			TL.tl().r("java.sql.Connection",null);
			c.close();}
		}catch(Exception e){e.printStackTrace();}}

	/**returns a string or null, which is the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static String q1str(String sql,Object...p)throws SQLException{return q1Str(sql,p);}
	public static String q1Str(String sql,Object[]p)throws SQLException
	{String r=null;ResultSet s=null;try{s=R(sql,p);r=s.next()?s.getString(1):null;}finally{closeRS(s);}return r;}//CHANGED:2015.10.23.16.06:closeRS ; CHANGED:2011.01.24.04.07 ADDED close(s,dbc());

	public static String newUuid()throws SQLException{return q1str("select uuid();");}

	/**returns an java obj, which the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	//public static Object q1obj(String sql,Object...p)throws Exception{ResultSet s=null;try{s=R(sql,p);return s.next()?s.getObject(1):null;}finally{close(s,dbc());}}
	public static Object q1obj(String sql,Object...p)throws SQLException{return q1Obj(sql,p);}
	public static Object q1Obj(String sql,Object[]p)throws SQLException
	{ResultSet s=null;try{
		s=R(sql,p);
		return s.next()?s.getObject(1):null;
	}finally{closeRS(s);}}

	/**returns an integer or df, which the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static int q1int(String sql,int df,Object
			...p)throws SQLException{return q1Int(sql,df,p);}

	public static int q1Int(String sql,int df,Object[]p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);return s.next()?s.getInt(1):df;}finally{closeRS(s);}}//CHANGED:2015.10.23.16.06:closeRS ;

	/**returns a double or df, which is the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static double q1dbl(String sql,double df,Object...p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);return s.next()?s.getDouble(1):df;}finally{closeRS(s);}}//CHANGED:2015.10.23.16.06:closeRS ;

	/**returns as an array of rows of arrays of columns of values of the results of the sql
	 , calls dbL() setting the variable-length-arguments values parameters-p*/
	public static Object[][]q(String sql,Object...p)throws SQLException{return Q(sql,p);}

	public static Object[][]Q(String sql,Object...p)throws SQLException
	{List<Object[]>r=L(sql,p);Object b[][]=new Object[r.size()][];r.toArray(b);r.clear();return b;}

	/**return s.getMetaData().getColumnCount();*/
	public static int cc(ResultSet s)throws SQLException{return s.getMetaData().getColumnCount();}

	/**calls L()*/
	public static List<Object[]> l(String sql,Object...p)throws SQLException{return L(sql,p);}

	/**returns a new linkedList of the rows of the results of the sql
	 ,each row/element is an Object[] of the columns
	 ,calls dbR() and dbcc() and dbclose(ResultSet,TL.dbc())*/
	public static List<Object[]> L(String sql,Object[]p)throws SQLException
	{TL t=TL.tl();ResultSet s=null;List<Object[]> r=null;try{s=R(sql,p);Object[]a;r=new LinkedList<Object[]>();
		int cc=cc(s);while(s.next()){r.add(a=new Object[cc]);
			for(int i=0;i<cc;i++){a[i]=s.getObject(i+1);
			}}return r;}finally{closeRS(s);//CHANGED:2015.10.23.16.06:closeRS ;
		if(t.logOut)try{t.log(t.jo().o("TL.DB.L:sql=")
				.o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){t.error(x,"TL.DB.List:",sql);}}}

	public static List<Object> q1colList(String sql,Object...p)throws SQLException
	{ResultSet s=null;List<Object> r=null;try{s=R(sql,p);r=new LinkedList<Object>();
		while(s.next())r.add(s.getObject(1));return r;}
	finally{closeRS(s);TL t=TL.tl();if(t.logOut)
		try{t.log(t.jo().o("TL.DB.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
				.o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){t.error(x,"TL.DB.q1colList:",sql);}}}

	public static Object[] q1col(String sql,Object...p)throws SQLException
	{List<Object> l=q1colList(sql,p);Object r[]=new Object[l.size()];l.toArray(r);l.clear();return r;}

	/**returns a row of columns of the result of sql
	 ,calls dbR(),dbcc(),and dbclose(ResultSet,TL.dbc())*/
	public static Object[] q1row(String sql,Object...p)throws SQLException{return q1Row(sql,p);}
	public static Object[] q1Row(String sql,Object[]p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);Object[]a=null;int cc=cc(s);if(s.next())
	{a=new Object[cc];for(int i=0;i<cc;i++)try{a[i]=s.getObject(i+1);}
	catch(Exception ex){TL.tl().error(ex,"TL.DB.q1Row:",sql);a[i]=s.getString(i+1);}}
		return a;}finally{closeRS(s);}}//CHANGED:2015.10.23.16.06:closeRS ;

	/**returns the result of (e.g. insert/update/delete) sql-statement
	 ,calls dbP() setting the variable-length-arguments values parameters-p
	 ,closes the preparedStatement*/
	public static int x(String sql,Object...p)throws SQLException{return X(sql,p);}

	public static int X(String sql,Object[]p)throws SQLException
	{int r=-1;try{PreparedStatement s=P(sql,p,false);r=s.executeUpdate();s.close();return r;}
	finally{TL t=TL.tl();if(t.logOut)try{
		t.log(t.jo().o("TL.DB.x:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
	catch(IOException x){t.error(x,"TL.DB.X:",sql);}}}

	/**output to tl.out the Json.Output.oRS() of the query*/
	public static void q2json(String sql,Object...p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);try{TL.tl().getOut()
			//(new Json.Output())//TODO:investigate where the Json.Output.w goes
			.o(s);
	}catch (IOException e) {e.printStackTrace();}}
	finally{closeRS(s);TL t=TL.tl();if(t.logOut)try{t.log(t.jo().o(
			"TL.DB.L:q2json=").o(sql).w(",prms=").o(p).toStrin_());
	}catch(IOException x){t.error(x,"TL.DB.q1json:",sql);}}}

	/**return a list of maps , each map has as a key a string the name of the column, and value obj*/
	static List<Map<String,Object>>json(String sql,Object...p) throws SQLException{return Lst(sql,p);}
	static List<Map<String,Object>>Lst(String sql,Object[ ]p) throws SQLException{
		List<Map<String,Object>>l=new LinkedList
				< Map < String ,Object>>();ItTbl i=new ItTbl(sql,p);
		List<String>cols=new LinkedList<String>();
		for(int j=1;j<=i.row.cc;j++)cols.add(i.row.m.getColumnLabel(j));
		for(ItTbl.ItRow w:i){Map<String,Object>m=
				new HashMap<String,Object>();l.add(m);
			for(Object o:w)m.put(cols.get(w.col-1),o);
		}return l;}

}//class DB
