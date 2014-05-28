package bean;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class LimitedQueue<E> extends LinkedList<E> {
	private final int limit;
	
	public LimitedQueue(int limit) {
		super();
		this.limit=limit;
	}
	
	
	@Override
	public boolean add(E o) {
		super.add(o);
		while (size() > limit) {super.remove();}
		return true;
	}

}
