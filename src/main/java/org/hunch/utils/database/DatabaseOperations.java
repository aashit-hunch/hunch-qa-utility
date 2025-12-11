package org.hunch.utils.database;

import org.apache.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.postgresql.util.*;
import java.sql.*;

public class DatabaseOperations {

    private final PostgresDBConnections dbConnection;
    private static final Logger LOGGER = Logger.getLogger(DatabaseOperations.class);

    public DatabaseOperations(PostgresDBConnections dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==================== CRUD OPERATIONS ====================

    // CREATE
    public int insert(String table, JSONObject data,String idColumnName) {
        try{
            StringBuilder cols = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
            List<Object> values = new ArrayList<>();

            for (String key : data.keySet()) {
                if (cols.length() > 0) {
                    cols.append(", ");
                    placeholders.append(", ");
                }
                cols.append(key);
                placeholders.append("?");
                values.add(data.get(key));
            }

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING "+idColumnName, table, cols, placeholders);
            try(Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                setParameters(stmt, values);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getInt(idColumnName) : -1;
            }

        }catch (SQLException w){
            LOGGER.info("Exception Occurred while Inserting Data into DB : "+w.getMessage());
            return -1;
        }

    }

    // READ - Single record
    public JSONObject findById(String table, int id,String idColumnName) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE "+idColumnName+" = ?", table);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            JSONArray arr = resultSetToJsonArray(rs);
            return arr.length() > 0 ? arr.getJSONObject(0) : null;
        }
    }

    // READ - All records
    public JSONArray findAll(String table) throws SQLException {
        String sql = String.format("SELECT * FROM %s", table);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return resultSetToJsonArray(stmt.executeQuery());
        }
    }

    // READ - With conditions
    public JSONArray findWhere(String table, String whereClause, Object... params) {
        try{
            String sql = String.format("SELECT * FROM %s WHERE %s", table, whereClause);
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                return resultSetToJsonArray(stmt.executeQuery());
            }
        }
        catch (Exception e){
            LOGGER.info("Exception Occurred while fetching Data from DB : "+e.getMessage());
        }

        return new JSONArray();
    }

    // UPDATE
    public int update(String table, JSONObject data, Object id,String idColumnName)  {
        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (String key : data.keySet()) {
            if (setClause.length() > 0) setClause.append(", ");
            setClause.append(key).append(" = ?");
            values.add(data.get(key));
        }
        values.add(id);

        String sql = String.format("UPDATE %s SET %s WHERE "+idColumnName+" = ?", table, setClause);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, values);
            return stmt.executeUpdate();
        }
        catch (Exception e){
            LOGGER.info("Exception Occurred while Updating Data into DB : "+e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // DELETE
    public int delete(String table, int id,String idColumnName)  {
        try{
            String sql = String.format("DELETE FROM %s WHERE "+idColumnName+" = ?", table);
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        }
        catch (Exception e){
            throw new RuntimeException("Exception Occurred while Deleting Data from DB : "+e);
        }

    }

    /**
     * Delete records from a table using a custom WHERE clause and parameters.
     * Example usage:
     *   deleteWhere("goss_requests", "sender_id IN (?, ?) AND receiver_id IN (?, ?) AND is_crush = ?", senderId1, senderId2, receiverId1, receiverId2, false);
     */
    public int deleteWhere(String table, String whereClause, Object... params){
        try {
            String sql = String.format("DELETE FROM %s WHERE %s", table, whereClause);
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Exception Occurred while Deleting Data from DB : "+e);
        }

    }

    // ==================== CUSTOM JSON METHODS ====================

    // Execute raw query and return JSONArray
    public JSONArray executeQuery(String sql, Object... params)  {
        LOGGER.info("Executing Query :: "+sql);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return resultSetToJsonArray(stmt.executeQuery());
        }
        catch (SQLException e){
            throw  new RuntimeException("Exception Occurred while executing Query : "+e.getMessage());
        }
    }

    // Execute raw query and return single JSONObject
    public JSONObject executeQuerySingle(String sql, Object... params) throws SQLException {
        JSONArray arr = executeQuery(sql, params);
        return arr.length() > 0 ? arr.getJSONObject(0) : null;
    }

    // Get paginated results as JSONObject with metadata
    public JSONObject findPaginated(String table, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;

        String countSql = String.format("SELECT COUNT(*) as total FROM %s", table);
        String dataSql = String.format("SELECT * FROM %s LIMIT ? OFFSET ?", table);

        JSONObject result = new JSONObject();

        try (Connection conn = dbConnection.getConnection()) {
            // Get total count
            try (PreparedStatement stmt = conn.prepareStatement(countSql)) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int total = rs.getInt("total");
                result.put("total", total);
                result.put("totalPages", (int) Math.ceil((double) total / pageSize));
            }

            // Get paginated data
            try (PreparedStatement stmt = conn.prepareStatement(dataSql)) {
                stmt.setInt(1, pageSize);
                stmt.setInt(2, offset);
                result.put("data", resultSetToJsonArray(stmt.executeQuery()));
            }

            result.put("page", page);
            result.put("pageSize", pageSize);
        }
        return result;
    }

    // Search with LIKE and return JSONArray
    public JSONArray search(String table, String column, String searchTerm) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s ILIKE ?", table, column);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            return resultSetToJsonArray(stmt.executeQuery());
        }
    }

    // Aggregate query returning JSONObject
    public JSONObject getAggregates(String table, String column) throws SQLException {
        String sql = String.format(
                "SELECT COUNT(*) as count, AVG(%s) as avg, SUM(%s) as sum, MIN(%s) as min, MAX(%s) as max FROM %s",
                column, column, column, column, table);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return executeQuerySingle(sql);
        }
    }

    // ==================== UTILITY METHODS ====================

    private JSONArray resultSetToJsonArray(ResultSet rs) throws SQLException {
        JSONArray arr = new JSONArray();
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        while (rs.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= cols; i++) {
                String colName = meta.getColumnLabel(i);
                Object value = rs.getObject(i);
                // Handle PGobject for JSON/JSONB columns
                if (value instanceof PGobject) {
                    PGobject pgObj = (PGobject) value;
                    String type = pgObj.getType();
                    String pgValue = pgObj.getValue();
                    if (("json".equalsIgnoreCase(type) || "jsonb".equalsIgnoreCase(type)) && pgValue != null) {
                        value = new JSONObject(pgValue);
                    } else {
                        value = pgValue;
                    }
                } else if (value instanceof Array) {
                    // Handle SQL Array columns (e.g., text[])
                    Object arrSql = ((Array) value).getArray();
                    if (arrSql instanceof Object[]) {
                        value = new JSONArray((Object[]) arrSql);
                    } else {
                        value = arr != null ? new JSONArray().put(arrSql) : JSONObject.NULL;
                    }
                }
                obj.put(colName, value != null ? value : JSONObject.NULL);
            }
            arr.put(obj);
        }
        return arr;
    }

    private void setParameters(PreparedStatement stmt, List<Object> values) throws SQLException {
        Connection conn = stmt.getConnection();
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value == null || value == JSONObject.NULL) {
                stmt.setNull(i + 1, java.sql.Types.NULL);
            }
            else if (value instanceof JSONArray) {
                // Robust handling for PostgreSQL text[] columns
                JSONArray arr = (JSONArray) value;
                String[] strArr = new String[arr.length()];
                for (int j = 0; j < arr.length(); j++) {
                    strArr[j] = arr.getString(j);
                }
                stmt.setArray(i + 1, conn.createArrayOf("text", strArr));
            }
            else if (value instanceof JSONObject) {
                // Convert JSON types to string for JSON/JSONB columns
                stmt.setObject(i + 1, value.toString(), Types.OTHER);
            }
            else if (value instanceof String && isTimestampFormat((String) value)) {
                // Convert timestamp-formatted strings to java.sql.Timestamp
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    java.util.Date parsedDate = sdf.parse((String) value);
                    stmt.setTimestamp(i + 1, new Timestamp(parsedDate.getTime()));
                } catch (Exception e) {
                    // If parsing fails, set as regular string
                    stmt.setObject(i + 1, value);
                }
            } else {
                stmt.setObject(i + 1, value);
            }
        }
    }

    private boolean isTimestampFormat(String value) {
        // Check if string matches timestamp pattern: yyyy-MM-dd HH:mm:ss.SSS
        return value != null && value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
    }

    // Execute update/insert/delete and return affected rows
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        }
    }

    // Batch insert returning inserted count
    public int batchInsert(String table, JSONArray dataArray) throws SQLException {
        if (dataArray.length() == 0) return 0;

        JSONObject first = dataArray.getJSONObject(0);
        StringBuilder cols = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String key : first.keySet()) {
            if (cols.length() > 0) {
                cols.append(", ");
                placeholders.append(", ");
            }
            cols.append(key);
            placeholders.append("?");
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, cols, placeholders);

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject row = dataArray.getJSONObject(i);
                int idx = 1;
                for (String key : first.keySet()) {
                    stmt.setObject(idx++, row.get(key));
                }
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return results.length;
        }
    }
}
