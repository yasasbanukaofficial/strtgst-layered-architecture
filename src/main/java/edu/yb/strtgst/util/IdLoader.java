package edu.yb.strtgst.util;

import edu.yb.strtgst.dao.SQLUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IdLoader {
    public static String getNextID(String tableName, String idColumn) throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT " + idColumn + " FROM " + tableName + " ORDER BY " + idColumn + " DESC LIMIT 1");
        char firstCharacter = tableName.charAt(0);

        if (rst.next()){
            String lastId = rst.getString(1);
            String lastIdNumString = lastId.substring(1);
            int lastIdNumber = Integer.parseInt(lastIdNumString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(firstCharacter + "%03d", nextIdNumber);
            return nextIdString;
        }
        return firstCharacter + "001";
    }

    // Build Specifically fo Subject Table
    public static String getNextIdForTwoChar(String tableName, String idColumn) throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT " + idColumn + " FROM " + tableName + " ORDER BY " + idColumn + " DESC LIMIT 1");
        String idCharacter = tableName.charAt(0) + "U";

        if (rst.next()){
            String lastId = rst.getString(1);
            String lastIdNumString = lastId.substring(2);
            int lastIdNumber = Integer.parseInt(lastIdNumString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(idCharacter + "%03d", nextIdNumber);
            return nextIdString;
        }
        return idCharacter + "001";
    }
    public static String fetchIdByName(String tableName, String idColumn, String name) throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT " + idColumn + " FROM " + tableName + "WHERE sub_name = " + name + " ORDER BY sub_name DESC LIMIT 1");

        if (rst.next()){
            String subId = rst.getString(1);
            return subId;
        }
        return null;
    }
}
