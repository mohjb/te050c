package te050c.tl.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import te050c.tl.TL;

public class Output {
	public Writer w;//JspWriter jw;
	public boolean initCache=false,includeObj=false,comment=false;
	Map<Object, String> cache;
	public static void out(Object o,Writer w,boolean initCache,boolean includeObj)
			throws IOException{Output t=new Output(w,initCache,includeObj);t.o(o);if(t.cache!=null){t.cache.clear();t.cache=null;}}
	public static String out(Object o,boolean initCache,boolean includeObj){StringWriter w=new StringWriter();
		try{out(o,w,initCache,includeObj);}catch(Exception ex){TL.tl().log("Json.Output.out",ex);}return w.toString();}
	public static String out(Object o){StringWriter w=new StringWriter();try{out(o,w,
			false,false);}catch(Exception ex){TL.tl().log("Json.Output.out",ex);}return w.toString();}
	public Output(){w=new StringWriter();}
	public Output(Writer p){w=p;}
	public Output(Writer p,boolean initCache,boolean includeObj)
	{w=p;this.initCache=initCache;this.includeObj=includeObj;}
	public Output(boolean initCache,boolean includeObj){this(new StringWriter(),initCache,includeObj);}
	public Output(String p)throws IOException{w=new StringWriter();w(p);}
	public Output(OutputStream p)throws Exception{w=new OutputStreamWriter(p);}

	public String toString(){return w==null?null:w.toString();}
	public String toStrin_(){String r=w==null?null:w.toString();clrSW();return r;}
	public Output w(char s)throws IOException{if(w!=null)w.write(s);return this;}
	public Output w(String s)throws IOException{if(w!=null)w.write(s);return this;}

	public Output p(String s)throws IOException{return w(s);}//equivelant to JspWriter.print
	public Output p(char s)throws IOException{return w(s);}
	public Output p(long s)throws IOException{return w(String.valueOf(s));}
	public Output p(int s)throws IOException{return w(String.valueOf(s));}
	public Output p(boolean s)throws IOException{return w(String.valueOf(s));}
	public Output p(Object...a)throws IOException{if(w!=null&&a!=null)//ADDED 2016.08.17.12.21
		for(Object o:a)
			if(o instanceof String )w.write((String)o);
			else if(o instanceof Character )w.write((Character)o);
			else w.write(String.valueOf(o));
		return this;}

	//the philosiphy of the methods `o`, is that they are recursive and traverse/drill-down the composition of the argument/parameter
	public Output o(Object...a)throws IOException{return o("","",a);}
	public Output o(Object a,String indentation)throws IOException{return o(a,indentation,"");}
	public Output o(String ind,String path,Object[]a)throws IOException
	{for(Object i:a)o(i,ind,path);return this;}

