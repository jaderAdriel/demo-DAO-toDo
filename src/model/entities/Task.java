package model.entities;

import model.entities.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private User user;

    public Task() {
    }

    public Task(Integer id, String title, String description, TaskStatus status, LocalDateTime creationDate, LocalDateTime start_date, LocalDateTime deadline, User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.creationDate = creationDate;
        this.startDate = start_date;
        this.deadline = deadline;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
