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

    // read the file, and save the process in the list
    private void loadFile(String infname) throws IOException{
        Scanner scfile = new Scanner(new File(infname));
        while (scfile.hasNextInt()){
            Process p = new Process();
            p.id = orgList.size();
            p.arrival = scfile.nextInt();
            p.service = scfile.nextInt();
            orgList.add(p);
            sortedList.add(p);
        }
        scfile.close();

        // sort the list by arrival time
        for (int i = 1; i < sortedList.size(); i++){
            int j = i;
            while (j > 0 && (sortedList.get(j-1).arrival > sortedList.get(j).arrival)){
                Process p = sortedList.get(j-1);
                sortedList.set(j-1, sortedList.get(j));
                sortedList.set(j, p);
                j--;
            }
        }

        resetProcess();
    }

    // reset all the process for next algorithm
    private void resetProcess(){
        for (int i = 0; i < orgList.size(); i++){
            Process p = orgList.get(i);
            p.qtime = 0;
            p.remain = p.service;
            p.turnaround = 0;
        }
    }

    // output the result to outf
    private void outputResult(){
        double total = 0;
        for (int i = 0; i < orgList.size(); i++){
            Process p = orgList.get(i);
            total += p.turnaround;
        }
        outfile.printf("%.2f", ((int)(total * 100/orgList.size()))/100.0);

        for (int i = 0; i < orgList.size(); i++){
            outfile.printf(" %d", orgList.get(i).turnaround);
        }
        outfile.println();

        resetProcess();
    }

    // perform the First Come First Serve algorithm
    private void fcfs(){
        int t = 0;

        t = sortedList.get(0).arrival;
        for (int i = 0; i < sortedList.size(); i++){
            Process p = sortedList.get(i);
            t += p.service;

            p.turnaround = t - p.arrival;
        }
    }

    // run the 4 test
    public void runTest(String infname, String outfname) throws IOException{
        outfile = new PrintWriter(outfname);
        loadFile(infname);

        fcfs();
        outputResult();

        
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
