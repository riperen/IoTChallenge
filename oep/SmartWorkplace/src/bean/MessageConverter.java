package bean;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import com.bea.wlevs.adapters.jms.api.MessageConverterException;
import com.bea.wlevs.adapters.jms.api.OutboundMessageConverter;

import event.RegisterOccupancyEvent;

public class MessageConverter implements OutboundMessageConverter {
	@Override
	public List<Message> convert(Session session, Object InputEvent)
			throws MessageConverterException, JMSException {
		RegisterOccupancyEvent ae = (RegisterOccupancyEvent) InputEvent;
		MapMessage mm = session.createMapMessage();

		if (null != ae) {
			mm.setString("CardID", ae.getCardId());
			mm.setString("DeviceID", ae.getDeviceId());
			if (ae.getRoomId() > 0)
				mm.setInt("RoomID", ae.getRoomId());
			if (ae.getDeskNum() > 0)
				mm.setInt("DeskNumber", ae.getDeskNum());
		}

		List<Message> jmsMessage = new ArrayList<Message>();
		jmsMessage.add(mm);

		return jmsMessage;
	}
}
