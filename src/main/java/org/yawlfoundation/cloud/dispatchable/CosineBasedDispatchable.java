package org.yawlfoundation.cloud.dispatchable;

import org.yawlfoundation.cloud.ProcessInstance;
import org.yawlfoundation.cloud.Server;

import java.util.List;

/**
 * Created by gary on 2/06/2017.
 */
public class CosineBasedDispatchable implements Dispatchable {

    @Override
    public Server.Engine dispatch(List<Server.Engine> engines, ProcessInstance instance) {


        int minPos=engines.size();
        long maxValue=Long.MIN_VALUE;
        for(int i=0;i<engines.size();i++){

            long multiply=0;
            for(int j=0;j<instance.getTimeVector().size();j++){

                multiply+=instance.getTimeVector().get(j)*engines.get(i).capacityTimeVector.get(j);

            }

            multiply=(engines.get(i).getMod()*instance.getMod())/multiply;


            if(multiply>maxValue){
                maxValue=multiply;
                minPos=i;
            }
        }

        System.out.println(minPos);
        Server.Engine e=engines.get(minPos);

        synchronized (e.capacityTimeVector) {
            for (int i = 0; i < instance.getTimeVector().size(); i++) {
                if(instance.getTimeVector().get(i)>0){
                    int origin = e.capacityTimeVector.get(i);
                    origin -= instance.getTimeVector().get(i);
                    int value=e.capacityTimeVector.remove(i);
                    e.abstractMod(value*value);

                    e.capacityTimeVector.add(i, origin);
                    value=e.capacityTimeVector.get(i);
                    e.addMod(value*value);
                }
            }
        }


        return engines.get(minPos);
    }



}
