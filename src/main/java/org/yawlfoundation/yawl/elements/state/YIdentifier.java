/*
 * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.elements.state;

import org.yawlfoundation.yawl.elements.*;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.YPersistenceManager;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * This class has control over data structures that allow for
 * storing an identifer and managing a set of children.
 *
 * @author Lachlan Aldred
 * @author Michael Adams (refactored for v2.0, 06/08 & 04/09)
 */
public class YIdentifier {

    // a location may be a condition or a task
    private List<YNetElement> _locations = new Vector<YNetElement>();

    private List<String> locationNames = new Vector<String>();
    private List<org.yawlfoundation.yawl.elements.state.YIdentifier> _children = new Vector<org.yawlfoundation.yawl.elements.state.YIdentifier>();
    private org.yawlfoundation.yawl.elements.state.YIdentifier _parent;
    private String id = null;
    private String _idString;

    private long _logKey = -1;                    // the FK of the logged task instance


    public YIdentifier() { }                       // only for hibernate


    public YIdentifier(String idString) {
        _idString = (idString != null) ? idString : YEngine.getInstance().getNextCaseNbr();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLocationNames() {
        return locationNames;
    }

    public void setLocationNames(List<String> names) {
        locationNames = names;
    }

    public String get_idString() {
        return _idString;
    }

    public void set_idString(String id) {
        _idString = id;
    }

    public void set_children(List<org.yawlfoundation.yawl.elements.state.YIdentifier> children) {
        _children = children;
    }

    public List<org.yawlfoundation.yawl.elements.state.YIdentifier> get_children() {
        return _children;
    }

    public org.yawlfoundation.yawl.elements.state.YIdentifier get_parent() {
        return _parent;
    }

    public void set_parent(org.yawlfoundation.yawl.elements.state.YIdentifier parent) {
        _parent = parent;
    }


    public List<org.yawlfoundation.yawl.elements.state.YIdentifier> getChildren() {
        return _children;
    }


    public Set<org.yawlfoundation.yawl.elements.state.YIdentifier> getDescendants() {
        Set<org.yawlfoundation.yawl.elements.state.YIdentifier> descendants = new HashSet<org.yawlfoundation.yawl.elements.state.YIdentifier>();
        descendants.add(this);

        for (org.yawlfoundation.yawl.elements.state.YIdentifier child : _children) {
            if (child != null) descendants.addAll(child.getDescendants());
        }
        return descendants;
    }


    public org.yawlfoundation.yawl.elements.state.YIdentifier createChild(YPersistenceManager pmgr) throws YPersistenceException {
        String newID = String.format("%s.%d", this._idString, _children.size() + 1);
        return createChildWithID(pmgr, newID);
    }

    public void clearChildren() {
        _children = new Vector<org.yawlfoundation.yawl.elements.state.YIdentifier>();
    }

    public boolean removeChild(org.yawlfoundation.yawl.elements.state.YIdentifier child) {
        return _children.remove(child);
    }


    /**
     * Creates a child identifier.
     *
     * @param childNum
     * @return the child YIdentifier object with id == childNum
     */
    public org.yawlfoundation.yawl.elements.state.YIdentifier createChild(YPersistenceManager pmgr, int childNum)
            throws YPersistenceException {
        if (childNum < 1) {
            throw new IllegalArgumentException("Childnum must be > 0");
        }
        String childNumStr = "" + childNum;
        for (org.yawlfoundation.yawl.elements.state.YIdentifier child : _children) {
            String childID = child.toString();
            String childIDSuffix = childID.substring(childID.lastIndexOf('.') + 1);
            if (childNumStr.equals(childIDSuffix)) {
                throw new IllegalArgumentException(
                        "Childnum uses an int already being used.");
            }
        }
        return createChildWithID(pmgr, this._idString + "." + childNumStr);
    }


    private org.yawlfoundation.yawl.elements.state.YIdentifier createChildWithID(YPersistenceManager pmgr, String id)
            throws YPersistenceException {

        org.yawlfoundation.yawl.elements.state.YIdentifier identifier = new org.yawlfoundation.yawl.elements.state.YIdentifier(id);
        _children.add(identifier);
        identifier.set_parent(this);

        if (pmgr != null) {
            pmgr.storeObjectFromExternal(identifier);
            updateThis(pmgr);
        }
        return identifier;
    }


    public org.yawlfoundation.yawl.elements.state.YIdentifier getParent() {
        return _parent;
    }

    public boolean hasParent() { return _parent != null; }


    public boolean isImmediateChildOf(org.yawlfoundation.yawl.elements.state.YIdentifier identifier) {
        return (_parent == identifier);
    }

    public boolean isAncestorOf(org.yawlfoundation.yawl.elements.state.YIdentifier identifier) {
        org.yawlfoundation.yawl.elements.state.YIdentifier parent = identifier.getParent();
        return parent != null && (parent.equals(this) || isAncestorOf(parent));
    }


    public String toString() {
        return _idString;
    }


    public synchronized void addLocation(YPersistenceManager pmgr, YNetElement condition)
            throws YPersistenceException {
        if (condition == null) {
            throw new RuntimeException("Cannot add null condition to this identifier.");
        }
        _locations.add(condition);

        if ((condition instanceof YCondition) && !(condition instanceof YInputCondition)) {
            String locName = condition.toString();
            locationNames.add(locName.substring(locName.indexOf(":") + 1, locName.length()));
        } else {
            locationNames.add(condition.toString());
        }

        updateThis(pmgr);
    }


    public synchronized void clearLocations(YPersistenceManager pmgr)
            throws YPersistenceException {
        _locations.clear();
        locationNames.clear();
        updateThis(pmgr);
    }


    public synchronized void clearLocation(YPersistenceManager pmgr, YNetElement condition)
            throws YPersistenceException {
        removeLocation(pmgr, condition);
    }


    public synchronized void removeLocation(YPersistenceManager pmgr,
                                            YNetElement condition)
            throws YPersistenceException {
        if (condition == null) {
            throw new RuntimeException("Cannot remove null condition from this identifier.");
        }

        _locations.remove(condition);

        if (condition instanceof YCondition && !(condition instanceof YInputCondition)) {
            String locName = condition.toString();
            locationNames.remove(locName.substring(locName.indexOf(":") + 1, locName.length()));
        } else {
            locationNames.remove(condition.toString());
        }
        updateThis(pmgr);
    }


    public synchronized void addLocation(YPersistenceManager pmgr, YTask task)
            throws YPersistenceException {
        if (task == null) {
            throw new RuntimeException("Cannot add null task to this identifier.");
        }
        _locations.add(task);
        locationNames.add(task.getID());
        updateThis(pmgr);
    }


    public synchronized void removeLocation(YPersistenceManager pmgr, YTask task)
            throws YPersistenceException {
        if (task == null) {
            throw new RuntimeException("Cannot remove null task from this identifier.");
        }
        _locations.remove(task);
        locationNames.remove(task.getID());
    }


    public synchronized List<YNetElement> getLocations() {
        return _locations;
    }


    public org.yawlfoundation.yawl.elements.state.YIdentifier getRootAncestor() {
        return getRootAncestor(this);
    }


    private org.yawlfoundation.yawl.elements.state.YIdentifier getRootAncestor(org.yawlfoundation.yawl.elements.state.YIdentifier identifier) {
        org.yawlfoundation.yawl.elements.state.YIdentifier parent = identifier.getParent();
        return (parent != null) ? getRootAncestor(parent) : identifier;
    }


    private void updateThis(YPersistenceManager pmgr) throws YPersistenceException {
        if (pmgr != null) {
            pmgr.updateObjectExternal(this);
        }
    }

    public long getLogKey() {
        return _logKey;
    }

    public void setLogKey(long key) {
        _logKey = key;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof org.yawlfoundation.yawl.elements.state.YIdentifier) {
            org.yawlfoundation.yawl.elements.state.YIdentifier otherID = (org.yawlfoundation.yawl.elements.state.YIdentifier) other;
            if ((toString() != null) && toString().equals(otherID.toString())) {
                return (getParent() == null) ? (otherID.getParent() == null) :
                        getParent().equals(otherID.getParent());
            }
        }
        return false;
    }

    public boolean equalsOrIsAncestorOf(org.yawlfoundation.yawl.elements.state.YIdentifier another) {
        return equals(another) || isAncestorOf(another);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see java.util.Hashtable
     */
    public int hashCode() {
        return this.toString().hashCode();
    }
}