	public Output o(Object a,String ind,String path)throws IOException
	{if(cache!=null&&a!=null&&((!includeObj&&path!=null&&path.length()<1)||cache.containsKey(a)))
	{Object p=cache.get(a);if(p!=null){o(p.toString());o("/*cacheReference*/");return this;}}
		final boolean c=comment;
		if(a==null)w("null"); //Object\n.p(ind)
		else if(a instanceof Map<?,?>)oMap((Map)a,ind,path);
		else if(a instanceof java.util.UUID)w("\"").p(a.toString()).w(c?"\"/*uuid*/":"\"");
		else if(a instanceof Boolean||a instanceof Number)w(a.toString());
		else if(a instanceof Throwable)oThrbl((Throwable)a,ind);
		else if(a instanceof java.util.Date)oDt((java.util.Date)a,ind);
		else if(a instanceof Object[])oArray((Object[])a,ind,path);
		else if(a.getClass().isArray())oarray(a,ind,path);
			//else if(a instanceof List<?>)oList((List<Object>)a,ind,cache,level);
		else if(a instanceof Collection<?>)oCollctn((Collection)a,ind,path);
		else if(a instanceof Enumeration<?>)oEnumrtn((Enumeration)a,ind,path);
		else if(a instanceof Iterator<?>)oItrtr((Iterator)a,ind,path);
		else if(a instanceof TL)oTL((TL)a,ind,path);
		else if(a instanceof ServletContext)oSC((ServletContext)a,ind,path);
		else if(a instanceof ServletConfig)oSCnfg((ServletConfig)a,ind,path);
		else if(a instanceof HttpServletRequest)oReq((HttpServletRequest)a,ind,path);
		else if(a instanceof HttpSession)oSession((HttpSession)a,ind,path);
		else if(a instanceof Cookie)oCookie((Cookie)a,ind,path);

		else if(a instanceof ResultSet)oResultSet(( ResultSet)a,ind,path);
		else if(a instanceof ResultSetMetaData)oResultSetMetaData((ResultSetMetaData)a,ind,path);
//else if(a instanceof java.sql.ConnectionMetaData)oConnectionMetaData((java.sql.ConnectionMetaData)a,ind,path);

			//else if(a instanceof Part)oPart((Part)a,ind,path);
		else if(a instanceof te050c.tl.Form)oForm((te050c.tl.Form)a,ind,path);
		else if(a instanceof String)oStr(String.valueOf(a),ind);
		else{w("{\"class\":").oStr(a.getClass().getName(),ind)
				.w(",\"str\":").oStr(String.valueOf(a),ind)
				.w(",\"hashCode\":").oStr(Long.toHexString(a.hashCode()),ind);
			if(c)w("}//Object&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");}return this;}

/*public Output oPart(Part p,String ind,String path)throws IOException{
String i2=ind+'\n';if(comment)w("{//javax.servlet.http.Part:").
w(p.getClass().toString()).w(':').oStr(path, ind).
w('\n').p(ind);else w("{");
w("\"name\":").oStr(p.getName(),ind).
w(",\"ContentType\":").oStr(p.getContentType(),ind).
//w(",\"SubmittedFileName\":").oStr(p.getSubmittedFileName(),ind).
w(",\"size\":").p(p.getSize()).w(",\"headers\":{");int comma=-1;
for(String f:p.getHeaderNames()){w(comma++ >0?',':' ')
.oStr(f,i2).w(':').o(p.getHeaders(f),i2,comment?path+'.'+f:path);
if(comment)w("//").w(f).w("\n").p(i2);}
return comment?w("}}//javax.servlet.http.Part:").oStr(path, ind).w('\n').w(ind):w("}}");}*/

	public Output oFormFlds(te050c.tl.Form p,String ind,String path)throws IOException{
		Field[]a=p.fields();String i2=ind+'\n';
		w("\"name\":").oStr(p.getName(),ind);
		for(Field f:a)
		{	w(',').oStr(f.getName(),i2).w(':')
				.o(p.v(f),ind,comment?path+'.'+f.getName():path);
			if(comment)w("//").w(f.toString()).w("\n").p(i2);
		}return this;}

	public Output oForm(te050c.tl.Form p,String ind,String path)throws IOException{
		if(p instanceof te050c.tl.db.tbl.Tbl)return oDbTbl((te050c.tl.db.tbl.Tbl)p,ind,path);
		if(comment)w("{//TL.Form:").w(p.getClass().toString()).w('\n').p(ind);
		else w('{');
		oFormFlds(p,ind,path);
		return (comment?w("}//TL.Form&cachePath=\"").p(path).w("\"\n").p(ind):w('}'));}

	public Output oDbTbl(te050c.tl.db.tbl.Tbl p,String ind,String path)throws IOException{
		if(comment)w("{//TL.DB.Tbl:pkc=").o(p.pkc())
			.p(':',p.getClass().toString(),"\n",ind);else w('{');
		oFormFlds(p,ind,path);
		return comment?p("}//TL.DB.Tbl&cachePath=\"",path,"\"\n",ind):w('}');}

