package te050c.tl.json;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Parser {
	public String p,comments=null;
	public int offset,len,row,col;
	public char c;Map<String,Object>cache=null;
	public final static Object NULL="null";

	public static Object parse(HttpServletRequest req)throws Exception{return parse(servletRequest_content2str(req));}

	public static Object parse(String p)throws Exception{Parser j=new Parser(p);return j.parse();}

	public static String servletRequest_content2str(HttpServletRequest req)throws Exception
	{int n=req.getContentLength(),i=0;byte[]ba;if(n<=0)return"{}";ba=new byte[n];java.io.InputStream is=req
			.getInputStream();while(n>0&&i>=0){i=is.read(ba,i,n);n-=i;}is.close();return new String(ba,"utf8");}

	public Parser(String p){init(p);}
	public void init(String p){this.p=p;offset=-1;len=p.length();row=col=1;c=peek();offset++;}

	public char peek(){return (offset+1<len)?p.charAt(offset+1):'\0';}

	public char next()
	{char c2=peek();if(c2=='\0'){if(offset<len)offset++;return c=c2;}
		if(c=='\n'||c=='\r'){row++;col=1;
			if(c!=c2&&(c2=='\n'||c2=='\r'))offset++;c=peek();offset++;}
		else{col++;offset++;c=c2;}return c;}

	public Object parse()throws Exception{Object r=null;while(c!='\0')r=parseItem();return r;}

	public Object parseItem()throws Exception
	{Object r=null;int i;skipWhiteSpace();switch(c)
	{case '"':case '\'':case '`':r=extractStringLiteral();break;
		case '0':case '1':case '2':case '3':case '4':
		case '5':case '6':case '7':case '8':case '9':
		case '-':case '+':case '.':r=extractDigits();break;
		case '[':r=extractArray();break;
		case '{':Map m=extractObject();
			r=m==null?null:m.get("class");
			if("date".equals(r)){r=m.get("time");
				r=new Date(((Number)r).longValue());}
			else r=m;break;
		case '(':next();skipWhiteSpace();r=parseItem();
			skipWhiteSpace();if(c==')')next();break;
		default:r=extractIdentifier();}skipWhiteSpace();
		if(comments!=null&&((i=comments.indexOf("cachePath=\""))!=-1
				||(cache!=null&&comments.startsWith("cacheReference"))))
		{if(i!=-1){if(cache==null)cache=new HashMap<String,Object>();int j=comments.indexOf
				("\"",i+=11);cache.put(comments.substring(i,j!=-1?j:comments.length()),r);}
		else r=cache.get(r);comments=null;}return r;}

	public void skipWhiteSpace()
	{boolean b=false;do{b=c==' '||c=='\t'||c=='\n'||c=='\r';
		while(c==' '||c=='\t'||c=='\n'||c=='\r')next();
		b=b||(c=='/'&&skipComments());}while(b);}

	public boolean skipComments()
	{char c2=peek();if(c2=='/'||c2=='*'){next();next();StringBuilder b=new StringBuilder();if(c2=='/')
	{while(c!='\0'&&c!='\n'&&c!='\r'){b.append(c);next();}
		if(c=='\n'||c=='\r'){next();if(c=='\n'||c=='\r')next();}
	}else
	{while(c!='\0'&&c2!='/'){b.append(c);next();if(c=='*')c2=peek();}
		if(c=='*'&&c2=='/'){b.deleteCharAt(b.length()-1);next();next();}
	}comments=b.toString();return true;}return false;}

	public String extractStringLiteral()throws Exception
	{char first=c;next();boolean b=c!=first&&c!='\0';
		StringBuilder r=new StringBuilder();while(b)
	{if(c=='\\'){next();switch(c)
	{case 'n':r.append('\n');break;case 't':r.append('\t');break;
		case 'r':r.append('\r');break;case '0':r.append('\0');break;
		case 'x':case 'X':next();r.append( (char)
			java.lang.Integer.parseInt(
					p.substring(offset,offset+2
					),16));next();//next();
		break;
		case 'u':
		case 'U':
			next();r.append( (char)
				java.lang.Integer.parseInt(
						p.substring(offset,offset+4
						),16));next();next();next();//next();
			break;default:if(c!='\0')r.append(c);}}
	else r.append(c);
		next();b=c!=first&&c!='\0';
	}if(c==first)next();return r.toString();}

	public Object extractIdentifier()
	{int i=offset;
		while(!Character.isUnicodeIdentifierStart(c))
		{System.err.println("unexpected:"+c+" at row="+row+", col="+col);next();return null;}
		next();
		while(c!='\0'&&Character.isUnicodeIdentifierPart(c))next();
		String r=p.substring(i,offset);
		return "true".equals(r)?new Boolean(true)
				:"false".equals(r)?new Boolean(false)
				:"null".equals(r)||"undefined".equals(r)?NULL:r;}

	public Object extractDigits()
	{int i=offset,iRow=row,iCol=col;boolean dot=c=='.';
		if(c=='0'&&offset+1<len)
		{char c2=peek();if(c2=='x'||c2=='X')
		{i+=2;next();next();
			while((c>='A'&&c<='F')
					||(c>='a'&&c<='f')
					||Character.isDigit(c))next();
			String s=p.substring(i,offset);
			try{return Long.parseLong(s,16);}
			catch(Exception ex){}return s;}}
		if(c=='-'||c=='+'||dot)next();
		else{offset=i;row=iRow;col=iCol;c=p.charAt(i);}
		while(c!='\0'&&Character.isDigit(c))next();
		if(!dot&&c=='.'){dot=true;next();}
		if(dot){while(c!='\0'&&Character.isDigit(c))next();}
		if(c=='e'||c=='E')
		{dot=false;next();if(c=='-'||c=='+')next();
			while(c!='\0'&&Character.isDigit(c))next();
		}else if(c=='l'||c=='L'||c=='d'||c=='D'||c=='f'||c=='F')next();
		String s=p.substring(i,offset);
		if(!dot)try{return Long.parseLong(s);}catch(Exception ex){}
		try{return Double.parseDouble(s);}catch(Exception ex){}return s;}

	public List<Object> extractArray()throws Exception
	{if(c!='[')return null;next();LinkedList<Object> r=new LinkedList<Object>();skipWhiteSpace();
		if(c!='\0'&&c!=']')r.add(parseItem());while(c!='\0'&&c!=']')
	{if(c!=',')throw new IllegalArgumentException("Array:("+row+","+col+") expected ','");
		next();r.add(parseItem());}if(c==']')next();return r;}

	public Map<Object,Object> extractObject()throws Exception
	{if(c=='{')next();else return null;skipWhiteSpace();HashMap<Object,Object> r=new HashMap<Object,Object>();
		Object k,v;Boolean t=new Boolean(true);while(c!='\0'&&c!='}')
	{v=t;if(c=='"'||c=='\''||c=='`')k=extractStringLiteral();else k=extractIdentifier();
		skipWhiteSpace();if(c==':'||c=='='){next();v=parseItem();}//else skipWhiteSpace();//{{
		if(c!='\0'&&c!='}'){if(c!=',')throw new IllegalArgumentException(
				"Object:("+row+","+col+") expected '}' or ','");next();skipWhiteSpace();
		}r.put(k,v);}if(c=='}')next();return r;}
}//class Json.Parser