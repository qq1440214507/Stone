import javassist.gluonj.Require;
import javassist.gluonj.Reviser;
import stone.StoneException;
import stone.ast.ASTree;

import java.util.List;

@Require(FuncEvaluator.class)
@Reviser
public class NativeEvaluator {
    @Reviser
    public static class NativeArgEx extends FuncEvaluator.ArgumentsEx{

        public NativeArgEx(List<ASTree> children) {
            super(children);
        }

        @Override
        public Object eval(Environment callerEnv, Object value) {
            if (!(value instanceof NativeFunction)){
                return super.eval(callerEnv, value);
            }
            NativeFunction nativeFunction = (NativeFunction) value;
            int nParams = nativeFunction.numOfParameters();
            if (size()!=nParams){
                throw new StoneException("bad number of arguments",this);
            }
            Object[] args = new Object[nParams];
            int num = 0;
            for (ASTree asTree : this) {
                BasicEvaluator.ASTreeEx asTreeEx = (BasicEvaluator.ASTreeEx) asTree;
                args[num++] = asTreeEx.eval(callerEnv);
            }
            return nativeFunction.invoke(args,this);
        }
    }
}
