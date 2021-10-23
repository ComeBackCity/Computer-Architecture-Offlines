import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class test {
    List<String> operations = new ArrayList<>();
    List<String> registers  = new ArrayList<>();
    HashMap<String ,Integer> labels = new HashMap<>();

    public test(){
        operations.add("sub");
        operations.add("nor");
        operations.add("j");
        operations.add("sll");
        operations.add("addi");
        operations.add("and");
        operations.add("srl");
        operations.add("ori");
        operations.add("subi");
        operations.add("sw");
        operations.add("andi");
        operations.add("lw");
        operations.add("or");
        operations.add("add");
        operations.add("beq");
        operations.add("bneq");

        registers.add("$zero");
        registers.add("$t0");
        registers.add("$t1");
        registers.add("$t2");
        //registers.add("$t2");
        registers.add("$t3");
        registers.add("$t4");
        registers.add("$sp");


    }

    String _2sComplement(String bin){

        String twos = "", ones = "";

        for (int i = 0; i < bin.length(); i++) {
            ones += flip(bin.charAt(i));
        }
        int number0 = Integer.parseInt(ones, 2);
        StringBuilder builder = new StringBuilder(ones);
        boolean b = false;
        for (int i = ones.length() - 1; i > 0; i--) {
            if (ones.charAt(i) == '1') {
                builder.setCharAt(i, '0');
            } else {
                builder.setCharAt(i, '1');
                b = true;
                break;
            }
        }
        if (!b)
            builder.append("1", 0, 7);

        twos = builder.toString();

        return twos;
    }

    public char flip(char c) {
        return (c == '0') ? '1' : '0';
    }

    public  String getbitBinary(int dec,int len){
        String bin = Integer.toBinaryString(dec);
        String zero = "0";

        for(int i=bin.length();i<len;i++){
            bin  = zero.concat(bin);
        }

        return bin;
    }

    public  String gethexBinary(String binary,int len){
        if(binary.equalsIgnoreCase("")){
            return "";
        }

        int binToDec = Integer.parseInt(binary,2);
        String hex   = Integer.toHexString(binToDec);
        String zero = "0";

        for(int i=hex.length();i<len;i++){
            hex  = zero.concat(hex);
        }

        return hex;
    }

    public void label_parse(File file){
        int labelCount = 0;
        Scanner sc = null;
        String line,str;
        int lineNo = 0;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc.hasNextLine()){
            line = sc.nextLine();
            lineNo++;
            if(line.contains(":")){
                str = line.trim().substring(0,line.length()-1);
                //System.out.println("--------------");
                System.out.println(str);
                labels.put(str,lineNo-labelCount);
                labelCount++;
                //System.out.println("--------------");
            }
        }
    }

    public String getMachineCode(String line,int lineNo){
        String machineCode = "";

        if(line.contains(":")){
            line = line.trim().replace(":","");
            //labels.put(line,lineNo);
        }

        else {
            String[] codes;
            String opcode;
            String[] oprands = new String[5];
            String src1,src2,dst,shift,imd;
            int flag = 0;

            /*line = line.replaceFirst(" ",",");
            line = line.replaceAll("\\s+","");*/

            line = line.replaceFirst(" ",",").replaceAll("\\s+","");

            codes = line.trim().split(",");

            opcode = codes[0];
            /*for(int i=1;i<codes.length;i++){
                oprands[i-1] = codes[i];
            }*/


            machineCode += getbitBinary(operations.indexOf(opcode),4);
            if(flag == 1){

            }
             else if(opcode.equalsIgnoreCase("srl") || opcode.equalsIgnoreCase("sll")){
                /*src2   = oprands[1];
                dst    = oprands[0];
                shift  = oprands[2];*/

                src2   = codes[2];
                dst    = codes[1];
                shift  = codes[3];


                machineCode += getbitBinary(0,4);
                machineCode += getbitBinary(registers.indexOf(src2), 4);
                machineCode += getbitBinary(registers.indexOf(dst),  4);
                machineCode += getbitBinary(Integer.parseInt(shift), 4); //shift = 0

            }

            else if(opcode.equalsIgnoreCase("lw") || opcode.equalsIgnoreCase("sw") ){
                String[] param ;
                String imdpos;
                String immediateBin1,immediateBin2;

                /*oprands[1] = oprands[1].replace("("," ");
                oprands[1] = oprands[1].replace(")"," ");

                param = oprands[1].split(" ");

                imd  = param[0];
                src1 = param[1];
                src2 = oprands[0];*/

                codes[2] = codes[2].replace("("," ");
                codes[2] = codes[2].replace(")"," ");

                param = codes[2].split(" ");

                imd  = param[0];
                src1 = param[1];
                src2 = codes[1];

                if(imd.charAt(0) == '-'){
                    imdpos = imd.substring(1,imd.length());
                    immediateBin1 = getbitBinary(Integer.parseInt(imdpos),8);
                    immediateBin2 = _2sComplement(immediateBin1);
                }
                else {
                    imdpos = imd;
                    immediateBin2 = getbitBinary(Integer.parseInt(imdpos),8);
                }

                //System.out.println(imd);

                machineCode += getbitBinary(registers.indexOf(src1), 4);
                machineCode += getbitBinary(registers.indexOf(src2),4);
                machineCode += immediateBin2;

            }
            else if(opcode.equalsIgnoreCase("j")){
                int address =0 ;
                //int address = labels.get(oprands[0]);
                System.out.println(Arrays.asList(labels));
                System.out.println(codes[1]);
                try{
                    address = labels.get(codes[1]);}
                catch (NullPointerException e){
                    e.printStackTrace();
                }
                System.out.println(address);

                machineCode += getbitBinary(address,8);
                machineCode += getbitBinary(0,8);
            }

            else if(opcode.equalsIgnoreCase("beq") || opcode.equalsIgnoreCase("bneq")){
                int adress;

                src1 = codes[2];
                src2 = codes[1];
                adress = labels.get(codes[3]);

                machineCode += getbitBinary(registers.indexOf(src1),4);
                machineCode += getbitBinary(registers.indexOf(src2),4);
                machineCode += getbitBinary(adress,8);
            }

            else {
                /*src1 = oprands[1];
                src2 = oprands[2];
                dst  = oprands[0];*/

                src1 = codes[2];
                src2 = codes[3];
                dst  = codes[1];

                machineCode += getbitBinary(registers.indexOf(src1), 4);    //source 1 --> common for all

                if(registers.contains(src2)){
                    machineCode += getbitBinary(registers.indexOf(src2),4); //source 2
                    machineCode += getbitBinary(registers.indexOf(dst), 4); //destination
                    machineCode += getbitBinary(0,4);                  //shift = 0
                }

                else{
                    String s = null;
                    boolean positive = true;
                    if(src2.substring(0,1).equalsIgnoreCase("-")){
                        s = src2.substring(1, src2.length());
                        positive = false;
                    }
                    else
                        s = src2;
                    if( !positive  && opcode.equalsIgnoreCase("addi")) {
                        machineCode = getbitBinary(operations.indexOf("subi"),4) + machineCode.substring(4,8);
                    }
                    else if( !positive && opcode.equalsIgnoreCase("subi")){
                        machineCode = getbitBinary(operations.indexOf("addi"),4) + machineCode.substring(4,8);

                    }
                    machineCode += getbitBinary(registers.indexOf(dst), 4); // destination
                    machineCode += getbitBinary(Integer.parseInt(s), 8); // immediate value

                }



            }
        }

        return  machineCode;
    }

    public void processMIPS() {
        File file = new File("input.txt");
        Scanner sc = null;
        String str = "";
        FileWriter writer;
        int lineNo = 0;

        label_parse(file);

        System.out.println(Arrays.asList(labels));

        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            writer = new FileWriter("output.txt", false);
            writer.write("v2.0 raw\n406ff\n");

            while (sc.hasNextLine()) {
                lineNo++;
                str = sc.nextLine().trim();
                System.out.println(str);

                if(str == null || str.isEmpty()) continue;

                String machineCode = getMachineCode(str,lineNo);
                System.out.println(machineCode);
                String hexCode     = gethexBinary(machineCode,5);

                if(!machineCode.equalsIgnoreCase("")) {
                    System.out.println(hexCode);
                    writer.write(hexCode);
                    writer.write("\r\n");   // write new line
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test t = new test();

        t.processMIPS();

        //System.out.println(t._2sComplement("00100"));
    }
}


