package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.TaskDao;
import model.entities.Task;
import model.entities.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDaoJDBC implements TaskDao {

    private final Connection conn;

    public TaskDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Task task) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    """
                          INSERT INTO Task (title, description, status, creation_date, start_date, deadline, user_id)
                          VALUES (?, ?, ?, ?, ?, ?, ?);
                      """, Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, task.getTitle());
            st.setString(2, task.getDescription());
            st.setString(3, task.getStatus().toString());
            st.setTimestamp(4, Timestamp.valueOf(task.getCreationDate()));

            LocalDateTime startDate = task.getStartDate();
            st.setTimestamp(5, startDate == null ? null : Timestamp.valueOf(startDate));

            LocalDateTime deadline = task.getDeadline();
            st.setTimestamp(6, deadline == null ? null : Timestamp.valueOf(deadline));

            st.setInt(7, task.getUser().getId());

            int rowsAffected =  st.executeUpdate();

            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    task.getUser().setId(id);
                }
            } else {
                throw new DbException("Unexpect error: No rows affected");
            }


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Task task) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    """
                          UPDATE Task
                          SET title = ?, description = ?, status = ?, creation_date = ?, start_date = ?, deadline = ?
                          WHERE id = ?;
                      """, Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, task.getTitle());
            st.setString(2, task.getDescription());
            st.setString(3, task.getStatus().toString());
            st.setTimestamp(4, Timestamp.valueOf(task.getCreationDate()));

            LocalDateTime startDate = task.getStartDate();
            st.setTimestamp(5, startDate == null ? null : Timestamp.valueOf(startDate));

            LocalDateTime deadline = task.getDeadline();
            st.setTimestamp(6, deadline == null ? null : Timestamp.valueOf(deadline));

            st.setInt(7, task.getId());

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM Task WHERE id = ?");
            st.setInt(1, id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Task findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    """
                            SELECT T.id AS task_id, T.title as task_id, T.description as task_description, T.creation_date as task_creation_date,\
                                   T.start_date as task_start_date, T.deadline as task_deadline, T.user_id,\
                                   U.name as user_name, U.email as user_email, U.password as user_password
                            FROM Task as T
                            JOIN User U on U.id = T.user_id
                            WHERE T.id = ?;"""
            );
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Task task = instantiateTask(rs);
                task.setUser(instantiateUser(rs));
                return task;
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }

        return null;
    }

    @Override
    public List<Task> findAll() {
        Statement st = null;
        ResultSet rs = null;
        List<Task> tasks = new ArrayList<>();
        Map<Integer, User> users = new HashMap<>();

        try {
            st = conn.createStatement();
            rs = st.executeQuery(
                    """
                            SELECT T.id AS task_id, T.title as task_title, T.description as task_description, T.creation_date as task_creation_date,\
                                   T.start_date as task_start_date, T.deadline as task_deadline, T.user_id,\
                                   U.name as user_name, U.email as user_email, U.password as user_password
                            FROM Task as T
                            JOIN User U on U.id = T.user_id
                       """
            );

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                User user = users.getOrDefault(userId, instantiateUser(rs));

                users.putIfAbsent(userId, user);

                Task task = instantiateTask(rs);
                task.setUser(user);
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    private Task instantiateTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("task_id"));
        task.setTitle(rs.getString("task_title"));
        task.setDescription(rs.getString("task_description"));
        task.setCreationDate(rs.getTimestamp("task_creation_date").toLocalDateTime());
        Timestamp startDate = rs.getTimestamp("task_start_date");
        task.setStartDate(startDate == null ? null : startDate.toLocalDateTime());

        Timestamp deadline = rs.getTimestamp("task_deadline");
        task.setDeadline(deadline == null ? null : deadline.toLocalDateTime());
        return task;
    }

    private User instantiateUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("user_email"));
        user.setPassword(rs.getString("user_password"));
        return user;
    }

}
