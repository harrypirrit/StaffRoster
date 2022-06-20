package domain;

import roster.App;

/**
 * EventInvitation - represents a shift that an employee is assigned.
 * @author harrypirrit
 */
public class EventInvitation {
    private Integer userID;
    private Integer eventID;
    private Boolean isAccepted;

    public EventInvitation(Integer userID, Integer eventID, Boolean isAccepted) {
        this.userID = userID;
        this.eventID = eventID;
        this.isAccepted = isAccepted;
    }
    
    public EventInvitation() {
        super();
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    /**
     * Checks if event exists with id
     * @return 
     */
    public boolean exists() {
        EventInvitation newInvite = App.dao.getInviteByUserIDAndEventID(eventID, userID);
        if (newInvite != null) {
            return true;
        } else {
            return false;
        }
    }
}
