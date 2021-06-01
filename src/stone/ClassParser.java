package stone;
import stone.ast.ClassBody;
import stone.ast.ClassStatement;
import stone.ast.Dot;

import static stone.Parser.rule;
public class ClassParser extends ClosureParser{
    Parser member = rule().or(def,simple);
    Parser class_body = rule(ClassBody.class).sep("{")
            .option(member).repeat(rule().sep(";",Token.EOL).option(member))
            .sep("}");
    Parser defClass = rule(ClassStatement.class).sep("class").identifier(reserved)
            .option(rule().sep("extends").identifier(reserved)).ast(class_body);

    public ClassParser(){
        postfix.insertChoice(rule(Dot.class).sep(".").identifier(reserved));
        program.insertChoice(defClass);
    }
}
