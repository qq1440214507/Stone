package stone.ast;

import java.util.Iterator;
import java.util.List;

public class ASTList extends ASTree{
    protected List<ASTree> children;

    public ASTList(List<ASTree> children) {
        this.children = children;
    }

    @Override
    public ASTree child(int i) {
        return children.get(i);
    }

    @Override
    public int numChildren() {
        return children.size();
    }

    @Override
    public Iterator<ASTree> children() {
        return children.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(' ');
        builder.append('(');
        String sep = "";
        for (ASTree tree : children) {
            builder.append(sep);
            sep = " ";
            builder.append(tree.toString());
        }
        return builder.append(')').toString();
    }

    @Override
    public String location() {
        for (ASTree tree : children) {
            String s = tree.location();
            if (s != null) {
                return s;
            }
        }

        return null;
    }
}
