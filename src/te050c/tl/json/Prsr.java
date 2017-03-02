/**
 * 
 */
package te050c.tl.json;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import te050c.tl.TL;

/**
 * @author moh
 */
public class Prsr {

 public StringBuilder buff=new StringBuilder() ,lookahead=new StringBuilder();
 public Reader rdr;public File f;public long fz,lm;public Object o;

public String comments=null;
public char c;Map<String,Object>cache=null;

enum Literal{Undefined,Null};//,False,True

public static Object parse(String p)throws Exception{
	Prsr j=new Prsr();j.rdr=new java.io.StringReader(p);return j.parse();}

public static Object parseItem(Reader p)throws Exception{
	Prsr j=new Prsr();j.rdr=p;return j.parseItem();}

public Object load(File f){
	long l=(this.f=f).lastModified();
	if( lm>=l)return o;
	lm=l;fz=f.length();
	try{rdr= new FileReader(f);
		o=parse();
	}catch(Exception ex){}
	return o;}

 /**skip Redundent WhiteSpace*/void skipRWS(){
	boolean b=Character.isWhitespace(c);
	while(b && c!='\0'){
		char x=peek();
		if(b=Character.isWhitespace(x))
			nxt();
	}
 }

void skipRWSx(char...p){skipRWS();
	char x=peek();int i=-1,n=p==null?0:p.length;boolean b=false;
	do{
		if((b=++i<n)&&p[i]==x){
			b=false;nxt();
		}
	}while(b);
 }// boolean chk(){boolean b=Character.isWhitespace(c)||c=='/';while(b && c!='\0'){//Character.isWhitespace(c)||)char x=peek();if(c=='/' &&(lookahead("//") || lookahead("/*"))){	skipWhiteSpace();b=Character.isWhitespace(c);}else if(x=='/' &&(lookahead(x+"//") || lookahead(x+"/*") )){}else{	if(b=Character.isWhitespace(x))nxt();}}return false;}

 public Object parse()throws Exception{
	Object r=c!='\0'?parseItem():null;
	skipWhiteSpace();if(c!='\0')
	{LinkedList l=new LinkedList();l.add(r);
	 while(c!='\0'){
		r=parseItem();
		l.add(r);
	}r=l;}
	return r;
 }

 public Object parseItem()throws Exception
 {Object r=null;int i;skipWhiteSpace();switch(c)
  { case '"':case '\'':case '`':r=extractStringLiteral();break;
	case '0':case '1':case '2':case '3':case '4':
	case '5':case '6':case '7':case '8':case '9':
	case '-':case '+':case '.':r=extractDigits();break;
	case '[':r=extractArray();break;
	case '{':Map m=extractObject();
		r=m==null?null:m.get("class");
		if("date".equals(r)){r=m.get("time");
			r=new Date(((Number)r).longValue());}
		else r=m;break;
	case '(':nxt();skipRWS();//skipWhiteSpace();
		r=parseItem();
		skipWhiteSpace();
		if(c==')')
			nxt();
		else{LinkedList l=new LinkedList();
			l.add(r);
			while(c!=')' && c!='\0'){
				r=parseItem();
				l.add(r);
				skipWhiteSpace();
		}if(c==')')
			nxt();
		r=l;}break;
	default:r=extractIdentifier();
	}skipRWS();//skipWhiteSpace();
	if(comments!=null&&((i=comments.indexOf("cachePath=\""))!=-1
		||(cache!=null&&comments.startsWith("cacheReference"))))
	{	if(i!=-1)
		{	if(cache==null)
				cache=new HashMap<String,Object>();
			int j=comments.indexOf("\"",i+=11);
			cache.put(comments.substring(i,j!=-1?j:comments.length()),r);
		}else 
			r=cache.get(r);
		comments=null;
	}
  return r;}

