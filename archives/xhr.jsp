<%  %>
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
	x.open('POST','http://localhost:8080/TE049C/fw.jsp')
	x.send(d)
	
	
xhr.jsp:
	restful json gateway
		*

	html-tbl
		* create page
		* create html-entry (rootLevel,or subHtml)
		* edit html-entry
		* delete html-entry
		* load page, generate /process page
			* page menu ( at top of page)
				* list
				* new
				* edit
				* delete
				* duplicate

			* dbLog menu ( at top of page)
				* params:list
				* undo
				* redo

			* html-entry menu (in page list tree)
				* list ( for the current page)
				* duplicate entry ; move to new parent
				* new
				* edit
				* delete

			* tbl-x
				* list
				* new entry
				* edit
				* delete

	DB scuffolding


CREATE TABLE `h` (
  `pageId` int(1) NOT NULL DEFAULT '0',
  `htmlId` int(1) NOT NULL DEFAULT '0',
  `parentHtml` int(1) NOT NULL DEFAULT '0',
  `jsonBld` varchar(7) CHARACTER SET cp850 NOT NULL DEFAULT '',
  `srvrJS` varchar(6) CHARACTER SET cp850 NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `x` (
  `sect` varchar(255) DEFAULT NULL,
  `i` varchar(255) DEFAULT NULL,
  `v` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `expensefamily` (
  `f` varchar(255) DEFAULT NULL,
  `c` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `incomefamily` (
  `f` int(11) DEFAULT NULL,
  `c` bigint(21) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `memberincome` (
  `row` int(11) NOT NULL AUTO_INCREMENT,
  `FS_ID` int(11) NOT NULL,
  `RECORD_ID` int(11) DEFAULT NULL,
  `TABLE_ID` int(11) DEFAULT NULL,
  `FSCYCLE` int(11) DEFAULT NULL,
  `MEM_ID` int(11) DEFAULT NULL,
  `FM_MM` varchar(1) DEFAULT NULL,
  `SALRY_1` int(11) DEFAULT NULL,
  `SALRY_2` int(11) DEFAULT NULL,
  `BONCE_1` int(11) DEFAULT NULL,
  `BONCE_2` int(11) DEFAULT NULL,
  `ALLOW_1` int(11) DEFAULT NULL,
  `ALLOW_2` int(11) DEFAULT NULL,
  `INCOME1` int(11) DEFAULT NULL,
  `INDUST` int(11) DEFAULT NULL,
  `COMMERCE` int(11) DEFAULT NULL,
  `TRANS` int(11) DEFAULT NULL,
  `CONSTRANT` int(11) DEFAULT NULL,
  `FAINANCE` int(11) DEFAULT NULL,
  `OWNWRK` int(11) DEFAULT NULL,
  `OTHERS` int(11) DEFAULT NULL,
  `INCOME2` int(11) DEFAULT NULL,
  `DIVIDENDS` int(11) DEFAULT NULL,
  `BONDS` int(11) DEFAULT NULL,
  `SAVINGAC` int(11) DEFAULT NULL,
  `INVESTMENT` int(11) DEFAULT NULL,
  `INCOME3` int(11) DEFAULT NULL,
  `LANDRENT` int(11) DEFAULT NULL,
  `PROJCTRENT` int(11) DEFAULT NULL,
  `MACHNRENT` int(11) DEFAULT NULL,
  `CPYRIGHT` int(11) DEFAULT NULL,
  `INCOME4` int(11) DEFAULT NULL,
  `MARIGGIFT` int(11) DEFAULT NULL,
  `RETIREDSAL` int(11) DEFAULT NULL,
  `TRETGRANT` int(11) DEFAULT NULL,
  `SOCILASST` int(11) DEFAULT NULL,
  `UNEMPALOW` int(11) DEFAULT NULL,
  `RENTALLOW` int(11) DEFAULT NULL,
  `OTHRGRANT` int(11) DEFAULT NULL,
  `INCOME5` int(11) DEFAULT NULL,
  `ACC_COMP` int(11) DEFAULT NULL,
  `LIFEINSUR` int(11) DEFAULT NULL,
  `REMTABROD` int(11) DEFAULT NULL,
  `REMTINSDE` int(11) DEFAULT NULL,
  `LEGITEXP` int(11) DEFAULT NULL,
  `CASHGIFT` int(11) DEFAULT NULL,
  `KINDGIFT` int(11) DEFAULT NULL,
  `INCOME6` int(11) DEFAULT NULL,
  `CSHINHERIT` int(11) DEFAULT NULL,
  `DOWRY` int(11) DEFAULT NULL,
  `SATELDLOAN` int(11) DEFAULT NULL,
  `SELGOODS` int(11) DEFAULT NULL,
  `LIFEINSRNC` int(11) DEFAULT NULL,
  `WITHDRAWS` int(11) DEFAULT NULL,
  `OTHERPROC` int(11) DEFAULT NULL,
  `INCOME7` int(11) DEFAULT NULL,
  `DEPOSIT` int(11) DEFAULT NULL,
  `WITHDRAW` int(11) DEFAULT NULL,
  `LENDING` int(11) DEFAULT NULL,
  `BORRWING` int(11) DEFAULT NULL,
  `PROCEEDS` int(11) DEFAULT NULL,
  `PAYMENTS` int(11) DEFAULT NULL,
  `SELSCRTIS` int(11) DEFAULT NULL,
  `BUYSCRTIS` int(11) DEFAULT NULL,
  `SELLAND` int(11) DEFAULT NULL,
  `BUYLAND` int(11) DEFAULT NULL,
  `SELBUILD` int(11) DEFAULT NULL,
  `BUYBUILD` int(11) DEFAULT NULL,
  `SELOTHER` int(11) DEFAULT NULL,
  `BUYOTHER` int(11) DEFAULT NULL,
  `INCOME8` int(11) DEFAULT NULL,
  PRIMARY KEY (`row`),
  KEY `FS_ID_index` (`FS_ID`),
  KEY `MEM_ID_index` (`MEM_ID`),
  KEY `FM_MM_index` (`FM_MM`)
) ENGINE=InnoDB AUTO_INCREMENT=21694 DEFAULT CHARSET=utf8;



CREATE TABLE `t` (
  `row` int(10) NOT NULL DEFAULT '0',
  `f` varchar(255) DEFAULT NULL,
  `i` varchar(255) DEFAULT NULL,
  `d` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `q` int(7) NOT NULL,
  `p` decimal(10,3) NOT NULL,
  `x` decimal(10,3) DEFAULT NULL,
  KEY `indx1` (`f`,`i`,`d`),
  KEY `indx2` (`d`,`x`,`i`),
  KEY `indx3` (`d`,`i`,`x`),
  KEY `indx4` (`f`,`d`,`x`),
  KEY `indx5` (`i`,`x`,`d`),
  KEY `indx6` (`i`,`d`,`f`),
  KEY `pk` (`row`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--><%

String fn="D:/apache-tomcat-8.0.15/webapps/ROOT/TE049C/db.gciAll."+System.currentTimeMillis()+".js";
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
