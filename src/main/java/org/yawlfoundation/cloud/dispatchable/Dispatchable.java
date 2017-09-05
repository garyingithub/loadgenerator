package org.yawlfoundation.cloud.dispatchable;

import org.yawlfoundation.cloud.ProcessInstance;
import org.yawlfoundation.cloud.Server;

import java.util.List;
import java.util.Random;

/**
 * Created by gary on 31/05/2017.
 */
public interface Dispatchable {


    Random random=new Random();
    default Server.Engine dispatch(List<Server.Engine> engines, ProcessInstance instance){

        return engines.get(random.nextInt(engines.size()));

    }



}
