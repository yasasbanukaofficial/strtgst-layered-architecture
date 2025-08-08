package edu.yb.strtgst.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationUtil {
    public static boolean updatePassword(String password, String email) throws SQLException {
        return CrudUtil.execute("UPDATE Student SET password = ? WHERE email = ?", password, email);
    }

    public boolean checkIfEmailExisting(String userEmail) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT email FROM Student WHERE email = ? ORDER BY email DESC LIMIT 1", userEmail);
        if (rst.next()){
            return true;
        }
        return false;
    }
}
