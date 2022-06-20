/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import domain.Event;
import domain.EventInvitation;
import domain.Role;
import domain.Status;
import domain.User;
import java.util.Collection;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import helpers.LogSqlFactory;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;

/**
 *
 * @author Liam
 */
@LogSqlFactory
public interface SQLiteDAO {

    @SqlUpdate("INSERT INTO User (firstName, lastName, email, password, phoneNumber, roleID) VALUES (:firstName, :lastName, :email, :password, :phoneNumber, :roleID)")
    public void insertUser(@BindBean User user);
    
    @SqlUpdate("INSERT OR REPLACE INTO Role(roleID, roleName) VALUES(:roleID, :roleName)")
    public void insertRole(@BindBean Role role);
    
    @SqlUpdate("INSERT OR REPLACE INTO Status(statusID, statusName) VALUES (:statusID, :statusName)")
    public void insertStatus(@BindBean Status status);
    
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO Event(userID, date, location, duration, description, statusID) VALUES (:userID, :date, :location, :duration, :description, :statusID)")
    public Integer insertEvent(@BindBean Event event);
    
    @SqlUpdate("INSERT INTO EventInvitation(userID, eventID, isAccepted) VALUES (:userID, :eventID, :isAccepted)")
    public void insertEventInvitation(@BindBean EventInvitation eventInvitation);
    
    @SqlUpdate("DELETE FROM Event WHERE eventID = :eventID;")
    public void deleteEvent(@Bind("eventID") Integer id);
    
    @SqlUpdate("DELETE FROM EventInvitation WHERE eventID = :eventID;")
    public void deleteEventInvitationsByEventID(@Bind("eventID") Integer id);
    @SqlUpdate("DELETE FROM EventInvitation WHERE eventID = :eventID AND userID = :userID;")
    public void deleteInviteByUserAndEventID(@Bind("eventID") Integer eventID, @Bind("userID") Integer userID);

    @SqlUpdate("DELETE FROM EventInvitation WHERE userID = :userID;")
    public void deleteInvitesByUserID(@Bind("userID") Integer userID);

    @SqlUpdate("DELETE FROM User WHERE userID = :userID;")
    public void deleteUserByUserID(@Bind("userID") Integer userID);

    @SqlUpdate("DELETE FROM Event WHERE userID = :userID;")
    public void deleteEventByUserID(@Bind("userID") Integer userID);
    
    @SqlQuery("SELECT User.userID, User.firstName, User.lastName, User.email, User.phoneNumber, User.roleID, Role.roleName FROM User "
            + "INNER JOIN Role on User.roleID = Role.roleID;")
    @RegisterBeanMapper(User.class)
    public Collection<User> getAllUsers();
    
    @SqlQuery("SELECT * FROM Role;")
    @RegisterBeanMapper(Role.class)
    public Collection<Role> getAllRoles();
    
    @SqlQuery("SELECT * FROM Status;")
    @RegisterBeanMapper(Status.class)
    public Collection<Status> getAllStatuses();
    
    @SqlQuery("SELECT * FROM Event;")
    @RegisterBeanMapper(Event.class)
    public Collection<Event> getAllEvents();
    
    @SqlQuery("SELECT * FROM EventInvitation;")
    @RegisterBeanMapper(EventInvitation.class)
    public Collection<EventInvitation> getAllEventInvitations();
    
    @SqlQuery("SELECT roleName FROM Role WHERE roleID = :roleID")
    public String getRoleNameByRoleID(@Bind("roleID") Integer id);
    
    @SqlQuery("SELECT * FROM Event WHERE eventID = :eventID")
    @RegisterBeanMapper(Event.class)
    public Event getEventByEventID(@Bind("eventID") Integer id);
    
    @SqlQuery("SELECT Event.* FROM Event "
            + "INNER JOIN EventInvitation ON Event.eventID = EventInvitation.eventID "
            + "INNER JOIN User ON EventInvitation.userID = User.userID "
            + "WHERE User.userID = :userID")
    @RegisterBeanMapper(Event.class)
    public Collection<Event> getEventsByInvitedUserID(@Bind("userID") Integer id);
    
    @SqlQuery("SELECT Event.* FROM Event "
            + "WHERE Event.userID = :userID")
    @RegisterBeanMapper(Event.class)
    public Collection<Event> getEventsByUserID(@Bind("userID") Integer id);
    
    @SqlQuery("SELECT EventInvitation.* FROM Event "
            + "INNER JOIN EventInvitation ON Event.eventID = EventInvitation.eventID "
            + "WHERE Event.eventID = :eventID")
    @RegisterBeanMapper(EventInvitation.class)
    public Collection<EventInvitation> getInvitesByEventID(@Bind("eventID") Integer id);
    
    @SqlQuery("SELECT * FROM EventInvitation "
            + "WHERE eventID = :eventID AND userID = :userID")
    @RegisterBeanMapper(EventInvitation.class)
    public EventInvitation getInviteByUserIDAndEventID(@Bind("eventID") Integer eventID, @Bind("userID") Integer userID);
    
    @SqlQuery("SELECT userID, firstName, lastName, email, phoneNumber, roleID FROM User WHERE userID = :userID")
    @RegisterBeanMapper(User.class)
    public User getUserByID(@Bind("userID") Integer id);
    
    @SqlQuery("SELECT userID, firstName, lastName, email, phoneNumber, roleID FROM User WHERE (email = :email AND password = :password)")
    @RegisterBeanMapper(User.class)
    public User getUserByEmailPassword(@BindBean User user);
    
    @SqlQuery("SELECT userID, firstName, lastName, email, phoneNumber, roleID FROM User WHERE (email = :email)")
    @RegisterBeanMapper(User.class)
    public User getUserByEmail(@Bind("email") String email);
    
    @SqlUpdate("UPDATE User SET Password = :password WHERE userID = :userID")
    void setPassword(@BindBean User user);
    
    @SqlQuery("SELECT userID, firstName, lastName, email, phoneNumber, roleID FROM User WHERE email = :email AND password IS NULL;")
    @RegisterBeanMapper(User.class)
    public User getUserWithoutPassword(@Bind("email") String email);
    
    @SqlQuery("SELECT * FROM Status WHERE statusID = :statusID")
    @RegisterBeanMapper(Status.class)
    public Status getStatusByID(@Bind("statusID") Integer id);
    
    
    @SqlUpdate("UPDATE User SET firstName = :firstName, lastName = :lastName, email = :email, phoneNumber = :phoneNumber, roleID = :roleID WHERE userID = :userID")
    public void updateUser(@BindBean User user);
    
    
    @SqlUpdate("UPDATE EventInvitation SET isAccepted = :isAccepted WHERE userID = :userID AND eventID = :eventID")
    public void updateEventInvitation(@BindBean EventInvitation eventInvite);

    @SqlUpdate("UPDATE Event SET statusID = :statusID WHERE eventID = :eventID")
    public void changeEventStatus(@BindBean Event event);
}
