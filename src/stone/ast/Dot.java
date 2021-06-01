package stone.ast;

import java.util.List;

public class Dot extends Postfix{
    public Dot(List<ASTree> children) {
        super(children);
    }
    public String name(){
        return ((ASTLeaf) child(0)).token().getText();
    }
    public String toString(){
        return "."+name();
    }
}
