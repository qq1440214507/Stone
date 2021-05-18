import javassist.gluonj.util.Loader;

//解释器启动程序
public class InterpreterRunner {
    public static void main(String[] args) throws Throwable {
        Loader.run(BasicInterpreter.class,args,BasicEvaluator.class);
    }
}
