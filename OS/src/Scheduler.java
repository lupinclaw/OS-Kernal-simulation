import java.time.Clock;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

public class Scheduler 
{
    //prio queues
    private List<KernelLandProcess> m_RealTime_KernalProcesses = Collections.synchronizedList(new LinkedList<>());
    private List<KernelLandProcess> m_Interactive_KernalProcesses = Collections.synchronizedList(new LinkedList<>());
    private List<KernelLandProcess> m_Backround_KernalProcesses = Collections.synchronizedList(new LinkedList<>());
    private Map<String,KernelLandProcess> m_ProccessMap = Collections.synchronizedMap(new HashMap<String,KernelLandProcess>());
    private Map<Integer,KernelLandProcess> m_WaitingForMessage = Collections.synchronizedMap(new HashMap<Integer,KernelLandProcess>());
    private Map<Integer,KernelLandProcess> m_ProccessPids = Collections.synchronizedMap(new HashMap<Integer,KernelLandProcess>());

    //sleep prio queue based on min heap using sleepduration field in kernallandprocess as the prio value
    private static PriorityQueue<KernelLandProcess> m_SleepingQueue = new PriorityQueue<>
    (
        (p1, p2) -> Long.compare(p1.SleepTimeGet(), p2.SleepTimeGet()) //comparater, im finally validated for learning anon funcs in elixir
    );

    private Timer m_Timer = new Timer();
    private KernelLandProcess m_CurrentProcess = null;

    //schedule Timer interrupt for every 250ms.
    public Scheduler( )
    {
        Interrupt v_Interrupt = new Interrupt();
        m_Timer.schedule(v_Interrupt, 250, 250);
    }
    
    /*contructs a KernelandProcess, add it to 
    the list of processes and, if nothing else is running, 
    call switchProcess() to get it started. 
    It should return the PID of the new process.
    defaults to interactive 
    @overloaded*/ 
    public int CreateProcess(UserlandProcess up)
    {
        //set prio as interactive and add to interactive queue
        KernelLandProcess temp = new KernelLandProcess(up, up.getClass().getSimpleName());
        temp.PrioritySet(OS.Priority.Interactive);
        m_Interactive_KernalProcesses.add(temp);
        m_ProccessPids.put(temp.GetPid(), temp);
        m_ProccessMap.put(temp.nameGet(), temp);

        if(m_CurrentProcess == null)
        {
            SwitchProcess();
        }
        return temp.GetPid();
    }

    //create process given prio
    //@overloaded
    public int CreateProcess(UserlandProcess up, OS.Priority p_Prio)
    {
        KernelLandProcess temp = new KernelLandProcess(up, up.getClass().getSimpleName());
        temp.PrioritySet(p_Prio);
        switch (p_Prio) 
        {
            case RealTime:
                m_RealTime_KernalProcesses.add(temp);
                break;
            case Interactive:
                m_Interactive_KernalProcesses.add(temp);
                break;
            case Backround:
                m_Backround_KernalProcesses.add(temp);
                break;
            default:
                break;
        }
        m_ProccessPids.put(temp.GetPid(), temp);
        m_ProccessMap.put(temp.nameGet(), temp);
        if(m_CurrentProcess == null)
        {
            SwitchProcess();
        }
        return temp.GetPid();
    }

    /*SwitchProcess has two jobs – first, if something is running, stop()
    it, and, if the process didn’t complete (is not done), then add it to 
    the end of the proper queue. Then it needs to get the first item 
    from the list of processes, then run() the process. */
    public synchronized void SwitchProcess()
    {
        clearTLB(); //clear tlb on switch or terminiate

        if (m_CurrentProcess != null) 
        {
            var tmp = m_CurrentProcess;
            m_CurrentProcess = null;
            tmp.stop();

            if (!tmp.isDone()) 
            {
                AddtoProperQueue(tmp);
            }
            else//if process is done closes all devices and remove from hashmap of active processes
            {
                //m_ProccessPids.remove(tmp.GetPid());
                m_ProccessMap.remove(tmp.nameGet());
                for(int i = 0; i < 10; i++)
                {
                    OS.Close(i);
                }
            }
        }
        WakeUpProcesses();
        m_CurrentProcess = TakeFromRandomQueue();
        if (m_CurrentProcess != null) 
        {
            m_CurrentProcess.run();
        }
    }

