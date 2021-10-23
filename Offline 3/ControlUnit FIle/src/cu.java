import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class cu {
    public static void main(String[] args) {
        String input= "cu.txt";
        File inputFile = new File(input);
        String s = "";
        FileWriter fw ;
        Scanner sc = null;

        try {
            sc = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fw = new FileWriter("output.txt");
            fw.write("v2.0 raw\n");

            while (sc.hasNextLine()){
                String str= sc.nextLine();
                int decimal = Integer.parseInt(str,2);
                String hexStr = Integer.toString(decimal,16);
                s+= hexStr + "\n";
            }

            fw.write(s);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
