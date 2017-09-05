package org.yawlfoundation.yawl.engine.interfce.interfaceB;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.exceptions.YAWLException;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gary on 26/05/2017.
 */
public
class ClusterInterfaceBWebsideController extends InterfaceBWebsideController {

    private Map<String,InterfaceBWebsideController> controllerMap=new HashMap<>();

    private Map<String,String> engineIdMap=new HashMap<>();

    private Map<String,String> sessionHandleMap=new HashMap<>();




    public WorkItemRecord checkOut(WorkItemRecord wir)
            throws IOException, YAWLException {

        String engineId=wir.engineId;
        String engineAddress=String.valueOf(Integer.valueOf(wir.engineId));

        if(controllerMap.get(engineAddress)==null){
            controllerMap.put(engineAddress,new ClusterInterfaceBWebsideController());
            if(!engineAddress.startsWith("http")){
                controllerMap.get(engineAddress).setUpInterfaceBClient("http://"+engineAddress+"/yawl/ib");
            }else {
                controllerMap.get(engineAddress).setUpInterfaceBClient(engineAddress+"/yawl/ib");
            }
            sessionHandleMap.put(engineAddress,controllerMap.get(engineAddress).connect(this.engineLogonName,this.engineLogonPassword));
            engineIdMap.put(engineAddress,engineId);
        }

        long start=System.currentTimeMillis();
        WorkItemRecord record=checkOut(wir.getID(),sessionHandleMap.get(engineAddress));

        record.engineId=engineId;
        record.engineAddress=engineAddress;
        return record;

    }


    public String checkInWorkItem(WorkItemRecord workItemRecord, Element inputData,
                                  Element outputData, String logPredicate)
            throws IOException, JDOMException{

        String engineId=workItemRecord.engineId;
        String engineAddress=workItemRecord.engineAddress;

        long start=System.currentTimeMillis();
        String result=checkInWorkItem(workItemRecord.getID(),inputData,outputData,sessionHandleMap.get(engineAddress));

        return result;
    }


    @Override
    public void handleEnabledWorkItemEvent(WorkItemRecord enabledWorkItem) {

    }

    @Override
    public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {

    }
}
