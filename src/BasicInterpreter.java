import stone.BasicParser;
import stone.Lexer;
import stone.ParseException;
import stone.Token;
import stone.ast.ASTree;
import stone.ast.NullStatement;

//解释器
public class BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        run(new BasicParser(),new BasicEnvironment());
    }
    public static void run(BasicParser basicParser,Environment environment) throws ParseException {
        Lexer lexer = new Lexer(new CodeDialog());
        while (lexer.peek(0)!= Token.TOKEN_EOF){
            ASTree asTree = basicParser.parse(lexer);
            if (!(asTree instanceof NullStatement)){
                Object eval = ((BasicEvaluator.ASTreeEx) asTree).eval(environment);
                System.out.println("=>"+eval);
            }

        }
    }
}
