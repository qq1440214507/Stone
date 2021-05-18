package stone.ast;

import java.util.List;
//单目减法运算符
public class NegativeExpr extends ASTList{
    public NegativeExpr(List<ASTree> children) {
        super(children);
    }
    public ASTree operand(){
        return  child(0);
    }
    @Override
    public String toString(){
        return "-" + operand();
    }
}