	public Output oStr(String a,String indentation)throws IOException
	{final boolean m=comment;if(a==null)return w(m?" null //String\n"+indentation:"null");
		w('"');for(int n=a.length(),i=0;i<n;i++)
	{char c=a.charAt(i);if(c=='\\')w('\\').w('\\');
	else if(c=='"')w('\\').w('"');
	else if(c=='\n'){w('\\').w('n');if(m)w("\"\n").p(indentation).w("+\"");}
	else if(c=='\r')w('\\').w('r');
	else if(c=='\t')w('\\').w('t');
	else if(c=='\'')w('\\').w('\'');
	else p(c);}return w('"');}

	public Output oDt(java.util.Date a,String indentation)throws IOException
	{if(a==null)return w(comment?" null //Date\n":"null");
		w("{\"class\":\"Date\",\"time\":").p(a.getTime())
				.w(",\"str\":").oStr(a.toString(),indentation);
		if(comment)w("}//Date\n").p(indentation);else w("}");
		return this;}
	
	/*public Output oThrbl(Throwable x,String indentation)throws IOException //CHANGED 2016.08.17.12.35
	{w("{\"message\":").oStr(x.getMessage(),indentation).w(",\"stackTrace\":");
		try{StringWriter sw=new StringWriter();
			x.printStackTrace(new PrintWriter(sw));
			oStr(sw.toString(),indentation);}catch(Exception ex)
		{TL.tl().log("Json.Output.x("+x+"):",ex);}return w("}");}*/
	
	public Output oThrbl(Throwable x,String indentation)throws IOException
	{w("{\"message\":").oStr(x.getMessage(),indentation).w(",\"stackTrace\":");
		try{//StringWriter sw=new StringWriter(); //CHANGED 2016.08.17.12.35
			x.printStackTrace(new PrintWriter(w));//oStr(sw.toString(),indentation);
		}catch(Exception ex){TL.tl().log("Json.Output.x("+x+"):",ex);}return w("}");}

	public Output oEnumrtn(Enumeration a,String ind,String path)throws IOException
	{final boolean c=comment;
		if(a==null)return c?w(" null //Enumeration\n").p(ind):w("null");
		boolean comma=false;String i2=c?ind+"\t":ind;
		if(c)w("[//Enumeration\n").p(ind);else w("[");
		if(c&&path==null)path="";if(c&&path.length()>0)path+=".";int i=0;
		while(a.hasMoreElements()){if(comma)w(" , ");else comma=true;
			o(a.nextElement(),i2,c?path+(i++):path);}
		return c?w("]//Enumeration&cachePath=\"").p(path).w("\"\n").p(ind):w("]");}

	public Output oItrtr(Iterator a,String ind,String path)throws IOException
	{final boolean c=comment;if(a==null)return c?w(" null //Iterator\n").p(ind):w("null");
		boolean comma=false;String i2=c?ind+"\t":ind;
		if(c){w("[//").p(a.toString()).w(" : Itrtr\n").p(ind);
			if(path==null)path="";if(path.length()>0)path+=".";}
		else w("[");int i=0;
		while(a.hasNext()){if(comma)w(" , ");else comma=true;o(a.next(),i2,c?path+(i++):path);}
		return c?w("]//Iterator&cachePath=\"").p(path).w("\"\n").p(ind):w("]");}

	public Output oArray(Object[]a,String ind,String path)throws IOException
	{final boolean c=comment;
		if(a==null)return c?w(" null //array\n").p(ind):w("null");
		String i2=c?ind+"\t":ind;
		if(c){w("[//array.length=").p(a.length).w("\n").p(ind);
			if(path==null)path="";if(path.length()>0)path+=".";}else w("[");
		for(int i=0;i<a.length;i++){if(i>0)w(" , ");o(a[i],i2,c?path+i:path);}
		return c?w("]//cachePath=\"").p(path).w("\"\n").p(ind):w("]");}

