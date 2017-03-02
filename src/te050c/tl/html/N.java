package te050c.tl.html;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
//import java.util.Map;

import te050c.tl.TL;
//import te050c.tl.html.IAct.E;
import te050c.tl.json.Output;

/**html Node, server-side DOM as a template*/
public class N<Nm> implements IAct{

 public void act(Html root){
	
 }

public E eAct(){IAct.E e=data instanceof IAct.E?(IAct.E)data:null;return e;}

class A<K,V> implements IAct {//Attribute

 A prv,nxt;K key;V val;int row,col;boolean vq;
 N elem(){return N.this;}

 A(K k,V v){key=k==null?null:k;val=v;setRC();append(this);}

 @Override public String toString(){
	return val!=null?(""+key+'@'+row+','+col+"=\""+val+'"'):key==null?null:key.toString()+'@'+row+','+col;}

 public Output html(Output b) throws IOException{
	b.w(key==null?null:key.toString());
	if(val!=null)b.w("=\"").w(val.toString()).w("\"");
	return b;}

 void setRC(){setRC(elem().root());}

 void setRC(N p){
	row= p.row;
	col= p.col;}

 void setRC(A p){
	row= p.row;
	col= p.col;}

 public void act(Html engine){}
 public E eAct(){IAct.E e=key instanceof IAct.E?(IAct.E)key:null;return e;}

}//class Att

// final Html h;
 T typ;
 N parent,prv,nxt
 ,chld,clast,close;
 A a1,al;
 int row,col;
 Nm data;
 Html root(){N n=parent;while(n.parent!=n )n=n.parent;return (Html)n;}

 int lvl(){return parent==null?0:parent.lvl()+1;}

 void setRC(){setRC(root());}

 void setRC(N p){
	row= p.row;
	col= p.col;}

 void setRC(A p){
	row= p.row;
	col= p.col;}

 void append(A c){
	if(a1==null)a1=al=c;
	else{c.prv=al;al.nxt=c;al=c;}}

 /**set this parent, taking into consideration the diff cases of parent.child and parent.clast*/
 void parent(N p){
	if(typ==T.eClose){parent=p;return;}
	if(typ==T.option){
		N x=p;while(x!=null&& x.typ!=T.select && x.typ!=T.option){x=x.parent;}
		if(x!=null&&x.typ==T.option)p=x.parent;
	}else if(typ.lvl()>0&&typ!=T.table){
		//the only allowed parent is one of the following: tr or table or thead or tbody or tfoot
		N x=p;
		while(x!=null&&x
		.typ.lvl()<typ.lvl())
			x=x.parent;
		if(x!=null)p=x;
	}
	parent=p;
	if(p.chld==null)p.chld=p.clast=this;
	else p.clast=(prv=p.clast).nxt=this;}

 N(T t,N p,Nm d){
	typ=t;parent=p!=null?p:this;data=d;setRC();
	if(p!=null)
	{if(p.typ.elem()||p instanceof Html)
		parent(p);
	 else if(p.parent!=null)
		parent(p.parent);
	 else
		(prv=p).nxt=this;}
	}

 N<String> newChild(T p,String d){N<String> n=new N<String>(p,this,d);return n;}
 N<Object> newChld(T p,Object d){N<Object> n=new N<Object>(p,this,d);return n;}

/* <K,V> A<K,V> newAttrib(K k,Prsr2 p){
	String s=k.toString();int i=s.indexOf(':');
	if(i!=-1){
		String n=s.substring(i+1);
		s=s.substring(0, i);
		if(IAct.E.xmlns.name().equalsIgnoreCase(n))
			 p.val=s;else
		if(p.val.toString().equalsIgnoreCase(n)){
			Html.Atr a=root().new Atr(null,null);
			try{a.atr=IAct.E.valueOf(s);}
			catch(Exception ex){
				IAct.E[]x=IAct.E.values();
				for(IAct.E e:x)
					if(s.equalsIgnoreCase(e.name()))
					{a.atr=e;break;}}
			return a;
		}}V v=null;
	A a=new A<K,V>(k,v);return a;}*/

 <K,V> A<K,V> newAttrib(K k,V v){
	A<K,V>a=new A<K,V>(k,v);
	return a;}


