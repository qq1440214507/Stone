import javassist.gluonj.Require;
import javassist.gluonj.Reviser;
import stone.ArrayParser;
import stone.StoneException;
import stone.ast.*;

import java.util.List;

@Require({FuncEvaluator.class, ArrayParser.class})
@Reviser
public class ArrayEvaluator {
    @Reviser
    public static class ArrayListEx extends ArrayLiteral{

        public ArrayListEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            int s = numChildren();
            Object[] res = new Object[s];
            int i =0;
            for (ASTree asTree : this) {
                res[i++] = ((BasicEvaluator.ASTreeEx) asTree).eval(env);
            }
            return  res;
        }
    }
    @Reviser
    public static class ArrayRefEx extends ArrayRef{

        public ArrayRefEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env,Object value){
            if (value instanceof Object[]){
                Object index = ((BasicEvaluator.ASTreeEx) index()).eval(env);
                if (index instanceof Integer){
                    return ((Object[]) value)[(int) index];
                }
            }
            throw new StoneException("bad array access",this);
        }
    }
    @Reviser
    public static class AssignEx extends BasicEvaluator.BinaryEx{

        public AssignEx(List<ASTree> children) {
            super(children);
        }

        @Override
        protected Object computeAssign(Environment env, Object rvalue) {
            ASTree left = left();
            if (left instanceof PrimaryExpr){
                FuncEvaluator.PrimaryEx primaryEx = (FuncEvaluator.PrimaryEx) left;
                if (primaryEx.hasPostfix(0) && primaryEx.postfix(0) instanceof ArrayRef){
                    Object object = primaryEx.evalSubExpr(env, 1);
                    if (object instanceof Object[]){
                        ArrayRef arrayRef = (ArrayRef) primaryEx.postfix(0);
                        Object index = ((BasicEvaluator.ASTreeEx) arrayRef.index()).eval(env);
                        if (index instanceof Integer){
                            ((Object[]) object)[(int) index] = rvalue;
                            return rvalue;
                        }
                    }
                    throw new StoneException("bad array access",this);
                }
            }
            return super.computeAssign(env, rvalue);
        }
    }
}