 public String extractStringLiteral()throws Exception
 {char first=c;nxt();boolean b=c!=first&&c!='\0';
	while(b)
	{if(c=='\\'){nxt();switch(c)
		{case 'n':buff('\n');break;case 't':buff('\t');break;
			case 'r':buff('\r');break;case '0':buff('\0');break;
			case 'x':case 'X':buff( (char)
				java.lang.Integer.parseInt(
					next(2)//p.substring(offset,offset+2)
					,16));nxt();//next();
			break;
			case 'u':
			case 'U':buff( (char)
			java.lang.Integer.parseInt(
				next(4)//p.substring(offset,offset+4)
				,16));//next();next();next();//next();
			break;default:if(c!='\0')buff(c);}}
		else buff(c);
		nxt();b=c!=first&&c!='\0';
	}if(c==first)nxt();return consume();}

 public Object extractIdentifier()
 {while(!Character.isUnicodeIdentifierStart(c))
	{System.err.println("unexpected:"+c+" at row,col="+rc());nxt();return Literal.Null;}
	bNxt();
	while(c!='\0'&&Character.isUnicodeIdentifierPart(c))bNxt();
	String r=consume();
	return "true".equals(r)?new Boolean(true)
		:"false".equals(r)?new Boolean(false)
		:"null".equals(r)?Literal.Null:"undefined".equals(r)?Literal.Undefined:r;}

 public Object extractDigits()
 {if(c=='0')//&&offset+1<len)
	{char c2=peek();if(c2=='x'||c2=='X')
	{nxt();nxt();
		while((c>='A'&&c<='F')
			||(c>='a'&&c<='f')
			||Character.isDigit(c))bNxt();
		String s=consume();
		try{return Long.parseLong(s,16);}
		catch(Exception ex){}return s;}
	}boolean dot=c=='.';
	bNxt();//if(c=='-'||c=='+'||dot)bNxt();else{c=p.charAt(i);}
	while(c!='\0'&&Character.isDigit(c))bNxt();
	if(!dot&&c=='.'){dot=true;bNxt();}
	if(dot){while(c!='\0'&&Character.isDigit(c))bNxt();}
	if(c=='e'||c=='E')
	{dot=false;bNxt();if(c=='-'||c=='+')bNxt();
		while(c!='\0'&&Character.isDigit(c))bNxt();
	}else if(c=='l'||c=='L'||c=='d'||c=='D'||c=='f'||c=='F')bNxt();
	String s=consume();//p.substring(i,offset);
	if(!dot)try{return Long.parseLong(s);}catch(Exception ex){}
	try{return Double.parseDouble(s);}catch(Exception ex){}return s;}

 public List<Object> extractArray()throws Exception
 {if(c!='[')return null;
	nxt();char x=0;
	LinkedList<Object> l=new LinkedList<Object>();
	Object r=null;
	skipWhiteSpace();
	if(c!='\0'&&c!=']')
	{	r=parseItem();
		l.add(r);
	}if(c!='\0'&&c!=']')
		skipRWSx(']',',');//skipRWS();x=peek();if(x==']'||x==',') nxt();//skipWhiteSpace();
	while(c!='\0'&&c!=']')
	{	if(c!=','&&!Character.isWhitespace(c))//throw new IllegalArgumentException
			System.out.println("Array:"+rc()+" expected ','");
		nxt();
		r=parseItem();
		l.add(r);
		skipRWSx(']',',');//skipRWS();x=peek();if(x==']'||x==',')nxt();//skipWhiteSpace();
	}if(c==']')
		nxt();
	 skipRWS();
	return l;}

