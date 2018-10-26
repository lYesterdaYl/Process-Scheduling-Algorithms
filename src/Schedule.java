import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;



public class Schedule {

    // run the 4 test
    public void runTest(String infname, String outfname) throws IOException{
        
    }
    // The main method, start the project
    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            System.out.println("Usage: java Schedule <inputfile> <outputfile>");
            return;
        }

        Schedule sche = new Schedule();
        sche.runTest(args[0], args[1]);
    }
}
