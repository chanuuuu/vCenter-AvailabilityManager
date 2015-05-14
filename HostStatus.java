import java.net.URL;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class HostStatus extends Thread{
	String hostname;
	
	HostStatus(String hostname)
	{
		this.hostname = hostname;
	}
	
	public ServiceInstance getServiceInstance() throws Exception
	{
		URL url = new URL("https://130.65.132.19/sdk");
		return(new ServiceInstance(url, "student@vsphere.local", "12!@qwQW", true));
	}
	public void run()
	{
		try{
			String status = this.checkHostStatus();//either Disabled or Off
			if (status.equals("Disabled")) 
			{
				System.out.println("********* NIC card disabled for Host : "+ hostname); 
				System.out.println("********* Recover the host...");
				//new DisasterRecoveryHost(hostname).start();
				System.out.println("********* DisasterRecoveryHost Thread started");
				
			}
			else if (status.equals("Off")) 
			{
				System.out.println("Powered off Host : " + hostname);										
			
				ServiceInstance si = getServiceInstance();
				Folder rootFolder = si.getRootFolder();
				ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
			
				for(int i = 0; i< mes.length; i++)
				{
					
					VirtualMachine vm = (VirtualMachine) mes[i];
					if(vm.getName().toString().contains(hostname.substring(6)))
					{
						Task task = vm.powerOnVM_Task(null);
						if (task.waitForMe() == Task.SUCCESS)
						{
							System.out.println("********* Powering on Host: " + vm.getName());
							Thread.sleep(1000 * 60 * 3);	/*TIME NEEDED FOR HOST TO BE POWERED ON*/
												
							System.out.println("********* Power on All Vm's  of the Host: " + vm.getName());
							
							URL url2 = new URL("https://130.65.132.102/sdk");
							ServiceInstance si2 = new ServiceInstance(url2, "administrator","12!@qwQW", true);
							Folder rootFolder2 = si2.getRootFolder();
							ManagedEntity h = new InventoryNavigator(rootFolder2).searchManagedEntity("HostSystem", hostname);
							{
								HostSystem hs = (HostSystem)h;
								VirtualMachine[] vms = hs.getVms();
								//System.out.println("********* Got the required VM's : " );
								for(int k = 0; k < vms.length; k++)
								{
									//System.out.println("********* VM : " + vms[k] );
									Task task1 = vms[k].powerOnVM_Task(null);//THIS IS GIVING EXCEPTION
									if (task1.waitForMe() == Task.SUCCESS)
									{
										System.out.println("********* Powering on VM: " + vms[k].getName() + " of Host: " + hs.getName());
									}
									else
										System.out.println("********* Could not power on VM: " + vms[k].getName() + " of Host: " + hs.getName());
								}
								
							}
						}
						else
							System.out.println("********* Could not power on Host: " + vm.getName());
						
					}
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("********* Couldn't check the Host Status : " + hostname);
		}
	}
	
	public String checkHostStatus() throws Exception{
		ServiceInstance si = getServiceInstance();
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		
		for(int i = 0; i< mes.length; i++)
		{
			VirtualMachine vm = (VirtualMachine) mes[i];
			if(vm.getName().toString().contains(hostname.substring(6)))
			{
				//System.out.println("checkHostStatus() : " + vm.getSummary().runtime.powerState.toString() + vm.getGuestHeartbeatStatus().toString());
				
				if ((vm.getSummary().runtime.powerState.toString().equals("poweredOn")) && ((vm.getGuestHeartbeatStatus().toString().equals("green")) || (vm.getGuestHeartbeatStatus().toString().equals("gray"))))
					return "Disabled";
				
				if ((vm.getSummary().runtime.powerState.toString().equals("poweredOff")) && ((vm.getGuestHeartbeatStatus().toString().equals("red")) || (vm.getGuestHeartbeatStatus().toString().equals("gray"))))
					return "Off";
				
			}
		}
		return null;
	}

}