 /**instanciate an Attrib based on cases:
	 * if k is str
	 * 		if k is xmlns
	 * 		else if v is str
	 * 		else //if v is json
	 * else //if k is json
	 * 	if v is str
	 * 	else //v is json
	 * */
 A attrParsed(Object key,Object val){//TODO: implement all cases , TODO: call attrCompleted from the associate States
	A attr=null;
	String vs=val instanceof String?(String)val:null;
	if(key instanceof IAct.E)
	{IAct.E ea=(IAct.E)key;
		 attr=vs==null
			?new A<IAct.E,Object>(ea,val)
			:new A<IAct.E,String>(ea,vs);
		 return attr;
	}
	String ks=key instanceof String?(String)key:null;
	if(ks!=null ){
		attr=vs!=null
			?new A<String,String>(ks, vs)
			:new A<String,Object>(ks, val);
	}
	else//k is json
		attr=vs==null
			?new A<Object,Object>(key, val)
			:new A<Object,String>(key, vs);
	return attr;
 }

 @Override public String toString(){return String.valueOf(typ)+'@'+row+','+col+':'+data;}

 LinkedList<N>elemsName(String n,LinkedList<N>b){
	N p=this;while(p!=null){
	if(p.typ.elem()//&&Parser.is(n,p.data)
			)//isi(n,ix+1,ik)
		(b!=null?b:(b=new LinkedList<N>())).add(p);
	if(p.chld!=null)b=p.chld.elemsName(n, b);
	//if(nxt !=null)b=nxt .elemsName(n, b);
	p=p.nxt;}return b;}

 LinkedList<N>elemsAtt(String n,String v,LinkedList<N>b){
	 N p=this;while(p!=null){
		 if(p.typ.elem()&&v.equals(p.att(n)))
		(b!=null?b:(b=new LinkedList<N>())).add(p);
	if(p.chld!=null)b=p.chld.elemsAtt(n, v,b);
	//if(nxt !=null)b=nxt .elemsAtt(n,v, b);
	p=p.nxt;}return b;}

 N elemId(String n){
	if(typ.elem()&&n.equals(att("id")))
		return this;
	N x=nxt==null?null:nxt.elemId(n);
	if(x==null&&chld!=null)x=chld.elemId(n);
	return x;}


A attrib(int i){A p=a1;while(--i>0&&p!=null)p=p.nxt;return p;}
N child(int i){N p=chld;while(--i>0&&p!=null)p=p.nxt;return p;}

N childElem(int i){N p=chld;
	p=p==null?null:p.typ.elem()?p:p.elemNxt();
	while(--i>0&&p!=null)p=p.elemNxt();return p;}

N elemNxt(){return nxt==null?nxt:nxt.typ.elem()?nxt:nxt.elemNxt();}
N elemPrv(){return prv==null?prv:prv.typ.elem()?prv:prv.elemPrv();}
N elemChld(){return chld==null?null:chld.typ.elem()?chld:chld.elemNxt();}
N elemLast(){return clast==null?null:clast.typ.elem()?clast:clast.elemPrv();}

int countChildren(){int i=0;N p=chld;
	while(p!=null){i++;p=p.nxt;}return i;}

int countChildrenElems(){
	int i=0;N p=chld;
	while(p!=null){
		if(p.typ.elem())i++;p=p.nxt;}
	return i;}

int countAttribs(){int i=0;A p=a1;
	while(p!=null){i++;p=p.nxt;}return i;}

 //int countActAtrb(){return 0;}IAct.E actAtrb(int p){return null;}

IAct findAct(IAct.E n){
	if(isNodeAct() && data==n)return this;
	A p=(A)afterAct(this);
	while(p!=null){
		if(p.key == n)
			return p;
		p=p.nxt;
	}return null;}

 int countAct(){
	boolean b=isNodeAct();
	int r=b?1:0;
	IAct i=b?this:afterAct(this);
	while(i!=null){r++;
		i=afterAct(i);}
	return r;}

 boolean isNodeAct(){
	IAct.E e=eAct();
	return e!=null;}

 Object[] acts(){
	LinkedList<Object>l=null;
	if(data instanceof IAct.E)(l=new LinkedList<>()).add(this);
	A p=a1;while(p!=null){
		if(p.key instanceof IAct.E)
			(l!=null?l:(l=new LinkedList<>())).add(p);
		p=p.nxt;}
	Object[]a=new Object[l.size()];l.toArray(a);
	return a;}

 IAct afterAct(IAct x){
	if(x instanceof N){
		if(a1==null)
			return null;
		else if(a1.key instanceof IAct.E)
			return a1;
		else x=a1;}
	A p=x instanceof A?(A)x:null;
	while(p!=null){
		if(p.key instanceof IAct.E)
			return p;
		p=p.nxt;}
	return null;}

