import java.net.URL;

import com.vmware.vim25.mo.ServiceInstance;

public class AvailabilityManager {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		int pingTime = 1, snapshotTime = 10;

//			
//		}
		
		String vmname = null, cloneName = null, newHostName = null;
		URL url = new URL("https://130.65.132.102/sdk");
		ServiceInstance si = new ServiceInstance(url, "administrator",
				"12!@qwQW", true);
		url = new URL("https://130.65.132.19/sdk");
		ServiceInstance si2 = new ServiceInstance(url, "student@vsphere.local",
				"12!@qwQW", true);
		
		System.out.println("Starting Ping thread...");
		new PingThread(si,pingTime).start();
		
		//new Ping(si).start();
		
		System.out.println("Starting Snapshot thread...");
		new Snapshot(si, si2,snapshotTime).start();
		
		
	}

}

