package org.yawlfoundation.cloud.dispatchable;

import org.yawlfoundation.cloud.Process;
import org.yawlfoundation.cloud.ProcessInstance;
import org.yawlfoundation.cloud.Server;
import org.yawlfoundation.cloud.dispatchable.Dispatchable;

import java.util.List;

/**
 * Created by gary on 2/06/2017.
 */
public class CrossDispatchable implements Dispatchable {


    @Override
    public Server.Engine dispatch(List<Server.Engine> engines, ProcessInstance instance) {

        if(Integer.valueOf(instance.getId())%2==0){

            System.out.println("engine 0 for"+instance.getId());
            return engines.get(0);
        }else {
            System.out.println("engine 1 for"+instance.getId());
            return engines.get(1);
        }


    }
}
