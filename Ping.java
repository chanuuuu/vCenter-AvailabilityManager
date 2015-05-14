

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.vmware.vim25.mo.*;

public class Ping {

	
	public static boolean pingCommon(String ip)
	{
		boolean result= false;
		String cmd = "ping "+ ip;
		String consoleResult="";
		try
		{
			if(ip!=null)
			{

				boolean reachable = (java.lang.Runtime.getRuntime().exec("ping -n 1 "+ip).waitFor()==0);
				
				if(reachable == false)
				{
					System.out.println("Ping not successful");
					result=false;
					
				}
				else
				{
					System.out.println("ping successful ");
					result=true;
					
				}

			}  
			else 
			{
				System.out.println("IP is not found!");
				result = false;
			
			} 
		} 
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		return result;
	}

	

}

