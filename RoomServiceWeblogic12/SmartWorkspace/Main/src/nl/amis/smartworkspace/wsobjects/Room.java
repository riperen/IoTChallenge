package nl.amis.smartworkspace.wsobjects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Room {
    public Room() {
        super();
    }
    
   public Room(Long roomId, String roomName, String dimensions){
        this.roomId = roomId;
        this.roomName = roomName;
        this.dimensions = dimensions;
    }
    
    Long roomId;
    String roomName;
    String dimensions;
    
    @XmlElement
    List<Desk> desks = new ArrayList<Desk>();

    public List<Desk> getDesks() {
        return desks;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getDimensions() {
        return dimensions;
    }
}
