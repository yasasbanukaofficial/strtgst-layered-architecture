package edu.yb.strtgst.dao;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CrudDAO<T> extends SuperDAO{

    boolean addEntity(T entity) throws SQLException;

    T getEntity(String name) throws SQLException;

    public boolean updateEntity(T entity) throws SQLException;

    String getEntityIdByUsername(String name) throws SQLException;

    public boolean deleteEntity(String id) throws SQLException;

    String fetchExistingID(String name) throws SQLException;

    public ArrayList<T> getAll() throws SQLException;

    String loadNextID();
}
