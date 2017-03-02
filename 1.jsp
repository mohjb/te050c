<%@ page import="te050c.tl.TL,te050c.tl.db.DB"
%><%	TL tl=null;try
{tl=TL.Enter(request,out);
	String i=tl.req("i");
	if("i".equals(i))
		DB.q2json("select distinct i from e2");
	else if("f".equals(i))
		DB.q2json("select distinct f from e2");
	else if("e2".equals(i) )
		DB.q2json("select * from e2");
	if(i!=null)
		return;
	%><script>//srvr:i=<%=i%>

app=a={	items:[]
	,	famis:[]
	,	e2:[],a:[],m:{f:{},i:{}}
	,asText:0
	,updt:function(){a.span.innerHTML=
		a.spanMsg+'<br/>'+a.i+'/'+a.spanN+'<br/>'
		+Math.floor((a.i/a.spanN)*100)+'%';}

	,keys:function keys(o){var a=[];for(var i in o)a.push(i);return a;}


	,init:function init(){try{a.span=document.createElement('span')
		document.body.appendChild(a.span);}catch(ex){console.log('app.init:doc create Element span status',ex);}
		a.i=0;a.spanN=1;
		a.spanMsg="Loading items id's";a.updt();
		var p={data:a.asText?'i=i':{i:'i'},asText:a.asText,asyncFunc:a.f1}
		a.xhr(p)}//function init
			
	,f1:function(o){a.items=o;
		var p={data:a.asText?'i=f':{i:'f'},asText:a.asText}
		a.famis=a.xhr(p)
		
		a.spanMsg="Loading aggregated expendeture";a.updt();
		p={data:a.asText?'i=e2':{i:'e2'},asText:a.asText}
		a.e2=a.xhr(p)
		a.initExp()}

	,initExp:function(){
			//var m={f:{},i:{}}//,items=a.items,famis=a.famis,e2=app.e2;
			a.spanN=(a.spanKeys=a.keys(a.spanA=a.items.a)).length;
			a.spanMsg='1/4 : items columns';a.i=0;a.updt();
			a.intrvl=setInterval(a.f1_intrvl,1)
			return a.a;
		}//initExp
	,f1_intrvl:function(){var x=a.spanKeys[a.i]
		a.m.i[a.spanA[x]=a.spanA[x][0]]=x;
		++a.i;a.updt();
		if(a.i>=a.spanN){
			clearInterval(a.intrvl);
			a.f2();}}

	,f2:function(){
			a.spanN=(a.spanKeys=a.famiKeys=a.keys(a.spanA=a.famis.a)).length;
			a.spanMsg='2/4 : families id';a.i=0;a.updt();
			a.intrvl=setInterval(a.f2_intrvl,1)
		}//f2 for(app.i in famis.a)m.f[famis.a[app.i]=famis.a[app.i][0]]=app.i;
	,f2_intrvl:function()
		{var x=a.spanKeys[a.i]
			a.m.f[a.spanA[x]=a.spanA[x][0]]=x;
			++a.i;a.updt();
			if(a.i>=a.spanN)
			{clearInterval(a.intrvl);
				a.f3();
			}
		}//f2_intrvl
			
	,f3:function(){
		a.spanN=(a.spanKeys=a.keys(a.spanA=a.e2.a)).length;
		a.spanMsg='3/4 : reformat to wide';a.i=0;a.updt()
		a.intrvl=setInterval(a.f3_intrvl,1)}//f3
	,f3_intrvl:function()
		{a.f3e2();a.updt();
			if(a.i>=a.spanN){
				clearInterval(a.intrvl);
				a.initTbl();}
		}//f3_intrvl
	,f3e2:function(){var x,c=102;
		while(--c>=0 && a.i<a.spanN){
			x=a.spanKeys[a.i]
			//m.f[app.spanA[x]=app.spanA[x][0]]=x;
			var r=a.spanA[x]
			,f=r[0]
			,i=r[1]
			,fi=a.m.f[f]
			,ii=a.m.i[i]
			,z=a.a[fi]
			;
			if(!z)
				a.a[fi]=z=[];
			z[ii]={q:r[2],p:r[3]};
			++a.i;
		}}


	,initTbl:function(){
		var tr=['Family']
		,tbl=a.tbl=[]
		a.i=0;a.spanA=a.famis.a;
		a.spanN=(a.spanKeys=a.famiKeys).length;
		a.spanMsg='4/4 : generate output';
		a.updt();		
		
		for(var i in a.items.a)
			tr.push('item '+a.items.a[i]+' qty\tprice')
		tbl.push(tr.join('\t'));
		a.intrvl=setInterval(a.f4,1);}
	,f4:function()
		{var f=a.spanKeys[a.i],tr,x;
		//for(var f in famis.a)
			tr=[a.famis.a[f]]
				for(var i in a.items.a){
					//td.title='family:'+a.famis.a[f]+'\nitem:'+a.items.a[i]
					tr.push((x=a.a[f][i])? (x.q+'\t'+x.p) : '\t' )
				}
			a.tbl.push(tr.join('\t'))
			++a.i;a.updt();
			if(a.i>=a.spanN)
			{clearInterval(a.intrvl);
				a.d=a.tbl.join('\n')
				a.spanMsg='saving output';a.updt()
				a.xhr({data:a.d,url:'http://localhost:8080/TE050C/fw.jsp'})
				a.spanMsg='done';a.updt()
			}
		}

/**
 * parameter p attributes
 *	data: body data of the xhr request sent to the server ,default null
 *	headers: json-object of name/value , set as request headers, defaults to null
 *	asyncFunc: reference to a function that is called in asynchronous mode
 		, when the server successfully responds the func is given as a param
 		the xhr obj and second param p, defaults to null which is synchronise mode
 *	method: string , POST, GET, defaults to POST
 *	url: the url of xhr , defaults to ''
 *	asText:boolean, if true returns xhr.responseText else returns JSON.parse(xhr.responseText)
 	* header contentType is set as: if asText then 'application/x-www-form-urlencoded' else 'text/json'
 * */
,xhr:function App_xhr(p)
{if(!p)return p;
 var ct='Content-Type',cs='charset'
 	,x=XMLHttpRequest?new XMLHttpRequest():new ActiveXObject("microsoft.XMLHTTP");
	x.open(p.method||'POST',p.url||'', p.asyncFunc);
	if(!p.headers || !p.headers[ct])x.setRequestHeader(ct,p.requestCt= p.asText? 'application/x-www-form-urlencoded': 'text/json');
	if(!p.headers || !p.headers[cs])x.setRequestHeader(cs, 'utf-8');
	if(p.asyncFunc)x.onreadystatechange=function App_xhrAsync()
		{//console.log('App_xhrAsync:x=',x,' ,p=',p);
		 if (x.readyState === 4 && x.status==200){var str=x . responseText
		 	, o=p.responseJsonResponse= p.asText? str :JSON.parse ( str ) ;
			p.asyncFunc(o,x,p);//if(p.asText || !o.errorMessage)p.asyncFunc(o,x,p);else console.log('App_xhrAsync:case cant call p.asyncFunc:',o,x,p);
		 }//elseconsole.log('App_xhrAsync:case not ready:',x,p);
		};
	if(p.headers)for(var i in p.headers)
		x.setRequestHeader( i , p.headers [ i ] ) ; //setRequestHeader( Name, Value )
	//if(!window.ax)window.ax=[ [x,p]];else window.ax.push([x,p]);
	x.send((typeof p.data)=='string'?p.data:(p.requestBodyDataJsonified=JSON
	 .stringify(p.data)));//console.log('scriptedReq:response:'+ajax.responseText);
	//console.log('xhr:end:x=',x,' ,p=',p, x.response);
	var str = x . responseText
	, o= p.responseJsonResponse = p.asText? str :JSON.parse ( str )
	return o ; //p.asJson?JSON.parse ( str ) : str ;
	}//function xhr
}//app
		</script>
		<%
}//try
finally{TL.Exit();}%>