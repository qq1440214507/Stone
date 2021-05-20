package stone.ast;

import java.util.List;

public class Arguments extends Postfix{


    public Arguments(List<ASTree> children) {
        super(children);
    }
    public int size(){
        return numChildren();
    }
}
