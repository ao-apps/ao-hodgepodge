package com.aoindustries.dao;

import com.aoindustries.sql.Database;
import com.aoindustries.sql.DatabaseConnection;
import com.aoindustries.sql.WrappedSQLException;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A report that is obtained from a SQL query database.
 */
public class QueryReport implements Report {

    public static class QueryColumn implements Report.Column {

        private final QueryReport report;
        private final String name;
        private final Report.Alignment alignment;
        private final ApplicationResourcesAccessor accessor;

        QueryColumn(QueryReport report, String name, Report.Alignment alignment, ApplicationResourcesAccessor accessor) {
            this.report = report;
            this.name = name;
            this.alignment = alignment;
            this.accessor = accessor;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getLabel() {
            return accessor==null ? name : accessor.getMessage("report."+report.getName()+".columns."+name+".label");
        }

        @Override
        public Report.Alignment getAlignment() {
            return alignment;
       }
    }

    public static class ReportResult implements Report.Result {

        private final List<QueryColumn> columns;
        private final List<List<Object>> tableData;

        ReportResult(List<QueryColumn> columns, List<List<Object>> tableData) {
            this.columns = columns;
            this.tableData = tableData;
        }

        @Override
        public List<QueryColumn> getColumns() {
            return columns;
        }

        @Override
        public List<List<Object>> getTableData() {
            return tableData;
        }
    }

    private final Database database;
    private final String name;
    private final ApplicationResourcesAccessor accessor;
    private final String description;
    private final String sql;
    private final Object[] params;

    public QueryReport(Database database, String name, String description, String sql, Object... params) {
        this.database = database;
        this.name = name;
        this.accessor = null;
        this.description = description;
        this.sql = sql;
        this.params = params;
    }

    public QueryReport(Database database, String name, String description, String sql, Collection<?> params) {
        this(database, name, description, sql, params.toArray());
    }

    public QueryReport(Database database, String name, ApplicationResourcesAccessor accessor, String sql, Object... params) {
        this.database = database;
        this.name = name;
        this.accessor = accessor;
        this.description = null;
        this.sql = sql;
        this.params = params;
    }

    public QueryReport(Database database, String name, ApplicationResourcesAccessor accessor, String sql, Collection<?> params) {
        this(database, name, accessor, sql, params.toArray());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return accessor==null ? name : accessor.getMessage("report."+name+".title");
    }

    @Override
    public String getDescription() {
        return accessor==null ? description : accessor.getMessage("report."+name+".description");
    }

    @Override
    public ReportResult getResult() throws SQLException {
        Connection conn = database.getConnection(Connection.TRANSACTION_READ_COMMITTED, true, 1);
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            try {
                DatabaseConnection.setParams(pstmt, params);
                ResultSet results = pstmt.executeQuery();
                try {
                    ResultSetMetaData meta = results.getMetaData();
                    int numColumns = meta.getColumnCount();
                    List<QueryColumn> columns = new ArrayList<QueryColumn>();
                    for(int columnIndex=1; columnIndex<=numColumns; columnIndex++) {
                        final Alignment alignment;
                        switch(meta.getColumnType(columnIndex)) {
                            case Types.BIGINT :
                            case Types.DECIMAL :
                            case Types.DOUBLE :
                            case Types.FLOAT :
                            case Types.INTEGER :
                            case Types.NUMERIC :
                            case Types.REAL :
                            case Types.SMALLINT :
                            case Types.TINYINT :
                                alignment = Alignment.right;
                                break;
                            case Types.BOOLEAN :
                            case Types.BIT :
                                alignment = Alignment.center;
                                break;
                            default :
                                alignment = Alignment.left;
                        }
                        columns.add(new QueryColumn(this, meta.getColumnName(columnIndex), alignment, accessor));
                    }
                    List<List<Object>> tableData = new ArrayList<List<Object>>();
                    while(results.next()) {
                        List<Object> row = new ArrayList<Object>(numColumns);
                        for(int columnIndex=1; columnIndex<=numColumns; columnIndex++) {
                            // Convert arrays to lists
                            Object value = results.getObject(columnIndex);
                            if(value instanceof Array) {
                                List<Object> values = new ArrayList<Object>();
                                ResultSet arrayResults = ((Array)value).getResultSet();
                                try {
                                    while(arrayResults.next()) values.add(arrayResults.getObject(2));
                                } finally {
                                    arrayResults.close();
                                }
                                value = AoCollections.optimalUnmodifiableList(values);
                            }
                            row.add(value);
                        }
                        tableData.add(Collections.unmodifiableList(row));
                    }
                    return new ReportResult(
                        Collections.unmodifiableList(columns),
                        Collections.unmodifiableList(tableData)
                    );
                } finally {
                    results.close();
                }
            } catch(SQLException e) {
                throw new WrappedSQLException(e, pstmt);
            } finally {
                pstmt.close();
            }
        } finally {
            database.releaseConnection(conn);
        }
    }
}
