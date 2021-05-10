package stone.ast;

import java.util.List;

//双目运算表达式 如 x+z
public class BinaryExpr extends ASTList{
    public BinaryExpr(List<ASTree> children) {
        super(children);
    }
    //获取左边的值
    public ASTree left(){
        return child(0);
    }
    //获取操作符
    public String operator(){
        return ((ASTLeaf) child(1)).token().getText();
    }

    //获取右边的值
    public ASTree right(){
        return child(2);
    }
}
