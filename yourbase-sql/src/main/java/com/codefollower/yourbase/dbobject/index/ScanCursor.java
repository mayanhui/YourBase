/*
 * Copyright 2004-2011 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package com.codefollower.yourbase.dbobject.index;

import java.util.Iterator;

import com.codefollower.yourbase.engine.Session;
import com.codefollower.yourbase.message.DbException;
import com.codefollower.yourbase.result.Row;
import com.codefollower.yourbase.result.SearchRow;

/**
 * The cursor implementation for the scan index.
 */
public class ScanCursor implements Cursor {
    private final ScanIndex scan;
    private Row row;
    private final Session session;
    private final boolean multiVersion;
    private Iterator<Row> delta;

    ScanCursor(Session session, ScanIndex scan, boolean multiVersion) {
        this.session = session;
        this.scan = scan;
        this.multiVersion = multiVersion;
        if (multiVersion) {
            delta = scan.getDelta();
        }
        row = null;
    }

    public Row get() {
        return row;
    }

    public SearchRow getSearchRow() {
        return row;
    }

    public boolean next() {
        if (multiVersion) {
            while (true) {
                if (delta != null) {
                    if (!delta.hasNext()) {
                        delta = null;
                        row = null;
                        continue;
                    }
                    row = delta.next();
                    if (!row.isDeleted() || row.getSessionId() == session.getId()) {
                        continue;
                    }
                } else {
                    row = scan.getNextRow(row);
                    if (row != null && row.getSessionId() != 0 && row.getSessionId() != session.getId()) {
                        continue;
                    }
                }
                break;
            }
            return row != null;
        }
        row = scan.getNextRow(row);
        return row != null;
    }

    public boolean previous() {
        throw DbException.throwInternalError();
    }

}
