package org.yawlfoundation.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import org.yawlfoundation.cloud.dispatchable.Dispatchable;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EnvironmentBasedClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedClient;
import org.yawlfoundation.yawl.logging.YLogDataItemList;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by gary on 30/05/2017.
 */
@Component
public class Server {


    @Value("${server.backend.list}")
    private String backEndStrings;

    @Value("${server.engine.port.list}")
    private String enginePortsString;

    private int pos=1;


    public static int interval=5000;

    private Logger logger= LoggerFactory.getLogger(Server.class);

    private InterfaceA_EnvironmentBasedClient[] clientAs;
    private InterfaceB_EnvironmentBasedClient[] clientBs;

    private List<Engine> engines=new ArrayList<>();


    public void loadSpecification(String specName,String specLocation){

        for(Engine engine:engines){
            engine.uploadSpecification(specName,specLocation);
        }

    }
    @PostConstruct
    public void init(){

        String[] backends= backEndStrings.split(",");
        String[] enginePorts=enginePortsString.split(",");

        String backend=backends[pos];

        for(String port:enginePorts){
            engines.add(new Engine(backend,port,200));
        }
        if(interval==0){
            interval=1000;
        }

    }


    public void launchCase(ProcessInstance instance){


        Engine e=new Dispatchable(){}.dispatch(this.engines,instance);
        if(e!=null) {
            e.launchCase(instance);
        }
    }


    private ExecutorService refreshService=Executors.newSingleThreadExecutor();
    public class Engine{

        private InterfaceA_EnvironmentBasedClient clientA;
        private InterfaceB_EnvironmentBasedClient clientB;

        private ExecutorService service=Executors.newCachedThreadPool();
        private String sessionHandle;

        private Map<String,YSpecification> specificationMap=new HashMap<>();
        public final List<Integer> capacityTimeVector=new ArrayList<>();


        private long mod;

        public long getMod(){
            return Math.round(Math.sqrt(mod));
        }
        Engine(String host, String port, int capacity){
            clientA=new InterfaceA_EnvironmentBasedClient("http://"+host+":"+port+"/yawl/ia");
            clientB=new InterfaceB_EnvironmentBasedClient("http://"+host+":"+port+"/yawl/ib");
            try {
                sessionHandle=clientB.connect("admin","YAWL");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for(int i=0;i<40000;i++){
                capacityTimeVector.add(capacity);
            }
            for(int value:capacityTimeVector){
                mod+=value*value;
            }

            refreshService.submit(()->{
                while (true){
                    synchronized (capacityTimeVector) {
                        capacityTimeVector.add(capacityTimeVector.size(),capacity);
                        mod+=capacity*capacity;
                        int old=capacityTimeVector.remove(0);
                        mod-=old*old;
                    }


                    try {
                        TimeUnit.MILLISECONDS.sleep(Server.interval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        public void abstractMod(long a){
            this.mod-=a;
        }

        public void addMod(long a){
            this.mod+=a;
        }


        public void uploadSpecification(String specName,String fileLocation){
            try {
                File file=new File(fileLocation);
                clientA.uploadSpecification(file,sessionHandle);
                specificationMap.put(specName,CommonUtils.COMMON_UTILS.getSpecification(file));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        class launchCaseRunnable implements Runnable{
            final String specName;
            final String caseId;
            final int mod;
            final String sessionHandle;

            launchCaseRunnable(String specName, String caseId, int mod, String sessionHandle) {
                this.specName = specName;
                this.caseId = caseId;
                this.mod = mod;
                this.sessionHandle = sessionHandle;

            }

            @Override
            public void run() {
                try {
                    final YSpecification specification=specificationMap.get(specName);
                    final String sessionHandle=this.sessionHandle;
                    String result="";
                    String cId=caseId;
                    Integer next=Integer.valueOf(cId);
                    do {
                       // logger.info(cId);
                        final long start=System.currentTimeMillis();
                        result = clientB.launchCase(specification.getSpecificationID(), "", sessionHandle, cId, new YLogDataItemList(), "");
                        //result=clientB.connect("admin","YAWL");
                        //logger.info(result);
                        //logger.info("response time is "+String.valueOf(System.currentTimeMillis()-start));

                        if(LoadgeneratorApplication.writer!=null){
                            LoadgeneratorApplication.writer.set(new Metric<Number>("1_response_time",System.currentTimeMillis()-start));
                        }
                        next+=mod;
                        cId=String.valueOf(next);
                    }while (result.contains("failure"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private void launchCase(final String specName,final String caseId,final int mod){
            service.submit(new launchCaseRunnable(specName,caseId,mod,this.sessionHandle));

        }

        public void launchCase(ProcessInstance instance){

            String specName=instance.getSpecName();
            String caseId=instance.getId();

            if(specificationMap.get(specName)==null){
                uploadSpecification(specName,instance.getSpecLocation());
            }
            launchCase(specName,caseId,instance.getMod());

        }



    }



}
