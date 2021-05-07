package stone;

/**
 * token对象，词法分析器解析的结果对象
 */
public abstract class Token {
    public static final Token TOKEN_EOF = new Token(-1) {
    };//程序结束
    public static final String EOL = "\\n";//换行符
    private final int lineNumber;

    protected Token(int line) {
        this.lineNumber = line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    //是否是标识符 变量名，函数名，类名 + -标点符号等
    public boolean isIdentifier() {
        return false;
    }

    //是否是整形字面量
    public boolean isNumber() {
        return false;
    }

    //是否是字符串
    public boolean isString() {
        return false;
    }

    public int getNumber() {
        throw new StoneException("not number token");
    }
    public String getText(){
        return "";
    }
}
