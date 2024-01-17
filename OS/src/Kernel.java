import java.util.LinkedList;
import java.util.Random;

public class Kernel implements Device
{
    private Scheduler m_Scheduler;
    private VFS m_VFS;
    public int m_PageIndex, m_swapfileFD;

    private boolean[] m_FreeSpace = new boolean[1024];

    

    public Kernel ()
    {
        //create isntance of scheduler
        m_Scheduler = new Scheduler();
        m_VFS = new VFS();

        for(int i = 0; i < 1000; i ++)
        {
            m_FreeSpace[i] = false; //is free
        }

        m_swapfileFD = m_VFS.Open("file src\\swapfile"); //swap file 
        m_PageIndex = 0;
    }

    //project 1 create process
    public int CreateProcess(UserlandProcess up)
    {
        m_VFS = new VFS();
        return m_Scheduler.CreateProcess(up);
    }

    //create process given prio
    public int CreateProcess(UserlandProcess up, OS.Priority p_prio)
    {
        return m_Scheduler.CreateProcess(up, p_prio);
    }

    public void Sleep(int p_milliseconds)
    {
        m_Scheduler.Sleep(p_milliseconds);
    }

    //opens a new file device given filename
    public int Open(String s)
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
 
        int vfsID = m_VFS.Open(s);
        if(vfsID == -1) {return -1;}

        int klpDeviceIndex = current.setDevice(vfsID);  //setDevice() in klp will return -1 if there is no spot in its device array
        if(klpDeviceIndex == -1) {return -1;}       //no spot

        return klpDeviceIndex;
    }

    //closes a device given a ulp device id
    public void Close(int id)
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        if(current != null)
        {
            int vfsID = current.getDevice(id);
            current.setDeviceClose(id); //sets item at givenindex to -1
            m_VFS.Close(vfsID);
        }
    }

    //reads @size bytes into given device id
    public byte[] Read(int id,int size)
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        int vfsID = current.getDevice(id);
        return m_VFS.Read(vfsID, size);
    }

    //seeks @to bytes in given device id
    public void Seek(int id,int to)
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        int vfsID = current.getDevice(id);
        m_VFS.Seek(vfsID, to);
    }
    //write @data to given device id
    public int Write(int id, byte[] data)
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        int vfsID = current.getDevice(id);
        return m_VFS.Write(vfsID, data);
    }
    
    //gets pid of current process
    public int GetPid()
    {
        return m_Scheduler.getCurrentlyRunning().GetPid();
    }
    //returns the pid of a process with that name. 
    public int GetPidByName(String p_NameString)
    {
        return m_Scheduler.GetPidByName(p_NameString);
    } 
    //sneds a kernal mesage to another process
    public void SendMessage(KernelMessage km)
    {
        KernelLandProcess sourceProcess = m_Scheduler.getCurrentlyRunning();
        KernelMessage tempMessage = new KernelMessage(km);//copy constructor to make a copy of the original message
        tempMessage.setSenderPid(sourceProcess.GetPid());//populate the sender’s pid
        KernelLandProcess targetProcess = null;

        targetProcess = m_Scheduler.FindProcessByPid(tempMessage.getReceiverPid());//find the target’s KernelandProcess
        
        if(targetProcess != null)//If we find our target pid, add this message to the message queue and finally,
        {
            targetProcess.addMessage(tempMessage);
            m_Scheduler.MessageHandler(tempMessage, targetProcess);
        }
    }
    //makes a process wait until they recieve a message
    public KernelMessage WaitForMessage()
    {
        KernelLandProcess currentProcess = m_Scheduler.getCurrentlyRunning();
        if (currentProcess != null) {
            LinkedList<KernelMessage> messageQueue = currentProcess.getMessageQueue();
            if (!messageQueue.isEmpty()) 
            {
                return messageQueue.removeFirst();
            } 
            else 
            {
                m_Scheduler.WaitForMessage();
            }
        }
        return null;
    }

    //gets virt,phys pair from current klp and randomly inserts into tlb
    void GetMapping(int virtualPageNumber)
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        VirtualToPhysicalMapping physical_page = current.getMemory(virtualPageNumber);

        if(current.getMemory(virtualPageNumber).m_PhysicalPageNumber == -1)
        {   
            boolean swappable = true;
            for(int i = 0; i < m_FreeSpace.length; i++)
            {
            
                if (!m_FreeSpace[i]) 
                {
                    m_FreeSpace[i] = true;
                    swappable = false;
                }
            }
            if (swappable) 
            {
                KernelLandProcess process = m_Scheduler.getRandomProcess();
                int index = process.getMemExist();
                VirtualToPhysicalMapping temp = process.getMemory(index);
                temp.m_DiskPageNumber = temp.m_PhysicalPageNumber;
                temp.m_PhysicalPageNumber = -1; 

                process.setMemory(index, null);
                byte[] tempdata = new byte[1024];

                for(int i = 0; i < 1024; i ++)
                {
                    tempdata[i] = UserlandProcess.PhysicalMemory[(temp.m_DiskPageNumber * 1024) + i];
                }
                OS.Write(m_swapfileFD, tempdata);
            }
        }

        Random random = new Random();

        if (random.nextInt(2) == 0) 
        {
            UserlandProcess.TLB[0][0] = virtualPageNumber;
            UserlandProcess.TLB[1][0] = physical_page.m_PhysicalPageNumber;
        }
        else
        {
            UserlandProcess.TLB[0][1] = virtualPageNumber;
            UserlandProcess.TLB[1][1] = physical_page.m_PhysicalPageNumber;
        }
    }


    //assigns mem
    public int AllocateMemory(int size) 
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        int pages = size / 1024;
        int offset = size % 1024;

        int ret = current.findHole(pages); //finds virtual hole for number of addresses
        if(ret == -1)
        {
            System.out.println("Seg Fault: no open spaces in this process virtual memory map");
            return -1;
        }
        for (int i = 0; i < m_FreeSpace.length; i++) //marks physical as in use for the number of 
        {
            int numberofaddys = 0;
            if (!m_FreeSpace[i] && pages != 0) 
            {
                m_FreeSpace[i] = true;
                VirtualToPhysicalMapping tmp = new VirtualToPhysicalMapping();
                tmp.m_PhysicalPageNumber = i; 
                current.setMemory(ret+numberofaddys, tmp);
                pages--;
                if(pages != 0)
                {
                    numberofaddys++;
                }
            }
        }
        return ret; //start of virtual mem space
    }
    //frees mem
    public boolean FreeMemory(int pointer, int size) 
    {
        KernelLandProcess current = m_Scheduler.getCurrentlyRunning();
        int pages = size / 1024;
        int start = pointer / 1024;

        for (int i = start; i < start + pages; i++) 
        {
            if (!m_FreeSpace[i]) 
            {
                return false;
            }
        }
        for (int i = start; i < start + pages; i++) 
        {
            m_FreeSpace[i] = false; //clears in use
            current.setMemory(i, null); //clears klp mem map
        }
        return true;
    }

    

}
