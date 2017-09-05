package org.yawlfoundation.cloud;

import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;

import java.io.*;

/**
 * Created by gary on 30/05/2017.
 */
public enum  CommonUtils {
    COMMON_UTILS;

    private String getSpecXML(File specFile){
        File file=specFile;
        StringBuilder builder=new StringBuilder();
        if (file.isFile() && file.exists()) {
            InputStreamReader read = null;//考虑到编码格式
            try {
                read = new InputStreamReader(
                        new FileInputStream(file), "GBK");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    builder.append(lineTxt);
                }

                read.close();


            } catch (  IOException e) {
                throw new RuntimeException(e);
            }

        }
        return builder.toString();

    }

    public YSpecification getSpecification(File specFile){


            try {
                return YMarshal.unmarshalSpecifications(getSpecXML(specFile)).get(0);
            } catch (YSyntaxException e) {
                throw new RuntimeException(e);
            }

    }


}
