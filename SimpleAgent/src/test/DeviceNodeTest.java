package test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;

import data.Host;
import device.Device;
import device.DeviceNode;
import device.IDevice;

public class DeviceNodeTest extends TestCase
{
	DeviceNode deviceNode;

	@Test public void testDeviceNodeTemplate1()
	{
	    Device dev = new Device(IDevice.DEVICE_POSITION2D_CODE,"localhost",6665,0);
	    
	    deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
	    
	    Device[] dl = deviceNode.getDeviceListArray();
	    
	    assertTrue(dl.length == 2); /** Device node is also in list */
	    assertTrue(dev.isInList(dl));	    
	}
	@Test public void testDeviceNodeTemplate2()
	{
	    Device dev = new Device(IDevice.DEVICE_RANGER_CODE,"localhost",6665,-1);
        
        deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length == 3); /** Device node is also in list */ 
	}
	@Test public void testDeviceNodeTemplate2a()
    {
        Device dev = new Device(IDevice.DEVICE_RANGER_CODE,"localhost",6665,0);
        
        deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length == 2); /** Device node is also in list */ 
    }
	@Test public void testDeviceNodeTemplate2b()
    {
        Device dev = new Device(IDevice.DEVICE_RANGER_CODE,"localhost",6665,1);
        
        deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length == 2); /** Device node is also in list */ 
    }
	@Test public void testDeviceNodeTemplate3()
    {
        Device dev = new Device(IDevice.DEVICE_RANGER_CODE,"localhost",-1,-1);
        
        deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length == 3); /** Device node is also in list */ 
    }
	@Test public void testDeviceNodeTemplate4()
    {
        Device dev = new Device(IDevice.DEVICE_RANGER_CODE,null,-1,-1);
        
        deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length == 3); /** Device node is also in list */ 
    }
	@Test public void testDeviceNodeTemplate5()
    {
        Device dev = new Device(-1,null,-1,-1);
        
        deviceNode = new DeviceNode(new Host("localhost", 6665), dev);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length > 4); /** Device node is also in list */
    }
	@Test public void testDeviceNodeTemplate6()
    {
        deviceNode = new DeviceNode(new Host("localhost", 6665), (Device) null);
        
        Device[] dl = deviceNode.getDeviceListArray();
                
        assertTrue(dl.length > 4); /** Device node is also in list */
    }
	
	@Test public void testDeviceNode1Node()
	{
		deviceNode = new DeviceNode(new Host("localhost", 6665));
	}

	@Test public void testDeviceNode2Nodes()
	{
	    deviceNode = new DeviceNode (
	            new Host[] {
                    new Host("localhost",6665),
                    new Host("localhost",6666)
	            }, (Device) null);
	}

	@Test public void testDeviceNode3Nodes()
	{
	    deviceNode = new DeviceNode (
	            new Host[] {
    	            new Host("localhost",6665),
    	            new Host("localhost",6666),
    	            new Host("localhost",6667)
	            }, (Device) null);
	}

	@Test public void testDeviceNode6Nodes()
	{
	    deviceNode = new DeviceNode (
            new Host[] {
                new Host("localhost",6665),
                new Host("localhost",6666),
                new Host("localhost",6667),
                new Host("localhost",6668),
                new Host("localhost",6669),
                new Host("localhost",6670)
            }, (Device) null);
    }

	@SuppressWarnings("deprecation")
    @Test public void testDeviceNodeAddDevices()
	{
		deviceNode = new DeviceNode(new Host("localhost", 6665));
		
		DeviceNode devNode2 = new DeviceNode(new Host("localhost", 6666));
		assertNotNull(devNode2);
		
		deviceNode.addDevicesOf(devNode2);
	}	
	
	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(DeviceNodeTest.class); 
    }

    /**
	 * @throws java.lang.Exception
	 */
    @After protected void tearDown() throws Exception
    {
        System.out.println("Devices found: "+deviceNode.getDeviceListArray().length);

	    assertNotNull(deviceNode);

    	deviceNode.runThreaded();
		assertTrue(deviceNode.isThreaded());
		
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		/** Check machine load. Must not be at ~100% */

		deviceNode.shutdown();
		assertFalse(deviceNode.isThreaded());
	}
}
