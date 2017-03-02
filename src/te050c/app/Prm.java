package te050c.app;

import java.util.Map;


public enum Prm{op(Op.class)

    ;Class<?>c;Prm(Class<?>p){c=p;}

    enum Op{none,login,logout
        ,projNew,projEdit,projDelete,projDuplicate
        ,pageNew,pageEdit,pageDelete,pageDuplicate
        ,htmlNew,htmlEdit,htmlDelete,htmlDuplicate
        ,projLoad,pageLoad,htmlLoad
        ,appOp
    }//enum Op

    enum UserLevel{Manage,Edit,View ,Suspended}

}//enum Prm