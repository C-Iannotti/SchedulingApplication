package model;

import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**A class to model customer data as Customer objects
 *
 * @author Conner Iannotti
 */
public final class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private ZonedDateTime createDate;
    private String createdBy;
    private ZonedDateTime lastUpdate;
    private String lastUpdatedBy;
    private int divisionId;
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    
    /**Constructor to crate Customer object to model data
     * 
     * @param id ID to set
     * @param name Name to set
     * @param address Address to set
     * @param postalCode Postal Code to set
     * @param phone Phone Number to set
     * @param createDate Created Datetime to set
     * @param createdBy Created By to set
     * @param lastUpdate Updated Datetime to set
     * @param lastUpdatedBy Updated By to set
     * @param divisionId Division ID to set
     */
    public Customer(int id, String name, String address, String postalCode, String phone, ZonedDateTime createDate,
            String createdBy, ZonedDateTime lastUpdate, String lastUpdatedBy, int divisionId) {
        this.id= id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionId = divisionId;
    }
    
    /**Sets the Customer's id
     * 
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**Gets the Customer's id
     * 
     * @return id
     */
    public int getId() {
        return id;
    }
    
    /**Sets the Customer's name
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**Gets the Customer's name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**Sets the Customer's address
     * 
     * @param address 
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**Gets the Customer's address
     * 
     * @return address
     */
    public String getAddress() {
        return address;
    }
    
    /**Sets the Customer's postalCode
     * 
     * @param postalCode 
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    /**Gets the Customer's postalCode
     * 
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }
    
    /**Sets the Customer's phone
     * 
     * @param phone 
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**Gets the Customer's phone
     * 
     * @return phone
     */
    public String getPhone() {
        return phone;
    }
    
    /**Sets the Customer's createDate datetime
     * 
     * @param createDate 
     */
    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }
    
    /**Gets the Customer's createDate datetime
     * 
     * @return createDate
     */
    public ZonedDateTime getCreateDate() {
        return createDate;
    }
    
    /**Sets the Customer's createdBy
     * 
     * @param createdBy 
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    /**Gets the Customer's createdBy
     * 
     * @return createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }
    
    /**Sets the Customer's lastUpdate datetime
     * 
     * @param lastUpdate 
     */
    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    /**Gets the Customer's lastUpdate datetime
     * 
     * @return lastUpdate
     */
    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    /**Sets the Customer's lastUpdateBy
     * 
     * @param lastUpdateBy 
     */
    public void setLastUpdatedBy(String lastUpdateBy) {
        this.lastUpdatedBy = lastUpdateBy;
    }
    
    /**Gets the Customer's lastUpdatedBy
     * 
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    
    /**Sets the Customer's Division ID
     * 
     * @param divisionId 
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
    
    /**Gets the Customer's Division ID
     * 
     * @return divisionId
     */
    public int getDivisionId() {
        return divisionId;
    }
    
    /**Adds an Appointment to the Customer's appointments
     * 
     * @param appointment
     * @return true if successful or
     * false if not
     */
    public boolean addAppointment(Appointment appointment) {
        return appointments.add(appointment);
    }
    
    /**Removes an Appointment from the Customer's appointments
     * 
     * @param appointment
     * @return true if successful or
     * false if not
     */
    public boolean removeAppointment(Appointment appointment) {
        return appointments.remove(appointment);
    }
    
    /**Makes a copy of the customer
     * 
     * @return Customer copy
     */
    public Customer copy() {
        Customer copy = new Customer(this.getId(),
        this.getName(),
        this.getAddress(),
        this.getPostalCode(),
        this.getPhone(),
        this.getCreateDate(),
        this.getCreatedBy(),
        this.getLastUpdate(),
        this.getLastUpdatedBy(),
        this.getDivisionId());
        
        return copy;
    }
    
    /**Returns a list of all appointments scheduled for the customer
     * 
     * @return copy of appointments
     */
    public ObservableList<Appointment> getAppointments() {
        ObservableList<Appointment> copy = FXCollections.observableArrayList();
        copy.addAll(appointments);
        return copy;
    }
    
}
