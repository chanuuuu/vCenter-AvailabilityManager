import java.net.URL;

import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class ManageHost {
	
	
	
	public static boolean addHost(String ip) throws Exception{
		URL url = new URL("https://130.65.132.102/sdk");
		ServiceInstance si = new ServiceInstance(url, "administrator","12!@qwQW", true);

		   Folder rootFolder = si.getRootFolder();
		   Datacenter dc;
		   
		   HostConnectSpec hSpec = new HostConnectSpec();
		   hSpec.setHostName(ip);
		   hSpec.setUserName("root");
		   hSpec.setPassword("12!@qwQW");
		   
		   hSpec.setSslThumbprint("B7:CB:D2:F4:EF:04:E1:83:45:A6:5A:D9:04:2B:AE:80:ED:51:DE:50");
		   hSpec.setForce(true);
		   
		   try {
		      dc = (Datacenter) new InventoryNavigator(rootFolder).searchManagedEntity("Datacenter", "T08-DC");
		      ComputeResourceConfigSpec compResSpec = new ComputeResourceConfigSpec();    
		          
		      Task tk = dc.getHostFolder().addStandaloneHost_Task(hSpec, compResSpec, true);
		      if(tk.waitForTask()==Task.SUCCESS){
		          return true;
		       }
		      
		      
		   } catch (Exception e) {
		         
		      e.printStackTrace();
		      
		   }return false; 
		}
	

}
