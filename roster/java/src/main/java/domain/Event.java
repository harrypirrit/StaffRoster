package domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import roster.App;

/**
 *
 * @author harrypirrit
 */
public class Event {
    private Integer eventID;
    private Integer userID;
    private String date;
    private String location;
    private Double duration;
    private String description;
    private Integer statusID;
    private Status status;
    private Collection<User> assignees = new HashSet();
    private User creator;

    public Event(Integer eventID, Integer userID, String date, String location, Double duration, String description, Integer statusID) {
        this.eventID = eventID;
        this.userID = userID;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.description = description;
        this.statusID = statusID;
    } 
    
    public Event() {
        super();
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatusID() {
        return statusID;
    }

    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Collection<User> getAssignees() {
        return assignees;
    }

    public void addAssignee(User user) {
        this.assignees.add(user);
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    /**
     * Checks if event exists with id
     * @return 
     */
    public boolean exists() {
        Event newEvent = App.dao.getEventByEventID(eventID);
        if (newEvent != null) {
            return true;
        } else {
            return false;
        }
    }
}
