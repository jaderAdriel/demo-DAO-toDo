package model.dao;

import db.DB;
import model.dao.impl.TaskDaoJDBC;
import model.dao.impl.UserDaoJDBC;
import model.entities.Task;

public class DaoFactory {
    public static UserDao createUserDao() {
        return new UserDaoJDBC(DB.getConnection());
    }
    public static TaskDao createTaskDao() {
        return new TaskDaoJDBC(DB.getConnection());
    }
}
