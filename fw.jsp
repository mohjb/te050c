<!--function onInitFs(fs) {
errorHandler= function(e) {
        console.log('Write failed: ' + e.toString());
      };
  fs.root.getFile('javascript.fileSystem.1stTest.txt', {create: true}, function(fileEntry) {

    // Create a FileWriter object for our FileEntry (log.txt).
    fileEntry.createWriter(function(fileWriter) {

      fileWriter.onwriteend = function(e) {
        console.log('Write completed.');
      };

     fileWriter.onerror =errorHandler 

      // Create a new Blob and write it to log.txt.
      var blob = new Blob(x, {type: 'text/plain'});

      fileWriter.write(blob);

    }, errorHandler);

  }, errorHandler);

}

window.requestFileSystem(window.TEMPORARY, 23* 1024*1024, onInitFs, errorHandler);

i=j=0;

i=j
j=i+1024*1024
x.substring(i,j)

xhr=function App_xhr(p)//data,callBack,asText)
{if(!p)return p;
 var ct='Content-Type',cs='charset',x=App.isIE?new 
		ActiveXObject("microsoft.XMLHTTP")
		:new XMLHttpRequest();
	x.open(p.method||'POST',p.url||'http://localhost:8080/TE049C/fw.jsp', p.asyncFunc);
	//if(p.headers)for(var i in p.headers)x.setRequestHeader(i,p.headers[i]);//console.log('scriptedReq:header['+i+']:'+headers[i]);
	if(!p.headers || !p.headers[ct])x.setRequestHeader(ct, 'text/json');//'application/x-www-form-urlencoded');
	if(!p.headers || !p.headers[cs])x.setRequestHeader(cs, 'utf-8');
	if(p.asyncFunc)x.onreadystatechange=function App_xhrAsync()
		{console.log('App_xhrAsync:x=',x,' ,p=',p);
		 if (x.readyState === 4 && x.status==200){var o=
			p.asJson?JSON.parse ( x . responseText ) : x . responseText;
			p.responseJsonResponse=o;
			if(!p.asJson || !o.errorMessage)
				p.asyncFunc(o['return'],x,p);
			else
				console.log('App_xhrAsync:case cant call p.asyncFunc:',o,x,p);
		 }else
			console.log('App_xhrAsync:case not ready:',x,p);
		};
	if(p.headers)for(var i in p.headers)
		x.setRequestHeader( i , p.headers [ i ] ) ; //setRequestHeader( Name, Value )
	if(!window.ax)window.ax=[ [x,p]];else window.ax.push([x,p]);
	x.send((typeof p.data)=='string'?p.data:(p.requestBodyDataJsonified=JSON
	 .stringify(p.data)));//console.log('scriptedReq:response:'+ajax.responseText);
	console.log('xhr:end:x=',x,' ,p=',p, x.response);
	return p.asJson?JSON.parse ( x . responseText ) : x . responseText ;
	}//function xhr
	
	d=JSON.stringify( db.sectors.gci,null,'\t');//dLen=d.length
	x=new XMLHttpRequest()
	x.open('POST','http://localhost:8080/TE050C/fw.jsp')
	x.send(d)
--><%

String fn="FileWriter.jsp."+System.currentTimeMillis()+".js";
byte[]b=new byte[1024*1024];
java.io.FileOutputStream w=null;
javax.servlet.ServletInputStream is=request.getInputStream();
int n=is.read(b),ttl=0;
if(n>0)
	w=new java.io.FileOutputStream(fn);
while(n>0){ttl+=n;%>
<%=n%><%
	w.write(b,0,n);
	n=is.read(b);
}
w.close();
%>
total-bytes-read=<%=ttl%>
to fn:<%=fn%>
