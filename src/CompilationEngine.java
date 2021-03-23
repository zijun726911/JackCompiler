import entity.Token;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;


public class CompilationEngine {
    JackTokenizer jt;
    Document document;




    public CompilationEngine(JackTokenizer jt) {
        this.jt=jt;
        Document document =  DocumentHelper.createDocument();
        this.document=document;

    }

    public void writeXML(String fileName) throws Exception {
        OutputFormat format = OutputFormat.createPrettyPrint();

        format.setExpandEmptyElements(true);
        format.setSuppressDeclaration(true);
        format.setTrimText(false);
        File file_=new File(fileName.split("\\.")[0]+"_.xml");
        File file=new File(fileName);
        FileWriter fileWriter= new FileWriter(file);


        // 输出xml文件
            XMLWriter writer = new XMLWriter(new FileOutputStream(file_), format);
            writer.write(document);
            writer.close();
//            System.out.println("dom4j CreateDom4j success!");



        FileReader fileReader  = new FileReader(file_);
        Long len = file_.length();
        char content[]=new char[len.intValue()-1];
//        System.out.println("len:"+content.length);
        fileReader.read();
        fileReader.read(content);
        fileReader.close();
        fileWriter.write(content);
        fileWriter.close();
        file_.delete();


    }

    public void compileClass(){

        Token token;


        Element classEle=document.addElement("class");
        classEle.addElement("keyword").addText(jt.advance().content);//class
        classEle.addElement("identifier").addText(jt.advance().content);//className
        classEle.addElement("symbol").addText(jt.advance().content);//{

        while((token=jt.advance()).content.equals("field")||token.content.equals("static") ){

            compileClassVarDec(classEle,token);
        }


        while(token.content.equals("function")||token.content.equals("method")||token.content.equals("constructor") ){

            compileSubroutineDec(classEle,token);
            token=jt.advance();



        }
        classEle.addElement("symbol").addText("}");//}



    }

    public void compileClassVarDec(Element parentEle,Token token){//variable declaration
        Element classVarDecEle = parentEle.addElement("classVarDec");
        classVarDecEle.addElement("keyword").addText(token.content);//field of static
        token=jt.advance();
        classVarDecEle.addElement(token.type.toString()).addText(token.content);//type name
        classVarDecEle.addElement("identifier").addText(jt.advance().content);//varName
        token=jt.advance();
        while(token.content.equals(",")){
            classVarDecEle.addElement(token.type.toString()).addText(token.content);//,
            token=jt.advance();
            classVarDecEle.addElement(token.type.toString()).addText(token.content);//varName
            token=jt.advance();
        }
        classVarDecEle.addElement(token.type.toString()).addText(token.content);//;
    }

    public void compileSubroutineDec(Element parentEle,Token token){
        Element subroutineDecEle = parentEle.addElement("subroutineDec");
        subroutineDecEle.addElement("keyword").addText(token.content);//method
        token=jt.advance();
        subroutineDecEle.addElement(token.type.toString()).addText(token.content);//void
        token=jt.advance();
        subroutineDecEle.addElement(token.type.toString()).addText(token.content);///funName
        token=jt.advance();
        subroutineDecEle.addElement(token.type.toString()).addText(token.content);//(

        token=compileParameterList(subroutineDecEle,jt.advance());


        subroutineDecEle.addElement("symbol").addText(")");

        compileSubroutineBody(subroutineDecEle);




    }

    public void compileSubroutineBody(Element parentEle){
        Element subroutineBodyEle = parentEle.addElement("subroutineBody");
        subroutineBodyEle.addElement("symbol").addText(jt.advance().content);//{
        Token token= jt.advance() ;
        while(token.content.equals("var")){
            token=compileVarDec(subroutineBodyEle,token);
        }

        token=compileStatements(subroutineBodyEle,token);
        subroutineBodyEle.addElement("symbol").addText(token.content);//}




        //}





    }

    public Token compileParameterList(Element parentEle,Token token){//variable declaration
        Element parameterListEle = parentEle.addElement("parameterList");
        if(token.content.equals(")")){
            parameterListEle.addText("\n");
            return token;
        }

        while (!(token).content.equals(")")){
            parameterListEle.addElement(token.type.toString()).addText(token.content);
            token=jt.advance();
        }

        return token;
    }

