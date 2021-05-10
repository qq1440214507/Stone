package stone.ast;

import java.util.Iterator;

//抽象语法树
public abstract class ASTree implements Iterable<ASTree>{
    //获取第几个子节点
    public abstract ASTree child(int i);
    //子节点个数
    public abstract int numChildren();
    //子节点迭代器
    public abstract Iterator<ASTree> children();

    public Iterator<ASTree> iterator(){
        return children();
    }

    public abstract String location();
}
