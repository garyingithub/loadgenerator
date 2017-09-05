package org.yawlfoundation.cloud;

import java.util.List;
import java.util.Map;

/**
 * Created by gary on 31/05/2017.
 */
public class Process {


    private String location;
    private String name;


    private List<ProcessInstance> instances;

    public Process(String name,String location,String logLocation,long interval){
        this.location=location;
        this.name=name;
        this.instances=XMLHelper.XML_HELPER.getInstancesFromLog(logLocation,interval,this);
    }

    public String getLocation() {
        return location;
    }

    public List<ProcessInstance> getInstances() {
        return instances;
    }

    public String getName() {
        return name;
    }
}