    /*private class to extend timertask for passing to timer*/
    private class Interrupt extends TimerTask
    {
        public void run()
        {
            if(m_CurrentProcess != null)
            {
                HandleDemotion(m_CurrentProcess);
            }
            SwitchProcess();
        }
    }

    public void Sleep(int p_milliseconds)
    {
        long SleepDuration = (Clock.systemDefaultZone().millis()) + p_milliseconds;
        m_CurrentProcess.resetDemotion();//reset demotion counter when a process sleeps
        m_CurrentProcess.SleepTimeSet(SleepDuration); //store sleep time
        m_SleepingQueue.add(m_CurrentProcess); // Put the process on sleep queue

        var tmp = m_CurrentProcess; //from doc
        m_CurrentProcess = null;
        SwitchProcess();  // Switch to the next process
        tmp.stop();
    }
    
    // Method to check and wake up processes
    private void WakeUpProcesses() 
    {
        while (!m_SleepingQueue.isEmpty()) 
        {
            var nextProcess = m_SleepingQueue.peek();
            if (nextProcess.SleepTimeGet() <= Clock.systemDefaultZone().millis()) 
            {
                // Remove and wake up the process
                var temp = m_SleepingQueue.poll();
                //System.out.println("waking up process " + temp.pidGet());
                AddtoProperQueue(temp);
            } 
            else //once we hit a process that doesnt need to be awoken then we are done
            {
                break;
            }
        }
    }

    //given a KLP puts in proper queue.
    private void AddtoProperQueue(KernelLandProcess p_Process)
    {
        switch (p_Process.PriorityGet()) 
        {
            case RealTime:
                m_RealTime_KernalProcesses.add(p_Process);
                break;
            case Interactive:
                m_Interactive_KernalProcesses.add(p_Process);
                break;
            case Backround:
                m_Backround_KernalProcesses.add(p_Process);
                break;
            default:
                m_Interactive_KernalProcesses.add(p_Process);
                break;
        }
    }

    //takes from random prio queue based on the desired stats in doc
    private KernelLandProcess TakeFromRandomQueue()
    {
        Random rand = new Random();
        int temp;

        KernelLandProcess returnProcess = null;
        if(!m_RealTime_KernalProcesses.isEmpty())
        {
            temp = rand.nextInt(10);
            if(temp <= 5)
            {
                //realtime
                returnProcess = m_RealTime_KernalProcesses.remove(0);
            }
            else if(temp > 5 && temp <= 8 && !m_Interactive_KernalProcesses.isEmpty())
            {
                //interactive
                returnProcess = m_Interactive_KernalProcesses.remove(0);
            }
            else if (!m_Backround_KernalProcesses.isEmpty())//temp =9
            {
                //backround
                returnProcess = m_Backround_KernalProcesses.remove(0);
            }
        }
        else
        {
            temp = rand.nextInt(3); //0-3
            if(temp <= 2 && !m_Interactive_KernalProcesses.isEmpty())
            {
                //realtime
                returnProcess = m_Interactive_KernalProcesses.remove(0);
            }
            else if (!m_Backround_KernalProcesses.isEmpty())//temp = 3
            {
                //backround
                returnProcess = m_Backround_KernalProcesses.remove(0);
            }
        }
        return returnProcess;
    }

    //demotes given process
    private void HandleDemotion(KernelLandProcess p_Process)
    {

        if(p_Process.PriorityGet() == OS.Priority.Backround)
        {
            return;
        }
        if(p_Process.demotableBool())
        {
            p_Process.resetDemotion();
            System.out.print("DEMONTING PROCESS " + p_Process.GetPid() + " WAS " + p_Process.PriorityGet());
            switch (p_Process.PriorityGet()) 
            {
                case RealTime:
                    p_Process.PrioritySet(OS.Priority.Interactive);
                    System.out.println(" NOW IS " + p_Process.PriorityGet());
                    break;
                case Interactive:
                    p_Process.PrioritySet(OS.Priority.Backround);
                    System.out.println(" NOW IS " + p_Process.PriorityGet());
                    break;
                default:
                    break;
            } 
        }
        p_Process.incremnetDemotion();
    }

