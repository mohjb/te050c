package te050c.tl.html;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import te050c.tl.TL;

 /** FSM , N.A.key is the current fsm-state , and N.A.val is the xmlns*/
 public class Prsr2 extends N<?>.A<State,String>{
 Html root(){rt=elem();return (Html)rt;}

 te050c.tl.json.Prsr p=new te050c.tl.json.Prsr(){
	@Override public char setEof(){
		key=State.eof;return c='\0';}
	@Override public void nlRC(){
		col=_col=1;row=++_row;}
	 @Override public void incCol(){col=++_col;}
	 @Override public String toString(){return "{c:"+c+'@'+row+','+col+",o:"+o+",buf:"+buff+",lookahead:"+lookahead+'}';}
 };

 static te050c.tl.json.Output tout=new te050c.tl.json.Output();
 static Prsr2 p2;
 /**current and rt are only for debugging*/static N current,rt;N setCurrent(N p){
	Html r=root();
 	return r.nxt=current=p;}

	int jsNestingLevel;
 char quot;
	/**key,val ; used to complete parsing the key then the val, and only then, determine which sub-class of an attrib and instanciate that Attrib */
	Object k;//,v;//v is replaced by p.o

 Prsr2(Html root){root.super(State.txt,IAct.E.serverside.name());p2=this;}

 public Html load(File f){
	long l=(p.f=f).lastModified();
	Html r=root();
	if( p.lm>=l)return r;
	p.lm=l;p.fz=f.length();
	try{p.rdr= new FileReader(f);
		load(p.rdr);
	}catch(Exception ex){ex.printStackTrace();}
	return r;}

 /**main loop of the parser, most of the implementation is in each different State , the state is referenced in instance variable "key"*/
 public Html load(Reader f){
	Html r=root();TL tl=TL.tl();
	try{p.rdr=p.rdr= f;key=State.txt;//p.mode2=true;
		p._col=p._row=row=col=1;//row=1;col=0;//root=new N(html,null,null,1,1,null);
		State ns=null;r.chld=r.clast=null;
		r.parent=r.nxt=r;p.c=' ';
		while(key!=State.eof){//int row=r.row;
			p.nxt();
			ns=key.next( this);
			if(ns!=key){
				key.onExit(tl,    this,ns);
				key=ns.onEnter(tl, this, key);
	}}}catch(Exception ex){TL.tl().error(ex,
		"te050c.tl.html.Frag.load");}
	return r;}

 void markRC(){root().setRC(this);}

 @Override void setRC(){setRC(elem().root());}

 N getCurrent(){N c=null,x=root();c=x==null?x:x.nxt;
	return c;}

 public void la2buff(){StringBuilder t=p.buff;p.buff=p.lookahead;p.lookahead=t;}

 State up(){N n=getCurrent(),p=n.parent;//Html r=root();
	setCurrent(p);//r.nxt=r.nxt.parent;
	return State.txt;}
 
 Prsr2 skip(int n){while(n-->0)p.nxt();
	return this;}//int skip(int n){int c=0;while(n>0){n--;car();c++;}return c;}

 T lookahead(){T[]a=T.values();
	for(T i:a)if(!i.is(T.TClss.SC) //i!=elem&&
	&& p.lookahead(i.toString()))
		return i;
	return T.elem;}

 /**gt=Greater Than char: &gt; 
  * when node is ended, e.g. the gt char >
  * */
 State gt(){//chld
	N n=getCurrent(),x=n.parent;
	if(x.close==n){up();up();}
	else if(n.typ.is(T.TClss.nrce)){root().typ=T.text;return State.txtElem;}
	else if(n.typ.is(T.TClss.Void))up();
	return State.txt;}

 /**checking next char when inside a node`s < ... > 
  * returns nodeWS , or txt ,
  * or null ,usually when an attribName is started
  * */
 State chk(char c){State r=
	Character.isWhitespace(c)?State.nodeWS
		:c=='/'&&p.lookahead("/>",1)?skip(1).up()//leaf(p)
		:c=='>'?gt()
		:null;return r;}

 Object interpretData(String s){
	String[]a=s==null?null:s.split(":");
	Object o=s;
	if(a!=null){
		if(a.length>0){
			o=IAct.E.xmlns.name().equalsIgnoreCase(a[0])
				?IAct.E.xmlns
				:val.equalsIgnoreCase(a[0])
				?IAct.E.serverside
				:a[0];
		}
		if(a.length>1){
			if(o==IAct.E.serverside)
				o= IAct.E.e(a[1]);
			if(!(o instanceof IAct.E && a.length==2))
			{Object[]b=new Object[a.length];
				for(int i=2;i<a.length;i++)b[i]=a[i];
				b[0]=IAct.E.serverside;b[1]=o;o=b;
			}
		}
	}	return o;
	}

 /**instanciate an Attrib based on cases:
	* if k is str
	*		if k is xmlns
	*		else if v is str
	*		else //if v is json
	* else //if k is json
	*	if v is str
	*	else //v is json
	* */
 void attrCompleted(){
	N<?> n=getCurrent();
	String ks=k instanceof String?(String)k:null
		,vs=p.o instanceof String?(String)p.o:null;
	if( ks!=null ){
		int ix=ks.indexOf(':');
		if(ix!=-1){
			String ns=ks.substring(0,ix),nm=ks.substring(ix+1);
			if(val.equalsIgnoreCase(ns))
			{	IAct.E ea=IAct.E.e(nm);
				n.attrParsed(ea!=null?ea:ks, p.o);
				return;
			}
			if( ns.equalsIgnoreCase(IAct.E.xmlns.name()) &&
			  IAct.E.serverside.name().equalsIgnoreCase(vs) )
				{val=nm;return;}
		}
	}n.attrParsed(k, p.o);
 }

@Override public String toString(){
 	try {synchronized (tout){
		tout.clrSW().w("html.Prsr2{p:" )
		.w( String.valueOf(p) )
		.w( ",state:"  )
		.w( String.valueOf(key) )
		.w( ",xmlns:"  )
		.w( val  )
		.w( ",k:"  )
		.w( String.valueOf(k)  )
		.w( ",current:"  )
		.w( String.valueOf( getCurrent() ) )
		.w( '}' );
		root().html(tout, 0);}
	}catch (Exception e){e.printStackTrace();}
	return tout.toStrin_();
 }

 public static void main(String[] args) {
	final String path=//"/Users/moh/gdrive/air/workspace/
"/Users/moh/Google Drive/air/apache-tomcat-8.0.30/webapps/ROOT/EclipseMarsWorkspace/eu059s/WebContent/testPrsr2.html";
	File f=new File(path);
	Html html=new Html();
	Prsr2 p= html.prsr();
	p.load(f);
	html.typ=T.elem;
	System.out.print(p.toString());
}

}//class Prsr2
