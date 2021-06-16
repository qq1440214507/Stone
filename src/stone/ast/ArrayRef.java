package stone.ast;

import java.util.List;

public class ArrayRef extends Postfix{
    public ArrayRef(List<ASTree> children) {
        super(children);
    }
    public ASTree index(){
        return child(0);
    }
    public String toString(){
        return "["+index()+"]";
    }
}
