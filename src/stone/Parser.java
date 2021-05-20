package stone;

import stone.ast.ASTLeaf;
import stone.ast.ASTList;
import stone.ast.ASTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Parser {
    protected static abstract class Element {
        protected abstract void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException;

        protected abstract boolean match(Lexer lexer) throws ParseException;
    }

    protected static class Tree extends Element {
        protected Parser parser;

        protected Tree(Parser parser) {
            this.parser = parser;
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException {
            asTrees.add(parser.parse(lexer));
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    protected static class OrTree extends Element {
        protected Parser[] parsers;

        protected OrTree(Parser[] parsers) {
            this.parsers = parsers;
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException {
            Parser parser = choose(lexer);
            if (parser == null) {
                throw new ParseException(lexer.peek(0));
            } else {
                asTrees.add(parser.parse(lexer));
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return choose(lexer) != null;
        }

        protected Parser choose(Lexer lexer) throws ParseException {
            for (Parser parser : parsers) {
                if (parser.match(lexer)) {
                    return parser;
                }
            }
            return null;
        }

        protected void insert(Parser parser) {
            Parser[] newParsers = new Parser[this.parsers.length + 1];
            newParsers[0] = parser;
            System.arraycopy(parsers, 0, newParsers, 1, parsers.length);
            parsers = newParsers;
        }

    }

    protected static class Repeat extends Element {
        protected Parser parser;
        protected boolean onlyOnce;

        protected Repeat(Parser parser, boolean onlyOnce) {
            this.parser = parser;
            this.onlyOnce = onlyOnce;
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException {
            while (parser.match(lexer)) {
                ASTree asTree = parser.parse(lexer);
                if (asTree.getClass() != ASTList.class || asTree.numChildren() > 0) {
                    asTrees.add(asTree);
                }
                if (onlyOnce) {
                    break;
                }
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    protected abstract static class AbstractToken extends Element {
        protected Factory factory;

        protected AbstractToken(Class<? extends ASTLeaf> clazz) {
            if (clazz == null) {
                clazz = ASTLeaf.class;
            }
            factory = Factory.get(clazz, Token.class);
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException {
            Token token = lexer.read();
            if (test(token)) {
                ASTree leaf = factory.make(token);
                asTrees.add(leaf);
            } else {
                throw new ParseException(token);
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return test(lexer.peek(0));
        }

        protected abstract boolean test(Token token);
    }

    protected static class IdToken extends AbstractToken {
        HashSet<String> reserved;

        protected IdToken(Class<? extends ASTLeaf> clazz, HashSet<String> reserved) {
            super(clazz);
            this.reserved = reserved == null ? new HashSet<>() : reserved;
        }

        @Override
        protected boolean test(Token token) {
            return token.isIdentifier() && !reserved.contains(token.getText());
        }
    }

    protected static class NumToken extends AbstractToken {

        protected NumToken(Class<? extends ASTLeaf> clazz) {
            super(clazz);
        }

        @Override
        protected boolean test(Token token) {
            return token.isNumber();
        }
    }
    protected static class StringToken extends AbstractToken{

        protected StringToken(Class<? extends ASTLeaf> clazz) {
            super(clazz);
        }

        @Override
        protected boolean test(Token token) {
            return token.isString();
        }
    }

    protected static class Leaf extends Element{
        protected String[] tokens;
        protected Leaf(String[] tokens){
            this.tokens = tokens;
        }
        @Override
        protected void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException {
            Token token = lexer.read();
            if (token.isIdentifier()){
                for (String stringToken : tokens) {
                    if (stringToken.equals(token.getText())){
                        find(asTrees,token);
                        return;
                    }
                }
            }
            if (tokens.length>0){
                throw new ParseException(tokens[0] + " expected",token);
            }else {
                throw new ParseException(token);
            }
        }
        protected void find(List<ASTree> asTrees,Token token){
            asTrees.add(new ASTLeaf(token));
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            Token token = lexer.peek(0);
            if (token.isIdentifier()){
                for (String stringToken : tokens) {
                    if (stringToken.equals(token.getText())){
                        return true;
                    }
                }
            }
            return false;
        }
    }
    protected static class Skip extends Leaf{
        protected Skip(String[] tokens){
            super(tokens);
        }

        @Override
        protected void find(List<ASTree> asTrees, Token token) {

        }
    }
    public static class Precedence{
        int value;
        boolean leftAssoc;

        public Precedence(int value, boolean leftAssoc) {
            this.value = value;
            this.leftAssoc = leftAssoc;
        }
    }

    public static class Operators extends HashMap<String,Precedence>{
        public static boolean LEFT = true;
        public static boolean RIGHT = false;
        public void add(String name,int precedence,boolean leftAssoc){
            put(name,new Precedence(precedence,leftAssoc));
        }
    }
    protected static class Expr extends Element{
        protected Factory factory;
        protected Operators operators;
        protected Parser parser;
        protected Expr(Class<? extends ASTree> clazz,Parser parser,Operators operators){
            factory = Factory.getForASTList(clazz);
            this.operators = operators;
            this.parser = parser;
        }

        @Override
        protected void parse(Lexer lexer, List<ASTree> asTrees) throws ParseException {
            ASTree right = parser.parse(lexer);
            Precedence precedence;
            while ((precedence = nextOperator(lexer)) != null){
                right = doShift(lexer,right,precedence.value);
            }
            asTrees.add(right);
        }
        private ASTree doShift(Lexer lexer,ASTree left,int precedence) throws ParseException {
            ArrayList<ASTree> list = new ArrayList<>();
            list.add(left);
            list.add(new ASTLeaf(lexer.read()));
            ASTree right = parser.parse(lexer);
            Precedence next;
            while ((next = nextOperator(lexer))!=null && rightIsExpr(precedence,next)){
                right = doShift(lexer,right,next.value);
            }
            list.add(right);
            return factory.make(list);
        }
        private Precedence nextOperator(Lexer lexer) throws ParseException{
            Token token = lexer.peek(0);
            if (token.isIdentifier()){
                return operators.get(token.getText());
            }else {
                return null;
            }
        }

        private static boolean rightIsExpr(int precedence,Precedence nextPrecedence){
            if (nextPrecedence.leftAssoc){
                return precedence < nextPrecedence.value;
            }else {
                return precedence <= nextPrecedence.value;
            }
        }

        @Override
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    public static final String factoryName = "create";

    protected static abstract class Factory {
        protected abstract ASTree make0(Object arg) throws Exception;

        protected ASTree make(Object arg) {
            try {
                return make0(arg);
            } catch (IllegalArgumentException e1) {
                throw e1;
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }

        @SuppressWarnings("unchecked")
        protected static Factory getForASTList(Class<? extends ASTree> clazz) {
            Factory f = get(clazz, List.class);
            if (f == null) {
                f = new Factory() {
                    @Override
                    protected ASTree make0(Object arg) throws Exception {
                        List<ASTree> results = (List<ASTree>) arg;
                        return results.size() == 1 ? results.get(0) : new ASTList(results);
                    }
                };
            }
            return f;
        }

        protected static Factory get(Class<? extends ASTree> clazz, Class<?> argType) {
            if (clazz == null) {
                return null;
            }
            try {
                final Method method = clazz.getMethod(factoryName, argType);
                return new Factory() {
                    @Override
                    protected ASTree make0(Object arg) throws Exception {
                        return (ASTree) method.invoke(null, arg);
                    }
                };
            } catch (NoSuchMethodException ignored) {
            }
            try {
                final Constructor<? extends ASTree> constructor = clazz.getConstructor(argType);
                return new Factory() {
                    @Override
                    protected ASTree make0(Object arg) throws Exception {
                        return constructor.newInstance(arg);
                    }
                };
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

    }


    protected List<Element> elements;
    protected Factory factory;

    protected Parser(Parser parser) {
        this.elements = parser.elements;
        this.factory = parser.factory;
    }

    public Parser(Class<? extends ASTree> clazz) {
        reset(clazz);
    }

    public ASTree parse(Lexer lexer) throws ParseException {
        ArrayList<ASTree> results = new ArrayList<>();
        for (Element element : elements) {
            element.parse(lexer, results);
        }
        return factory.make(results);
    }

    protected boolean match(Lexer lexer) throws ParseException {
        if (elements.size() == 0) {
            return true;
        }
        Element element = elements.get(0);
        return element.match(lexer);
    }

    public static Parser rule() {
        return rule(null);
    }

    public static Parser rule(Class<? extends ASTree> clazz) {
        return new Parser(clazz);
    }

    public Parser reset() {
        elements = new ArrayList<>();
        return this;
    }

    public Parser reset(Class<? extends ASTree> clazz) {
        elements = new ArrayList<>();
        factory = Factory.getForASTList(clazz);
        return this;
    }

    public Parser number() {
        return number(null);
    }

    public Parser number(Class<? extends ASTLeaf> clazz) {
        elements.add(new NumToken(clazz));
        return this;
    }
    public Parser identifier(HashSet<String> reserved){
        return identifier(null,reserved);
    }
    public Parser identifier(Class<? extends ASTLeaf> clazz,HashSet<String> reserved){
        elements.add(new IdToken(clazz,reserved));
        return this;
    }
    public Parser string() {
        return string(null);
    }
    public Parser string(Class<? extends ASTLeaf> clazz) {
        elements.add(new StringToken(clazz));
        return this;
    }

    public Parser token(String... pat){
        elements.add(new Leaf(pat));
        return this;
    }
    public Parser sep(String... pat){
        elements.add(new Skip(pat));
        return this;
    }
    public Parser ast(Parser parser){
        elements.add(new Tree(parser));
        return this;
    }
    public Parser or(Parser... parsers){
        elements.add(new OrTree(parsers));
        return this;
    }
    public Parser maybe(Parser parser){
        Parser newParser = new Parser(parser);
        newParser.reset();
        elements.add(new OrTree(new Parser[]{parser,newParser}));
        return this;
    }
    public Parser option(Parser parser){
        elements.add(new Repeat(parser,true));
        return this;
    }
    public Parser repeat(Parser parser){
        elements.add(new Repeat(parser,false));
        return this;
    }
    public Parser expression(Parser sub,Operators operators){
        elements.add(new Expr(null,sub,operators));
        return this;
    }
    public Parser expression(Class<? extends ASTree> clazz,Parser sub,Operators operators){
        elements.add(new Expr(clazz,sub,operators));
        return this;
    }
    public Parser insertChoice(Parser parser){
        Element element = elements.get(0);
        if (element instanceof OrTree){
            ((OrTree) element).insert(parser);
        }else {
            Parser otherwise = new Parser(this);
            reset(null);
            or(parser,otherwise);
        }
        return this;
    }

}
