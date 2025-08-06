package edu.yb.strtgst.util;

import edu.yb.strtgst.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudUtil {
    public static <T> T execute(String sql, Object... obj) throws SQLException{
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement(sql);

        for (int i = 0; i < obj.length; i++) {
            pst.setObject(i + 1, obj[i]);
        }

        if (sql.startsWith("select") || sql.startsWith("SELECT")){
            ResultSet rst = pst.executeQuery();
            return (T) rst;
        } else {
            int i = pst.executeUpdate();
            return (T) (Boolean) (i > 0);
        }
    }
}