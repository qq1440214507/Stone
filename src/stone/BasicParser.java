package stone;

import stone.Parser.Operators;
import stone.ast.*;

import static stone.Parser.rule;
import java.util.HashSet;

//语法分析器
public class BasicParser {
    HashSet<String> reserved = new HashSet<>();
    Operators operators = new Operators();
    Parser expr0 = rule();
    Parser primary = rule(PrimaryExpr.class)
            .or(rule().sep("(").ast(expr0).sep(")"),
                    rule().number(NumberLiteral.class),
                    rule().identifier(Name.class,reserved),
                    rule().string(StringLiteral.class)
                    );
    Parser factor = rule().or(rule(NegativeExpr.class).sep("-").ast(primary),primary);
    Parser expr = expr0.expression(BinaryExpr.class,factor,operators);
    Parser statement0 = rule();
    Parser block = rule(BlockStatement.class)
            .sep("{").option(statement0)
            .repeat(rule().sep(";",Token.EOL).option(statement0))
            .sep("}");
    Parser simple = rule(PrimaryExpr.class).ast(expr);
    Parser statement = statement0.or(
        rule(IfStatement.class).sep("if").ast(expr).ast(block)
        .option(rule().sep("else").ast(block)),
        rule(WhileStatement.class).sep("while").ast(expr).ast(block),
        simple
    );
    Parser program = rule().or(statement,rule(NullStatement.class))
            .sep(";",Token.EOL);
    public BasicParser(){
        reserved.add(";");
        reserved.add("}");
        reserved.add(Token.EOL);
         //Operators 标识左结合还是右结合 也就是优先级
        // 1 + 2 + 3
        //左结合为：（1+2）+3
        operators.add("=",1,Operators.RIGHT);
        operators.add("==",2,Operators.LEFT);
        operators.add(">",2,Operators.LEFT);
        operators.add("<",2,Operators.LEFT);
        operators.add("+",3,Operators.LEFT);
        operators.add("-",3,Operators.LEFT);
        operators.add("*",4,Operators.LEFT);
        operators.add("/",4,Operators.LEFT);
        operators.add("%",4,Operators.LEFT);
    }
    public ASTree parse(Lexer lexer) throws ParseException{
        return program.parse(lexer);
    }

}
