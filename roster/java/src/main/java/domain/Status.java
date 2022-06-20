/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Liam
 */
public class Status {
    private Integer statusID;
    private String statusName;

    public Status(Integer statusID, String statusName) {
        this.statusID = statusID;
        this.statusName = statusName;
    }
    
    public Status() {
        super();
    }

    public Integer getStatusID() {
        return statusID;
    }

    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
