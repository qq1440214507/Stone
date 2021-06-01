import javassist.gluonj.util.Loader;

public class ClassRunner {
    public static void main(String[] args) throws Throwable {
        Loader.run(ClassInterpreter.class,args,ClassEvaluator.class,NativeEvaluator.class,ClosureEvaluator.class);;
    }
}
