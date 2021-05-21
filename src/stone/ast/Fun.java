package stone.ast;

import java.util.List;

public class Fun extends ASTList{
    public Fun(List<ASTree> children) {
        super(children);
    }
    public ParameterList parameters(){
        return (ParameterList) child(0);
    }
    public BlockStatement body(){
        return ((BlockStatement) child(1));
    }
    public String toString(){
        return "(fun "+parameters()+" "+body()+")";
    }
}
