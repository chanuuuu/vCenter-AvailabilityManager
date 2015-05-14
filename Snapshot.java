

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

public class Snapshot extends Thread {
	ServiceInstance si, si2 = null;
	int duration;

	Snapshot(ServiceInstance si, ServiceInstance si2) 
	{
		this.si = si;
		this.si2 = si2;
	}
	//new constructor
	Snapshot(ServiceInstance si, ServiceInstance si2, int time) 
	{
		this.si = si;
		this.si2 = si2;
		this.duration = time;
	}
	
	public void run() {
		while (true) 
		{
			try 
			{
				createVMSnapshot();
				createHostSnapshot();
				
				System.out.println("++++++++ Sleeping Snapshot Thread for " + duration +" minutes");
				System.out.println();
				Thread.currentThread().sleep(1000 * 60 * duration);// sleep for 60 * X seconds
				
			}//try

			catch (Exception e) 
			{
				System.out.println("++++++++ Exception while getting Snapshot, Service instance interupted : " + e.getMessage());
				System.out.println(e.getStackTrace());
			}
			/*
			//I can move this try catch and include it in the above catch 
			try
			{
				System.out.println("++++++++ Sleeping Snapshot Thread for 15 minutes");
				Thread.currentThread().sleep(1000 * 60 * 15);// sleep for 60 * X seconds
			} 
			catch (Exception e) 
			{
				System.out.println("++++++++ Snapshot thread interrupted : "+ e.getMessage());
				System.out.println(e.getStackTrace());
			}*/

		}
	}
	
	public void createVMSnapshot()
	{
		try
		{
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		String snapshotname;
		String desc ;
		for (int i = 0; i < mes.length; i++) 
		{
			VirtualMachine vm = (VirtualMachine) mes[i];
			
			//Only Powered On VMs, IP address not null and OS status either Green or Gray
			
			if (vm.getSummary().runtime.powerState.toString().equals("poweredOn") && (vm.getGuest().getIpAddress() != null) && ((vm.getGuestHeartbeatStatus().toString().equals("green")) || (vm.getGuestHeartbeatStatus().toString().equals("gray"))))
			{
				//remove snapshot
				Task task1 = vm.removeAllSnapshots_Task();
				if (task1.waitForMe() == Task.SUCCESS)
					System.out.println("Removed all snapshots for " + vm.getName());
				else
					System.out.println("++++++++ No Snapshots available for VM : "+ vm.getName());

				
				//create new snapshot
				snapshotname = vm.getName() + "_Snapshot";
				desc = "This is a snapshot for " + vm.getName();
				Task task = vm.createSnapshot_Task(snapshotname, desc,false, false);
				if (task.waitForMe() == Task.SUCCESS)
					System.out.println("++++++++ Snapshot created for VM : "+ vm.getName());
				else
					System.out.println("++++++++ Snapshot creation interrupted for VM :"+ vm.getName());
			}//if block
			else
				System.out.println("In snapshot thread " +vm.getName() + " is in powered off state.");
		}//for loop
		}catch(Exception e)
		{
			System.out.println("Exception" +e);
		}
	}
	
	public void createHostSnapshot()
	{
		//Host Snapshot
		try
		{
		String snapshotname="";
		String desc="";
		Folder rootFolder2 = si2.getRootFolder();
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] mesHost = new InventoryNavigator(rootFolder2).searchManagedEntities("VirtualMachine");
		//Get number of Hosts
		ManagedEntity[] mesHost115 = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
		System.out.println("mesHost leng : " +mesHost.length);
		for (int j = 0; j < mesHost115.length; j++)
		{
			HostSystem hs = (HostSystem) mesHost115[j];
			String hostip = hs.getName();
			
			for(int i = 0; i< mesHost.length; i++)
			{		
				VirtualMachine vm = (VirtualMachine) mesHost[i];
				
				if(vm.getName().toString().contains(hs.getName().substring(7)) && vm.getSummary().runtime.powerState.toString().equals("poweredOn") && Ping.pingCommon(hostip))//check ip
				{
					System.out.println("===============in host snapshot=============host is powered on");
					//remove snapshot
					Task task = vm.removeAllSnapshots_Task();
					if (task.waitForMe() == Task.SUCCESS)
						;//System.out.println("");
					else
						System.out.println("++++++++ No Snapshots available for Host : "+ vm.getName());
					snapshotname = hs.getName()+"_Host_Snapshot";
					desc = "This is a snapshot for " + hs.getName();
					//create new snapshot
					task = vm.createSnapshot_Task(snapshotname, desc,false, false);
					if (task.waitForMe() == Task.SUCCESS)
						System.out.println("++++++++ Snapshot created for Host : "+ vm.getName());
					else
						System.out.println("++++++++ Snapshot creation interrupted for Host : "+ vm.getName());
				}
			}//for
		}//for
	}catch(Exception e)
	{
		System.out.println("Exception"+e);
	}
	}
}

