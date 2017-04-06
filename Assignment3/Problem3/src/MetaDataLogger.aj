import org.aspectj.lang.reflect.MethodSignature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Keenan on 2017-04-04.
 */
public aspect MetaDataLogger {

    BufferedWriter bw = null;
    FileWriter fw = null;
    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    Date startDate = null;
    long startTime;
    Date endDate = null;
    long endTime;
    String commandArgs = "";

    pointcut simCall(): (call(* ConwaysGameOfLife.simulate(..)));
    pointcut writeCall(): (call(* ConwaysGameOfLife.writeState(..)));

    before(): simCall(){
        //Grab all Args
        for (Object arg: thisJoinPoint.getArgs())
        {
            //Get String of Args (this looks weird, I know. Trust me on this)
            commandArgs = Arrays.toString((String[]) arg);

            //Set start Date and start time
            startDate = new Date();
            startTime = System.currentTimeMillis();


        }
    }

    after() returning(): writeCall(){
        //Get our end times as this is the end
        endDate = new Date();
        endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        //Make our new file for logging (or attempt to)
        File logFile = new File("CongwaysGoL-Log.txt");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw = new FileWriter(logFile.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write("========================================\n");
            bw.write("Conway's Game of Life Simulation started\n");
            bw.write("Arguments given: " + commandArgs + "\n");
            bw.write("Start Time: " + df.format(startDate.getTime()) + "\n");
            bw.write("End Time: " + df.format(endDate.getTime()) + "\n");
            bw.write("Total Run Time: " + totalTime + "ms\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }



    }
}
