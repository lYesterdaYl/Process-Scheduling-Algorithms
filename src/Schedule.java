import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;



public class Schedule {

    public static final int N = 5;  // queue size for MLF
    public static final int T = 1;  // time piece size

    // record the information of the process
    class Process{
        int id;
        int arrival;
        int service;

        int remain;    // used by srt

        int qtime;     // spent in current queue, used by mlf

        int turnaround;  // final result
    }

    class MLFQueue{
        Queue<Process> queue;
        int piece;   // time piece for this queue
    }

    private PrintWriter outfile;

    private ArrayList<Process> orgList;
    private ArrayList<Process> sortedList;

    // default constructor
    public Schedule(){
        orgList = new ArrayList<Process>();
        sortedList  = new ArrayList<Process>();
    }

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
