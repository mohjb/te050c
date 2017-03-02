package te050c.tl.html.frag;

import te050c.tl.TL;
import te050c.app.App;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Html {
 StringBuilder b=new StringBuilder();
 public File f;FileInputStream fis;
 long fz,lm;State state=State.txt;
 
 char car(){
	if(state==State.eof)return '\0';
	int h=-1;try{h=fis.read();}
	catch(Exception ex){TL.tl().error(ex, "car");}
	boolean eof=false;
	char c=(eof=(h==-1))?'\0':(char)h;
	if(eof)state=State.eof;
	else if(c=='\n')
		root.row++;
	return c;
 }

 String consume(){String s=b.toString();b.replace(0, b.length(), "");return s;}

 enum State{txt//,tagName
	 ,tag 
	 , braceOpen, braceClose
	 ,name
	 ,eof
 }

 State next(){
  char c;State old=state;
  switch(state){
	case txt:while(state==old)
	{	c=car();
		if(c=='<')
		{	c=car();
			if(c=='%')
				state=State.tag;
			else
			{	b.append('<');
				while(c=='<')
				{	c=car();
					if(c=='%')
						state=State.tag;
					else if(c=='<')
						b.append('<');
				}if( state==old && c!='<' )
					b.append(c);
			}
		}else b.append(c);
	}break;
	case tag:while(state==old)
	{	c=car();
		if(c=='%')
		{	c=car();
		 if(c=='{')state=State.braceOpen;
		 else if(c=='}')state=State.braceClose;
		 else if(c=='>')
			state=State.txt;
		 else
		 {	b.append('%');
			while(c=='%')
			{	c=car();
				if(c=='{')state=State.braceOpen;
				else if(c=='}')state=State.braceClose;
				else if(c=='>')
					state=State.txt;
				else if(c=='%')
					b.append('%');
			}if(state==old){
				if(c=='{')state=State.braceOpen;
				else if(c=='}')state=State.braceClose;
				else b.append(c);
			}
		 }
		}
		else if(c=='{')state=State.braceOpen;
		else if(c=='}')state=State.braceClose;
		else b.append(c);
	}break;

	case name:while(state==old)
	{	c=car();
		if(c=='%')
		{	c=car();
		 if(c=='{'){b.append('%');state=State.braceOpen;}
		 else if(c=='}'){b.append('%');state=State.braceClose;}
		 else if(c=='>')
			state=State.txt;
		 else
		 {	b.append('%');
			while(c=='%')
			{	c=car();
				if(c=='{'){b.append('%');state=State.braceOpen;}
				else if(c=='}'){b.append('%');state=State.braceClose;}
				else if(c=='>')
					state=State.txt;
				else if(c=='%')
					b.append('%');
			}
			if(state==old && c!='%'){
				if(c=='{')state=State.braceOpen;
				else if(c=='}')state=State.braceClose;
				else b.append(c);
			}
		 }
		}
		else if(c=='{')state=State.braceOpen;
		else if(c=='}')state=State.braceClose;
		else b.append(c);
	}break;
  }//switch
  return state;}

 I current(){
	N n=root.prv;
	I p=n instanceof I?(I)n:n.prnt;
	return p;}

public I load(){
	long l=f.lastModified();
	if( lm>=l)return root;
	lm=l;fz=f.length();
	try{fis= new FileInputStream(f);
	state=State.txt;
		root=new I(null);//root.prv=root.prnt=root;
		root.nxt=x0=null;
		root.row=1;
		State old=state=State.txt;
		while(state!=State.eof){
			old=state;int r=root.row;next();
			switch( old ){
			case txt:{String d=consume();if(d.trim().length()>0)
				new X(d).row=r;}break;
			case name:current().t=consume().trim();break;
			}
			switch(state){
			case braceOpen:
				new I("");
				state=State.name;
				break;
			case braceClose:
				I i=current();
				root.prv=i.prnt;
				state=State.tag;
				break;
			}
		}
	}catch(Exception ex){TL.tl().error(ex,
		"te050c.tl.html.Frag.load");}
	return root;}
 
 /**in root:
	* row is the row counter
	* prv is last
	* nxt is last X
  * */
 public I root;
 public X x0;

/**N Node in tree of fragments */
public abstract class N{
	public N prv,nxt;
	public I prnt;
	public int row;//iBegin,iEnd,
	public String t;
	Html html(){return Html.this;}

	/**set the parent of this from root .prv( which is last/ most recent leaf)*/
	void init(String data){
		if(root==null)
			prv=root=prnt=(I)this;
		else{row=root.row;
			t=data;
			prnt=current();
			if(prnt.chld==null)
				prnt.chld=prnt.last=root.prv=this;
			else{
				prv=prnt.last;
				prv.nxt=prnt.last=this;
			}
		}
		if(this instanceof X)
		{X x=(X)this;
		 if(x0==null)
			root.nxt=x0=x;
		 else {x.xp=(X)root.nxt;
			x.xp.xn=x;
			root.nxt=x;
		 }
		}
		root.prv=this;
	}
}//abstract class N

//Html-output-teXt
public class X extends N{X xp,xn;
 X(String data){init(data);}
}//class X

/**Internal Node , <% %> */
 public class I extends N{

	public N chld,last;

 I(String p){init(p);}

 I byId(String p){
	if(t!=null && t.equals(p))//id!=null&&id.equals(p))
		return this;
	N n=chld;I i;while(n!=null){
		i=n instanceof I?((I)n).byId(p):null;
		if(i!=null)
			return i;
		n=n.nxt;
	}return null;}

}//class I

 public static Html load(File f){
	Html g=new Html();g.f=f;
	g.load();
	return g;}


 public I byId(String p){
	N n=root;I i;while(n!=null){
		i=n instanceof I?((I)n).byId(p):null;
		if(i!=null)
			return i;
		n=n.nxt;
	}return null;}

 void serv(TL tl)throws Exception{
	N n=root.chld;while(n!=null){serv(tl,n);n=n.nxt;}}

 void serv(TL tl,N n)throws Exception{
	if(n instanceof X)tl.o(n.t);else if(n instanceof I){
		I x=(I)n;
		String t=n.t.trim();
		if(t.indexOf(' ')!=-1){
			String[]a=t.split(" ");t=a[0].trim();
			if("if".equals(t));else
			if("switch".equals(t));
		}
		else if(t.indexOf('.')!=-1){
			String[]a=t.split(".");t=a[0].trim();
			if("e".equals(t));
			if("Prm".equals(t));
		}
		else if(t.indexOf('(')!=-1){}
		else if(t.startsWith("@")){}
		else if(t.startsWith("$")){}
		else if(t.startsWith("#")){}
		else
		//if(t.startsWith("if")){}else
		//if(t.startsWith("e")){}else
		//if(t.startsWith("Prm")){}else
		//if(t.startsWith("switch")){}else
				;
 }}

 public static void main(String[]args)
 {try{File f=new File("/Users/moh/gdrive/air/workspace/eu059s/WebContent/testHtmlFrag.html");
	 Html h=Html.load(f);
	 if(h!=null)
		 ;
 }catch(Exception ex){ex.printStackTrace();}}
 
 public static class Eval{
 enum C{If("if")//each should be preceded with &
	,Else("else")
	,eqeq("==")
	,lt("<")
	,le("<=")
	,gt(">")
	,ge(">=")
	,and("&&")
	,or("||")
	,semicolon(";")//,While("while"),eq("="),Db() 	,oPrn("(")	,cPrn(")")
	;
	String t;C(String p){t=p;}
 }//enum C
 Object evalAttrib(TL tl,String p){
	Object o=tl.r(p);
	if(o==null)
		o=tl.s(p);
	else if(o==null)
		o=tl.a(p);
	return o;
 }

 Object evalMmbr(TL tl,Object p,String n)throws Exception{
	Class c=p instanceof Class?(Class)p:p.getClass();
	if(c.isEnum())//else if(c.isArray()){} c.isInterface() c.isAnnotation()
	{//tl.logo("moh.tl.html.Html.evalMmbr:isEnum:(clss,param,name)=",c,p,n);
		Object[]a=c.getEnumConstants();
		for(Object i:a)if(n.equals(i)||n.equals(i.toString()))
			return i;
	}
	Field f=c.getDeclaredField(n);
	Object o=f==null?f:f.get(p);
	if(o==null){Class[]a=c.getDeclaredClasses();
		for(Class i:a)if(i.getName().equals(n))
			return i;
	}
	return o;
 }

 Object evalDots(TL tl,Object p,String[]a,int ix)throws Exception{
	int n=a.length;String t=ix<n?a[ix].trim():null;
	if(ix==0&&p==null)p=App.app(tl);
	Object x=t==null?t:evalMmbr(tl,p,t),o=null;
	if(ix+1>=n)
		return x;else if(x!=null)
		o=evalDots(tl,x,a,ix+1);
	return o;
 }

 Object evalExpr(TL tl,N n,Object[]a,int ix){return null;}

 boolean evalBool(TL tl,N n,String[]a,int ix){return false;}

 Object evalStr(TL tl,N n,String t) throws Exception{
	if(t.indexOf(' ')!=-1){
		String[]a=t.split(" ");t=a[0].trim();
		if("if".equals(t));
		else if("while".equals(t));
		else if("set".equals(t));
		else if("switch".equals(t));
	}
	else if(t.indexOf('.')!=-1){
		String[]a=t.split(".");t=a[0].trim();
		if("e".equals(t));// tl TL DB e
		else if("Prm".equals(t));
		else if("DB".equals(t));
		else if("DB".equals(t));
		else{ return evalDots(tl,null,a,0);} 
	}
	else if(t.startsWith("@")){
		App e=App.app(tl);
		Class c=e.getClass();
		Method[]a=c.getMethods();
		for(Method i:a)if(t.substring(1).trim().equals(i.getName()))
			return i.invoke(e, tl,n);
	}
	else if(t.startsWith("$")){}
	else if(t.startsWith("#")){}
/* r s a
file inclusion
map/json db-json 
db-proto-html-form 
html-form 
html-form-dbt-mapping 
html-form-db-master-detail
html-form dbt-json mapping
html-form-pojo-mapping 
html-form-javaBean-mapping
html-form-select lookup mapping
translation declaration
translation invocation
*/
	else
	//else if(t.indexOf('(')!=-1){}
	//if(t.startsWith("if")){}else
	//if(t.startsWith("e")){}else
	//if(t.startsWith("Prm")){}else
	//if(t.startsWith("switch")){}else
	{
		
	}
	return null;}

 }//class Eval
}//class Html
