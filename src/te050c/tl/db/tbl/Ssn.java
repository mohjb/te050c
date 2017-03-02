package te050c.tl.db.tbl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import te050c.tl.TL;
import te050c.tl.Util;
import te050c.tl.Form;
import te050c.tl.db.tbl.Tbl;
import te050c.tl.db.tbl.Cols;

public class Ssn extends Tbl {//implements Serializable
	public static final String dbtName="ssn";
	static final String SessionAttrib="TL.DB.Tbl."+dbtName;
	Usr usr;

	@Override public String getName(){return dbtName;}//public	Ssn(){super(Name);}
	@F public Integer sid,uid;
	@F public Date dt,auth,last;

	public void onLogout()throws Exception{
		TL tl=TL.tl();tl.ssn=null;tl.usr=null;
		tl.s(Ssn.SessionAttrib,null);
		HttpSession s=tl.getSession();
		s.setMaxInactiveInterval(1);
	}

	public static void onEnter(){
		TL tl=TL.tl();Object o=tl.s(Ssn.SessionAttrib);
		if(o instanceof Ssn){
			Ssn n=(Ssn)o;
			tl.ssn=n;tl.usr=n.usr;
			tl.ssn.last=tl.now;
			n.save(C.last);}}

	public enum C implements CI{sid,uid,dt,auth,last;
		@Override public Class<? extends Tbl>cls(){return Ssn.class;}
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

	@Override public CI pkc(){return C.sid;}
	@Override public Object pkv(){return sid;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return Util.lst(
				Util.lst("int(6) PRIMARY KEY NOT NULL AUTO_INCREMENT"//sid
					,"int(6) NOT NULL"//uid
					,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//dt
					,"timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'"//auth
					,"timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'"//last
				),Util.lst(C.dt,C.auth,C.last,Util.lst(C.uid,C.dt))
		);/*

CREATE TABLE `ssn` (
`sid` int(6) NOT NULL AUTO_INCREMENT,
`uid` int(6) NOT NULL ,
`dt` timestamp not null,
`auth` timestamp,
`last` timestamp not null,
PRIMARY KEY (`sid`),
KEY `kDt` (`dt`),
KEY `kAuth` (`auth`),
KEY `kLast` (`last`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
*/}

}//class Ssn
