package stone.ast;

import stone.Token;

//数字字面量
public class NumberLiteral extends ASTLeaf{
    public NumberLiteral(Token token) {
        super(token);
    }
    public int value(){
        return token().getNumber();
    }
}
