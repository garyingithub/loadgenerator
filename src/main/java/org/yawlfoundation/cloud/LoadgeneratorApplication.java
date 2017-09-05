package org.yawlfoundation.cloud;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.yawlfoundation.cloud.metrics.InfluxDBGaugeWriter;

import java.util.Scanner;

import java.util.concurrent.TimeUnit;

/**
 * Created by gary on 21/04/2017.
 */
@SpringBootApplication
public class LoadgeneratorApplication {

    public static InfluxDBGaugeWriter writer;
    @Bean
    public CommandLineRunner runner(final Server server, final InfluxDBGaugeWriter writer){

        return new CommandLineRunner() {


            @Override
            public void run(String... strings) throws Exception {
                    LoadgeneratorApplication.writer=writer;
           //     final String[] specLocations = {"C:\\Users\\garyc\\Dropbox\\Performance Management\\Simulation.yawl"};

          //      final String[] logLocations={"C:\\Users\\garyc\\Dropbox\\Performance Management\\Idea Projects\\LogExecutorService\\target\\docker\\Simulation"};

                final String[] specLocations = {"/home/gary/experiment/logexperiment/Simulation.yawl"};

                final String[] logLocations={"/home/gary/experiment/logexperiment/Simulation"};

                final String[] specNames={"twoCycle"};
                Process[] processes=new Process[specLocations.length];

                for(int i=0;i<specLocations.length;i++){
                    processes[i]=new Process(specNames[i],specLocations[i],logLocations[i],Server.interval);
                }


                int count=0;
                while (true){

                    Scanner scanner=new Scanner(System.in);

                    int times=scanner.nextInt();

                    for(int i=0;i<times;){

                        for(Process process:processes){

                           // if(i%2==0){
                                server.launchCase(process.getInstances().get((count%process.getInstances().size())));
                          //  }else {
                           //     server.launchCase(process.getInstances().get(((count+1000)%process.getInstances().size())));
                         //   }
                            TimeUnit.MILLISECONDS.sleep(1000/times);

                              i++;
                           // TimeUnit.MILLISECONDS.sleep(1*Server.interval);
                        }
                        count++;

                    }

                }


            }
        };



    }


    public static void main(String[] args) {


        SpringApplication.run(LoadgeneratorApplication.class);

    }
}
