package entity;

public class Token {
    public enum Type{


        KEYWORD("keyword"),SYMBOL("symbol"),
        IDENTIFIER("identifier"),INT_CONST("integerConstant"),
        STRING_CONST("stringConstant");

        String str;

        Type(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public Type type;
    public String content;

    public Token(Type type, String content) {
        this.type = type;
        this.content = content;
    }
}
