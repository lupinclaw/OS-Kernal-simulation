public class Pong extends UserlandProcess
{
    public void run()
    {
        String val = "hello from Pong ";
        System.out.println("I am Pong w/ pid: " + OS.GetPid() + " Ping has pid: " + OS.GetPidByName("Ping"));
        int msg_counter = 0;

        while(true)
        {
            var temp = OS.WaitForMessage();
            if(temp != null)
            {
                switch (temp.getMessageType()) 
                {
                    case 1:
                        System.out.println("Pong (pid: " +  OS.GetPid()  +") received message from process: " + temp.getSenderPid() + "\n\t" + new String(temp.getData()) + " msg# " + msg_counter++);
                        KernelMessage msg = new KernelMessage(OS.GetPid(), OS.GetPidByName("Ping"), 1, val.getBytes());
                        OS.SendMessage(msg);
                        break;
                
                    default:
                        break;
                }
            }
            OS.Sleep(500);
        }
    }
    
}
