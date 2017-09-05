package org.yawlfoundation.cloud.dispatchable;

import org.yawlfoundation.cloud.ProcessInstance;
import org.yawlfoundation.cloud.Server;

import java.util.List;

/**
 * Created by gary on 2/06/2017.
 */
public class BestFitDisapatchable implements Dispatchable {


    @Override
    public Server.Engine dispatch(List<Server.Engine> engines, ProcessInstance instance) {


        int maxLast= Integer.MIN_VALUE;
        int pos=0;
        for(int j=0;j<engines.size();j++) {
            int last=0;
            for (int i = 0; i < instance.getTimeVector().size(); i++) {
                if(instance.getTimeVector().get(i)>0)
                    last += engines.get(j).capacityTimeVector.get(i)-instance.getTimeVector().get(i);

            }

            if(last>maxLast){
                pos=j;
                maxLast=last;
            }
            System.out.println(pos);
        }

        Server.Engine e=engines.get(pos);
        synchronized (e.capacityTimeVector) {
            for (int i = 0; i < instance.getTimeVector().size(); i++) {
                if(instance.getTimeVector().get(i)>0){
                    int origin = e.capacityTimeVector.get(i);
                    origin -= instance.getTimeVector().get(i);
                    e.capacityTimeVector.remove(i);
                    e.capacityTimeVector.add(i, origin);
                }
            }
        }
        return e;
    }
}
