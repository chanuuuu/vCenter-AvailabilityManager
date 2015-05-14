
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
public class AlarmManagerClass {
	
	ServiceInstance si = null;
	String vmname = null;
	AlarmManagerClass(ServiceInstance si, String vmname) 
	{
		this.si = si;
		this.vmname = vmname;
	}

	
	public boolean getAlarmStatus() throws Exception{
		InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
        VirtualMachine vm = (VirtualMachine)inv.searchManagedEntity("VirtualMachine", vmname);
    	Alarm[] alarms = si.getAlarmManager().getAlarm(vm);
    	//AlarmState[] alarmStates = vm.getTriggeredAlarmState();
    	
    	AlarmState[] alarmStates = vm.getTriggeredAlarmState();
    	if(alarmStates == null)
    	{
    		//System.out.println("ALARM: Not Triggered Alarm for vm : " + vm.getName());
        	return false;
    	}	
    	else if(alarms[0].getAlarmInfo().name.equals("VmPowerStateAlarm" + vm.getName()) && (alarmStates[0].overallStatus.name().equals("red")))
		{
    		System.out.println("in Alarm manager ------ALARM: Triggered Alarm for vm : " + vm.getName());
    		return true;
		}
    	else
    	{
    	System.out.println("ALARM: NO Condition satisfied : " + vm.getName());
    	return false;
    	}
		}
	
	
	public boolean getHostAlarmStatus(HostSystem hs) throws Exception{
		ServiceInstance siAdminVCenter = new ServiceInstance(new URL("https://130.65.132.19/sdk"),
				"student@vsphere.local", "12!@qwQW", true);
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(siAdminVCenter.getRootFolder())
		.searchManagedEntity("VirtualMachine", hs.getName());
		
		
		
        
        System.out.println("In host alarm----");
    	Alarm[] alarms = si.getAlarmManager().getAlarm(vm);
    	//AlarmState[] alarmStates = vm.getTriggeredAlarmState();
    	
    	AlarmState[] alarmStates = vm.getTriggeredAlarmState();
    	if(alarmStates == null)
    	{
    		System.out.println("ALARM: Not Triggered Alarm for host : " + hs.getName());
        	return false;
    	}	
    	else if(alarms[0].getAlarmInfo().name.equals("HostPowerStateAlarm" + hs.getName()) && (alarmStates[0].overallStatus.name().equals("red")))
		{
    		System.out.println("in Alarm manager ------ALARM: Triggered Alarm for host : " + vm.getName());
    		return true;
		}
    	else
    	{
    	System.out.println("ALARM: NO Condition satisfied : " + vm.getName());
    	return false;
    	}
		}
	

	public void setAlarm() 
	{
		//ServiceInstance si = ConnectVCenter.getServiceInstance();
		InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
		VirtualMachine vm;
		try {
			vm = (VirtualMachine)inv.searchManagedEntity("VirtualMachine", vmname);
		
	    AlarmManager alarmMgr = si.getAlarmManager();
	    AlarmSpec spec = new AlarmSpec();
	    StateAlarmExpression expression = createStateAlarmExpression();
	    
	    spec.setExpression(expression);
	    spec.setName("VmPowerStateAlarm" + vm.getName());
	    spec.setDescription("Monitor VM state and send email " + "and power it on if VM powers off");
	    spec.setEnabled(true);    
	    
	    AlarmSetting as = new AlarmSetting();
	    as.setReportingFrequency(0); //as often as possible
	    as.setToleranceRange(0);
	    
	    spec.setSetting(as);
	    //System.out.println("ALARM: Create Alarm for vm : " + vm.getName());
	    alarmMgr.createAlarm(vm, spec);
	    return;
	    
		} catch (InvalidProperty e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static StateAlarmExpression createStateAlarmExpression()
	{
	    StateAlarmExpression expression = new StateAlarmExpression();
	    expression.setType("VirtualMachine");
		expression.setStatePath("runtime.powerState");
		expression.setOperator(StateAlarmOperator.isEqual);
		expression.setRed("poweredOff");
	    return expression;
	}
	
	public void removeAlarm(VirtualMachine vm) throws Exception 
    {
		//ServiceInstance si = ConnectVCenter.getServiceInstance();
		Alarm[] alarms = si.getAlarmManager().getAlarm(vm);
    	for(Alarm a : alarms)
    	{
    		a.removeAlarm();
    		//System.out.println("ALARM: Remove Alarm for vm : " + vm.getName());
    		return;
    	}
    }
}
