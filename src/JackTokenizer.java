import entity.Token;


import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer {

    Matcher matcher;
    String allCode;
    final String keywordRegex="(class|constructor|function|method|field|static|var|int|char" +
            "|boolean|void|true|false|null|this|let|do|" +
            "if|else|while|return)";


    final String symbolRegex="(\\{|\\}|\\(|\\)|\\[|\\]" +
            "|\\.|\\,|\\;|\\+|\\-|\\*" +
            "|\\/|\\&|\\||\\<|\\>|\\=|\\~)";

    final String skipRegex= "(//.*)|(/\\*(.|[\\r\\n])*?\\*/)|(\\s+)";


    final String stringConstantRegex= "(\\\"([^\\\"]*)\\\")";

    final  String integerConstantRegex= "(\\d+)";
    final  String identifierRegex= "([a-zA-Z_][a-zA-Z\\d_]*)";



    public JackTokenizer(File jackFile) {


        String encoding = "UTF-8";

        Long filelength = jackFile.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(jackFile);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
             this.allCode=new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            this.allCode= null;
        }

        Pattern allPattern=Pattern.compile(skipRegex+"|"+symbolRegex+
                "|"+keywordRegex+"|"+stringConstantRegex
                +"|"+integerConstantRegex+"|"+identifierRegex);
        Matcher matcher = allPattern.matcher(allCode);
        this.matcher=matcher;


    }


    public Token advance(){
        Token.Type type = null;

        while(matcher.find()){

            String word=matcher.group(0);
            if(Pattern.matches(skipRegex,word)) continue;
            else if(Pattern.matches(symbolRegex,word))  type=Token.Type.SYMBOL;
            else if(Pattern.matches(keywordRegex,word))type=Token.Type.KEYWORD;
            else if(Pattern.matches(stringConstantRegex,word)){
                word=word.replaceAll("\"","");
                type=Token.Type.STRING_CONST;
            }
            else if(Pattern.matches(integerConstantRegex,word))type=Token.Type.INT_CONST;
            else if(Pattern.matches(identifierRegex,word)) type=Token.Type.IDENTIFIER; //identifier
//            System.out.println(type+" "+word);
            this.matcher=matcher;
            return new Token(type,word);
        }
        return null;





    }


}
