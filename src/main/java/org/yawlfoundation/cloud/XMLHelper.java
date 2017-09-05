package org.yawlfoundation.cloud;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by gary on 16/04/2017.
 */

public enum  XMLHelper {

    XML_HELPER;
    XMLHelper(){

    }


    private Integer count=0;
    public List<ProcessInstance> getInstancesFromLog(String fileLocation,long interval,Process process)  {
        SAXReader saxReader = new SAXReader();

        Document document = null;
        try {
            document = saxReader.read(new File(fileLocation));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        Element root = document.getRootElement();
        Element firstWorldElement = root.element("Process");


        List<ProcessInstance> result=new ArrayList<>();
        for (Iterator iter = firstWorldElement.elementIterator(); iter.hasNext();)
        {
            Element e = (Element) iter.next();
            List<Element> workItems=e.elements("AuditTrailEntry");
            String id=e.attributeValue("id");
            ProcessInstance instance=new ProcessInstance(String.valueOf(count),process);
            Map<String,Long> assignTimeMap=new HashMap<String, Long>();

            int firstHalf=0;
            int secondHalf=0;
            int flag=0;
            for(Element element:workItems){
                String event=element.element("EventType").getText();
                String taskName=element.element("WorkflowModelElement").getText();
                if(taskName.startsWith("EVENT")){
                    continue;
                }
                String timeStamp=element.element("Timestamp").getText();
                timeStamp=timeStamp.substring(0,timeStamp.length()-6);
                SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date= null;
                try {
                    date = format.parse(timeStamp);
                } catch (ParseException e1) {
                    throw new RuntimeException(e1);
                }
                Long time=date.getTime();

                if(event.equals("assign")){
                    assignTimeMap.put(taskName,time);
                }

                if(event.equals("complete")){
                    if(assignTimeMap.get(taskName)==null){
                        instance.addTask(taskName,1000L);
                    }else {

                        instance.addTask(taskName,time-assignTimeMap.get(taskName));

                    }

                    if(flag==0){
                        firstHalf++;
                    }else {
                        secondHalf++;
                    }
                }


                if(taskName.equals("Third")){
                    flag=1;
                }

            }
            instance.countTimeVector(interval);
            result.add(instance);
            count++;
          //  System.out.println("first "+firstHalf);
         //   System.out.println("second "+secondHalf);
        }

        normalize(result);

     //   result.sort((o1, o2) -> Long.compare(o2.getCountMod(),o1.getCountMod()));

        return result;


    }


    private void normalize(List<ProcessInstance> map){

        int maxLength=0;
        for(ProcessInstance instance:map){
            maxLength=Math.max(instance.getTimeVector().size(),maxLength);
        }

        for(ProcessInstance instance:map){

            final int initialSize=instance.getTimeVector().size();
            for(int i=0;i<maxLength-initialSize;i++){
                instance.getTimeVector().add(0);
            }

        }





    }



}
