//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
// OpenNMS Licensing       <license@opennms.org>
//     http://www.opennms.org/
//     http://www.opennms.com/
//

package org.opennms.web.svclayer;

public class SurveillanceTable {
    
    String m_label = null;
    AggregateStatus[][] m_statusTable = null;
    String[] m_rowHeaders = null;
    String[] m_columnHeaders = null;
    
    public SurveillanceTable() {
        
    }
    
    public SurveillanceTable(int rows, int columns) {
        m_statusTable = new AggregateStatus[rows][columns];
        m_rowHeaders = new String[rows];
        m_columnHeaders = new String[columns];
    }
    
    public void setStatus(int row, int col, AggregateStatus status) {
        m_statusTable[row][col] = status;
    }
    
    public void setRowHeader(int row, String header) {
        m_rowHeaders[row] = header;
    }
    
    public void setColumnHeader(int col, String header) {
        m_columnHeaders[col] = header;
    }

    public String[] getColumnHeaders() {
        return m_columnHeaders;
    }

    public String[] getRowHeaders() {
        return m_rowHeaders;
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public AggregateStatus[][] getStatusTable() {
        return m_statusTable;
    }
    
}
