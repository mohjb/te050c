package te050c.tl.db.tbl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import te050c.tl.TL;
import te050c.tl.db.DB;
import te050c.tl.Form;
import te050c.tl.Util;

public class Log extends Tbl {//implements Serializable
	public static final String dbtName="log";

	@Override public List creationDBTIndices(TL tl){
		return Util.lst(
				Util.lst("int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
						,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//dt
						,"int(11) NOT NULL"//uid
						,"enum('projects','usr','sheets','json','ssn','log','buildings','floors')"//entity
						,"int(11) NOT NULL"//pk
						,"enum('New','Update','Delete','Login','Logout','Log','Error')"//act
						,"text"//json
				)
				,Util.lst(Util.lst(C.uid,C.dt)
						,C.dt
						,Util.lst(C.entity,C.act,C.dt)
						,Util.lst(C.entity,C.pk,C.dt))
		);	/*projects,sheets,imgs

CREATE TABLE `log` (
`no` int(11) primary key,
`dt` timestamp not null,
`uid` int(11)not null,
`entity` enum('projects','usr','sheets','img','ssn','log'),
`pk` int(11) DEFAULT NULL,
`act` enum('New','Update','Delete','Login','Logout','Log','Error'),
`val` text,
`old` text,
key(uid,dt),
key(dt),
key(`entity`,`act`,`dt`),
key(`entity`,`pk`,`dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
*/}
	@Override public String getName(){return dbtName;}//public	Ssn(){super(Name);}
	@F public Integer no;
	@F public Date dt;
	@F public Integer uid;
	public enum Entity{projects,usr,sheets,ssn,log,json}//,img //CHANGED 2016.08.17.10.49
	@F public Entity entity;
	@F public Integer pk;
	public enum Act{New,Update,Delete,Login,Logout,Log,Error}
	@F public Act act;
	@F public String json;//,old;

	public enum C implements CI{no,dt,uid,entity,pk,act,json;//,old;
		@Override public Class<? extends Tbl>cls(){return Log.class;}
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
	@Override public C[]columns(){return C.values();}

	public static int log(Entity e
			,Integer pk,Act act,Map val){return log(TL.tl(),e,pk,act,val);}

	public static int log(TL t,Entity e
			,Integer pk,Act act,Map val){//,Map old
		int r=-1;try{//throws SQLException, IOException
			r= DB.x(
					"insert into `"+dbtName+"`(`"+C.uid+"`,`"+C.entity+"`,`"+C.pk+"`,`"+C.act+"`,`"+C.json+"`) values(?,?,?,?,?)"
					,t.usr!=null?t.usr.uid:-1,e.toString(),pk , act.toString()
					, t.jo().clrSW().o(val).toString()
					//, null//t.jo().clrSW().o(old).toString()
			);}
		catch(Exception x){t.error(x,
				"TL.DB.Tbl.Log.log:ex:");}return r;}

	public static int log_(TL t,Entity e
			,Integer pk,Act act,Object val){//,Map old
		int r=-1;try{r= DB.x(
				"insert into `"+dbtName+"`(`"+C.uid+"`,`"+C.entity+"`,`"+C.pk+"`,`"+C.act+"`,`"+C.json+"`) values(?,?,?,?,?)"
				,t.usr!=null?t.usr.uid:-1,e.toString(),pk , act.toString()
				, t.jo().clrSW().o(val).toString()
				//, null//t.jo().clrSW().o(old).toString()
		);t.log("TL.DB.Tbl.Log.log_:",e,",",pk,",",act,",",val);}
		catch(Exception x){t.error(x,"TL.DB.Tbl.Log.log:ex:");}
		return r;}

}//class Log