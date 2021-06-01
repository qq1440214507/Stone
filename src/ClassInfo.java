import stone.StoneException;
import stone.ast.ClassBody;
import stone.ast.ClassStatement;

public class ClassInfo {
    protected ClassStatement definition;
    protected Environment environment;
    protected ClassInfo superClass;
    public ClassInfo(ClassStatement cs,Environment env){
        definition = cs;
        environment = env;
        Object obj = env.get(cs.superClass());
        if (obj == null){
            superClass = null;
        }else if (obj instanceof ClassInfo){
            superClass = (ClassInfo) obj;
        }else {
            throw new StoneException("unknown super class: "+cs.superClass(),cs);
        }
    }
    public String name(){
        return definition.name();
    }
    public ClassInfo superClass(){
        return superClass;
    }
    public ClassBody body(){
        return definition.body();
    }
    public Environment environment(){
        return  environment;
    }

    @Override
    public String toString() {
        return "<class "+name()+">";
    }
}