    //prints contents of queues, exists for testing purposes
    private void showme()
    {
        int i = 0;
        System.out.print("\nProcess in RealTime queue:");
        while(i < m_RealTime_KernalProcesses.size())
        {
            var temp = m_RealTime_KernalProcesses.get(i);
            System.out.print(" " + temp.GetPid());
            i++;
        }
        i = 0;
        System.out.print("\nProcess in Intereractive queue:");
        while(i < m_Interactive_KernalProcesses.size())
        {
            var temp = m_Interactive_KernalProcesses.get(i);
            System.out.print(" " + temp.GetPid());
            i++;
        }
        i = 0;
        System.out.print("\nProcess in Backrond queue:");
        while(i < m_Backround_KernalProcesses.size())
        {
            var temp = m_Backround_KernalProcesses.get(i);
            System.out.print(" " + temp.GetPid());
            i++;
        }
        System.out.println("\n");   
    }

    //returns device id array for a KLP
    public KernelLandProcess getCurrentlyRunning()
    {
        return m_CurrentProcess;
    }
    //handles wait for message process logic, called by WaitforMessage in kernal
    public void WaitForMessage()
    {
        m_WaitingForMessage.put(m_CurrentProcess.GetPid(), m_CurrentProcess); // Put the process on waiting for message queue
        var tmp = m_CurrentProcess; //like sleep
        m_CurrentProcess = null;
        SwitchProcess();
        tmp.stop();
    }
    //handles moving process to and fro queues based on messages
    public void MessageHandler(KernelMessage p_Message, KernelLandProcess p_targerProcess)
    {
        if(m_WaitingForMessage.containsKey(p_Message.getReceiverPid())) //if this KernelandProcess is waiting for a message (see below), restore it to its proper runnable queue (like we did with Sleep)
        {
                m_WaitingForMessage.remove(p_targerProcess.GetPid());//remove targetprocess form waiting for mesage queue
                AddtoProperQueue(p_targerProcess); //restore it to its proper runnable queue 
        }
    }
    //returns a process based on pid
    public KernelLandProcess FindProcessByPid(int p_pid)
    {
        return m_ProccessPids.get(p_pid);
    }
    //returns the pid of a process with that name. -1 if fail
    public int GetPidByName(String p_NameString)
    {
        return m_ProccessMap.get(p_NameString).GetPid();
    }
    
    //gets virt,phys pair from current klp and randomly inserts into tlb
    public void GetMapping(int virtualPageNumber)
    {
        KernelLandProcess current = m_CurrentProcess;
        VirtualToPhysicalMapping physical_page = current.getMemory(virtualPageNumber);
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
    //clear tlb
    private void clearTLB()
    {
        for (int i = 0; i < UserlandProcess.TLB.length; i++) 
        {
            UserlandProcess.TLB[i][0] = -1;
            UserlandProcess.TLB[i][1] = -1;
        }
    }

    public KernelLandProcess getRandomProcess()
    {
        
        Random random = new Random();
        int randomInt = random.nextInt(m_ProccessPids.size());
        KernelLandProcess temp = m_ProccessPids.get(randomInt); 

        boolean hasPhysicalPage = true;
        while (hasPhysicalPage) 
        {
            for(int i = 0; i < temp.mapSize(); i++)
            {
                if(temp.getMemory(i) != null)
                {
                    if (temp.getMemory(i).m_PhysicalPageNumber == -1 && temp.getMemory(i).m_DiskPageNumber != -1)
                    {
                        continue;
                    }
                    else if (temp.getMemory(i).m_PhysicalPageNumber != -1)
                    {
                        hasPhysicalPage = false;
                        break;
                    }
                }
            }
            if (hasPhysicalPage) 
            {
                randomInt = random.nextInt(m_ProccessPids.size());   
            }
        }
        return temp;
        
    }

}
