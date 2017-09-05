package org.yawlfoundation.yawl.authentication;

import org.yawlfoundation.yawl.authentication.YAbstractSession;

/**
 * @author Michael Adams
 * @date 13/10/13
 */
public interface ISessionCache {

    String connect(String name, String password, long timeOutSeconds);

    boolean checkConnection(String handle);

    YAbstractSession getSession(String handle);

    void expire(String handle);

    void disconnect(String handle);

    void shutdown();

}
