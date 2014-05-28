package nl.amis.smartworkspace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import nl.amis.smartworkspace.database.SmartWorkspaceDB;
import nl.amis.smartworkspace.wsobjects.Room;

@Path("smartworkspace")
public class SmartWorkspaceService {
    public SmartWorkspaceService() {
        super();
    }

    @GET
    @Produces("application/json")
    public Room getRoom(@QueryParam("roomId") Long roomId){
        SmartWorkspaceDB db = new SmartWorkspaceDB();
        return db.getRoom(roomId);
    }
    
}
