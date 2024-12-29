package application;

import model.dao.DaoFactory;
import model.dao.TaskDao;
import model.dao.UserDao;
import model.entities.Task;
import model.entities.User;


public class Program {
    public static void main(String[] args) {
        TaskDao taskDao = DaoFactory.createTaskDao();
        UserDao userDao = DaoFactory.createUserDao();

        for (User user1 : userDao.findAll()) {
            System.out.println(user1);
        }

        for (Task task : taskDao.findAll()) {
            System.out.println(task);
        }

    }
}
