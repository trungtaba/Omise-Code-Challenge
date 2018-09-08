package codechallenge;
//import codechallenge.ParseJson.ParseJson;
import codechallenge.MatchingEngine.MatchingEngine;
import codechallenge.MatchingEngine.MatchingType;
import java.io.File;

public class CodeChallenge {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //ParseJson parseJson=new ParseJson();
        MatchingEngine matchingEngine=new MatchingEngine();
        matchingEngine.Matching(MatchingType.File);
//        String pathDirIn="src/codechallenge/File/input/";
//        String pathDirOut="src/codechallenge/File/output/";
//        File directory=new File(pathDirIn);
//        int fileCount=directory.list().length;
//        for(int i=1;i<=fileCount;i++){
//            String fileInput=pathDirIn + "input"+i+".json";
//            String filePathOutput=pathDirOut + "output"+i+".json";
//            matchingEngine.MatchingFromFile(fileInput,filePathOutput);
//            matchingEngine.FormatOrderList();
//        }  
    } 
}
