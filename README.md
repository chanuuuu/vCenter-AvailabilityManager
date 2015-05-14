# vCenter-AvailabilityManager
An availability manager that monitors the liveness of the virtual machines (VM) running on any one of the hosts and restarts any virtual machines that fail on alternate, healthy hosts, using an earlier cached version of the VM. (In practice, such caching is done to enable recovering from complete site failures.)

For the purpose of this project, a VM can be considered to be live if it responds to pings, and dead if it stops responding for a configurable amount of time (say 1 minute ping heartbeat) .

When the code detects that a VM has failed, it shall try to check if the vHost is active. If the vHost has failed, it will check if an alternative vHost is available. If a second live vHost is found, vHost for the VM is selected and VM will be restarted on that vHost using a VM image format that is suitable for that vHost. In case if no other vHost is found, another vHost will be added to the vCenter, and VM will be restarted on that vHost. The image format conversions are done ahead of time and stored for later use.

Project provide a mechanism for refreshing this image cache from the current running instance of the virtual machine, for example every 10 minute update. The conversion and refreshing are automated.

When selecting a candidate host, availability manager takes into account whether the host itself is live. (i.e., responds to pings).

This project also make use of unique capabilities of each of the hypervisors/management servers.

This project provides a mechanism for adding or removing a host from the set of hosts in the resource pool (collection of hosts (say 2 vHosts) being monitored), and for configuring the VMs that are to be monitored by it.

Project has following features:

1. Refreshes the backup cache update every 10 minute.
2. When a VM fails with ping heartbeat, then failover the VM to another vHost/resource pool using VMDK image format (Cold migration)
3. If the vHost is not alive, an attempt is made to make it alive. If even after a fixed number of attempts, the vHost does not come up then a vHost will be added to the vCenter if there exists only one vHost and that vHost is not alive.
4. Register VM’s to the new vHost.
5. Sets up an alarm on VM power off. If a VM is powered off by a user, then it will be able to prevent a failover from occurring. (A VM is not failed in this case by powered off by a user)
