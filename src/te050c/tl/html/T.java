package te050c.tl.html;

public 
//Node Type
enum T{text	(TClss.SC)
	,eClose	(TClss.SC,"</",">")
	,instruction(TClss.SC,"<!",">")//doc,
	,cdata	(TClss.SC,"<![CDATA[","]]>")
	,entity	(TClss.SC,"&",";")
	,cmnt	(TClss.SC,"<!--","-->")
	,scriptlet(TClss.SC,"{","}")//server-side js script that outputs the evaluated value as an output to the http-response to the client

	//non-replaceable character elements
	,script	(TClss.elem,TClss.nrce)
	,pre	(TClss.elem,TClss.nrce)
	,title	(TClss.elem,TClss.nrce)
	,style	(TClss.elem,TClss.nrce)
	,textarea(TClss.elem,TClss.ff,TClss.nrce)

	//void elements in HTML:
	,area	(TClss.elem,TClss.Void)
	,base	(TClss.elem,TClss.Void)
	,br		(TClss.elem,TClss.Void)
	,col	(TClss.elem,TClss.Void)
	,command(TClss.elem,TClss.Void)
	,embed	(TClss.elem,TClss.Void)
	,hr		(TClss.elem,TClss.Void)
	,img	(TClss.elem,TClss.Void)
	,input	(TClss.elem,TClss.Void,TClss.ff)//,TClss.nrce
	,keygen	(TClss.elem,TClss.Void)
	,link	(TClss.elem,TClss.Void)
	,meta	(TClss.elem,TClss.Void)
	,param	(TClss.elem,TClss.Void)
	,source	(TClss.elem,TClss.Void)
	,track	(TClss.elem,TClss.Void)
	,wbr	(TClss.elem,TClss.Void)//count of 1st letter : t10,s3,b2,c2,i2,p2,a2,e1,h1,k1,l1,m1,w1 ;seven lists non-ones, six lists ones
	,jsonNode(TClss.Void)

	,table	(TClss.elem,TClss.tbl)//,4)   //,table(1)
	,thead	(TClss.elem,TClss.tbl)//,3)   //,thead(2)
	,tbody	(TClss.elem,TClss.tbl)//,3)   //,tbody(2)
	,tfoot	(TClss.elem,TClss.tbl)//,3)   //,tfoot(2)
	,tr		(TClss.elem,TClss.tbl)//,2)      //,tr(3)
	,th		(TClss.elem,TClss.tbl)//,1)      //,th(4)
	,td		(TClss.elem,TClss.tbl)//,1)      //,td(4)

	//,html	(TClss.elem),head(TClss.elem),body(TClss.elem),a(TClss.elem),form(TClss.elem)
	,button	(TClss.elem,TClss.ff)
	,select	(TClss.elem,TClss.ff)
	,option	(TClss.elem,TClss.ff)

	,elem	(TClss.elem);

	String s1,s2;int flags;
	//T(TClss c,int p){lvl=p;}
	T(TClss...p){flags=0;for(TClss i:p)flags+=i.f();}
	T(TClss p,String o,String c){s1=o;s2=c;flags+=p.f();}
	
	/** non-replaceable character elements */boolean nrce(){return is(TClss.nrce);}
	boolean is(TClss p){boolean b= (flags & p.f())!=0;
		return b;}
	boolean leaf(){return is(TClss.Void);}
	boolean elem(){return is(TClss.elem);}//ordinal()>cmnt.ordinal();}
	boolean sc(){return is(TClss.SC);}//ordinal()<=textarea.ordinal();}//sc:Special Case

	int fe(String n){if(!is(TClss.ff))return -1;
		return this==input?0
		:this==textarea?1
		:this==select?2
		:this==button?3
		:this==option?4
		//:!elem()?-1//:is("select",n)?2:is("button",n)?3:is("option",n)?4
		:-1;}//form element

	//nesting restriction
	int lvl(){T t=this;return t==table?4
		:!is(TClss.tbl)?0
		:t==thead||t==tbody||t==tfoot?3
		:t==tr?2:t==th||t==td?1:0;}

 /**Type classification*/
 enum TClss{/**Special Case*/SC,/**htmlElement*/elem,/**Non-Replacable Character Elements*/nrce,/**void element*/Void,tbl,/**html-Form Field*/ff;
	int f(){int i=(int)Math.pow(2 , ordinal());
		return i;}}

}//NodeType