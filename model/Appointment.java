package model;

import java.time.ZonedDateTime;

/**A class to model appointment data as Appointment objects.
 *
 * @author Conner Iannotti
 */
public final class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime createDate;
    private String createdBy;
    private ZonedDateTime lastUpdate;
    private String lastUpdatedBy;
    private int customerId;
    private int userId;
    private int contactId;
    
    /**Constructor to create Appointment object to model data
     * 
     * @param id ID to set
     * @param title title to set
     * @param description description to set
     * @param location location to set
     * @param type type to set
     * @param start start datetime to set
     * @param end end datetime to set
     * @param createDate created datetime to set
     * @param createdBy created by to set
     * @param lastUpdate last update datetime to set
     * @param lastUpdatedBy last updated by to set
     * @param customerId Customer ID to set
     * @param userId User ID to set
     * @param contactId Contact ID to set
     */
    public Appointment(int id, String title, String description, String location, String type, ZonedDateTime start,
            ZonedDateTime end, ZonedDateTime createDate, String createdBy, ZonedDateTime lastUpdate, String lastUpdatedBy,
            int customerId, int userId, int contactId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    /**Gets the Appointment's id
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**Sets the Appointment's id
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**Gets the Appointment's title
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**Sets the Appointment's title
     * 
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**Gets the Appointment's description
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**Sets the Appointment's description
     * 
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**Gets the Appointment's location
     * 
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**Sets the Appointment's location
     * 
     * @param location 
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**Gets the Appointment's type
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**Sets the Appointment's type
     * 
     * @param type 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**Gets the Appointment's start datetime
     * 
     * @return start
     */
    public ZonedDateTime getStart() {
        return start;
    }

    /**Sets the Appointment's start datetime
     * 
     * @param start 
     */
    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    /**Gets the Appointment's end datetime
     * 
     * @return end
     */
    public ZonedDateTime getEnd() {
        return end;
    }

    /**Sets the Appointment's end datetime
     * 
     * @param end 
     */
    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    /**Gets the Appointment's created datetime
     * 
     * @return createDate
     */
    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    /**Sets the Appointment's created datetime
     * 
     * @param createDate 
     */
    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    /**Gets the Appointment's created by
     * 
     * @return createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**Sets the Appointment's created by
     * 
     * @param createdBy 
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**Gets the Appointment's last update datetime
     * 
     * @return lastUpdate
     */
    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**Sets the Appointment's last update datetime
     * 
     * @param lastUpdate 
     */
    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**Gets the Appointment's last updated by
     * 
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**Sets the Appointment's last updated by
     * 
     * @param lastUpdatedBy 
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**Gets the Appointment's Customer ID
     * 
     * @return customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**Sets the Appointment's Customer ID
     * 
     * @param customerId 
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**Gets the Appointment's User ID
     * 
     * @return userId
     */
    public int getUserId() {
        return userId;
    }

    /**Sets the Appointment's User ID
     * 
     * @param userId 
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**Gets the Appointment's Contact ID
     * 
     * @return contactId
     */
    public int getContactId() {
        return contactId;
    }

    /**Sets the Appointment's Contact ID
     * 
     * @param contactId 
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
    
    /**Makes a copy of the appointment
     * 
     * @return Appointment copy
     */
    public Appointment copy() {
        Appointment copy = new Appointment(this.getId(),
        this.getTitle(),
        this.getDescription(),
        this.getLocation(),
        this.getType(),
        this.getStart(),
        this.getEnd(),
        this.getCreateDate(),
        this.getCreatedBy(),
        this.getLastUpdate(),
        this.getLastUpdatedBy(),
        this.getCustomerId(),
        this.getUserId(),
        this.getContactId());
        
        return copy;
    }
}
