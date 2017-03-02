package te050c.tl.html;

import te050c.tl.TL;

/**the Finite Sate Machine*/
enum State{
 eof{@Override State next(Prsr2 p){return eof;}
	@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
 }
,txt{@Override State next(Prsr2 p){
	Html r=p.root();
	if(p.p.c=='&'){
		r.typ =T.entity;return entity;}
	if(p.p.c=='{'){
		r.typ =T.scriptlet;return jsScriptlet;}
	if(p.p.c=='<'){char c1=p.p.peek();
	  if( c1!='<' && !Character.isWhitespace(c1)){
		if(c1=='/'){
			p.skip(1);r.typ =T.eClose;return nodeClose;}
		if(c1=='!'){
			if(p.p.lookahead(T.cmnt .s1,1)){
				p.skip(T.cmnt .s1.length()-1);r.typ =T.cmnt ;return comment ;}
			if(p.p.lookahead(T.cdata.s1,1)){
				p.skip(T.cdata.s1.length()-1);r.typ =T.cdata;return txtCData;}
			p.skip((r.typ =T.instruction).s1.length()-1);return instruction;
		}if(chkJsonChar(c1)){
			r.typ=T.jsonNode;return nodeJson;}
		r.typ =p.lookahead();
		return nodeName;
	}}return super.next( p);}
	@Override State onEnter(TL tl,Prsr2 p,State old){
		p.root().typ=T.text;
		return txt;}//super.onEnter(tl, p, old);
	@Override void onExit (TL tl,Prsr2 p,State ns ){
		N n=p.getCurrent();
		if(n instanceof Html || n.typ!=T.text)
		{String s=p.p.consume(),t=softTrim(s);
			if(t.length()>0)
				n.newChild(T.text, s);}
		else
			super.onExit(tl, p, ns);
		if(n.typ==T.text)
			p.up();
		p.markRC();}
	String softTrim(String p){
		int i=0,j=p==null?0:p.length();
		if(j<2)return p;
		char c=p.charAt(i),w=c,y;
		boolean b=Character.isWhitespace(c),e=b,f;
		while(i+1<j&& e){
			c=p.charAt(++i);
			e=Character.isWhitespace(c);
			if( e && w!='\n')
				w=c;
		}
		c=y= i<j ? p.charAt(j-1) : ';';
		e=f=Character.isWhitespace(c);
		while(i<j-1 && e){
			c=p.charAt(--j -1);
			e=Character.isWhitespace(c);
			if( e && y!='\n')
				y=c;
		}
		if(b&&f)p=w+p.substring(i,j)+y;
		else if(f)p=p.substring(i,j)+y;
		else if(b)p=w+p.substring(i,j);
		return p;
	}//softTrim
 }//txt

