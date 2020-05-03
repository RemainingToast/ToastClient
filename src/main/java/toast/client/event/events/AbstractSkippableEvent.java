package toast.client.event.events;

import toast.client.event.IEvent;

public abstract class AbstractSkippableEvent implements IEvent {

	private boolean isSkipped;

	protected AbstractSkippableEvent() {

	}

	public boolean isCancelled() {
		return this.isSkipped;
	}

	public void setCancelled(boolean isCancelled) {
		this.isSkipped = isCancelled;
	}

}
