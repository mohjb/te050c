window.app=app={xhr:function xhr(dta,h){
	var x=new XMLHttpRequest()
	x.open('POST','',false)
	x .setRequestHeader("Content-Type", "text/json")//;charset=UTF-8");
	//if(h)x
	console.log('xhr:',dta)
	x.send(dta);
	console.log('xhr=',x)
	return x;
}//function xhr
,did:function did(i){return document.getElementById(i);}
,dce:function dce(n,p,o){//3rd param is either text, or, obj with props:t,id,styl,attribs,clss,clk,mo
		var n=document.createElement(n);
		if(o)
		{var t=typeof(o)=='string'?o:(o&&o.t)
			if(t!=undefined)
				app.dct(t,n);
			if(o.id!=undefined)
				n.id=o.id;
			if(o.clss!=undefined)
				n.className=o.clss;
			if(o.styl!=undefined){
				if(typeof(o.styl)=='string')
					n.style.cssText=o.styl;
				else
					for(var i in o.styl)
						n.style[i]=o.styl[i];}
			if(o .attribs)
				for(var i in o.attribs)
					n.setAttribute(i,n[i]=o.attribs[i])
			if(o.clk)
				n.onclick=o.clk;
		}//if(mo)n.mouseover=mo;
		if(p)p.appendChild(n);return n;
	}//dce
,dct:function dct(t,p){
		var n=document.createTextNode(t);
		if(p)p.appendChild(n);return n;
	}//dct
,dci:function dci(nm,p,o){//o obj:typ,val,chkd,id,lbl,attribs,styl,clss,chng,clk
		var r=app.dce('input',0,o),lbl;//0,o&&o.id,o&&o.styl,attribs,cl,clk);
		r.name=nm;if(o)
		{r.type=(o&&o.typ)||'text';
			if(o.val!=undefined)r.value=o&&o.val;
			if(o.chkd)r.checked='checked'
				if(o.chng)
					r.onchange=o.chng;
			if(o.lbl)lbl=app.dce('label',0,
								 {	t:o.lbl
								 ,id:o.id?o.id+'.lbl':null
								 ,attribs:{"for":o.id}
								 });
		}
		if(p){p.appendChild(r);
			if(lbl)p.appendChild(lbl);}
		return r;
	}
,bldChart_ln2:function bldChart_ln2(
j/*j is object:{ 
	axisLabels  //array of Axis-labels:[ y-axis-label , x-axis-label ]
	,c		//array of y-values
	,keys	//array of keys ,associated with j.c
	,headings	//array of x-values
	,domNode
}//j object

after this method , j will be changed as:
	j.chart	//=nv.models.lineChart()
	j.chartData		//array of objects:{key:<j.keys[i]> , values:[... {x:j.headings[k+1] , y:j.c[k] } ...] ,color:<string:hsl>}
*/
){
	//for(var dataIx=0;dataIx<sys.output.length;dataIx++)j=sys.output[dataIx],
    {//function() {console.log('nv.addGraph:func:',arguments)
		var l=j.axisLabels,n=l&&l.length
        j.chart = nv.models.lineChart()
            .options({
                duration: 300,
                useInteractiveGuideline: true
            })
        ;
		
        // chart sub-models (ie. xAxis, yAxis, etc) when accessed directly, return themselves, not the parent chart, so need to chain separately
        j.chart.xAxis
            .axisLabel(l[n-1])//"Time (s)"
            //.tickFormat(d3.format(',.1f'))
            .staggerLabels(true)
        ;

        j.chart.yAxis
            .axisLabel(l[1])//l[n-1]//'Voltage (v)'
            .tickFormat(function(d) {
                if (d == null) {
                    return 'N/A';
                }
                return d3.format(',.2f')(d);
            })
        ;

        //j.data = prepareData();		function prepareData(){	,j=sys.output[dataIx]
		var dn=j&&j.c&&j.c.length,i
		j.chartData=[]
		for( i=0;i<dn;i++)
		{	var c=j.c[i],w=480
			,o={key:j.keys[i],values:[]
				,color:d3.hsl(sys.hsl.h+(360*i)/dn
					,sys.hsl.s,sys.hsl.l+sys.hsl.dl).toString()};
			j.chartData.push(o);
			for(var k=0,kn=c.length;k<kn;k++)
				o.values.push({x:j.headings[k+1],y:c[k]});
		}//for i
		//return r;}//function prepareData()
/*
data format:
	array of objects
		{
			area:<bool>							//optional
			,values:[ { x:<int> , y:<num> } ]
			,key: <str>
			,color:<#rrggbb>
			,strokeWidth:<num>					//optional
			,classed:<str:'dashed'>				//optional
			,fillOpacity:<num: 0.0 ... 1.0>		//optional
		}
*/
        d3.select(j.domNode).append('svg')
		.attr('style','width:75%;height:50%')
            .datum(j.chartData)
            .call(j.chart);

        nv.utils.windowResize(j.chart.update);

        //return chart;}
		nv.addGraph(j.chart);
	}//for dataIx
}//function bldCharts_ln2

,parseTSVdata:function(){
	var dataNode=app.did('data')
	,dataStr=dataNode.innerHTML
	,rowsStr=dataStr.split('\n')
	,rn=rowsStr.length
	,sectors={rows:[],cols:rowsStr[0].split('\t'),ids:{},c0:[]};// rows::=1st row is heading , 1st column is sectorName ; cols::= mapping index to sectorName  ;ids::= mapping sectorName to index 
	sectors.rows.push(sectors.cols)
	for(var rowIx=1;rowIx=rn;rowIx++)
	{	var str=rowsStr[rowIx]
		,x=str.split('\t')
		sectors.rows.push(x)
		sectors.c0.push(x[0]);
		sectors.ids[x[0]]=i;
	}return sectors;}
	

/*,initTSVdata:function(){
	var dataNode=app.did('data')
	,dataStr=dataNode.innerHTML
	,rowsStr=dataStr.split('\n')
	,n=rowsStr.length,d=[]
	,sectors={n:n,cols:rowsStr[0].split('\t')
	,ids:{},c0:[]
	,d:d// rows::=1st row is heading , 1st column is sectorName ; cols::= mapping	index to sectorName  ;ids::= mapping sectorName to index 
	,avgs:{cols:[],rows:[],total:0}
	,sums:{cols:[],rows:[],total:0}
	,sort:{cols:[],rows:[]}
	}
	d.push(sectors.cols)
	for(var i=1;i<n;i++)
	{	sectors.sums.cols[i]=sectors.sums.rows[i]=0
		sectors.sort.cols[i]=[sectors.cols[i]];}
	for(var i=1;i<n;i++)
	{	var s=rowsStr[i],k,v
		,x=str.split('\t')
		d.push(x)
		sectors.ids[s=x[0]]=i;
		sectors.c0.push( s );
		for(var j=1;j<n;j++)
		{v=x[j];
			sectors.sums.rows[i]+=v
			sectors.sums.cols[j]+=v
			s=sectors.sort.rows
			if(j==1){
				s[i]=[x[0],v];
			}else{
				k=1;while(k<=j && v<x[s[k]])k++;
				sectors.sort.rows[i].splice(k,0,j);
			}
			k=1;while(k<=j && d[j][i])k++;
			sectors.sort.cols[i].splice(k,0,j);
		}//for j
		sectors.sums.total+=sectors.sums.rows[i];
	}
	return sectors;}*/

,initTotals:function(sectors){
	//sectors.sort:{cols:[ [ sectorIx ] ,,, ] , rows:[ [sectorIx] ,,,]}
	//sectors.sums:{cols:[ num,,, ] , rows:[ num,,, ]}
	//sectors.avgs:{cols:[ num,,, ] , rows:[ num,,, ]}
	sectors.avgs={cols:[],rows:[],total:0}
	sectors.sums={cols:[],rows:[],total:0}
	sectors.sort={cols:[],rows:[]}
	var d=sectors.rows,n=sectors.n=d.length;
	for(var i=1;i<n;i++)
	 sectors.sums.cols[i]=sectors.sums.rows[i]=0;
	for(var rowIx=1;rowIx<n;rowIx++)
	{	var row=d[rowIx;]
		for(var colIx=1,cn=row.length;
			colIx<cn;colIx++){
			var v=row[i]
			sectors.sums.cols[i]+=v
			sectors.sums.rows[i]+=v;}}
	for(var i=1;i<n;i++)
	{var r=sectors.sums.rows[i]
		,c=sectors.sums.cols[i];
		sectors.sums.total+ =r;
		sectors.avgs.cols[i]=c/n;
		sectors.avgs.rows[i]=r/n;	}
	sectors.avgs.total=sectors.sums.total/(n*n);

	/*sort*/
	for(var i=1;i<n;i++)
	{	var g=d[i],v=g[1],k
			,sc=sectors.sort
			,sr=sc.rows[i]=[v];
		sc=sc.cols[i]=[d[1][i]];
		for(var j=2;j<n;j++)
		{	v=g[j];k=1;
			while(k<j&&v>g[sr[k]])k++;
			sr.splice(k,0,j)
			k=1;g=sectors.rows;v=g[j][i]
			while(k<j && v>g[sc[k]][i])k++;
			sc.splice(k,0,j)
		}
	}
	return sectors
 }

,giniCofficient:function(sectors){
	var d=sectors.d,n=sectors.n,ga=sectors.giniCofficients=[]
	//step1: ordering based on values in sector, done in function initTotals.
	//step2: share ::= y= d@i / sector_total
	//step3: init 45degrees line I : I@i= i/n ; n is number of sectors : sectors.n
	//step4: estimate gini-cofficient by summing( I@i - share@i )
	for(var si=1;si<n;si++){
		
	}
	return ga;
}
,init:function(){
app.parseTSVdata()

	var j={axisLabels:[]
		,c:[]
		,keys:[]
		,headings:[]
		,domNode:document.body
	}

	app.bldChart_ln2(j);

}//init
}//app