 ,txtCData{// [CDATA[
	@Override State next(Prsr2 p){
		//if(p.p.c!=']')return super.next( p);
		if(p.p.c==']'&&p.p.lookahead(T.cdata.s2,1))
		{p.skip(T.cdata.s2.length()-1);return txt;}
		return super.next( p);}
	@Override void onExit (TL tl,Prsr2 p,State ns ){
		super.onExit(tl, p, ns);
		p.up();}}
 ,txtElem{// script,pre,textarea,title,style
	@Override State next(Prsr2 p){
		if(p.p.c!='<')return super.next( p);
		N n=p.getCurrent();
		String s=n.parent.typ.name();//data;
		if(p.p.lookahead("/"+s))//p.p.lookahead("</"+s,1)
		{p.skip(1);//2 //s.length()-1);
			return nodeClose;}
		return super.next( p);}
	@Override void onExit (TL tl,Prsr2 p,State ns
		){p.getCurrent().data= p.p.consume();p.up();}}
 ,nodeName{
	@Override State next(Prsr2 p){
		State r=p.chk(p.p.c);
		return r!=null?r:super.next( p);}
	@Override void onExit(TL tl,Prsr2 p,State ns ){
		super.onExit(tl, p, ns);
		N n=p.getCurrent(),x=n.parent;
		n.data=p.interpretData(String.valueOf(n.data));
		if(x.close==n)
		{String s=String.valueOf(n.data).toUpperCase();
			x.close=null;Html r=p.root();
			while(x!=r && !s.
				equalsIgnoreCase(String.valueOf(x.data)))
				x=x.parent;
			x.close=n;n.parent=x;}}//enum subFSM{no,maybe ,ok,yes}
	}
 ,nodeWS{//whitespace
	@Override State next(Prsr2 p){
		State r=p.chk(p.p.c);
		return r!=null?r
			:p.p.c=='{'?jsScriptlet
			:chkJsonChar(p.p.c)?attrNameJson
			:attrName;}
	@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
	@Override void onExit (TL tl,Prsr2 p,State ns ){}
	}
 ,nodeClose{
	@Override State next(Prsr2 p){return eof;}
	@Override State onEnter(TL tl,Prsr2 p,State old){
		p.root().typ=T.eClose;p.la2buff();
		super.onEnter(tl, p, old);
		N n=p.getCurrent();
		n.parent.close=n;
		return p.key=State.nodeName;}//return this;
	@Override void onExit (TL tl,Prsr2 p,State ns ){}}
 ,nodeJson{
	@Override State onEnter(TL tl,Prsr2 p,State old){
		Object o=null;Html r=p.root();
		N c=p.getCurrent()
		,n=p.setCurrent(c.newChld( r.typ=T.jsonNode , o ));
		try{if(p.p.c=='<')p.p.nxt();
			n.data=p.p.parseItem();
		} catch (Exception e) {
			TL.tl().error(e, "te050c.tl.html.State.nodeJson.onEnter:json.parseItem:");}
		return nodeWS;}
	@Override State next(Prsr2 p){return nodeWS;}
	@Override void onExit(TL tl,Prsr2 p,State ns ){}}
 ,attrNameJson{
	@Override State onEnter(TL tl,Prsr2 p,State old){
		p.k=p.p.o=null;p.markRC();State r=this;
		try {p.k=p.p.parseItem();
			r=p.p.c=='='?attrEq:State.attrWs;//Character.isWhitespace(p.p.c)?:
		}catch(Exception e){TL.tl().error(e,
			"te050c.tl.html.State.attrNameJson.onEnter:json.parseItem:");}
		return r;}}
 ,attrVJson{
	@Override State onEnter(TL tl,Prsr2 p,State old){
		State r=attrVal;
		try {p.p.o=p.p.parseItem();
			onExit(tl,p,r=nodeWS);
		}catch(Exception e){TL.tl().error(e,
			"te050c.tl.html.State.attrVJson.onEnter:json.parseItem:");}
		return r;}

	@Override State next(Prsr2 p){State r=nodeWS.next(p);return r;}
	@Override void onExit(TL tl,Prsr2 p,State ns ){
		p.attrCompleted();}//p.p.o=p.p.consume();
	}
 ,attrName{
	@Override State next(Prsr2 p){
		State r=p.p.c=='='?attrEq:p.chk(p.p.c);
		if(r==nodeWS)r=attrWs;
		return r!=null?r:super.next( p);}
	@Override State onEnter(TL tl,Prsr2 p,State old){p.k=p.p.o=null;p.p.buff();p.markRC();return this;}
	@Override void onExit (TL tl,Prsr2 p,State ns ){
		p.k=p.p.consume();
	}}
 ,attrWs{
	@Override State next(Prsr2 p){
		State r=p.p.c=='='?attrEq:p.chk(p.p.c);
		if(r==nodeWS)r=attrWs;else
		if(r==null){r=attrName;p.attrCompleted();}//p.p.buff();
		return r;}
	@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
	@Override void onExit (TL tl,Prsr2 p,State ns ){}}
 ,attrEq{
	@Override State next(Prsr2 p){
		p.quot= (p.p.c =='"' || p.p.c == '\'' || p.p.c == '`' ) ? p.p.c : '\0';
		State r=p.quot!='\0'?attrQVal:p.chk(p.p.c);
		if(r==nodeWS)r=attrEq;else 
		if(chkJsonChar(p.p.c))
			r=State.attrVJson;else
		if(r==null){p.p.buff();r=attrVal;}
		return r;}
	@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
	@Override void onExit (TL tl,Prsr2 p,State ns ){}}
 ,attrQVal{
	@Override State next(Prsr2 p){
		return p.p.c==p.quot?nodeWS:super.next( p);}
	@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
	@Override void onExit (TL tl,Prsr2 p,State ns ){p.p.o=p.p.consume();p.attrCompleted();}}
 ,attrVal{
	@Override State next(Prsr2 p){
		State r=p.chk(p.p.c);
		return r!=null?r:super.next( p);}
	@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
	@Override void onExit (TL tl,Prsr2 p,State ns ){p.p.o=p.p.consume();p.attrCompleted();}}//,charEntity//,charEntClose
 ,entity{@Override State next(Prsr2 p){return p.p.c==';'?txt:super.next( p);}
	@Override void onExit (TL tl,Prsr2 p,State ns ){
		super.onExit(tl, p, ns);
		p.up();}}
 ,comment{@Override State next(Prsr2 p){
	if(p.p.c!='-')return super.next( p);
	if(p.p.lookahead(T.cmnt.s2,1))
	{p.skip(T.cmnt.s2.length()-1);return txt;}
	return super.next( p);}
	@Override void onExit (TL tl,Prsr2 p,State ns ){super.onExit(tl, p, ns);p.up();}}
 ,instruction{
	@Override State next(Prsr2 p){return p.p.c=='>'?txt:super.next( p);}
	@Override void onExit (TL tl,Prsr2 p,State ns ){super.onExit(tl, p, ns);p.up();}}

