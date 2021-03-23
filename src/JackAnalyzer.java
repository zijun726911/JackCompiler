import java.io.File;

public class JackAnalyzer {

  public static void main(String[] args) throws InterruptedException {

      File filePath=new File(args[0]);//Square.jack
//      File filePath=new File("D:\\nand2tetris2\\JackCompiler\\ExpressionLessSquare");//Square.jack




      if(filePath.isDirectory()){
          File[] files = filePath.listFiles();

          for(File file:files){
              if(!file.toString().split("\\.")[1].equals("jack")) continue;

              String xmlFileName=file.toString().split("\\.")[0]+".xml";

              JackTokenizer jt = new JackTokenizer(file);
              CompilationEngine ce = new CompilationEngine(jt);
              ce.compileClass();
              try {
                  ce.writeXML(xmlFileName);
              } catch (Exception e) {
                  e.printStackTrace();
              }

          }
      }else {//compile one file
          if(!filePath.toString().split("\\.")[1].equals("jack")) return;
          JackTokenizer jt = new JackTokenizer(filePath);
          CompilationEngine ce = new CompilationEngine(jt);
          ce.compileClass();
          String xmlFileName=filePath.toString().split("\\.")[0]+".xml";

          try {
              ce.writeXML(xmlFileName);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }




  }


}
