package te050c.tl;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TL {
	//TL member variables
	public String ip;
	public te050c.tl.db.tbl.Usr usr;
	public te050c.tl.db.tbl.Ssn ssn;
	public Map<String,Object>json;//accessing request in json-format
	public Map<Object,Object> response;
	public Date now;//,sExpire;
	/**wrapping JspWriter or any other servlet writer in "out" */
	te050c.tl.json.Output out,/**jo is a single instanceof StringWriter buffer*/jo;
	int htmlIndentation;
	/**the static/class variable "tl"*/ static ThreadLocal<TL> tl=new ThreadLocal<TL>();
	static boolean LogOut=false;//tlLog=true;
	public boolean logOut=LogOut;
	public static final String CommentHtml[]={"\n<!--","-->\n"},CommentJson[]={"\n/*","\n*/"};
	public String comments[]=CommentJson;
	public HttpServletRequest req;
	//public HttpServletResponse rspns;//JspWriter out;
	//PageContext pc;//GenericServlet srvlt;

	//public TL(GenericServlet s,HttpServletRequest r,HttpServletResponse n,PrintWriter o){_srvlt=s;req=r;rspns=n;out=o;}
	public TL(HttpServletRequest r,Writer o){//HttpServletResponse n,
		req=r;out=new te050c.tl.json.Output(o);}//rspns=n;

	public te050c.tl.json.Output jo(){if(jo==null)try{jo=new te050c.tl.json.Output();}catch(Exception x){error(x,"te050c.TL.jo:IOEx:");}return jo;}
	public te050c.tl.json.Output getOut() throws IOException{return out;}//JspWriter//if(out==null)out=rspns.getWriter();
	public HttpServletRequest getRequest(){return req;}
	public HttpSession getSession(){return req.getSession();}
	public ServletContext getServletContext(){return getSession().getServletContext();}//srvlt.getServletContext();
	/**sets a new TL-instance to the localThread*/

	//public static TL Enter(GenericServlet s,HttpServletRequest r,HttpServletResponse n,PrintWriter o)throws IOException{TL p;tl.set(p=new TL(s,r,n,o));p.onEnter();return p;}
	//public static TL Enter(ServletContext p)throws IOException {TL t;tl.set(t=new TL(p.	,p.getOut()));t.onEnter();return t;}

	public static TL Enter(
			HttpServletRequest r,
			//HttpServletResponse s,
			Writer o)
			throws IOException
	{TL p;tl.set(p=new TL(r,o));p.onEnter();return p;}

	private void onEnter()throws IOException
	{ip=getRequest().getRemoteAddr();
		now=new Date();
		try{Object o=req.getContentType();
			o=o==null?null
					:o.toString().contains("json")?te050c.tl.json.Parser.parse(req)
					:o.toString().contains("part")?getMultiParts():null;
			json=o instanceof Map<?, ?>?(Map<String, Object>)o:null;//req.getParameterMap() ;
			response=te050c.tl.Util.mapCreate(//"msg",0 ,
					"return",false , "op",req("op"),"req",o);
			te050c.tl.db.tbl.Ssn.onEnter();
		}catch(Exception ex){error(ex,"TL.onEnter");}
		//if(pages==null){rsp.setHeader("Retry-After", "60");rsp.sendError(503,"pages null");throw new Exception("pages null");}
		if(logOut)out.w(comments[0]).w("TL.tl.onEnter:\n").o(this).w(comments[1]);
		//else log(new Json.Output().o(this).toString());
	}//onEnter

	private void onExit(){usr=null;ssn=null;ip=null;now=null;req=null;response=null;json=null;out=jo=null;}//srvlt=null;rspns=null;

	/**unsets the localThread, and unset local variables*/
	public static void Exit()//throws Exception
	{TL p=TL.tl();if(p==null)return;
		te050c.tl.db.DB.close((Connection)p.r(Context.DB.reqCon.str));
		p.onExit();tl.set(null);}

	Map getMultiParts()
	{	Map<Object,Object>m=null;
		if(ServletFileUpload.isMultipartContent(req))try
		{DiskFileItemFactory factory=new DiskFileItemFactory();
			factory.setSizeThreshold(40000000);//MemoryThreshold);
			//factory.setRepository(new File(System.getProperty("java.io.tmpdir","defaultDirUpload")));
			//upload.setFileSizeMax(MaxFileSize);
			//upload.setSizeMax(MaxRequestSize);
			//final String pth="",UploadDirectory="sheetUploads";
			String path=te050c.app.App.app(this).getUploadPath(this);
			String real=Context.getRealPath(this, path);//getServletContext().getRealPath(path);
			File f=null,uploadDir;
		/*if(real==null){int i=0; boolean b=false;
			while( i<context.ROOT.a.length && (b=(f==null|| !f.exists())) )
			try{
				f=new File(context.ROOT.a[i++]);
			}catch(Exception ex){}
			real=(b?"./":f.getCanonicalPath())+path;
		}*/
			uploadDir=new File(real);
			if( ! uploadDir.exists() )
				uploadDir.mkdirs();//mkDir();
			factory.setRepository(uploadDir);
			ServletFileUpload upload=new ServletFileUpload(factory);
			List<FileItem>formItems=upload.parseRequest(req);
			if(formItems!=null && formItems.size()>0 )
			{	m=new HashMap<Object,Object>();
				for(FileItem item:formItems)
				{	String fieldNm=item.getFieldName();
					boolean fld=item.isFormField();//mem=item.isInMemory(),
					if(fld)
					{String v=item.getString();
						Object o=v;
						if(fieldNm.indexOf("json")!=-1)
							o=te050c.tl.json.Parser.parse(v);
						m.put(fieldNm, o);
					}else{
						long sz=item.getSize();
						if(sz>0){
							String ct=item.getContentType()
									,nm=item.getName();
							int count=0;
							f=new File(uploadDir,nm);
							while(f.exists())
								f=new File(uploadDir,(count++)+'.'+nm);
							//String path=pth+f.getCanonicalPath().substring(real.length());
							m.put(fieldNm,te050c.tl.Util.mapCreate(//"name",fieldNm,
									"contentType",ct,"size",sz
									,"fileName",path+f.getName()
									//,"isInMemory",mem//,"isFormField",fld
									//,"data",item.get()//byt[](sz,item.getInputStream())
							));
							item.write(f);
						}//if sz > 0
					}//if isField else
				}//for(FileItem item:formItems)
			}//if(formItems!=null && formItems.size()>0 )
		}catch(Exception ex){
			error(ex,"TL.getMultiParts");}
		//if(ServletFileUpload.isMultipartContent(req))
		return m;
	}

	/**get the TL-instance for the current Thread*/
	public static TL tl(){Object o=tl.get();return o instanceof TL?(TL)o:null;}

	/**get a request-scope attribute*/
	public Object r(Object n){return req.getAttribute(String.valueOf(n));}

	/**set a request-scope attribute*/
	public Object r(Object n,Object v){req.setAttribute(String.valueOf(n),v);return v;}

	/**get a session-scope attribute*/
	public Object s(Object n){return getSession().getAttribute(String.valueOf(n));}

	/**set a session-scope attribute*/
	public Object s(Object n,Object v){getSession().setAttribute(String.valueOf(n),v);return v;}

	/**get an application-scope attribute*/
	public Object a(Object n){return getServletContext().getAttribute(String.valueOf(n));}

	/**set an application-scope attribute*/
	public void a(Object n,Object v){getServletContext().setAttribute(String.valueOf(n),v);}


	/**get variable, a variable is considered
	 1: a parameter from the http request
	 2: if the request-parameter is not null then set it in the session with the attribute-name pn
	 3: if the request-parameter is null then get pn attribute from the session
	 4: if both the request-parameter and the session attribute are null then return null
	 @parameters String pn Parameter/attribute Name
	 HttpSession ss the session to get/set the attribute
	 HttpServletRequest rq the http-request to get the parameter from.
	 @return variable value.*/
	public Object var(String pn)
	{HttpSession ss=getSession();//HttpServletRequest rq=p.getRequest();
		Object r=null;try{Object sVal=ss.getAttribute(pn);String reqv=req(pn);
		if(reqv!=null&&!reqv.equals(sVal)){ss.setAttribute(pn,r=reqv);//logo("TL.var(",pn,")reqVal:sesssion.set=",r);
		}
		else if(sVal!=null){r=sVal; //logo("TL.var(",pn,")sessionVal=",r);
		}}catch(Exception ex){ex.printStackTrace();}return r;}

	public Number var(String pn,Number r)
	{Object x=var(pn);return x==null?r:x instanceof Number?(Number)x:Double.parseDouble(x.toString());}

	public String var(String pn,String r)
	{Object x=var(pn);return x==null?r:String.valueOf(x);}

	public boolean var(String pn,boolean r)
	{Object x=var(pn);return x==null?r:x instanceof
			Boolean?(Boolean)x:Boolean.parseBoolean(x.toString());}

	/**mostly used for enums , e.g. "enum Screen"*/
	public <T>T var(String n,T defVal)
	{	String r=req(n);
		if(r!=null)
			s(n,defVal=Util.parse(r,defVal));
		else{
			Object s=s(n);
			if(s==null)
				s(n,defVal);
			else{Class c=defVal.getClass();
				if(c.isAssignableFrom(s.getClass()))
					defVal=(T)s;//s(n,defVal=(T)s); //changed 2016.07.18
				else
					log("TL.var(",n,",<T>",defVal,"):defVal not instanceof ssnVal:",s);//added 2016.07.18
			}
		}return defVal;
	}

	/////////////////////////////// */


	public String req(String n)
	{if(json!=null )
	{Object o=json.get(n);if(o!=null)return o.toString();}
		String r=req.getParameter(n);
		if(r==null)r=req.getHeader(n);
		if(logOut)log("TL.req(",n,"):",r);
		return r;}

	public int req(String n,int defval)
	{String s=req(n);
		int r=Util.parseInt(s, defval);
		return r;}

	public Date req(String n,Date defval)
	{String s=req(n);if(s!=null)
		defval=Util.parseDate(s);//(s, defval);
		return defval;}

	public double req(String n,double defval)
	{String s=req(n);if(s!=null)
		try{defval=Double.parseDouble(s);//(s, defval);
		}catch(Exception x){}
		return defval;}

	public <T>T req(String n,T defVal)
	{String s=req(n);if(s!=null)
		defVal=Util.parse(s,defVal);
		return defVal;}

	////////////////////////////////
	
	public te050c.tl.json.Output o(Object...a)throws IOException{if(out!=null&&out.w!=null)for(Object s:a)out.w.write(s instanceof String?(String)s:String.valueOf(s));return out;}
	
	public String logo(Object...a){String s=null;
		if(a!=null&&a.length>0)
			try{//TL p=this;
				te050c.tl.json.Output o=tl().jo().clrSW();//new Json.Output();
				for(Object i:a)o.o(i);
				s=o.toStrin_();
				getServletContext().log(s);//CHANGED 2016.08.17.10.00
				if(logOut){out.flush().
						w(comments[0]//"\n/*"
						).w(s).w(comments[1]//"*/\n"
				);}}catch(Exception ex){ex.printStackTrace();}return s;}

	/**calls the servlet log method*/
	public void log(Object...s){logA(s);}
	public void logA(Object[]s){try{
		jo().clrSW();//StringBuilder b=new StringBuilder();//builder towards the log
		for(Object t:s)jo.w(String.valueOf(t));//b.append(t);//g.log(String.valueOf(t));{if(p.logOut)p.getOut().w(t);}
		String t=jo.toStrin_();
		getServletContext().log(t);
		if(logOut)out.flush().w(comments[0]).w(t).w(comments[1]);//"*/\n");
	}catch(Exception ex){ex.printStackTrace();}}

	public void error(Throwable x,Object...p){try{
		String s=jo().clrSW().w("error:").o(p,x).toString();
		getServletContext().log(s);
		if(logOut)out.w(comments[0]//"\n/*
		).w("error:").w(s.replaceAll("<", "&lt;"))
				.w("\n---\n").o(x).w(comments[1]//"*/\n"
				);if(x!=null)x.printStackTrace();}
	catch(Exception ex){ex.printStackTrace();}}

	/**get a pooled jdbc-connection for the current Thread, calling the function dbc()*/
	public Connection dbc()throws SQLException
	{TL p=this;Object s=Context.DB.reqCon.str,o=p.r(s);
		if(o==null||!(o instanceof Connection))
			p.r(s,o=te050c.tl.db.DB.c());
		return (Connection)o;}

	
}//class TL