	//Tag braces ,alternatively if the output is suppose to be literally a curly brace then use html entity &#123; for opening curly-brace , &#125; for closing curly-brace
	,jsScriptlet{
		@Override State onEnter(TL tl,Prsr2 p,State old){
			if(isJs(old))
				return this;
			if(old==nodeWS)
			{p.jsNestingLevel=-1;return this;}
			p.jsNestingLevel=1;return super.onEnter(tl,p,old);}
		@Override State next(Prsr2 p){int old=p.jsNestingLevel<0?-1:1;
			if(p.p.c=='/'){char x=p.p.peek();
				if(x=='*')
				{p.p.bNxt();return jsCommentBlock;}
				else if(x=='/')
				{p.p.bNxt();return jsCommentLine;}
			}else
			if(p.p.c=='"' || p.p.c=='\'')
			{p.p.bNxt();return jsQuotedString;}
			else if(p.p.c=='{')
				p.jsNestingLevel+=old;
			else if(p.p.c=='}')
				p.jsNestingLevel-=old;
			return p.p.c=='}'&& Math.abs(p.jsNestingLevel)<1
				?(old<0?nodeWS:txt):super.next( p);}
		@Override void onExit (TL tl,Prsr2 p,State ns ){
			if(!isJs(ns)){
				if(ns==nodeWS){p.k=T.scriptlet;p.p.o=p.p.consume();p.attrCompleted();}
				else{super.onExit(tl, p, ns);p.up();}}}
		boolean isJs(State p){return p==jsCommentBlock||p==jsCommentLine||p==jsQuotedString;}
	}
	,jsCommentLine{
		@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
		@Override State next(Prsr2 p){return p.p.c=='\n'?jsScriptlet:super.next( p);}
		@Override void onExit (TL tl,Prsr2 p,State ns ){p.p.bNxt();}}

	,jsCommentBlock{
		@Override State onEnter(TL tl,Prsr2 p,State old){return this;}
		@Override State next(Prsr2 p){char x=p.p.c=='*'?p.p.peek():p.p.c;
			return p.p.c=='*'&&x=='/'?jsScriptlet:super.next( p);}
		@Override void onExit (TL tl,Prsr2 p,State ns ){p.p.bNxt();}}

	,jsQuotedString{
		@Override State onEnter(TL tl,Prsr2 p,State old){p.quot=p.p.c;return this;}
		@Override State next(Prsr2 p){//char x=p.p.c=='\\'?p.p.peek():p.p.c;
			while(p.p.c=='\\'){p.p.bNxt();p.p.bNxt();}
			return p.p.c==p.quot?jsScriptlet:super.next( p);}
		@Override void onExit (TL tl,Prsr2 p,State ns ){p.p.bNxt();}}

	//	int nestingLevel;

;

	State next(Prsr2 p){
		p.p.buff();
		return this;}

	static boolean chkJsonChar(char p){return p=='(' || p=='{' || p=='[';}

	State onEnter(TL tl,Prsr2 p,State old){
		Html r=p.root();p.markRC();
		T t=r.typ;
		N n=p.getCurrent().newChild(t, p.p.consume());
		p.setCurrent(n);return this;}//	}

	void onExit (TL tl,Prsr2 p,State ns ){
		String str=p.p.consume();if(str!=null && str.length()>0){
		N n=p.getCurrent();
		n.data=str;}}//p.interpretData(str)

	/*enum SS{ 
		script,If
		,ElseIf
		,Else
		,While
		,For
		,Switch
		,Case
		,db
		,tbl
		,pj // prototype based json, props get/set, and instantiate new objects, and deletation of props or instance
		,declareDbTbl,declareDbCol, declarePrototype //each would have a "use" statement
		
		//IDEA: after the nodeName from these 
	 * builtin/pre-programmed enum/commands, 
	 * then comes(without whitespace)
	 * paranthesis , and nested are js statements, 
	 * the parser counts nested parenthesis
	 * 
	 * for example, with the if/while statement, 
	 * the parenthesis would have a js boolean expression
	 * 
	 * ex2. the for statement would have nested inside the parenthesis, two semicolons separating three js expressions
	 * ex3. the switch/case would have nested inside the parenthesis, js expression
	 * ex4. db would have nested inside the parenthesis, sql , would iterate
	}*/
}//enum State

