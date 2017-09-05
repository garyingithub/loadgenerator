package org.yawlfoundation.cloud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gary on 16/04/2017.
 */
public class ProcessInstance {


    private Process process;
    private String id;

    public String getId() {
        return id;
    }

    private List<Pair<String,Long>> taskDurationList;
    final private Logger logger= LoggerFactory.getLogger(ProcessInstance.class);

    public ProcessInstance(String id,Process process){
        this.id=id;
        this.taskDurationList=new ArrayList<>();
        this.process=process;
    }

    public String getSpecLocation(){
        return process.getLocation();
    }

    public String getSpecName(){
        return process.getName();
    }

    public void addTask(String taskName,Long durationTime){
        this.taskDurationList.add(new Pair<>(taskName, durationTime ));

    }


    public int getMod(){
        return process.getInstances().size();
    }

    // 单位为毫秒
    public Long getDurationTime(String taskName){
        Pair<String ,Long> temp=null;
        for(Pair<String,Long> pair:this.taskDurationList){
            if(pair.getKey().equals(taskName)){
                temp=pair;
                break;
            }
        }
        if(temp==null){
            throw new RuntimeException("Wrong log");
        }
        Long result=temp.getValue();
        this.taskDurationList.remove(temp);


        return result;
    }


    private List<Integer> timeVector;

    public List<Integer> getTimeVector() {
        return timeVector;
    }

    private long mod;

    public long getCountMod(){
        return mod;
    }
    public void countTimeVector(long interval){

        long sum=0;
        long scope=interval;

        List<Integer> result=new ArrayList<>();
        result.add(0);

        for(Pair<String,Long> duration:taskDurationList){
            sum+=duration.getValue();
            while (sum>scope){
                result.add(0);
                scope+=interval;
            }
            int last=result.get(result.size()-1);
            result.remove(result.size()-1);
            result.add(last+1);
        }
        this.timeVector=result;

        for(int value:timeVector){
            mod+=value*value;
        }
        mod=Math.round(Math.sqrt(mod));
       // return result;
     //   System.out.println(result);


    }
    public Pair<String, Long> next(String[] taskNames){

        for(Pair<String,Long> pair:this.taskDurationList){
            for(String taskName:taskNames) {
                if (taskName.equals(pair.getKey())) {
                    return pair;
                }
            }
        }
        throw new RuntimeException("No such task");
    }



}
