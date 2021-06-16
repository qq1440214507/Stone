package stone.ast;

import java.util.List;

public class ArrayLiteral extends ASTList{
    public ArrayLiteral(List<ASTree> children) {
        super(children);
    }
    public int size(){
        return numChildren();
    }
}
