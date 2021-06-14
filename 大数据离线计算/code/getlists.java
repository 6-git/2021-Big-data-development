import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class getlists {
    private List db_list = new ArrayList();
    private List tb_list = new ArrayList();
    private List column_list = new ArrayList();
    private List data_list = new ArrayList();
    private ResultSet resultSet;
    private Statement statement;

    getlists() throws SQLException {
        String url = "jdbc:hive2://bigdata129.depts.bingosoft.net:";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", "user");
        properties.setProperty("password", "password");
        Connection connection = DriverManager.getConnection(url, properties);
        statement = connection.createStatement();
    }

    //得到所有数据库名
    public Object[] get_db_list() throws SQLException {
        resultSet = statement.executeQuery("show databases");
        while (resultSet.next()) {
            String db_name = resultSet.getString(1);
            db_list.add(db_name);
        }
        return db_list.toArray();
    }

    //得到所有表名
    public Object[] get_table_list(String db_name) throws SQLException {
        resultSet = statement.executeQuery("show tables from "+db_name);
        //每个database都要初始化table_list，否则会将其他database的也保存下来
        tb_list = new ArrayList();
        while (resultSet.next()) {
            String tableName = resultSet.getString(1);
            tb_list.add(tableName);
        }
        return tb_list.toArray();
    }

    //获取所有表中所有列
    public Object[] get_column_list(String table_name) throws SQLException {
        resultSet = statement.executeQuery("show columns from "+table_name);
        column_list = new ArrayList();
        while (resultSet.next()) {
            String columnName = resultSet.getString(1);
            column_list.add(columnName);
        }
        return column_list.toArray();
    }

    //获取查询语句结果的所有列名
    public Object[] get_column_name_list(String sql_state) throws SQLException {
        List column_name = new ArrayList();
        resultSet = statement.executeQuery(sql_state);
        int column = resultSet.getMetaData().getColumnCount();
        for(int i = 1; i <= column; i++){
            column_name.add(resultSet.getMetaData().getColumnName(i));
        }
        return column_name.toArray();
    }

    //获取查询语句的结果数据
    public ResultSet get_resultset(String sql_state) throws SQLException {
        resultSet = statement.executeQuery(sql_state);
        return resultSet;
    }
}
