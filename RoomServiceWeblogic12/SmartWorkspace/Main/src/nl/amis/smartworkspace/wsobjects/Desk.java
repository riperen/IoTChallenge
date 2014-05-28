package nl.amis.smartworkspace.wsobjects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Desk {
    public Desk() {
        super();
    }
    
    /**
     * CONSTRUCTOR
     * 
     * @param roomId
     * @param deskNumber
     * @param dimensions
     */
    public Desk(Long roomId, Long deskNumber, String dimensions){
        this.roomId = roomId;
        this.deskNumber = deskNumber;
        this.dimensions = dimensions;
    }
    
    Long roomId;
    Long deskNumber;
    String dimensions;
    
    @XmlElement
    Device device;
    
    @XmlElement
    Employee employee;

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setDeskNumber(Long deskNumber) {
        this.deskNumber = deskNumber;
    }

    public Long getDeskNumber() {
        return deskNumber;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getDimensions() {
        return dimensions;
    }

}
