package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    String url_db;
    String user;
    String password;
    Properties prop = new Properties();

    FileInputStream fileInputStream;

    public DataBaseConfig() {
        try {
            fileInputStream = new FileInputStream("application.properties");
            prop.load(fileInputStream);

            url_db = prop.getProperty("url_db");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that create a connection to the server
     * @throws ClassNotFoundException - throws ClassNotFound exception
     * @throws SQLException - throws SQL exception
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                url_db,user,password); // serverTimezone need to be set to be able to use Date() objects
    }

    /**
     * Method that close a database connection
     * @param con - connection to the database
     */
    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    /**
     * Method that close a prepared statement
     * @param ps - prepared statement
     */
    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    /**
     * Method that close a resultSet
     * @param rs - result set object
     */
    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
