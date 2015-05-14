

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.InventoryNavigator;

public class RecoverHost {
	
               

public static VirtualMachine getvHostFromAdminVCenter(String vHostName)
            throws InvalidProperty, RuntimeFault, RemoteException, MalformedURLException {

	URL url = new URL("https://130.65.132.19/sdk");
	ServiceInstance si = new ServiceInstance(url, "student@vsphere.local",
			"12!@qwQW", true);
	
	
        
        Folder rootAdmin = si.getRootFolder();
        ComputeResource computeResource = null;
        ManagedEntity[] mesAdmin = new InventoryNavigator(rootAdmin).searchManagedEntities("ComputeResource");
        for(int j=0;j<mesAdmin.length;j++){
        if(mesAdmin[j].getName().equals("130.65.132.61")){
             computeResource = (ComputeResource) mesAdmin[j];
        }
        }
       
        //System.out.println(computeResource.getName());
        ResourcePool rp = computeResource.getResourcePool();
        for(int index=0;index<rp.getResourcePools().length;index++){
            if(rp.getResourcePools()[index].getName().equals("Team02_vHost")){
                ResourcePool myResource = rp.getResourcePools()[index];
                //System.out.println(myResource.getVMs()[2].getName());
                for(int i=0;i<myResource.getVMs().length;i++){
                    if(myResource.getVMs()[i].getName().contains(vHostName)){
                        //System.out.println("GetvHostFromAdminVCenter :vm found");
                        return myResource.getVMs()[i];
                    }
                       
                //System.out.println("GetvHostFromAdminVCenter" +myResource.getVMs()[i].getName());
                }
            }
        }
       
        return null;
    }


public static boolean reconnectHostandRecoverVM(VirtualMachine vm,HostSystem hs) throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException, MalformedURLException{
    
    VirtualMachine vmFromAdmin =getvHostFromAdminVCenter(hs.getName().substring(11, hs.getName().length()));   
   
    System.out.println("Reconnection Host and Recover: Trying to power ON the vHost " +hs.getName());
    if(vmFromAdmin.getSummary().runtime.powerState ==  vmFromAdmin.getSummary().runtime.powerState.poweredOff){
        Task task = vmFromAdmin.powerOnVM_Task(null);
   
        while (task.getTaskInfo().state == task.getTaskInfo().state.running) {
        System.out.print(". ");
        }
    }
   
    if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn){
    System.out.println("Reconnection Host and Recover:vHost "+hs.getName()+"is powered on now..");
    }
   
    System.out.println("Reconnection Host and Recover:waiting for vHost "+hs.getName()+" to be available");
   
    //while(!pingVirtualMachine("130.65.132.162"));
   
    Thread.sleep(1000 *60 *1);
   
    System.out.println("Reconnection Host and REcover:Trying to reconnect vHost...");
   
    for(int i=0;i<15;i++){
        System.out.println(" ");
        System.out.println("Reconnection Host and Recover: Attempt - "+i);
       
        Task taskvHost = hs.reconnectHost_Task(null);

   
        while (taskvHost.getTaskInfo().state == taskvHost.getTaskInfo().state.running) {
        System.out.print(".");
        }
   
   
        if(taskvHost.getTaskInfo().state == taskvHost.getTaskInfo().state.success){
            if(hs.getSummary().runtime.powerState == hs.getSummary().runtime.powerState.poweredOn){
                System.out.println("Reconnection Host and Recover: vHost "+hs.getName()+" is connected now..");
//                if(!AlarmHandler.checkAlarm(vm.getName()))
//                    recoverVMtoSameHost(vm,hs);
            }

            break;   
        }
       
    }
       
    return false;
}


}
