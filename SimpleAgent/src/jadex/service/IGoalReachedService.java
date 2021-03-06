package jadex.service;


/**
 *  Service can receive chat messages.
 */
public interface IGoalReachedService
{
	/**
	 *  Hear something.
	 *  @param name The name of the sender.
	 *  @param robotName The text message.
	 *  @param content The new goal.
	 */
	public void receive(String name, String robotName, Object content);
		
}
