package resource;

import dao.SQLiteDAO;
import domain.Event;
import domain.EventInvitation;
import domain.Status;
import domain.User;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;
import io.jooby.Jooby;
import io.jooby.StatusCode;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;

import service.EmailService;

public class EventResource extends Jooby {

    private String jwtPassphrase = "this is a staff events roster";

    public EventResource(SQLiteDAO dao) {
        path("/api", () -> {
            path("/events", () -> {

                /**
                 * Gets all events by user
                 */
                get("", ctx -> {
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        } else {
                            Collection<Event> events;
                            if (user.getRoleID() == 1) {
                                events = dao.getAllEvents();
                            } else {
                                events = dao.getEventsByInvitedUserID(user.getUserID());
                                Collection<Event> ownedEvents = dao.getEventsByUserID(user.getUserID());
                                if (ownedEvents != null && !ownedEvents.isEmpty()) {
                                    for (Event ownedEvent : ownedEvents) {
                                        if (!events.contains(ownedEvent)) {
                                            events.add(ownedEvent);
                                        }
                                    }
                                }
                            }
                            if (events != null && !events.isEmpty()) {
                                for (Event event : events) {
                                    Collection<EventInvitation> invites = dao.getInvitesByEventID(event.getEventID());
                                    if (invites != null && !invites.isEmpty()) {
                                        for (EventInvitation invite : invites) {
                                            User assignee = dao.getUserByID(invite.getUserID());
                                            assignee.setIsAccepted(invite.getIsAccepted());
                                            event.addAssignee(assignee);
                                        }
                                    }

                                    User creator = dao.getUserByID(event.getUserID());
                                    if (creator != null) {
                                        event.setCreator(creator);
                                    }
                                    Status status = dao.getStatusByID(event.getStatusID());
                                    if (status != null) {
                                        event.setStatus(status);
                                    }

                                }
                            }
                            return events;
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                });

                /**
                 * Gets and returns a list of all statuses
                 */
                get("/statuses", ctx -> {
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                   return dao.getAllStatuses();
                });
            });

            path("/event", () -> {

                /**
                 * Adds new event
                 */
                post("", ctx -> {
                    String creatorsName;
                    Event newEvent = ctx.body().to(Event.class);
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                        creatorsName = user.getFirstName() + " " + user.getLastName();
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                    if (newEvent != null) {
                        newEvent.setStatusID(1);
                        newEvent.setEventID(dao.insertEvent(newEvent));
                        if (newEvent.getAssignees() != null && newEvent.getAssignees().size() >= 1) {
                            for (User assignee : newEvent.getAssignees()) {
                                assignee = dao.getUserByID(assignee.getUserID());
                                EventInvitation newInvite = new EventInvitation(assignee.getUserID(),
                                        newEvent.getEventID(), false);
                                if (!newInvite.exists()) {
                                    Signer signer = HMACSigner.newSHA256Signer(jwtPassphrase);
                                    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                                    ZonedDateTime expiry = now.plusDays(2);
                                    JWT jwt = new JWT().setIssuedAt(now)
                                            .setUniqueId(assignee.getUserID().toString())
                                            .setSubject(newEvent.getEventID().toString())
                                            .setExpiration(expiry);

                                    String accessToken = JWT.getEncoder().encode(jwt, signer);
                                    String emailBody = "Hello " + assignee.getFirstName() + ","
                                            + "<br>"
                                            + creatorsName + " has invited you to attend " + newEvent.getDescription()
                                            + ", to see more details, please click the link below."
                                            + "<br>"
                                            + "<a href='" + ctx.header("origin").to(String.class)
                                            + "/events/view-invite?token=" + accessToken + "'>View more</a>";
                                    String emailSubject = "Invitation to attend " + newEvent.getDescription();
                                    EmailService emailService = new EmailService();
                                    if (emailService.SendEmail(assignee.getEmail(), emailSubject, emailBody)) {
                                        dao.insertEventInvitation(newInvite);
                                    }
                                }
                            }
                        }

                        return ctx.send(StatusCode.CREATED);
                    } else {
                        return ctx.send(StatusCode.NO_CONTENT);
                    }
                });

                /**
                 * Deletes event using ID
                 */
                delete("/{id}", ctx -> {
                    String eventID = ctx.path("id").value();
                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        User user = dao.getUserByID(Integer.parseInt(jwt.uniqueId));
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }
                    if (eventID != null || !eventID.equals("")) {
                        Event event = dao.getEventByEventID(Integer.parseInt(eventID));
                        if (event.exists()) {
                            Collection<EventInvitation> invites = dao.getInvitesByEventID(event.getEventID());
                            if (invites != null && invites.size() >= 1) {
                                dao.deleteEventInvitationsByEventID(event.getEventID());
                            }
                            dao.deleteEvent(event.getEventID());
                            return ctx.send(StatusCode.NO_CONTENT);
                        } else {
                            return ctx.send(StatusCode.NOT_FOUND);
                        }
                    } else {
                        return ctx.send(StatusCode.NO_CONTENT);
                    }
                });

                /**
                 * Gets event which user is being invited to.
                 * returns null if token is invalid
                 */
                get("/getInvitedEvent/{token}", ctx -> {
                    try {
                        String token = ctx.path("token").value();
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(token, verifier);
                        Event event = dao.getEventByEventID(Integer.parseInt(jwt.subject));
                        event.setCreator(dao.getUserByID(event.getUserID()));
                        EventInvitation invite = dao.getInviteByUserIDAndEventID(event.getEventID(),
                                Integer.parseInt(jwt.uniqueId));
                        if (invite.getIsAccepted()) {
                            return null;
                        }
                        return event;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                });

                /**
                 * Accepts the invite
                 */
                post("/acceptInvite", ctx -> {
                    try {
                        String token = ctx.body().to(String.class);
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(token, verifier);
                        EventInvitation eventInvite = new EventInvitation(Integer.parseInt(jwt.uniqueId),
                                Integer.parseInt(jwt.subject), true);
                        dao.updateEventInvitation(eventInvite);
                        return ctx.send(StatusCode.OK);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return ctx.send(StatusCode.BAD_REQUEST);
                    }
                });

                /**
                 * Invites new users to an event
                 */
                put("/inviteUsers", ctx -> {
                    Event event = ctx.body().to(Event.class);

                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }

                    if (event != null) {
                        Collection<User> users = event.getAssignees();
                        if (users != null) {
                            event = dao.getEventByEventID(event.getEventID());
                            User creator = dao.getUserByID(event.getUserID());

                            for (User user : users) {
                                User assignee = dao.getUserByID(user.getUserID());
                                EventInvitation newInvite = new EventInvitation(assignee.getUserID(),
                                        event.getEventID(), false);
                                if (!newInvite.exists()) {
                                    Signer signer = HMACSigner.newSHA256Signer(jwtPassphrase);
                                    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                                    ZonedDateTime expiry = now.plusDays(2);
                                    JWT jwt = new JWT().setIssuedAt(now)
                                            .setUniqueId(assignee.getUserID().toString())
                                            .setSubject(event.getEventID().toString())
                                            .setExpiration(expiry);

                                    String accessToken = JWT.getEncoder().encode(jwt, signer);
                                    String emailBody = "Hello " + assignee.getFirstName() + ","
                                            + "<br>"
                                            + creator.getFirstName() + " " + creator.getLastName() + " has invited you to attend " + event.getDescription()
                                            + ", to see more details, please click the link below."
                                            + "<br>"
                                            + "<a href='" + ctx.header("origin").to(String.class)
                                            + "/events/view-invite?token=" + accessToken + "'>View more</a>";
                                    String emailSubject = "Invitation to attend " + event.getDescription();
                                    EmailService emailService = new EmailService();
                                    if (emailService.SendEmail(assignee.getEmail(), emailSubject, emailBody)) {
                                        dao.insertEventInvitation(newInvite);
                                    }
                                }
                            }
                            return ctx.send(StatusCode.OK);
                        } else {
                            return ctx.send(StatusCode.NO_CONTENT);
                        }
                    } else {
                        return ctx.send(StatusCode.NO_CONTENT);
                    }
                });

                put("/changeStatus", ctx -> {
                    Event event = ctx.body().to(Event.class);

                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }

                    if (event != null) {
                        Status status = dao.getStatusByID(event.getStatusID());
                        event = dao.getEventByEventID(event.getEventID());
                        event.setStatusID(status.getStatusID());
                        dao.changeEventStatus(event);
                    }
                    return ctx.send(StatusCode.OK);
                });

                post("/uninvite/{eventID}", ctx -> {
                    Integer userID = ctx.body().to(Integer.class);
                    Integer eventID = Integer.parseInt(ctx.path("eventID").value());

                    String encodedJWT = ctx.header("Authorization").to(String.class);
                    if (encodedJWT != null && !encodedJWT.equals("")) {
                        Verifier verifier = HMACVerifier.newVerifier(jwtPassphrase);
                        JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
                        if (jwt.isExpired() && jwt.issuedAt.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
                            return ctx.send(StatusCode.UNAUTHORIZED);
                        }
                    } else {
                        return ctx.send(StatusCode.UNAUTHORIZED);
                    }

                    if (userID != null && eventID != null) {
                        dao.deleteInviteByUserAndEventID(eventID, userID);
                        return ctx.send(StatusCode.OK);
                    } else {
                        return ctx.send(StatusCode.NO_CONTENT);
                    }
                });
            });
        });
    }
}
