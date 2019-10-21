package Dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;
import java.sql.Connection;

public class C3P0DataSource {

    private static BasicDataSource ds = new BasicDataSource();

    static {
        ds.setUrl("jdbc:postgresql://localhost:5432/codecool");
        ds.setUsername("postgres");
        ds.setPassword("postgres");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
    }

    public static C3P0DataSource getInstance(){
        return new C3P0DataSource();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private C3P0DataSource(){ }
}
