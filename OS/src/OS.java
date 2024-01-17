//everything mus be static, as no instance will be created
public class OS
{   
    //single instance of kernal made by OS, cant be accesed by userland
    private static Kernel m_Kernal;
    //prio enum in order left to right
    public enum Priority {RealTime, Interactive, Backround}

    public static void Startup(UserlandProcess init)
    {
        //populate the Kernel member with a new instance and call CreateProcess on “init”
        m_Kernal = new Kernel();
        m_Kernal.CreateProcess(init, OS.Priority.RealTime);
    } 
    
    //create process "system call" given prio
    public static int CreateProcess(UserlandProcess up, Priority p_prio)
    {
        return m_Kernal.CreateProcess(up, p_prio);
    }

    //project 1 create process
    public static int CreateProcess(UserlandProcess up)
    {
        return m_Kernal.CreateProcess(up);
    }
    
    public static void Sleep(int p_milliseconds)
    {
        m_Kernal.Sleep(p_milliseconds);
    }
    //passes thru to kernal level device commend
    public static int Open(String s)
    {
        return m_Kernal.Open(s);
    }
    //passes thru to kernal level device commend
    public static void Close(int id)
    {
        m_Kernal.Close(id);
    }
    //passes thru to kernal level device commend
    public static byte[] Read(int id,int size)
    {
        return m_Kernal.Read(id, size);
    }
    //passes thru to kernal level device commend
    public static void Seek(int id,int to)
    {
        m_Kernal.Seek(id, to);
    }
    //passes thru to kernal level device commend
    public static int Write(int id, byte[] data)
    {
        return m_Kernal.Write(id, data);
    }
    //gets pid of current process
    public static int GetPid()
    {
        return m_Kernal.GetPid();
    }
    //returns the pid of a process with that name. 
    public static int GetPidByName(String p_NameString)
    {
        return m_Kernal.GetPidByName(p_NameString);
    } 
    //sneds a kernal mesage to another process
    public static void SendMessage(KernelMessage km)
    {
         m_Kernal.SendMessage(km);
    }
    //makes a process wait until they recieve a message
    public static KernelMessage WaitForMessage()
    {
        return m_Kernal.WaitForMessage();
    }
    //gets virt,phys pair from current klp and randomly inserts into tlb
    public static void GetMapping(int virtualPageNumber)
    {
        m_Kernal.GetMapping(virtualPageNumber);
    }
    //assigns mem
    public static int AllocateMemory(int size) 
    {
        if (size % 1024 != 0) 
        {
            return -1;
        }
        return m_Kernal.AllocateMemory(size);
    }
    //frees mem
    public static boolean FreeMemory(int pointer, int size) 
    {
        if (size % 1024 != 0 || pointer % 1024 != 0) 
        {
            return false;
        }
        return m_Kernal.FreeMemory(pointer, size);
    }
}
