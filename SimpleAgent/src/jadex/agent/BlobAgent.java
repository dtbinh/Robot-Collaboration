/**
 * 
 */
package jadex.agent;

import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.service.IReceiveNewGoalService;
import jadex.service.ReceiveNewGoalService;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import data.Host;
import data.Position;
import device.Device;
import device.DeviceNode;
import device.Simulation;
import device.external.IDevice;

/**
 * @author sebastian
 * 
 */

@Agent
@Arguments(
{
		@Argument(name = "host", description = "Player", clazz = String.class, defaultvalue = "\"localhost\""),
		@Argument(name = "port", description = "Player", clazz = Integer.class, defaultvalue = "6665"),
		@Argument(name = "X", description = "Meter", clazz = Double.class, defaultvalue = "0.0"),
		@Argument(name = "Y", description = "Meter", clazz = Double.class, defaultvalue = "0.0"),
		@Argument(name = "blob", description = "color", clazz = String.class, defaultvalue = "\"green\"") })

@ProvidedServices(@ProvidedService(type = IReceiveNewGoalService.class, implementation = @Implementation(ReceiveNewGoalService.class)))

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BlobAgent extends MicroAgent
{
    /** Logging support */
    static Logger logger = Logger.getLogger (BlobAgent.class.getName ());

	Position blobPose;
	DeviceNode dn;
	Simulation simu;

	@AgentCreated
	public IFuture agentCreated()
	{
		blobPose = new Position((Double) getArgument("X"),
				(Double) getArgument("Y"), 0);

		String host = (String) getArgument("host");
		Integer port = (Integer) getArgument("port");

		/** Device list */
		CopyOnWriteArrayList<Device> devList = new CopyOnWriteArrayList<Device>();
		devList.add(new Device(IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));

		/** Host list */
		CopyOnWriteArrayList<Host> hostList = new CopyOnWriteArrayList<Host>();
		hostList.add(new Host(host, port));

		/** Get the device node */
		dn = new DeviceNode(hostList.toArray(new Host[hostList.size()]),
				devList.toArray(new Device[devList.size()]));
		dn.runThreaded();

		simu = (Simulation) dn.getDevice(new Device(
				IDevice.DEVICE_SIMULATION_CODE, null, -1, -1));
		
		return IFuture.DONE;
	}

	@AgentBody
	public IFuture executeBody()
	{
		ReceiveNewGoalService.send(getExternalAccess(),"" + getComponentIdentifier(),
				"collectGoal", blobPose);
		if (simu != null)
		{
			logger.info("Simulation device found");
//			System.out.println("Simulation device found");
			
			String bName = (String) getArgument("blob");
			simu.setPositionOf(bName, blobPose);
			simu.sync();
		} else {
			logger.warning("No simulation device found");
//			System.out.println("No simulation device found");

		}
		
		killBlobAgent();

		return IFuture.DONE;
	}

	@AgentKilled
	public IFuture<Void> agentKilled()
	{
		killBlobAgent();
		return IFuture.DONE;
	}
	
	void killBlobAgent()
	{
		dn.shutdown();
	}
	
	public ReceiveNewGoalService getReceiveNewGoalService()
	{
		return (ReceiveNewGoalService) getRawService(IReceiveNewGoalService.class);		
	}
}
