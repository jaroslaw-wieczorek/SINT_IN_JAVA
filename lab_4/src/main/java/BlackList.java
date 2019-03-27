import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class BlackList {

    LinkedList<String> blackList = new LinkedList<String>();


    public LinkedList<String> getBlackList(){
        String blackListFile = "blacklist.txt";
        String line;
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(blackListFile));

            while ((line = br.readLine()) != null) {
                if (!blackList.contains(line)){
                    blackList.add(line);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  blackList;
    }

}