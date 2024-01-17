//manages kernal processes

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KernelLandProcess 
{
    //members denoted with m_ 
    private static int m_Nextpid = 0;
    private int m_pid;
    private boolean m_ThreadStarted;
    private Thread m_Thread;
    private OS.Priority m_CurrentPrio;
    private long m_TimeToSleep;
    private int demotable = 0;
    private int[] m_deviceIDs = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    private String m_Name;
    private LinkedList<KernelMessage> m_Messages = new LinkedList<>();

    //mem
    private VirtualToPhysicalMapping[] MemoryMap = new VirtualToPhysicalMapping[100];

    KernelLandProcess(UserlandProcess up, String nameString) 
    /* creates thread, sets pid */
    {
        m_Thread = new Thread(up);
        m_pid = m_Nextpid;
        m_Nextpid++;
        m_ThreadStarted = false;
        m_Name = nameString;

    }

    void stop() 
    /* if the thread has started, suspend it */
    {
        if(m_ThreadStarted)
        {
            m_Thread.suspend();
        }
    }
    
    boolean isDone() 
    /* true if the thread started and not isAlive() */
    {
        if(m_ThreadStarted && !(m_Thread.isAlive()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    void run() 
    /* resume() or start() and update “started” */
    {
        if(m_ThreadStarted)
        {
            m_Thread.resume();
        }
        else
        {
            m_ThreadStarted = true;
            m_Thread.start();
        }
        
    }

    //gets open deivce id array
    public int getDevice(int index)
    {
        return this.m_deviceIDs[index];
    }
    //set device index to -1 i.e. close
    public void setDeviceClose(int index)
    {
        m_deviceIDs[index] = -1;
    }
    //set device
    public int setDevice(int p_Value)
    {
        for(int i = 0; i < 10; i++)
        {
            if(m_deviceIDs[i] == -1)
            {
                m_deviceIDs[i] = p_Value;
                return i;
            }
        }
        //no space
        return -1;
    }
     
    //prio setter
    public void PrioritySet(OS.Priority p_Priority)
    {
        this.m_CurrentPrio = p_Priority;
    }
    //prio getter
    public OS.Priority PriorityGet()
    {
        return this.m_CurrentPrio;
    }

    //sleep time setter
    public void SleepTimeSet(long p_SleepTime)
    {
        this.m_TimeToSleep = p_SleepTime;
    }
    //sleep time getter
    public long SleepTimeGet()
    {
        return this.m_TimeToSleep;
    }

    //pid setter
    public void pidSet(int p_Pid)
    {
        this.m_pid = p_Pid;
    }
    //pid getter
    public int GetPid()
    {
        return this.m_pid;
    }
    //name setter
    public void nameSet(String p_name)
    {
        this.m_Name = p_name;
    }
    //name getter
    public String nameGet()
    {
        return this.m_Name;
    }
    //resets demotion
    public void resetDemotion()
    {
        this.demotable = 0;
    }
    //++demotion
    public void incremnetDemotion()
    {
        this.demotable++;
    }
    //demotable
    public boolean demotableBool()
    {
        if(this.demotable > 4)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //messaage queue getter
    public LinkedList<KernelMessage> getMessageQueue()
    {
        return this.m_Messages;
    }

    //adds message to message queue
    public void addMessage(KernelMessage p_Message)
    {
        this.m_Messages.add(p_Message);
    }

    //gets mem addy
    public VirtualToPhysicalMapping getMemory(int index)
    {
        return MemoryMap[index];
    }
    //sets index to value in mem
    public void setMemory(int index, VirtualToPhysicalMapping value)
    {
        MemoryMap[index] = value;
    }
    public int setIfEmpty(int physical_addy)
    {
        for(int i = 0; i < MemoryMap.length; i++)
        {
            if(MemoryMap[i].m_PhysicalPageNumber == -1)
            {
                MemoryMap[i].m_PhysicalPageNumber = physical_addy;
                return i;
            }
        }

        return -1;
    }
    public int findHole (int size)
    {
        boolean hole = true;
        for(int i = 0; i < MemoryMap.length; i++)
        {
            if(MemoryMap[i] == null)
            {
                for(int j = 0; j < size; j++)
                {
                    if(MemoryMap[j+i] != null) 
                    {
                        hole = false;  
                    }
                }
                if (hole) 
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public int mapSize()
    {
        return this.MemoryMap.length;
    }

    public int getMemExist()
    {
        for(int i = 0; i < this.MemoryMap.length; i ++)
        {
            if (this.MemoryMap[i] != null) 
            {
                return i;
            }
        }
         return -1;
    }

}
