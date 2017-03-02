package te050c.tl;

import java.io.File;

public enum Context {
	ROOT(
			"/public_html/theblueone/te050c/v1/"
			,"/Users/moh/Google Drive/air/apache-tomcat-8.0.30/webapps/ROOT/"//"/Users/moh/apache-tomcat-8.0.30/webapps/ROOT/"
			,"D:\\apache-tomcat-8.0.15\\webapps\\ROOT/"
			);
		String str,a[];Context(String...p){str=p[0];a=p;}
		public enum DB{
			pool("dbpool-adoqs-te050c")
			,reqCon("javax.sql.PooledConnection")
			,server("localhost")
			,dbName("te050c")
			,un("root")
			,pw("qwerty","root","");
			public String str,a[];DB(String...p){str=p[0];a=p;}
		}


		public static String getRealPath(TL t,String path){
			String real=t.getServletContext().getRealPath(path);
			boolean b=true;
			try{File f=null;
				if(real==null){int i=0;
					while( i<ROOT.a.length && (b=(f==null|| !f.exists())) )
						try{
							f=new File(ROOT.a[i++]);
						}catch(Exception ex){}//t.error
					real=(b?"./":f.getCanonicalPath())+path;
				}
			}catch(Exception ex){
				t.error(ex,"te050c.TL.context.getRealPath:",path);}
			return real==null?"./"+path:real;}

}
