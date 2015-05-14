
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class Host {

	static HashMap<String,String> map = new HashMap<String,String>();
	
	
	public static void main(String [] args) throws Exception
	{
		
		URL url = new URL("https://130.65.132.19/sdk");
		
		ServiceInstance si = new ServiceInstance(url, "student@vsphere.local", "12!@qwQW", true);
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] mesHost = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
		System.out.println("no.of hosts" +mesHost.length);
		for (int j = 0; j < mesHost.length; j++) 
		{
			
			HostSystem hs = (HostSystem) mesHost[j];
			//System.out.println("Host " +hs.getName());
			VirtualMachine[] mes = hs.getVms();
			
			
			for (int i = 0; i < mes.length; i++) 
			{
				VirtualMachine vm = (VirtualMachine) mes[i];
				//System.out.println(vm.getName());
				if(vm.getName().contains("T02"))
				{
					Ping.pingCommon(hs.getName());
					System.out.println("inside T02 " +hs.getName());
					System.out.println("inside T02 " +vm.getName() +" " + vm.getGuest().ipAddress );
					map.put(hs.getName(),vm.getName());
				}
			}
		}
				
	}

}