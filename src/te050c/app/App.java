package te050c.app;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

import te050c.tl.TL;
import te050c.tl.Util;
import te050c.tl.db.DB;
import te050c.tl.db.tbl.*;

public class App {
 enum UsrLvl{suspended,view,edit,admin}

	static Usr usr(Object uid){
		Usr u=new Usr();
		u.load(uid);return u;}


	static final String SsnNm="TE050c.App"
			,UploadPth="/te050cUploads/";

	//Prm.Screen screen;
	UsrLvl usrLvl=UsrLvl.suspended;

	//Html jsp;

	public static App app(){return app(TL.tl());}
	public static App app(TL tl){
		Object o=tl.s(SsnNm);
		if(o==null)
			tl.s(SsnNm,o=new App());
		App e=(App)o;//e.tl=tl;
		if(tl.usr==null && tl.a(SsnNm+".checkDBTCreation")==null
				){//e.h.checkDBTCreation(tl);
			new Json().checkDBTCreation(tl);
			new JsonProp().checkDBTCreation(tl);
			new Usr ().checkDBTCreation(tl);
			new te050c.tl.db.tbl.Ssn().checkDBTCreation(tl);
			new Log().checkDBTCreation(tl);
			tl.a(SsnNm+".checkDBTCreation",tl.now);
		}return e;}

	/**path to the uploaded files for Sheet (the sheet stored in the session)*/
	public String getUploadPath(TL tl){
		readVars(tl);//if(proj.no!=sheet.p)proj.no=sheet.p;

		return UploadPth//+proj.no+'/'+bld.no+'/'+flr.no+'/'+sheet.no+'/'
				//+sheet.p+'/'+sheet.b+'/'+sheet.f+'/'+sheet.no
		+'/';}

	App readVars(TL tl){return init(tl);}

 App init(TL tl){try{
	//screen=tl.var(Prm.screen.toString(),Prm.Screen.ProjectsList);
	if( tl.s(SsnNm)!=this )
		tl.s( SsnNm , this );
	//proj=(Project)tl.s("proj");
	//Integer n=tl.var(Prm.projNo.toString(),-1).intValue();//proj.no);

  }catch(Exception ex){
	TL.tl().error(ex,SsnNm,".init");}
	return this;
 }//init


 void doOp(TL tl,Prm.Op op){
  try{if(op!=null)switch(op)
  {case htmlDelete:
	  case htmlDuplicate:
	  case htmlEdit:
	  case htmlLoad:
	  case htmlNew:
	  case login:
	  case logout:
	  case pageDelete:
	  case pageDuplicate:
	  case pageEdit:
	  case pageLoad:
	  case pageNew:
	  case projDelete:
	  case projDuplicate:
	  case projEdit:
	  case projLoad:
	  case projNew:
	  case appOp:
	case none:default:
   }
  }catch(Exception ex){tl.error(ex,"/adoqs/te050c/index.jsp:do op:ex:");}
 }//doOp

 void init2(Prm.Op op) throws Exception{TL tl=TL.tl();
	if(tl.usr==null&&op==Prm.Op.login)
	{tl.logo("index:4:login");
		Usr u=Usr.login();tl.logo("index:5:login");
		if(u!=null){u.onLogin();
			Object ul=u.json.get("UsrLvl");
			try{if(ul!=null)usrLvl=UsrLvl.valueOf(ul.toString());}catch(Exception ex){}
			Log.log(Log.Entity.usr
				, tl.usr.uid
				, Log.Act.Login
				,Util.mapCreate("usr",tl.usr,"request",tl.req));
		}else// msg="incorrect login";
			Log.log(Log.Entity.usr
				, tl.usr.uid, Log.Act.Log
				,Util.mapCreate("msg","incorrect login","request",tl.req));
		//tl.logo("index:6:login:msg=",msg);
	}
	if(tl.usr==null && tl.getSession().isNew())
		Log.log(Log.Entity.ssn, -1, Log.Act.Log,
			Util.mapCreate("msg","new Connection","request",tl.req));

	if(tl.usr!=null&&op==Prm.Op.logout)
	{ Log.log(Log.Entity.usr, tl.usr.uid
			, Log.Act.Logout
			,Util.mapCreate("usr",tl.usr));
		tl.ssn.onLogout();}

	if(tl.usr==null){try{//tl.o("version 2016.05.04 08:36");
		//pageContext.include("flat-login-form/index.html");
		tl.o("<script>location=\"flat-login-form/index.html\"</script>");
	}catch(Exception x){tl.error(x,"te050c.App.init2");}
		tl.logo("index:8:end");
		return;
	}tl.logo("index:9");
 }//init2

 void initProtoRoot(){
 	if(Json.root==null){Json.root=JsonProp.LoadRef(0);}
 }//initProtoRoot

 public static void jsp
 (HttpServletRequest request
	,HttpServletResponse response
	,javax.servlet.http.HttpSession session
	,JspWriter out
	,javax.servlet.jsp.PageContext pageContext)
 throws IOException, javax.servlet.ServletException
 {TL tl=null;try
  {tl=TL.Enter(request,out);
	Prm.Op op=tl.req(Prm.op.toString(),Prm.Op.none);tl.logo("index:1:",op);
	response.setContentType("text/html; charset=UTF-8");
	tl.logOut=tl.var("logOut",false);
	App e=App.app(tl).init(tl);

	e.init2(op);
	if(e.usrLvl==UsrLvl.suspended){
		tl.o("User suspended, please contact System-Admin");
		return;}
	e.doOp(tl,op);
	  DB.q2json("select * from e2");
  }
  catch(Exception x){
	if(tl!=null)
		tl.error(x,"/adoqs/index.jsp:");
	else
		x.printStackTrace();}
  finally{try{
		List l=tl!=null && tl.response!=null?(
				List)tl.response.get(te050c.tl.db.ItTbl.ErrorsList):null;
		if(l!=null)//errors from DB.ItTbl iterator
			tl.o("<!--",l,"-->");
	}catch(Exception ex){}
		TL.Exit();
  }
	out.write("</body></html>");
 }//jsp

}//class App