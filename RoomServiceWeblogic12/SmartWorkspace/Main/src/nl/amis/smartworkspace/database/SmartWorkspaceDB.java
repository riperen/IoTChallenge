package nl.amis.smartworkspace.database;

import nl.amis.smartworkspace.wsobjects.Desk;
import nl.amis.smartworkspace.wsobjects.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import nl.amis.smartworkspace.wsobjects.Device;

import nl.amis.smartworkspace.wsobjects.Employee;

import oracle.jdbc.pool.OracleDataSource;


public class SmartWorkspaceDB {
    public SmartWorkspaceDB() {
        super();
    }
    
    Connection connection = null;

    public Room getRoom(Long roomId) {
 
        Room room = null;
        try {
            connection = getDBConnection();
            PreparedStatement statement = connection.prepareStatement("select * from rooms where room_id = ?");
            statement.setLong(1, roomId);
            ResultSet resultset = statement.executeQuery();
            while(resultset.next()){
                room = new Room(roomId, resultset.getString("ROOM_NAME"), resultset.getString("ROOM_DIMENSIONS"));
                break; //ID is primary key, there can be only one
            }
            
            //No room found
            if (room == null){
                return null;
            }
            
            //Add desks to this room
            addDesks(room);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return room;
   }
    
    /**
     * Query all desks for this room
     * @param room
     */
    private void addDesks(Room room) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(getDeskQuery());
            statement.setLong(1, room.getRoomId());
            ResultSet resultset = statement.executeQuery();
            while(resultset.next()){
                Desk desk = new Desk(room.getRoomId(), resultset.getLong("DESK_NUMBER"), resultset.getString("DESK_DIMENSIONS"));
                if (resultset.getString("DEVICE_ID") != null){
                    desk.setDevice(new Device(resultset.getString("DEVICE_ID"),resultset.getString("DEVICE_NAME"), resultset.getString("DEVICE_TYPE")));
                    
                    //If there is a device_id, than there is also an employee_id
                    desk.setEmployee(new Employee(resultset.getLong("EMPLOYEE_ID"),resultset.getString("FIRST_NAME"), resultset.getString("MIDDLE_NAME"), resultset.getString("LAST_NAME"), resultset.getString("JOB_TITLE"), resultset.getString("EXPERTISE")));
                }

                    
                
                
                room.getDesks().add(desk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void closeConnection(Connection connection){
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    }

    public Connection getDBConnection() throws SQLException {
        Connection conn;
        OracleDataSource ds;
        ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:flex/flex@localhost:1521:XE");
        conn = ds.getConnection("flex", "flex");
        return conn;
    }
    
    private String getDeskQuery(){
        return "select desks.room_id, desks.desk_number, desks.desk_dimensions\n" + 
                    ",      occupancies.card_id, occupancies.device_id\n" + 
                    ",      devices.device_name, devices.device_type, devices.employee_id\n" + 
                    ",      employees.first_name, employees.middle_name, employees.last_name, employees.expertise, employees.job_title\n" + 
                    "from   desks          \n" + 
                    ",      occupancies    \n" + 
                    ",      devices        \n" + 
                    ",      employees      \n" + 
                    "where  desks.desk_number    = occupancies.desk_number (+)\n" + 
                    "and    desks.room_id        = occupancies.room_id (+)\n" + 
                    "and    occupancies.device_id      = devices.device_id (+)\n" + 
                    "and    devices.employee_id    = employees.employee_id (+) and desks.room_id = ?";
    }
}
