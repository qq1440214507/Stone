import javassist.gluonj.Reviser;
import stone.StoneException;
import stone.Token;
import stone.ast.*;

import java.util.List;

@Reviser
public class BasicEvaluator {
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    @Reviser
    public static abstract class ASTreeEx extends ASTree {
        public abstract Object eval(Environment env);
    }

    @Reviser
    public static  class ASTListEx extends ASTList {

        public ASTListEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            throw new StoneException("cannot eval: "+toString(),this);
        }
    }
    @Reviser
    public static class ASTLeafEx extends ASTLeaf{

        public ASTLeafEx(Token token) {
            super(token);
        }
        public Object eval(Environment env){
            throw new StoneException("cannot eval: "+toString(),this);
        }
    }
    @Reviser
    public static class NumberEx extends NumberLiteral{

        public NumberEx(Token token) {
            super(token);
        }
        public Object eval(Environment env){
            return value();
        }
    }
    @Reviser
    public static class StringEx extends StringLiteral{

        public StringEx(Token token) {
            super(token);
        }
        public Object eval(Environment env){
            return value();
        }
    }
    @Reviser
    public static class NameEx extends Name{

        public NameEx(Token token) {
            super(token);
        }
        public Object eval(Environment env){
            Object value = env.get(name());
            if (value==null){
                throw new StoneException("undefined name: "+name(),this);
            }else {
                return value;
            }
        }
    }


    @Reviser
    public static class NegativeEx extends NegativeExpr{

        public NegativeEx(List<ASTree> children) {
            super(children);
        }

        public Object eval(Environment env){
            Object value = ((ASTreeEx) operand()).eval(env);
            if (value instanceof Integer){
                return -(Integer) value;
            }else {
                throw new StoneException("bad type for -",this);
            }
        }
    }
    @Reviser
    public static class BinaryEx extends BinaryExpr{

        public BinaryEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            String operator = operator();
            if ("=".equals(operator)){
                Object right = ((ASTreeEx) right()).eval(env);
                return computeAssign(env,right);
            }else {
                Object left = ((ASTreeEx) left()).eval(env);
                Object right = ((ASTreeEx) right()).eval(env);
                return computeOp(left,operator,right);
            }
        }

        protected Object computeAssign(Environment env,Object rvalue){
            ASTree l = left();
            if (l instanceof Name){
                env.put(((Name) l).name(),rvalue);
                return rvalue;
            }else {
                throw new StoneException("bad assignment",this);
            }
        }
        protected Object computeOp(Object left,String op,Object right){
            if (left instanceof Integer && right instanceof Integer){
                return computeNumber(((Integer) left),op, ((Integer) right));
            }else{
                if (op.equals("+")){
                    return String.valueOf(left)+ right;
                }else if (op.equals("==")){
                    if (left == null){
                        return right == null?TRUE:FALSE;
                    }else {
                        return left.equals(right)?TRUE:FALSE;
                    }
                }else {
                    throw new StoneException("bad type",this);
                }
            }
        }
        protected Object computeNumber(Integer left,String op,Integer right){
            int leftValue = left;
            int rightValue = right;
            switch (op) {
                case "+":
                    return leftValue + rightValue;
                case "-":
                    return leftValue - rightValue;
                case "*":
                    return leftValue * rightValue;
                case "/":
                    return leftValue / rightValue;
                case "%":
                    return leftValue % rightValue;
                case "==":
                    return leftValue == rightValue ? TRUE : FALSE;
                case ">":
                    return leftValue > rightValue ? TRUE : FALSE;
                case "<":
                    return leftValue < rightValue ? TRUE : FALSE;
                default:
                    throw new StoneException("bad operator", this);
            }
        }
    }
    @Reviser
    public static class BlockEx extends BlockStatement{

        public BlockEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            Object result = 0;
            for (ASTree asTree : this) {
                if (!(asTree instanceof NullStatement)){
                    result = ((ASTreeEx) asTree).eval(env);
                }
            }
            return result;
        }
    }
    @Reviser
    public static class IfEx extends IfStatement{

        public IfEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            Object con = ((ASTreeEx) condition()).eval(env);
            if (con instanceof Integer && (Integer) con !=FALSE){
                return ((ASTreeEx) thenBlock()).eval(env);
            }else {
                ASTree asTree = elseBlock();
                if (asTree == null){
                    return 0;
                }else {
                    return ((ASTreeEx) asTree).eval(env);
                }

            }
        }
    }
    @Reviser
    public static class WhileEx extends WhileStatement{

        public WhileEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            Object result = 0;
            for (;;){
                Object con = ((ASTreeEx) condition()).eval(env);
                if (con instanceof Integer && ((Integer) con == FALSE)){
                    return result;
                }else {
                    result = ((ASTreeEx) body()).eval(env);
                }
            }
        }
    }
}
