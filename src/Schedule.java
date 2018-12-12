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

        for (int i = 0; i < sortedList.size(); i++){
            Process p = sortedList.get(i);
            if (t < p.arrival){
                t = p.arrival;
            }
            t += p.service;

            p.turnaround = t - p.arrival;
        }
    }

    // perform the Shortest Job First algorithm
    private void sjf(){
        int finished = 0;
        int t = 0;
        int nextIdx = 0;    // next arrival index in sortedList
        ArrayList<Process> waiting = new ArrayList<Process>();

        while (finished < sortedList.size()){
            // update current time to next arrival if no waiting process
            if (waiting.isEmpty()){
                if (t < sortedList.get(nextIdx).arrival)
                    t = sortedList.get(nextIdx).arrival;
            }

            // add new arrival to waiting
            while (nextIdx < sortedList.size() && sortedList.get(nextIdx).arrival <= t){
                waiting.add(sortedList.get(nextIdx));
                nextIdx++;
            }

            // find the shortest job in waiting queue
            Process curr = waiting.get(0);
            for (int i = 1; i < waiting.size(); i++){
                Process tmp = waiting.get(i);
                if (tmp.service < curr.service || (tmp.service == curr.service && tmp.id < curr.id) ){
                    curr = tmp;
                }
            }
            waiting.remove(curr);

            t += curr.service;

            // calculate turnaround time
            curr.turnaround = t - curr.arrival;

            finished++;
        }

    }

    // perform the Shortest Remain Time algorithm
    private void srt(){
        int finished = 0;
        int t = 0;
        int nextIdx = 0;    // next arrival index in sortedList
        ArrayList<Process> waiting = new ArrayList<Process>();

        while (finished < sortedList.size()){
            // update current time to next arrival if no waiting process
            if (waiting.isEmpty()){
                if (t < sortedList.get(nextIdx).arrival){
                    t = sortedList.get(nextIdx).arrival;
                }
            }

            // add new arrival to waiting
            while (nextIdx < sortedList.size() && sortedList.get(nextIdx).arrival <= t){
                waiting.add(sortedList.get(nextIdx));
                nextIdx++;
            }

            // find the shortest remaining job in waiting queue
            Process curr = waiting.get(0);
            for (int i = 1; i < waiting.size(); i++){
                Process tmp = waiting.get(i);
                if (tmp.remain < curr.remain || (tmp.remain == curr.remain && tmp.id < curr.id) ){
                    curr = tmp;
                }
            }

            waiting.remove(curr);

            // is this process preempted by next arrival process?
            if ( nextIdx < sortedList.size() && t + curr.remain > sortedList.get(nextIdx).arrival){
                curr.remain -= (sortedList.get(nextIdx).arrival - t);

                // add back to waiting
                waiting.add(curr);

                // update time to next arrival
                t = sortedList.get(nextIdx).arrival;
            }
            else{
                t += curr.remain;
                // calculate turnaround time
                curr.turnaround = t - curr.arrival;

                finished++;
            }

        }
    }

    // perform the Multilevel Feedback algorithm
    private void mlf(){
        MLFQueue[] q = new MLFQueue[N];
        int t = T;
        for (int i = 0; i < N; i++){
            q[i] = new MLFQueue();
            q[i].piece = t;
            q[i].queue = new LinkedList<Process>();
            t *= 2;
        }

        int nextIdx = 0;
        int finished = 0;
        int i = 0;

        t = sortedList.get(nextIdx).arrival;

        while (finished < sortedList.size()){
            // find highest priority queue which is not empty
            for (i = 0; i < N; i++){
                if (!q[i].queue.isEmpty()){
                    break;
                }
            }

            // if no waiting process, update time to next arrival
            if (i == N && nextIdx < sortedList.size() && t < sortedList.get(nextIdx).arrival)
                t = sortedList.get(nextIdx).arrival;

            // add new arrival to first queue
            while (nextIdx < sortedList.size() && sortedList.get(nextIdx).arrival <= t){
                Process tmp = sortedList.get(nextIdx);
                nextIdx++;

                // add to first queue
                q[0].queue.add(tmp);
                i = 0;
            }

            // run piece
            Process curr = q[i].queue.peek();

            int burst = q[i].piece;
            if (burst > curr.remain){
                burst = curr.remain;
            }

            if (i > 0 && nextIdx < sortedList.size() && sortedList.get(nextIdx).arrival < t + burst){
                burst = sortedList.get(nextIdx).arrival - t;
            }
            if (curr.qtime + burst > q[i].piece){
                burst = q[i].piece - curr.qtime;
            }

            t += burst;
            curr.remain -= burst;
            curr.qtime += burst;

            if (curr.remain == 0){
                // finished, remove from the queue
                q[i].queue.poll();

                // calculate turnaround time
                curr.turnaround = t - curr.arrival;

                finished++;
            }
            else if (curr.qtime == q[i].piece){
                // complete all allocated time, move to next queue
                q[i].queue.poll();

                if (i < N-1)
                    i++;
                curr.qtime = 0;

                // add to next waiting queue
                q[i].queue.add(curr);
            }
            else{
                // prempted by arrival process
            }

        }
    }

    // run the 4 test
    public void runTest(String infname, String outfname) throws IOException{
        outfile = new PrintWriter(outfname);
        loadFile(infname);

        fcfs();
        outputResult();

        sjf();
        outputResult();

        srt();
        outputResult();

        mlf();
        outputResult();

        outfile.close();

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
