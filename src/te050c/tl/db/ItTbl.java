package te050c.tl.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import te050c.tl.TL;
import te050c.tl.Util;

public class ItTbl implements Iterator<ItTbl.ItRow>,Iterable<ItTbl.ItRow>{
	public ItRow row=new ItRow();

	public ItRow getRow(){return row;}

	public static ItTbl it(String sql,Object...p){return new ItTbl(sql,p);}

	public ItTbl(String sql,Object[]p){
		try {init(DB.R(sql, p));}
		catch (Exception e) {TL.tl().logo("TL.DB.ItTbl.<init>:Exception:sql=",sql,",p=",p," :",e);}}

	public ItTbl(ResultSet o) throws SQLException{init(o);}

	public ItTbl init(ResultSet o) throws SQLException
	{row.rs=o;row.m=o.getMetaData();row.row=row.col=0;
		row.cc=row.m.getColumnCount();return this;}

	public static final String ErrorsList="TL.DB.ItTbl.errors";

	@Override public boolean hasNext(){
		boolean b=false;try {if(b=row!=null&&row.rs!=null&&row.rs.next())row.row++;
		else DB.closeRS(row.rs);//CHANGED:2015.10.23.16.06:closeRS ;	//,row.rs.getStatement().getConnection());
		}catch (SQLException e) {//e.printStackTrace();
			TL t=TL.tl();//changed 2016.06.27 18:05
			final String str="TL.DB.ItTbl.next";
			t.error(e,str);
			List l=(List)t.response.get(ErrorsList);
			if(l==null)t.response.put(ErrorsList,l=new LinkedList());
			l.add(Util.lst(str,row!=null?row.row:-1,e));
		}return b;}

	@Override public ItRow next() {if(row!=null)row.col=0;return row;}
	@Override public void remove(){throw new UnsupportedOperationException();}

	@Override public Iterator<ItRow>iterator(){return this;}

	public class ItRow implements Iterator<Object>,Iterable<Object>{
		public ResultSet rs;int cc,col,row;public ResultSetMetaData m;
		public int getCc(){return cc;}
		public int getCol(){return col;}
		public int getRow(){return row;}
		@Override public Iterator<Object>iterator(){return this;}

		@Override public boolean hasNext(){return col<cc;}

		@Override public Object next(){
			try {return rs==null?null:rs.getObject(++col);}
			catch (SQLException e) {//changed 2016.06.27 18:05
				TL t=TL.tl();
				final String str="TL.DB.ItTbl.ItRow.next";
				t.error(e,str);
				List l=(List)t.response.get(ErrorsList);
				if(l==null)t.response.put(ErrorsList,l=new LinkedList());
				l.add(Util.lst(str,row,col,e));
			}//.printStackTrace();}
			return null;}

		@Override public void remove(){throw new UnsupportedOperationException();}

		public int nextInt(){
			try {return rs==null?-1:rs.getInt(++col);}
			catch (SQLException e) {e.printStackTrace();}
			return -1;}
		public String nextStr(){
			try {return rs==null?null:rs.getString(++col);}
			catch (SQLException e) {e.printStackTrace();}
			return null;}

	}//ItRow

}//ItTbl