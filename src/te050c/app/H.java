package te050c.app;

import te050c.tl.Form;
import te050c.tl.TL;
import te050c.tl.Util;
import te050c.tl.json.Output;
import te050c.tl.db.tbl.*;

import java.io.IOException;
import java.lang.reflect.Field;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by moh on 18/12/16.
 */
public class H extends Tbl {

	@Override public List creationDBTIndices(TL tl){
		return Util.lst(
			Util.lst("int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
			,"int(24) NOT NULL"//parent
			,"text not null DEFAULT ''"//jsonBld
			,"text not null DEFAULT ''"//srvrJS
		));/*

in "te050c.2016.12.15.17.32.sql" file ( includes data)

CREATE TABLE `h` (
  `no` int(24) NOT NULL DEFAULT '0',
  `parent` int(1) NOT NULL DEFAULT '0',
  `jsonBld` text NOT NULL DEFAULT '',
  `srvrJS` text NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `x` (
  `sect` varchar(255) DEFAULT NULL,
  `i` varchar(255) DEFAULT NULL,
  `v` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ideas of xhr.jsp:
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

*/}

	@F public Integer no,parent;
	@F public String jsonBld,srvrJS;

	Object eval;

 @Override public String getName() {return dbtName;}


 public enum C implements CI{no,parent,jsonBld,srvrJS;
		@Override public Class<? extends Tbl>cls(){return H.class;}
		@Override public Class<? extends Form>clss(){return cls();}
		@Override public String text(){return name();}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public Tbl tbl(){return Tbl.tbl(cls());}
		@Override public void save(){tbl().save(this);}
		@Override public Object load(){return tbl().load(this);}
		@Override public Object value(){return val(tbl());}
		@Override public Object value(Object v){return val(tbl(),v);}
		@Override public Object val(Form f){return f.v(this);}
		@Override public Object val(Form f,Object v){return f.v(this,v);}
	}//C

	@Override public CI pkc(){return H.C.no;}
	@Override public Object pkv(){return no;}
	@Override public CI[]columns(){return H.C.values();}
	public static final String dbtName="h";

public H(H p){copy(p);}

public H copy(H p){
	for (C c:C.values())
		v(c,p.v(c));
	return this;}

public H loadParent(){H p=new H(this);
	p=(H)p. load(parent);
	return p;}

public List<H> loadChildren(){
	List l=new LinkedList<H>();
	H p=new H(this),x;
	for (Tbl t: p.query(Tbl.where(C.parent,no))
	     ) {x=new H(p);
		l.add(x);
	}
	return l;}

	Output output(Output o) throws IOException {
		outputRec(o);
		o.p("\njsonBldOnload(",no,");\n");
		return o;}

/*	Output outputRecur(Output o) throws IOException {
		o.p("loadJsonBld(",no,',',jsonBld,',',srvrJS,",[");
		List<H>a=loadChildren();boolean comma=false;
		for (H h:a ) {
			if (comma)o.p(',');else comma=true;
			o.p(h.no);
		}o.p("])\n");
		for (H h:a )
			h.outputRecur(o);
		return o;}*/

	Output outputRec(Output o) throws IOException {
		o.p("loadJsonBld(",no,',',parent,',',jsonBld,',',srvrJS,");\n");

		boolean comma=false;
		H x=new H(this);
		for (Tbl t:x.query(Tbl.where(C.parent,no))) {
			if (comma)o.p(',');else comma=true;
			x.outputRec(o);
		}
		return o;}

}//class h Tbl
