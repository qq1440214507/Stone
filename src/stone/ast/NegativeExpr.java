package stone.ast;

import java.util.List;

public class NegativeExpr extends ASTList{
    public NegativeExpr(List<ASTree> children) {
        super(children);
    }
    public ASTree operate(){
        return  child(0);
    }
    @Override
    public String toString(){
        return "-" + operate();
    }
}