    public Token compileVarDec(Element parentEle,Token token){//compile one variable declaration
        Element varDecEle=parentEle.addElement("varDec");

        varDecEle.addElement("keyword").addText(token.content);//var
        while (!(token =jt.advance()).content.equals(";")){
            varDecEle.addElement(token.type.toString()).addText(token.content); //varName and ,

        }
        varDecEle.addElement("symbol").addText(";");//keyword or identifier
        return jt.advance();


    }

    public Token compileStatements(Element parentEle,Token token){
        Element statementsEle=parentEle.addElement("statements");


        while (token.content.equals("if")||token.content.equals("let")
                ||token.content.equals("while")||token.content.equals("do")
                ||token.content.equals("return")){
            if(token.content.equals("if")){

                token=compileIf(statementsEle,token);
            }
            else if(token.content.equals("let")) {

                token=compileLet(statementsEle,token);
            }
            else if(token.content.equals("while")) {

                token=compileWhile(statementsEle,token);
            }
            else if(token.content.equals("do")) {

                token=compileDo(statementsEle,token);
            }

            else if(token.content.equals("return")) {

                token=compileReturn(statementsEle,token);
            }


        }

        return token;




    }

    public Token compileLet(Element parentElement,Token token)  {

        if(!token.content.equals("let")) try {
            throw new Exception("let entry fault");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element letStatementEle=parentElement.addElement("letStatement");
        letStatementEle.addElement(token.type.toString()).addText(token.content);//"let"
        token=jt.advance();
        letStatementEle.addElement(token.type.toString()).addText(token.content);//identifier, varName

        token=jt.advance();

        if(token.content.equals("[")){
            letStatementEle.addElement(token.type.toString()).addText(token.content);//[
            token=compileExpression(letStatementEle,jt.advance());
            letStatementEle.addElement("symbol").addText("]");//]
            token=jt.advance();
        }
        letStatementEle.addElement(token.type.toString()).addText(token.content);//"="

        token=compileExpression(letStatementEle,jt.advance());

        letStatementEle.addElement(token.type.toString()).addText(token.content);//";"
        return jt.advance();



    }

    public Token compileIf(Element parentElement,Token token){
        Element ifStatementEle=parentElement.addElement("ifStatement");

        ifStatementEle.addElement(token.type.toString()).addText(token.content);//"if"
        ifStatementEle.addElement("symbol").addText(jt.advance().content);//"("
        token=compileExpression(ifStatementEle,jt.advance());
        ifStatementEle.addElement("symbol").addText(token.content);//")"
        ifStatementEle.addElement("symbol").addText(jt.advance().content);//"{"
        token=compileStatements(ifStatementEle,jt.advance());
        ifStatementEle.addElement(token.type.toString()).addText(token.content);//"}"
        token = jt.advance();
//        System.out.println("in ifStat "+token.type.toString()+" "+token.content);
        if(token.content.equals("else")){
            ifStatementEle.addElement(token.type.toString()).addText(token.content);//"else"
            ifStatementEle.addElement("symbol").addText(jt.advance().content);//"{"
            token=compileStatements(ifStatementEle,jt.advance());
            ifStatementEle.addElement("symbol").addText(token.content);//"}"
            token = jt.advance();
        }

        return token;


    }

    public Token compileWhile(Element parentElement,Token token){
        Element whileStatementEle=parentElement.addElement("whileStatement");
        whileStatementEle.addElement(token.type.toString()).addText(token.content) ; //"while"
        whileStatementEle.addElement("symbol").addText(jt.advance().content);//"("
        token=compileExpression(whileStatementEle,jt.advance());
        whileStatementEle.addElement("symbol").addText(token.content);//")"
        whileStatementEle.addElement("symbol").addText(jt.advance().content);//"{"
        token=compileStatements(whileStatementEle,jt.advance());
        whileStatementEle.addElement("symbol").addText(token.content);//"}"
        return jt.advance();

    }

    public Token compileDo(Element parentElement,Token token){

        Element doStatementEle=parentElement.addElement("doStatement");

        doStatementEle.addElement(token.type.toString()).addText(token.content);//"do"
        token=compileSubroutineCall(doStatementEle,jt.advance());
        if(!token.content.equals(";")) try {
            throw new Exception("do fault");
        } catch (Exception e) {
            e.printStackTrace();
        }
        doStatementEle.addElement("symbol").addText(token.content);//";"

        return jt.advance();
    }

    public Token compileSubroutineCall(Element parentElement,Token token){
        parentElement.addElement(token.type.toString()).addText(token.content);//subroutineName or className or varName
        token=jt.advance();
        parentElement.addElement(token.type.toString()).addText(token.content);// ”(“ or "."
        if(token.content.equals("(")){//method call
            token=compileExpressionList(parentElement, jt.advance());
            parentElement.addElement(token.type.toString()).addText(token.content);// ”)“

        }else if(token.content.equals(".")){//static function call
            token=jt.advance();
            parentElement.addElement(token.type.toString()).addText(token.content);// subroutineName
            token=jt.advance();
            parentElement.addElement(token.type.toString()).addText(token.content);//(
            token=compileExpressionList(parentElement,jt.advance());
            parentElement.addElement(token.type.toString()).addText(token.content);//)

        }else {
            try {
                throw new Exception("compileSubroutineCall error");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return jt.advance();
    }

    public Token compileReturn(Element parentElement,Token token){
        Element returnStatementEle=parentElement.addElement("returnStatement");

        returnStatementEle.addElement(token.type.toString()).addText(token.content);//"return"
         token=jt.advance();
        if(!token.content.equals(";")){
            token=compileExpression(returnStatementEle,token);

        }

        returnStatementEle.addElement("symbol").addText(token.content);//";"
        return jt.advance();
    }

    public Token compileExpression(Element parentElement,Token token){
        Element expressionEle=parentElement.addElement("expression");

        token=compileTerm(expressionEle,token);

        while(isOp(token)){
            expressionEle.addElement("symbol").addText(token.content);
            token=compileTerm(expressionEle, jt.advance());

        }

        return token;
    }

    public Token compileTerm(Element parentElement,Token token){
        Element termEle=parentElement.addElement("term");
        if(token.type== Token.Type.INT_CONST||token.type== Token.Type.STRING_CONST||iskeywordConstant(token)){
            termEle.addElement(token.type.toString()).addText(token.content);
            token=jt.advance();
        }else if(token.content.equals("(")){ //(expression)
            termEle.addElement(token.type.toString()).addText(token.content);//(
            token=compileExpression(termEle,jt.advance());
            termEle.addElement(token.type.toString()).addText(token.content);//)
            token=jt.advance();

        }else if(token.type== Token.Type.IDENTIFIER){ //varName or  Varname[expression] or subroutineCall,
            termEle.addElement(token.type.toString()).addText(token.content);//subroutine name, varName or Classname
            token=jt.advance();
            if(token.content.equals("[")){//Varname[expression]
                termEle.addElement(token.type.toString()).addText(token.content);//[
                token=compileExpression(termEle,jt.advance());
                termEle.addElement(token.type.toString()).addText(token.content);//]
                token=jt.advance();

            }else if (token.content.equals("(")||token.content.equals(".")){// subroutine call
                termEle.addElement(token.type.toString()).addText(token.content);//( or .

                if(token.content.equals("(")){//method call
                    token=compileExpressionList(termEle, jt.advance());
                    termEle.addElement(token.type.toString()).addText(token.content);// ”)“


                }else if(token.content.equals(".")){//static function call
                    token=jt.advance();
                    termEle.addElement(token.type.toString()).addText(token.content);// subroutineName
                    token=jt.advance();
                    termEle.addElement(token.type.toString()).addText(token.content);//(
                    token=compileExpressionList(termEle,jt.advance());
                    termEle.addElement(token.type.toString()).addText(token.content);//)

                }

                token=jt.advance();
            }


        }else if(isUnaryop(token)){
            termEle.addElement(token.type.toString()).addText(token.content);//unaryOp
            token=compileTerm(termEle,jt.advance());

        }


        return token;

    }

    public Token compileExpressionList(Element parentElement,Token token){
        Element expressionListEle=parentElement.addElement("expressionList");
        if (token.content.equals(")")){
            expressionListEle.addText("\n");
            return token;
        }
        token=compileExpression(expressionListEle,token);

         while(token.content.equals(",")){
             expressionListEle.addElement("symbol").addText(token.content);//,
             token=compileExpression(expressionListEle,jt.advance());

         }
         return token;

    }


    private boolean isOp(Token token){
        return Pattern.matches("\\+|\\-|\\*|\\/|\\&|\\||\\<|\\>|\\=",token.content);
    }
    private boolean isUnaryop(Token token){
        return Pattern.matches("\\-|~",token.content);
    }
    private boolean iskeywordConstant(Token token){
        return Pattern.matches("true|false|null|this",token.content);
    }





}