 class ItrtrAct implements Iterator<IAct>,Iterable<IAct>{
  IAct p;public ItrtrAct(){
	 if(isNodeAct())p=N.this;else p=afterAct(N.this);}
  @Override public boolean hasNext() {return p!=null;}
  @Override public IAct next(){ IAct x=p;p=p==null?p:afterAct(p);return x;}
  @Override public void remove() {}
  public Iterator<IAct> iterator() {return this;}}

 String att(String n){A p=attr(n);return p==null?null:p.val==null?null:p.val.toString();}

 A attr(String n){
	A p=a1;
	while(p!=null){
		if(p.key.equals(n))
			return p;
		p=p.nxt;
	}return null;}

 /**Iterator over all children elements*/
 ItrtrE children(){return new ItrtrE(chld);}
 class ItrtrE implements Iterator<N>,Iterable<N>{
  N p;public ItrtrE(N x){
	if((p=x)==null)return;T t=p.typ;
	if(p==root())p=p.elemChld();else //t==TT.doc
		if(!t.elem())p=p.elemNxt();}
@Override public boolean hasNext() {return p!=null;}
@Override public N next(){N x=p;p=p.elemNxt();return x;}
@Override public void remove() {}
public Iterator<N> iterator() {return this;}}

/**Iterator over all children nodes, even if they aren't elements*/
Ichldrn chldrn(){return new Ichldrn(chld);}
class Ichldrn implements Iterator<N>,Iterable<N>{
	N p;public Ichldrn (N x){p=x;}
	@Override public boolean hasNext() {return p!=null;}
	@Override public N next(){N x=p;p=p.nxt;return x;}
	@Override public void remove() {}
	public Iterator<N> iterator() {return this;}}

/**Iterator over all attributes, in the same order from the source*/
Iattribs attribs(){return new Iattribs(a1);}
class Iattribs implements Iterator<A>,Iterable<A>{
	A p;public Iattribs(N.A x){p=x;}
	@Override public boolean hasNext(){return p!=null;}
	@Override public A next(){A x=p;p=p.nxt;return x;}
	@Override public void remove() {}
	public Iterator<A> iterator() {return this;}}


/**Iterator of html-Elements Breadth-First traversal over all elements in the sub-tree of the parameter of the constructor*/
EBFT traverseEBF(){return new EBFT(this);}//Breadth First traversal
//static 
 class EBFT implements Iterator<N>,Iterable<N>{
	java.util.LinkedList<N>x=new 
	java.util.LinkedList<N>();
	public EBFT(N p){
		if(p==null)return;
		T t=p.typ;
		if(!t.elem())
			p=p.elemNxt();
		if(p!=null)x.add(p);}
	@Override public boolean hasNext() {return x.size()>0;}
	@Override public N next(){
		 N p=x.removeFirst()
		,q=p==null?p:p.elemChld();
		 while(q!=null){
			 x.add(q);q=q.elemNxt();}
		 return p;}
	@Override public void remove() {}
	@Override public Iterator<N> iterator() {return this;}
 }//class EBFT*/

 Output nl (Output b,int ind)throws IOException{return tab(b.w('\n'),ind);}
 Output tab(Output b,int tab)throws IOException{for(;tab>0;tab--)b.w('\t');return b;}


/**write to the param-writer from index x to index k
	* @throws IOException */
 Output html(Output b,int tab) throws IOException{
	if(typ==T.text)return b.o(data);//String.valueOf(
	if(typ==T.cdata||typ==T.cmnt)
		return b.p(typ.s1,data,typ.s2);
	 A id=attr("id");
	 htmlHead(b);if(close==this||typ.leaf())return b;
	N p=chld;
	while(p!=null){
		if(p.typ==T.text)
			b.w(String.valueOf(data));
		else p.html(//m,
			p.prv==null||p.prv.typ!=T.text?
			nl(b,tab+1)
			:b,tab+1);
		p=p.nxt;}
	nl(b,tab);
	if(typ.elem() && !typ.is(T.TClss.Void))b.p("</",data,">");
	return b;
 }//o

 public Output htmlHead(Output b) throws IOException
 {	if(typ.sc()){
		if(typ.s1!=null)
			b.w(typ.s1);
		else b.w("<")
			.w(typ.s1);}
	else if(typ.elem()){b.w("<");
		if(data instanceof String)
			b.w((String)data);
		else b.o(data);//String.valueOf(
		if(a1!=null)htmlAtt(b);
		return b.w(close==this||typ.leaf()?"/>":">");//close==null||
 	}else
 		b.w(String.valueOf(data));
	 return b;}

 public Output htmlAtt(Output b) throws IOException{
	A p=a1;while(p!=null){
		p.html(b.w(" "));
		p=p.nxt;}return b;}

}//class html Node
