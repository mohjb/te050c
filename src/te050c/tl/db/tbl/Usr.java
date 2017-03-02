package te050c.tl.db.tbl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import te050c.tl.TL;
import te050c.tl.Util;
import te050c.tl.Form;
import te050c.tl.db.tbl.Tbl;
import te050c.tl.db.tbl.Cols;

/**represents a row in the `usr` mysql table ,
 * a sub class from TL.DB.Tbl,
 * hence has built-in methods to operate with
 * the mysql-table, like querying and and updating*/
public class Usr extends Tbl{
	static final String dbtName="Usr";
	/**the attribute-name	in the session*/
	public final static String prefix=dbtName;

	//public Usr(){super(Name);}
	@Override public String getName(){return dbtName;}
	@F public Integer uid;
	//public enum Gender{male,female}
	//public enum Flags{eu,auth,admin}
	//@F public Flags flags;
	@F public String un;
	@F(prmPw=true) public String pw;
	//@F public String full,tel,email;
	//@F public Date dob;
	//@F public Gender gender;
	//@F public URL img;
	@F(json=true) public Map json;

	/**load uid by un,pw , load from mysql*/
	public Integer loadUid()throws Exception{
		TL tl=TL.tl();tl.logo("Usr.loadUid:",this);
		Object o=obj(C.uid, where(C.un,un,C.pw,pw));
		if(o==null)uid=null;else
		if(o instanceof Integer)uid=(Integer)o;else
		if(o instanceof Number)uid=((Number)o).intValue();
		else uid=Integer.parseInt(o.toString());
		return uid;}

	/**returns null if the credentials are invalid,
	 * the credentials are username and password
	 * which are read from the http-request
	 * parameters "un" , "pw" */
	public static Usr login()throws Exception{//String un,String pw
		Usr u=new Usr();TL tl=TL.tl();
		u.readReq(prefix+".");tl.logo("Usr.login:",u);
		try{u.loadUid();tl.logo("Usr.login:2:",u);}catch(Exception x){tl.error(x,"DB.Tbl.Usr.login:loadUid");tl.logo("Usr.login:3:",u);}
		tl.log("Usr.login:u=",u);//n=",u.un," ,pw=",u.pw);
		if(u!=null&&u.uid!=null)u.load();
		else u=null;tl.logo("Usr.login:4:",u);
		return u;}

	/**update the member variables , load from the mysql table*/
	public void onLogin()throws Exception{
		TL tl=TL.tl();tl.logo("Usr.onLogin:1:",this);
		tl.usr=this;
		Ssn n=tl.ssn=new Ssn();
		n.sid=null;//0;
		n.uid=uid;
		n.auth=n.dt=
				n.last=new Date();
		n.save();
		n.usr=this;
		tl.s(Ssn.SessionAttrib,n);
		Object o=tl.s(StrSsnTbls);
		Map<Class<? extends Tbl>,Tbl>
				tbls=o instanceof Map?(Map)o:null;
		if(tbls==null)tl.s(StrSsnTbls,tbls=new
				java.util.HashMap<Class<? extends Tbl>,Tbl>());
		tbls.put(Usr.class, tl.usr);
		tbls.put(Ssn.class, n);tl.logo("Usr.onLogin:n:",this);}

	/**update the member variables , save to the mysql table*/
	public void onSignup()throws Exception{onLogin();save();}

	public enum C implements CI{uid,un,pw,json;//,flags,full,tel,email,dob,gender,img;
		@Override public Class<? extends Tbl>cls(){return Usr.class;}
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

	@Override public CI pkc(){return C.uid;}
	@Override public Object pkv(){return uid;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return Util.lst(
				Util.lst("int(6) NOT NULL AUTO_INCREMENT PRIMARY KEY "//uid
						,"varchar(255) not null"//un
						,"varchar(255) not null"//pw
						,"text"//json
				)
				,Util.lst(C.un)
				,Util.lst(Util.lst("1","admin","admin","{title:\"admin\",avatar:\"avatar.jpg\"}"))
		);/*CREATE TABLE `usr` (
`uid` int(6) NOT NULL AUTO_INCREMENT,
`flags` set('eu','auth','admin') not null default 'eu',
`un` varchar(255) NOT NULL,
`pw` varchar(255) NOT NULL,
`full` text ,
`tel` text ,
`email` text ,
`dob` date,
`gender` set('male','female'),
`img` text ,
PRIMARY KEY (`uid`),
KEY `uk` (`un`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

insert into usr values
(1,'admin','admin',password('admin'),'admin','admin','admin','1/1/1','male','admin'),
(2,'auth','auth',password('auth'),'auth','auth','auth','1/1/1','male','auth'),
(3,'eu','eu',password('eu'),'eu','eu','eu','1/1/1','male','eu');
*/}
}//class Usr