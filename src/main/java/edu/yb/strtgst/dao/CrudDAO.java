package edu.yb.strtgst.dao;

import java.sql.SQLException;

public interface CrudDAO<T> extends SuperDAO{

    boolean addEntity(T entity) throws SQLException;

    T getEntity(String username) throws SQLException;

    public boolean updateEntity(T entity) throws SQLException;

    String getEntityIdByUsername(String username) throws SQLException;
}
