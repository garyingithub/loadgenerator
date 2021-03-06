package org.yawlfoundation.yawl.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Workitem completion types
 * @author Michael Adams
 * @date 26/09/2016
 */
public enum WorkItemCompletion {

    Normal(0),                    // a vanilla successful completion
    Force(1),                     // a forced, but successful, completion
    Fail(2),                      // a failed, and unsuccessful, completion
    Invalid(-1);

    private int _ord;

    WorkItemCompletion(int i) { _ord = i; }

    private static final Map<Integer, org.yawlfoundation.yawl.engine.WorkItemCompletion> _fromMap =
            new HashMap<Integer, org.yawlfoundation.yawl.engine.WorkItemCompletion>(4);

    static {
        for (org.yawlfoundation.yawl.engine.WorkItemCompletion completion : values()) {
            _fromMap.put(completion._ord, completion);
        }
    }


    public static org.yawlfoundation.yawl.engine.WorkItemCompletion fromInt(int i) {
        org.yawlfoundation.yawl.engine.WorkItemCompletion completion = _fromMap.get(i);
        return completion != null ? completion : Invalid;
    }
}
