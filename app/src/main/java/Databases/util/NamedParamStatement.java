package Databases.util;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedParamStatement {
    public NamedParamStatement(Connection conn, String statementWithNames) throws SQLException {
        Pattern findParametersPattern = Pattern.compile("(@)([\\w]*)(@)");//@parameterCode@
        Matcher matcher = findParametersPattern.matcher(statementWithNames);

        while (matcher.find()) {
            fields.add(matcher.group());
        }

        prepStmt = conn.prepareStatement(statementWithNames.replaceAll(findParametersPattern.pattern(), "?"));
    }

    public PreparedStatement getPreparedStatement() {
        return prepStmt;
    }

    public ResultSet executeQuery() throws SQLException {
        return prepStmt.executeQuery();
    }

    public boolean execute() throws SQLException {
        return prepStmt.execute();
    }

    public void close() throws SQLException {
        prepStmt.close();
    }

    public void setInt(String name, int value) throws SQLException {
        for(int index : getIndexes(name))
            prepStmt.setInt(index, value);
    }

    public void setString(String name, String value) throws SQLException {
        for(int index : getIndexes(name))
            prepStmt.setString(index, value);
    }
    public void setNString(String name, String value) throws SQLException {
        for(int index : getIndexes(name))
            prepStmt.setString(index, value);
    }
    public void setBigDecimal(String name, BigDecimal value) throws SQLException {
        for(int index : getIndexes(name))
            prepStmt.setBigDecimal(index, value);
    }

    public void setDate(String name, java.sql.Date value) throws SQLException {
        for(int index : getIndexes(name))
            prepStmt.setDate(index, value);
    }

    private List<Integer> getIndexes(String name) {
        List<Integer> indexes = new ArrayList<>();
        for (int index = 0; index < fields.size();index++){
            if(fields.get(index).equals(name))
                indexes.add(index+1);
        }

        return indexes;
    }
    private PreparedStatement prepStmt;
    private List<String> fields = new ArrayList<>();

    public NamedParamStatement setQueryTimeout(int timeout ) throws  SQLException{
        this.prepStmt.setQueryTimeout(timeout);
        return this;
    }
}
