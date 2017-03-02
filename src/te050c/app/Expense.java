package te050c.app;

import te050c.tl.Form;
import te050c.tl.TL;
import te050c.tl.Util;
import te050c.tl.db.tbl.*;

import java.lang.reflect.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by moh on 18/12/16.
 */
public class Expense extends Tbl {

@Override public List creationDBTIndices(TL tl){
	return Util.lst(Util.lst("int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT"//row
		," varchar(255) DEFAULT NULL"//f
		," varchar(255) DEFAULT NULL"//i
		," datetime NOT NULL DEFAULT '0000-00-00 00:00:00'"//d
		," int(7) NOT NULL"//q
		," decimal(10,3) NOT NULL"//p
		," decimal(10,3) DEFAULT NULL"//x
		)
		,Util.lst(Util.lst(C.f,C.i,C.d)
			,Util.lst(C.d,C.x,C.i)
			,Util.lst(C.d,C.i,C.x)
			,Util.lst(C.f,C.d,C.x)
			,Util.lst(C.i,C.x,C.d)
			,Util.lst(C.i,C.d,C.f)));
	/*

attached rar of mysqlDump and xhr.jsp


CREATE TABLE `Expense` (
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

*/}

@F public Integer row;
@F public String f,i;
@F public Date d;
@F public Integer q;
@F public Float p,x;

@Override public String getName() {return dbtName;}


public enum C implements CI{row,f,i,d,q,p,x;
	@Override public Class<? extends Tbl>cls(){return Expense.class;}
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

@Override public CI pkc(){return Expense.C.row;}
@Override public Object pkv(){return row;}
@Override public CI[]columns(){return Expense.C.values();}
public static final String dbtName="Expense";

}//class Expense Tbl