 public Map<Object,Object> extractObject()throws Exception
 {if(c=='{')nxt();
	else return null;
	skipWhiteSpace();
	HashMap<Object,Object> r=new HashMap<Object,Object>();
	Object k,v;Boolean t=new Boolean(true);
	while(c!='\0'&&c!='}')
	{v=t;
		k=parseItem();//if(c=='"'||c=='\''||c=='`')k=extractStringLiteral();else k=extractIdentifier();
		skipWhiteSpace();//skipRWSx('}',':','=',',');
		if(c==':'||c=='='){//||Character.isWhitespace(c)
			nxt();v=parseItem();skipWhiteSpace();
		}//else if(c==','){nxt();//{{
		if(c!='\0'&&c!='}'){
			if(c!=',')System.out.print(//throw new IllegalArgumentException(
				"Object:"+rc()+" expected '}' or ','");
			nxt();
			skipWhiteSpace();//skipRWSx('}',',');//skipRWS();x=peek();if(x=='}'||x==',')nxt();
		}r.put(k,v);
	}if(c=='}')nxt();skipRWS();
	return r;}

 public void skipWhiteSpace()
 {boolean b=false;do{
	while(b=Character.isWhitespace(c))nxt();
	b=b||(c=='/'&&skipComments());}while(b);}

 public boolean skipComments()
 {char c2=peek();if(c2=='/'||c2=='*'){nxt();nxt();
	StringBuilder b=new StringBuilder();if(c2=='/')
	{while(c!='\0'&&c!='\n'&&c!='\r')bNxt();
		if(c=='\n'||c=='\r'){nxt();if(c=='\n'||c=='\r')nxt();}
	}else
	{while(c!='\0'&&c2!='/'){bNxt();if(c=='*')c2=peek();}
		if(c=='*'&&c2=='/'){b.deleteCharAt(b.length()-1);nxt();nxt();}
	}comments=b.toString();return true;}return false;}

 /**read a char from the rdr*/
 char read(){
	int h=-1;try{h=rdr.read();}
	catch(Exception ex){TL.tl().error(ex, "te050c.tl.json.prsr.read");}
	char c= h==-1?'\0':(char)h;
	return c;}

 public char peek(){
	char c='\0';
	int n=lookahead.length();
	if(n<1){
		c=read();
		lookahead.append(c);}
	else c=lookahead.charAt(0);
	return c;}

 public int _row,_col;String rc(){return "("+_row+','+_col+')';}
 public void nlRC(){_col=1;_row++;}public void incCol(){_col++;}
 //boolean eof,mode2=false;
 public char setEof(){return c='\0';}
 /**update the instance-vars (if needed):c,row,col,eof*/
 public char nxt(char h){
	if(h=='\0'||h==-1||c=='\0')return setEof();
	//if(c=='\0')return setEof();//c='\0';
	else c=h;
	if(c=='\n')
		nlRC();
	else incCol();
	return c;}

 public char bNxt(){buff();return nxt();}
 public char nxt(){
	char h='\0';
	if(c=='\0')return setEof();//=h;
	if(lookahead.length()>0){
		h=lookahead.charAt(0);
		lookahead.deleteCharAt(0);
	}else h=read();
	c=nxt(h);
	return c;}

 /**this method works differently than next(), in particular how char c is read and buffered*/
 public String next(int n)
 {String old=consume(),retVal=null;while(n-->0)buff(nxt());retVal=consume();buff.append(old);return retVal;}

 public char buff(){return buff(c);}
 char buff(char p){buff.append(p);return p;}
 public String consume(){String s=buff.toString();buff.replace(0, buff.length(), "");return s;}

 public boolean lookahead(String p,int offset){
	int i=0,pn=p.length()-offset,ln=lookahead.length();
	boolean b=false;char c=0,h=0;if(pn>0)
	do{h=p.charAt(i+offset);
		if(i<ln)
			c=lookahead.charAt(i);
		else{
			c=read();
			lookahead.append(c);
		}
	}while( (b=(c==h || 
		Character.toUpperCase(c)==
		Character.toUpperCase(h))
		)&& (++i)<pn );
	return b;}

 public boolean lookahead(String p){return lookahead(p,0);}

}//te050c.tl.json.Prsr
