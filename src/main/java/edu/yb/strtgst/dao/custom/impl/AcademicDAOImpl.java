package edu.yb.strtgst.dao.custom.impl;

import edu.yb.strtgst.dao.custom.AcademicDAO;
import edu.yb.strtgst.entity.Academic;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AcademicDAOImpl implements AcademicDAO {

    @Override
    public boolean addEntity(Academic entity) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Academic VALUES (?, ?, ?, ?, ?, ?, ?)",
                entity.getId(),
                entity.getTitle(),
                entity.getLocation(),
                entity.getIsFullDay(),
                entity.getFromDateTime(),
                entity.getToDateTime(),
                entity.getRepeatType()
        );
    }

    @Override
    public Academic getEntity(String id) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Academic WHERE id = ?", id);
        if (rst.next()) {
            return new Academic(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getBoolean(4),
                    rst.getTimestamp(5),
                    rst.getTimestamp(6),
                    rst.getString(7)
            );
        }
        return null;
    }

    @Override
    public boolean updateEntity(Academic entity) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Academic SET title = ?, location = ?, is_full_day = ?, from_date = ?, to_date = ?, repeat_type = ? WHERE id = ?",
                entity.getTitle(),
                entity.getLocation(),
                entity.getIsFullDay(),
                entity.getFromDateTime(),
                entity.getToDateTime(),
                entity.getRepeatType(),
                entity.getId()
        );
    }

    @Override
    public String getEntityIdByUsername(String name) throws SQLException {
        return "";
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return CrudUtil.execute("DELETE FROM Academic WHERE id = ?", id);
    }

    @Override
    public String fetchExistingID(String name) throws SQLException {
        return "";
    }

    @Override
    public ArrayList<Academic> getAll() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Academic");

        ArrayList<Academic> academics = new ArrayList<>();
        while (rst.next()) {
            Academic academic = new Academic(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getBoolean(4),
                    rst.getTimestamp(5),
                    rst.getTimestamp(6),
                    rst.getString(7)
            );
            academics.add(academic);
        }

        return academics;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        return "";
    }

    @Override
    public boolean syncEntryByAi(String query) throws SQLException {
        String[] statements = query.split(";");
        for (String stmt : statements) {
            if (!stmt.trim().isEmpty()) {
                boolean success = CrudUtil.execute(stmt.trim());
                if (!success) return false;
            }
        }
        return true;
    }

    @Override
    public Academic getRecentDetails(String tableName) throws SQLException {
        ResultSet rst = CrudUtil.execute(
                "SELECT * FROM " + tableName + " WHERE from_date >= CURRENT_DATE ORDER BY from_date ASC LIMIT 1"
        );
        if (rst.next()) {
            return new Academic(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getBoolean(4),
                    rst.getTimestamp(5),
                    rst.getTimestamp(6),
                    rst.getString(7)
            );
        }
        return null;
    }
}
