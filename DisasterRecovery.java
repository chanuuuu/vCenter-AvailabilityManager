

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

public class DisasterRecovery extends Thread {
	VirtualMachine vm = null;
	String snapshotname = "recent";

	DisasterRecovery(VirtualMachine vm) 
	{
		this.vm = vm;
	}

	public void run() 
	{ 
		try{		
			Task task = vm.revertToCurrentSnapshot_Task(null);
			if (task.waitForMe() == Task.SUCCESS) 
			{
				System.out.println("........ Reverting to snapshot... : " + vm.getSnapshot().toString() );
				Thread.sleep(5000);
				task = vm.powerOnVM_Task(null);
				if (task.waitForMe() == Task.SUCCESS)
				{
					System.out.println("........ SUCCESSFULLY reverted, powering on: " + vm.getName());
					Thread.sleep(5000);
				}
				else
					System.out.println("........ Could not power on VM: " + vm.getName());
				
				//After reverting, power on the VM , check if we need to sleep for few seconds...
			}
			else
			{
				System.out.println("........ Couldn't get the snapshot:" + snapshotname);
			}
		}//try
		catch (Exception e) 
		{
			System.out.println("........ Couldn't revert the VM:" + snapshotname);
		}
		
	}
}
		