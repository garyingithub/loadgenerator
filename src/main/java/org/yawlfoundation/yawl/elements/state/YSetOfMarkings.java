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

import org.yawlfoundation.yawl.elements.state.*;
import org.yawlfoundation.yawl.elements.state.YMarking;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Lachlan Aldred
 * Date: 19/06/2003
 * Time: 15:22:56
 *
 */
public class YSetOfMarkings {
    private Set<org.yawlfoundation.yawl.elements.state.YMarking> _markings = new HashSet<org.yawlfoundation.yawl.elements.state.YMarking>();



    //moe - ResetAnalyser
    public void addMarking(org.yawlfoundation.yawl.elements.state.YMarking marking) {
        if (! contains(marking)) {
            _markings.add(marking);
        }
    }

    //moe - ResetAnalyser
    public void addAll(org.yawlfoundation.yawl.elements.state.YSetOfMarkings newMarkings) {
        for (org.yawlfoundation.yawl.elements.state.YMarking marking : newMarkings.getMarkings()) {
            addMarking(marking);
        }
    }

    //changed by moe - ResetAnalyser
    public boolean contains(org.yawlfoundation.yawl.elements.state.YMarking marking) {
        for (org.yawlfoundation.yawl.elements.state.YMarking yMarking : _markings) {
            if (yMarking.equivalentTo(marking)) return true;
        }
        return false;
    }

    //added by moe - ResetAnalyser
    public boolean equals(org.yawlfoundation.yawl.elements.state.YSetOfMarkings markings) {
        Set<org.yawlfoundation.yawl.elements.state.YMarking> markingsToCompare = markings.getMarkings();
        return (_markings.size() == markingsToCompare.size()) &&
                containsAll(markingsToCompare) && markings.containsAll(_markings);
    }

    public boolean containsAll(Set<org.yawlfoundation.yawl.elements.state.YMarking> markingsToCompare) {
        for (org.yawlfoundation.yawl.elements.state.YMarking yMarking : markingsToCompare) {
            if (! this.contains(yMarking)) return false;
        }
        return true;
    }

    //moe - ResetAnalyser
    public void removeAll(){
        _markings.clear();
    }

    public Set<org.yawlfoundation.yawl.elements.state.YMarking> getMarkings() {
        return _markings;
    }

    public int size() {
        return _markings.size();
    }

    public org.yawlfoundation.yawl.elements.state.YMarking removeAMarking() {
        if (_markings.size() > 0) {
            org.yawlfoundation.yawl.elements.state.YMarking marking = (org.yawlfoundation.yawl.elements.state.YMarking) _markings.iterator().next();
            _markings.remove(marking);
            return marking;
        }
        return null;
    }

    public boolean containsEquivalentMarkingTo(org.yawlfoundation.yawl.elements.state.YSetOfMarkings possibleFutureMarkingSet) {
        for (org.yawlfoundation.yawl.elements.state.YMarking possibleMarking : possibleFutureMarkingSet.getMarkings()) {
            for (org.yawlfoundation.yawl.elements.state.YMarking marking : _markings) {
                if (possibleMarking.equivalentTo(marking)) return true;
            }
        }
        return false;
    }

    //moe - ResetAnalyser
    public boolean containsBiggerEqual(org.yawlfoundation.yawl.elements.state.YMarking m) {
        for (YMarking marking : _markings) {
            if (marking.isBiggerThanOrEqual(m)) return true;
        }
        return false;
    }
}