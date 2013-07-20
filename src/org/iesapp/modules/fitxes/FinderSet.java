/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Josep
 */
public class FinderSet {
    
    protected Statement statement;
    protected ResultSet resultSet;

    public Statement getStament() {
        return statement;
    }

    public void setStament(Statement statement) {
        this.statement = statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }
    
    public void close() throws SQLException{
        if(resultSet!=null){
            resultSet.close();
        }
        if(statement!=null)
        {
            statement.close();
        }
        
    }
}
