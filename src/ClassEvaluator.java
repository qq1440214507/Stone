import javassist.gluonj.Require;
import javassist.gluonj.Reviser;
import stone.StoneException;
import stone.ast.ASTree;
import stone.ast.ClassBody;
import stone.ast.ClassStatement;
import stone.ast.Dot;

import java.util.List;

@Require(FuncEvaluator.class)
@Reviser
public class ClassEvaluator {
    @Reviser
    public static class ClassStatementEx extends ClassStatement{

        public ClassStatementEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            ClassInfo ci = new ClassInfo(this,env);
            ((FuncEvaluator.EnvEx) env).put(name(),ci);
            return name();
        }
    }
    @Reviser
    public static class ClassBodyEx extends ClassBody{

        public ClassBodyEx(List<ASTree> children) {
            super(children);
        }
        public Object eval(Environment env){
            for (ASTree asTree : this) {
                ((BasicEvaluator.ASTreeEx) asTree).eval(env);
            }
            return null;
        }
    }
    @Reviser
    public static class DotEx extends Dot{

        public DotEx(List<ASTree> children) {
            super(children);
        }

        public Object eval(Environment env,Object value){
            String member = name();
            if (value instanceof ClassInfo){
                if ("new".equals(member)){
                    ClassInfo ci = (ClassInfo) value;
                    NestedEnv e = new NestedEnv(ci.environment());
                    StoneObject so = new StoneObject(e);
                    e.put("this",e);
                    initObject(ci,e);
                    return  so;

                }
            }else if (value instanceof StoneObject){
                try{
                    return ((StoneObject) value).read(member);
                }catch (StoneObject.AccessException e){}
            }
            throw new StoneException("bad member access: "+member,this);
        }
        public void initObject(ClassInfo ci,Environment env){
            if (ci.superClass()!=null){
                initObject(ci.superClass(),env);
            }
            ((ClassBodyEx) ci.body()).eval(env);
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
            if (left instanceof FuncEvaluator.PrimaryEx){
                FuncEvaluator.PrimaryEx p = (FuncEvaluator.PrimaryEx) left;
                if (p.hasPostfix(0) && p.postfix(0) instanceof Dot){
                    Object t = ((FuncEvaluator.PrimaryEx) left).evalSubExpr(env, 1);
                    if (t instanceof StoneObject){
                        return setField(((StoneObject) t), ((Dot) p.postfix(0)),rvalue);
                    }
                }
            }
            return super.computeAssign(env, rvalue);
        }
        protected Object setField(StoneObject obj,Dot expr,Object rvalue){
            String name = expr.name();
            try{
                obj.write(name,rvalue);
                return rvalue;
            }catch (StoneObject.AccessException accessException){
                throw new StoneException("bad number access "+location()+": "+name);
            }
        }
    }
}
