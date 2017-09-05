package org.yawlfoundation.cloud.dispatchable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.cloud.ProcessInstance;
import org.yawlfoundation.cloud.Server;

import java.util.List;

/**
 * Created by gary on 31/05/2017.
 */
public class WorkloadBasedDispatchable implements Dispatchable {

    private Logger logger= LoggerFactory.getLogger(WorkloadBasedDispatchable.class);
    @Override
    public Server.Engine dispatch(List<Server.Engine> engines, ProcessInstance instance) {
        Server.Engine e=null;
        for(Server.Engine engine:engines){
            synchronized (engine.capacityTimeVector) {
                int instanceLength = instance.getTimeVector().size();
                while (engine.capacityTimeVector.size()<instanceLength){
                    engine.capacityTimeVector.add(engine.capacityTimeVector.get(engine.capacityTimeVector.size()-1));
                }
                int i = 0;
                for (; i < instanceLength; i++) {

                    if (engine.capacityTimeVector.get(i) < instance.getTimeVector().get(i)) {
                        break;
                    }

                }
                if(i>=instanceLength) {
                    e = engine;
                    break;
                }
            }
        }
        if(e==null){
            e=engines.get(0);
            logger.info("No sufficient Engine");
            return null;
        }
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
       // System.out.println(e.capacityTimeVector.toString());
        return e;
    }
}
