import javassist.gluonj.util.Loader;

public class ArrayRunner {
    public static void main(String[] args) throws Throwable {
        Loader.run(ClassInterpreter.class,args,ClassEvaluator.class,ArrayEvaluator.class,NativeEvaluator.class,ClosureEvaluator.class);
    }
}