	public Output oarray(Object a,String ind,String path)throws IOException
	{final boolean c=comment;
		if(a==null)return c?w(" null //array\n").p(ind):w("null");
		int n=Array.getLength(a);String i2=c?ind+"\t":ind;
		if(c){w("[//array.length=").p(n).w("\n").p(ind);
			if(path==null)path="";if(path.length()>0)path+=".";}else w("[");
		for(int i=0;i<n;i++){if(i>0)w(" , ");o(Array.get(a,i),i2,c?path+i:path);}
		return c?w("]//cachePath=\"").p(path).w("\"\n").p(ind):w("]");}

	public Output oCollctn(Collection o,String ind,String path)throws IOException
	{if(o==null)return w("null");final boolean c=comment;
		if(c){w("[//").p(o.getClass().getName()).w(":Collection:size=").p(o.size()).w("\n").p(ind);
			if(cache==null&&initCache)cache=new HashMap<Object, String>();
			if(cache!=null)cache.put(o,path);
			if(c&&path==null)path="";if(c&&path.length()>0)path+=".";
		}else w("[");
		Iterator e=o.iterator();int i=0;
		if(e.hasNext()){o(e.next(),ind,c?path+(i++):path);
			while(e.hasNext()){w(",");o(e.next(),ind,c?path+(i++):path);}}
		return c?w("]//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind)
				:w("]");}

	public Output oMap(Map o,String ind,String path) throws IOException
	{if(o==null)return w("null");final boolean c=comment;
		if(c){w("{//").p(o.getClass().getName()).w(":Map\n").p(ind);
			if(cache==null&&initCache)cache=new HashMap<Object, String>();
			if(cache!=null)cache.put(o,path);}else w("{");
		Iterator e=o.keySet().iterator();Object k,v;
		//if(o instanceof Store.Obj)w("uuid:").o(((Store.Obj)o).uuid);
		if(e.hasNext()){k=e.next();v=o.get(k);//if(o instanceof Store.Obj)w(",");
			o(k,ind,c?path+k:path);w(":");o(v,ind,c?path+k:path);}
		while(e.hasNext()){k=e.next();v=o.get(k);w(",");
			o(k,ind,c?path+k:path);w(":");o(v,ind,c?path+k:path);}
		if(c) w("}//")
				.p(o.getClass().getName())
				.w("&cachePath=\"")
				.p(path)
				.w("\"\n")
				.p(ind);else w("}");
		return this;}

	public Output oReq(HttpServletRequest r,String ind,String path)throws IOException
	{final boolean c=comment;try{boolean comma=false,c2;//,d[]
		String k,i2=c?ind+"\t":ind,ct;int j;Enumeration e,f;
		(c?w("{//").p(r.getClass().getName()).w(":HttpServletRequest\n").p(ind):w("{"))
				.w("\"dt\":").oDt(TL.tl().now,i2)//new java.util.Date()
				.w(",\"AuthType\":").o(r.getAuthType(),i2,c?path+".AuthTyp":path)
				.w(",\"CharacterEncoding\":").o(r.getCharacterEncoding(),i2,c?path+".CharacterEncoding":path)
				.w(",\"ContentLength\":").o(r.getContentLength(),i2,c?path+".ContentLength":path)
				.w(",\"ContentType\":").o(ct=r.getContentType(),i2,c?path+".ContentType":path)
				.w(",\"ContextPath\":").o(r.getContextPath(),i2,c?path+".ContextPath":path)
				.w(",\"Method\":").o(r.getMethod(),i2,c?path+".Method":path)
				.w(",\"PathInfo\":").o(r.getPathInfo(),i2,c?path+".PathInfo":path)
				.w(",\"PathTranslated\":").o(r.getPathTranslated(),i2,c?path+".PathTranslated":path)
				.w(",\"Protocol\":").o(r.getProtocol(),i2,c?path+".Protocol":path)
				.w(",\"QueryString\":").o(r.getQueryString(),i2,c?path+".QueryString":path)
				.w(",\"RemoteAddr\":").o(r.getRemoteAddr(),i2,c?path+".RemoteAddr":path)
				.w(",\"RemoteHost\":").o(r.getRemoteHost(),i2,c?path+".RemoteHost":path)
				.w(",\"RemoteUser\":").o(r.getRemoteUser(),i2,c?path+".RemoteUser":path)
				.w(",\"RequestedSessionId\":").o(r.getRequestedSessionId(),i2,c?path+".RequestedSessionId":path)
				.w(",\"RequestURI\":").o(r.getRequestURI(),i2,c?path+".RequestURI":path)
				.w(",\"Scheme\":").o(r.getScheme(),i2,c?path+".Scheme":path)
				.w(",\"UserPrincipal\":").o(r.getUserPrincipal(),i2,c?path+".UserPrincipal":path)
				.w(",\"Secure\":").o(r.isSecure(),i2,c?path+".Secure":path)
				.w(",\"SessionIdFromCookie\":").o(r.isRequestedSessionIdFromCookie(),i2,c?path+".SessionIdFromCookie":path)
				.w(",\"SessionIdFromURL\":").o(r.isRequestedSessionIdFromURL(),i2,c?path+".SessionIdFromURL":path)
				.w(",\"SessionIdValid\":").o(r.isRequestedSessionIdValid(),i2,c?path+".SessionIdValid":path)
				.w(",\"Locales\":").oEnumrtn(r.getLocales(),ind,c?path+".Locales":path)
				.w(",\"Attributes\":{");
		comma=false;
		e=r.getAttributeNames();while(e.hasMoreElements())
			try{k=e.nextElement().toString();if(comma)w(",");else comma=true;
				o(k).w(":").o(r.getAttribute(k),i2,c?path+"."+k:path);
			}catch(Throwable ex){TL.tl().error(ex,"HttpRequestToJsonStr:attrib");}
		w("}, \"Headers\":{");comma=false;e=r.getHeaderNames();
		while(e.hasMoreElements())try
		{k=e.nextElement().toString();
			if(comma)w(",");else comma=true;o(k).w(":[");
			f=r.getHeaders(k);c2=false;j=-1;while(f.hasMoreElements())
		{if(c2)w(",");else c2=true;o(f.nextElement(),i2,c?path+".Headers."+k+"."+(++j):path);}
			w("]");
		}catch(Throwable ex){TL.tl().error(ex,"Json.Output.oReq:Headers");}
		w("}, \"Parameters\":").oMap(r.getParameterMap(),i2,c?path+".Parameters":path)
				.w(",\"Session\":").o(r.getSession(false),i2,c?path+".Session":path)
				.w(", \"Cookies\":").o(r.getCookies(),i2,c?path+".Cookies":path);
		//if(ct!=null&&ct.indexOf("part")!=-1)w(", \"Parts\":").o(r.getParts(),i2,c?path+".Parts":path);
		//AsyncContext =r.getAsyncContext();
		//long =r.getDateHeader(arg0)
		//DispatcherType =r.getDispatcherType()
		//String =r.getLocalAddr()
		//String =r.getLocalName()
		//int =r.getLocalPort()
		//int =r.getRemotePort()
		//RequestDispatcher =r.getRequestDispatcher(String)
		//StringBuffer r.getRequestURL()
		//String r.getServerName()
		//int r.getServerPort()
		//ServletContext =r.getServletContext()
		//String r.getServletPath()
		//boolean r.isAsyncStarted()
		//boolean r.isAsyncSupported()
		//boolean r.isUserInRole(String)
	}catch(Exception ex){TL.tl().error(ex,"Json.Output.oReq:Exception:");}
		if(c)w("}//").p(r.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
		else w("}");
		return this;}

	Output oSession(HttpSession s,String ind,String path)throws IOException
	{final boolean c=comment;try{if(s==null)w("null");else
	{String i2=c?ind+"\t":ind;
		(c?w("{//").p(s.getClass().getName()).w(":HttpSession\n").p(ind):w("{"))
				.w("{\"isNew\":").p(s.isNew()).w(",sid:").oStr(s.getId(),ind)
				.w(",\"CreationTime\":").p(s.getCreationTime())
				.w(",\"MaxInactiveInterval\":").p(s.getMaxInactiveInterval())
				.w(",\"attributes\":{");Enumeration e=s.getAttributeNames();boolean comma=false;
		while(e.hasMoreElements())
		{Object k=e.nextElement().toString();if(comma)w(",");else comma=true;
			o(k,i2).w(":").o(s.getAttribute(String.valueOf(k)),i2,c?path+".Attributes."+k:path);
		}w("}");}}catch(Exception ex){TL.tl().error(ex,"Json.Output.Session:");}
		if(c)w("}//").p(s.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
		else w("}");
		return this;}

	public Output oCookie(Cookie y,String ind,String path)throws IOException
	{final boolean c=comment;try{(c?w("{//")
			.p(y.getClass().getName()).w(":Cookie\n").p(ind):w("{"))
			.w("\"Comment\":").o(y.getComment())
			.w(",\"Domain\":").o(y.getDomain())
			.w(",\"MaxAge\":").p(y.getMaxAge())
			.w(",\"Name\":").o(y.getName())
			.w(",\"Path\":").o(y.getPath())
			.w(",\"Secure\":").p(y.getSecure())
			.w(",\"Version\":").p(y.getVersion())
			.w(",\"Value\":").o(y.getValue());
	}catch(Exception ex){TL.tl().error(ex,"Json.Output.Cookie:");}
		if(c)try{w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
		}catch(Exception ex){TL.tl().error(ex,"Json.Output.Cookie:");}else w("}");
		return this;}

	Output oTL(TL y,String ind,String path)throws IOException
	{final boolean c=comment;try{String i2=c?ind+"\t":ind;
		(c?w("{//").p(y.getClass().getName()).w(":PageContext\n").p(ind):w("{"))
				.w("\"ip\":").o(y.ip,i2,c?path+".ip":path)
				.w(",\"usr\":").o(y.usr,i2,c?path+".usr":path)//.w(",uid:").o(y.uid,i2,c?path+".uid":path)
				.w(",\"ssn\":").o(y.ssn,i2,c?path+".ssn":path)//.w(",sid:").o(y.sid,i2,c?path+".sid":path)
				.w(",\"now\":").o(y.now,i2,c?path+".now":path)
				.w(",\"json\":").o(y.json,i2,c?path+".json":path)
				.w(",\"response\":").o(y.response,i2,c?path+".response":path)
				.w(",\"Request\":").o(y.getRequest(),i2,c?path+".request":path)
				//.w(",\"Session\":").o(y.getSession(false))
				.w(",\"application\":").o(y.getServletContext(),i2,c?path+".application":path)
		//.w(",\"config\":").o(y.req.getServletContext().getServletConfig(),i2,c?path+".config":path)
		//.w(",\"Page\":").o(y.srvlt,i2,c?path+".Page":path)
		//.w(",\"Response\":").o(y.rspns,i2,c?path+".Response":path)
		;
	}catch(Exception ex){TL.tl().error(ex,"Json.Output.oTL:");}
		if(c)try{w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);}
		catch(Exception ex){TL.tl().error(ex,"Json.Output.oTL:closing:");}
		else w("}");
		return this;}

	Output oSC(ServletContext y,String ind,String path)
	{final boolean c=comment;try{String i2=c?ind+"\t":ind;(c?w("{//").p(y.getClass().getName()).w(":ServletContext\n").p(ind):w("{"))
			.w(",\"ContextPath\":").o(y.getContextPath(),i2,c?path+".ContextPath":path)
			.w(",\"MajorVersion\":").o(y.getMajorVersion(),i2,c?path+".MajorVersion":path)
			.w(",\"MinorVersion\":").o(y.getMinorVersion(),i2,c?path+".MinorVersion":path);
		if(c)
			w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
		else w("}");
	}catch(Exception ex){TL.tl().error(ex,"Json.Output.ServletContext:");}
		return this;}

	Output oSCnfg(ServletConfig y,String ind,String path)throws IOException
	{final boolean c=comment;try{if(c)w("{//").p(y.getClass().getName()).w(":ServletConfiguration\n").p(ind);
	else w("{");
		//String getInitParameter(String)
		//Enumeration getInitParameterNames()
		//getServletContext()
		//String getServletName()	.w(",:").o(y.(),i2,c?path+".":path)
	}catch(Exception ex){TL.tl().error(ex,"Json.Output.ServletConfiguration:");}
		return c?w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind)
				:w("}");}

	Output oBean(Object o,String ind,String path)
	{final boolean c=comment;try{String i2=c?ind+"\t":ind,i3=c?i2+"\t":ind;Class z=o.getClass();
		(c?w("{//").p(z.getName()).w(":Bean\n").p(ind):w("{"))
				.w("\"str\":").o(o.toString(),i2,c?path+".":path)
//.w(",:").o(o.(),i2,c?path+".":path)
		;java.lang.reflect.Method[]a=z.getMethods();//added 2015.11.21
		for(java.lang.reflect.Method m:a){String n=m.getName();
			if(n.startsWith("get")&&m//.getParameterCount()
					.getParameterTypes().length==0)
				w("\n").w(i2).w(",").p(n).w(':').o(m.invoke(o), i3, path+'.'+n);}
		if(c)w("}//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind)
				;else w("}");}catch(Exception ex){TL.tl().error(ex,"Json.Output.Bean:");}return this;}

	Output oResultSet(ResultSet o,String ind,String path)
	{final boolean c=comment;try{String i2=c?ind+"\t":ind;
		te050c.tl.db.ItTbl it=new te050c.tl.db.ItTbl(o);
		(c?w("{//").p(o.getClass().getName()).w(":ResultSet\n").p(ind):w("{"))
				.w("\"h\":").oResultSetMetaData(it.row.m,i2,c?path+".h":path)
				.w("\n").p(ind).w(",\"a\":").o(it,i2,c?path+".a":path)
		;if(c)w("}//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind)
				;else w("}");}catch(Exception ex){TL.tl().error(ex,"Json.Output.ResultSet:");}return this;}

	Output oResultSetMetaData(ResultSetMetaData o,String ind,String path)
	{final boolean c=comment;try{String i2=c?ind+"\t":ind;
		int cc=o.getColumnCount();
		if(c)w("[//").p(o.getClass().getName()).w(":ResultSetMetaData\n").p(ind);
		else w("[");
		for(int i=1;i<=cc;i++){
			if(i>1){if(c)w("\n").p(i2).w(",");else w(",");}
			w("{\"name\":").oStr(o.getColumnName( i ),i2)
			.w(",\"label\":").oStr(o.getColumnLabel( i ),i2)
			.w(",\"width\":").p(o.getColumnDisplaySize( i ))
			.w(",\"className\":").oStr(o.getColumnClassName( i ),i2)
			.w(",\"type\":").oStr(o.getColumnTypeName( i ),i2).w("}");
		}//for i<=cc
		if(c)w("]//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
		else w("]");}catch(Exception ex){TL.tl().error(ex,"Json.Output.ResultSetMetaData:");}return this;}

	public Output clrSW(){if(w instanceof StringWriter){((StringWriter)w).getBuffer().setLength(0);}return this;}
	public Output flush() throws IOException{w.flush();return this;}
}//class te050c.tl.json.Output