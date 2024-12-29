package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.UserDao;
import model.entities.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBC implements UserDao {

    private final Connection conn;

    public UserDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(User user) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    """
                          INSERT INTO User (name, email, password)
                          VALUES (?, ?, ?);
                      """, Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, user.getPassword());

            int rowsAffected =  st.executeUpdate();

            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    user.setId(id);
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
    public void update(User user) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    """
                          UPDATE User
                          SET name = ?, email = ?, password = ?
                          WHERE id = ?;
                      """, Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, user.getPassword());
            st.setInt(4, user.getId());
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
            st = conn.prepareStatement("DELETE FROM User WHERE id = ?");
            st.setInt(1, id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public User findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    """
                            SELECT id, name, email, password
                            FROM User
                            WHERE id = ?;
                      """
            );

            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                return instantiateUser(rs);
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
    public List<User> findAll() {
        Statement st = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        try {
            st = conn.createStatement();
            rs = st.executeQuery(
                    """
                            SELECT id, name, email, password
                            FROM User
                      """
            );

            while (rs.next()) {
                users.add(instantiateUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    private User instantiateUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}
