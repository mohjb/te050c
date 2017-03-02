package te050c.tl.html;

import te050c.tl.TL;
//import te050c.tl.Element.Output;
import java.util.Map;

 interface IAct
 {
	void act(Html engine);E eAct();

 /**Action Enumeration, server-side attributes*/
 enum E{
	 xmlns,serverside
	,repeat// repeat the node while attrib-expr evals truthful
	,iter // name of var-iterator , defaults: i
	,For // repeat node and iterate on array/iterator/map of the eval
	,db // obj(default) ; iterate: col , row , objs , array/tbl
	,dbp//params for preparedStatement, used with Atr.db and Atr.tbl
	,tbl // DB.Tbl-iterator
	,nodeName// substitute node-name with evaled-val from attrib-val
	,outterHtml// replace/output node with the evaled attrib-val
	,innerHTML// replace/output node.innerHTML with the evaled attrib-val	//if returns null or undefined , then no innerHTML output, if returns Html.Atr.innerHTML(default) then continue execution casually
	,id//to store reference of the node in global(eng).ids[<id-val>]
	,className//to store reference of the node in global(eng).classes[<val>]
	,path//to store reference of the node in global(eng).path[<val>] // tree based on path/names of parents
	,If//if expr not evals-truthful the node is skipped
	,ElseIf//after if/elseIf sibling
	,Else//after if/elseIf sibling
	,attrib// set attrib val from the evaled val ,e.g. <server-xmlns>:attrib:<name>="expr"
	,script	//as an element nodeName e.g. <serverside:script	//server will execute javascript based on javax.Script-rhino-javascript
	,Switch
	,Case//from switch of parent
	,form//read submitted form name, form-name from evaled-attrib ; also ,on the client html-form-elem prepare from the names of the form-fields, e.g. global(i.e. eng).forms.<formName>.<fieldName>
	,onPost//eval attrib-val if post (in general any form)
	,onLoad// called/eval-attrib-val when htmlParser-loader completes reading source-code of the elem
	,onTag//resume the output of the tag-head, output from the returned value of the evaled-attrib-val 
	,onChild// called/eval-attrib-val for-each elemNode child

	,contentType//set servletResponse-contentType
	,httpHead//set servletResponse-Header
	,importPackages
	,writeFile//output file to the servletResponse
	,include//execute another server-side-html

	,codeJson// TODO:the parser has to accept the json

	,declarePrototype	,usePrototype
	,declareDbTbl		,useDbTbl
	,declareColumn		,useColumn
	,declareRootPage	,useRootPage
	,declareHtmlTemplate,useHtmlTemplate

	,storedJsonNew		,storedJsonDelete
	,storedJsonPropGet	,storedJsonPropSet
	,storedJsonPropDelete

	/*,declareFunc,useFunc
	,declareRelation,useRelation
	,declareElement,useElement
	,storedElementNew,storedElementDelete
	,storedElementPropGet,storedElementPropSet
	,storedElementPropDelete
	
	declare user-defined tag
		parsering stage / api
		execution stage / api
	*/;
 public static E e(String s){
	E[]a=values();
	for(E e:a){String p=e.name();if(p.equalsIgnoreCase(s))
		return e;}
	return null;
 }
 void act(IAct current,Html engine){}
}//E
}//IAct


/**Output Engine and Interpreter, based on the Html tree-Nodes 
 * html Node, server-side DOM ghost-root
 * , for outputting as a back-end
 * , and as a server side js-scripting 
 * and declarative serverside-dom scripting
 * */
public class Html extends N {
	Html(){super(null,null,null);parent=this;
		new M(this,"ids");new M(this,"forms");
		new Prsr2(this);}

	N nextAct,currentAct;IAct auxAct;
	@Override Html root(){return this;}//java.util.LinkedList<N>callStack;

 public void act(Html root){
	currentAct=chld;
	while(currentAct!=null)
	{nextAct=currentAct.chld;//afterAct(currentAct);
	 IAct.E e= currentAct.eAct();
	 if(e==null);
	 else e.act(currentAct,this);
		
	}
 }

 public void output(){
	N n=chld;
	while(n!=null){
		if(n.chld!=null)n=n.chld;
		else if(n.nxt!=null)n=n.nxt;
		else{
			N p=n.parent;n=null;
			while(n==null && p!=null && p.parent!=null ){
				p=p.parent;
				if(p.nxt!=null)
					n=p.nxt;
			}
		}
	}//while n
 }//output

 Prsr2 prsr(){A p=a1;//(Prsr2)attr("prsr");
	while(p!=null && !(p instanceof Prsr2) )p=p.nxt;
	return(Prsr2)p;}

 M m(String key){M p=(M)attr(key);
	return p;};

 M ids(){return m("ids");}
 M forms(){return m("forms");}

 //Html load(String fileName){return this;}
 Html load(java.io.File f){
	prsr().load(f);
	return this;}

/**lookup table of Id's */
public class M extends N.A{
 M(Html root,String key){root.super(key,new java.util.HashMap<String,Object>());}
 Map<String,Object>val(){return(Map<String,Object>)val;}
}//class M

}//class Html
