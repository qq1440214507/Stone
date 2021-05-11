import stone.BasicParser;
import stone.Lexer;
import stone.ParseException;
import stone.Token;
import stone.ast.ASTree;

public class ParserRunner {
    public static void main(String[] args) throws ParseException {
        Lexer l = new Lexer(new CodeDialog());
        BasicParser basicParser = new BasicParser();
        while (l.peek(0) != Token.TOKEN_EOF){
            ASTree asTree = basicParser.parse(l);
            System.out.println("=>"+asTree.toString());
        }
    }
}
