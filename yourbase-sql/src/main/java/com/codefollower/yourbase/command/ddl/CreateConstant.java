/*
 * Copyright 2004-2011 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package com.codefollower.yourbase.command.ddl;

import com.codefollower.yourbase.command.CommandInterface;
import com.codefollower.yourbase.constant.ErrorCode;
import com.codefollower.yourbase.dbobject.Constant;
import com.codefollower.yourbase.dbobject.Schema;
import com.codefollower.yourbase.engine.Database;
import com.codefollower.yourbase.engine.Session;
import com.codefollower.yourbase.expression.Expression;
import com.codefollower.yourbase.message.DbException;
import com.codefollower.yourbase.value.Value;

/**
 * This class represents the statement
 * CREATE CONSTANT
 */
public class CreateConstant extends SchemaCommand {

    private String constantName;
    private Expression expression;
    private boolean ifNotExists;

    public CreateConstant(Session session, Schema schema) {
        super(session, schema);
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public int update() {
        session.commit(true);
        session.getUser().checkAdmin();
        Database db = session.getDatabase();
        if (getSchema().findConstant(constantName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.CONSTANT_ALREADY_EXISTS_1, constantName);
        }
        int id = getObjectId();
        Constant constant = new Constant(getSchema(), id, constantName);
        expression = expression.optimize(session);
        Value value = expression.getValue(session);
        constant.setValue(value);
        db.addSchemaObject(session, constant);
        return 0;
    }

    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }

    public void setExpression(Expression expr) {
        this.expression = expr;
    }

    public int getType() {
        return CommandInterface.CREATE_CONSTANT;
    }

}
