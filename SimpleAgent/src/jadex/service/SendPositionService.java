package jadex.service;

import jadex.bridge.IExternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.SServiceProvider;
import jadex.micro.IMicroExternalAccess;
import jadex.commons.service.IService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This service is should be used regularly to send the agents position.
 * It is received (or not) by some control agent to update its internal
 * knowledge of all participating agents.
 * Normally an agent needs not to receive any message of this service.
 * @author sebastian
 *
 */
public class SendPositionService extends BasicService implements IService {

//-------- attributes --------
	
	/** The agent. */
	protected IMicroExternalAccess agent;
	
	/** The listeners. */
	@SuppressWarnings("rawtypes")
	protected List listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new helpline service.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SendPositionService(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IService.class, null);
		this.agent = (IMicroExternalAccess)agent;
		this.listeners = Collections.synchronizedList(new ArrayList());
	}
	
	//-------- methods --------	
	/**
	 *  Tell something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public void send(final String name, final Object obj)
	{
		SServiceProvider.getServices(agent.getServiceProvider(), SendPositionService.class, true, true)
			.addResultListener(new DefaultResultListener()
		{
			@SuppressWarnings("rawtypes")
			public void resultAvailable(Object source, Object result)
			{
				if(result!=null)
				{
					for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
					{
						SendPositionService ts = (SendPositionService)it.next();
						ts.receive(name, obj);
					}
				}
			}
		});
	}
	
	/**
	 *  Hear something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	@SuppressWarnings("unchecked")
	public void receive(String name, Object obj)
	{
		IChangeListener[] lis = (IChangeListener[])listeners.toArray(new IChangeListener[0]);
		for(int i=0; i<lis.length; i++)
		{
			lis[i].changeOccurred(new ChangeEvent(this, null, new Object[]{name, obj}));
		}
	}
	
	/**
	 *  Add a change listener.
	 */
	@SuppressWarnings("unchecked")
	public void addChangeListener(IChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "TestService, "+agent.getComponentIdentifier();
	}
}
