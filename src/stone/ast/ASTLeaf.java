package stone.ast;

import stone.Token;

import java.util.ArrayList;
import java.util.Iterator;

//叶子节点
public class ASTLeaf extends ASTree{
    private static final ArrayList<ASTree> empty = new ArrayList<>();

    protected Token token;

    public ASTLeaf(Token token) {
        this.token = token;
    }

    @Override
    public ASTree child(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int numChildren() {
        return 0;
    }

    @Override
    public Iterator<ASTree> children() {
        return empty.iterator();
    }

    @Override
    public String toString() {
        return token.getText();
    }

    @Override
    public String location() {
        return "at line "+token.getLineNumber();
    }
    public Token token(){
        return token;
    }
}
