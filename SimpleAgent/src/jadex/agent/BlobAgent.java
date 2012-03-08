/**
 * 
 */
package jadex.agent;

import jadex.commons.future.Future;
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
		@Argument(name = "blob", description = "color", clazz = String.class, defaultvalue = "green")
})
@ProvidedServices(@ProvidedService(type = IReceiveNewGoalService.class, implementation = @Implementation(ReceiveNewGoalService.class)))
public class BlobAgent 
{
	@Agent
	MicroAgent agent;
	Position blobPose;
	DeviceNode dn;
	Simulation simu;

	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		blobPose = new Position((Double) agent.getArgument("X"),
				(Double) agent.getArgument("Y"), 0);

		String host = (String) agent.getArgument("host");
		Integer port = (Integer) agent.getArgument("port");

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
	public IFuture<Void> executeBody()
	{
		ReceiveNewGoalService.send(agent.getExternalAccess(),"" + agent.getComponentIdentifier(),
				"collectGoal", blobPose);
		if (simu != null)
		{
			String bName = (String) agent.getArgument("blob");
			simu.setPositionOf(bName, blobPose);
			simu.sync();
		}
		return new Future<Void>();
	}


	@AgentKilled
	public IFuture<Void> agentKilled()
	{
		return IFuture.DONE;
	}

	
	public IReceiveNewGoalService getReceiveNewGoalService()
	{
		System.out.println(agent);
		return (IReceiveNewGoalService) (agent.getServiceContainer()
				.getProvidedServices(IReceiveNewGoalService.class)[0]);
	}
}
