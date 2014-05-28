/* (c) 2014 AMIS.  All rights reserved. */
package adapter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.bea.wlevs.ede.api.Adapter;
import com.bea.wlevs.ede.api.EventRejectedException;
import com.bea.wlevs.ede.api.StreamSink;

import event.RegisterOccupancyEvent;
import event.UnregisterOccupancyEvent;

public class DatabaseAdapter implements Adapter, StreamSink {
	private DataSource dataSource;

	@Resource(name = "SmartwpDS")
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void onInsertEvent(Object event) throws EventRejectedException {
		System.out.println("DatabaseAdapter: " + event);
		if ((event instanceof RegisterOccupancyEvent)) {
			RegisterOccupancyEvent registerE = (RegisterOccupancyEvent) event;
			insertOccupancy(registerE);
		} 
		else if ((event instanceof UnregisterOccupancyEvent)) {
			UnregisterOccupancyEvent unregisterE = (UnregisterOccupancyEvent) event;
			deleteOccupancy(unregisterE);
		} 
		else {
			return;
		}
	}
	
	private void insertOccupancy(RegisterOccupancyEvent event) {
		Connection conn;
		Statement stmt;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			String sql = "INSERT INTO occupancies " +
						 "VALUES ("+event.getCardId()+", '"+event.getDeviceId()+"'" +
						 "       ,"+event.getRoomId()+", "+event.getDeskNum()+")" +
						 "WHERE NOT EXISTS (" +
						 "  SELECT * FROM occupancies " +
						 "  WHERE card_id = "+event.getCardId()+")";
			
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteOccupancy(UnregisterOccupancyEvent event) {
		Connection conn;
		Statement stmt;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			String sql = "DELETE FROM occupancies " +
						 "WHERE card_id = "+event.getCardId()+"" +
						 "AND device_id = '"+event.getDeviceId()+"'";
			
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}