public class Ping extends UserlandProcess
{
    public void run()
    {
        String val = "hello from ping ";
        System.out.println("I am Ping w/ pid: " + OS.GetPid() + " Pong has pid: " + OS.GetPidByName("Pong"));
        int msg_counter = 0;

        while(true)
        {
            KernelMessage msg = new KernelMessage(OS.GetPid(), OS.GetPidByName("Pong"), 1, val.getBytes());
            OS.SendMessage(msg);
            var temp = OS.WaitForMessage();
            if(temp != null)
            {
                switch (temp.getMessageType()) 
                {
                    case 1:
                        System.out.println("Ping (pid: " +  OS.GetPid() + ") received message from process: " + temp.getSenderPid() + "\n\t" + new String(temp.getData()) + " msg# " + msg_counter++);
                        break;
                
                    default:
                        break;
                }
            }
            OS.Sleep(500);
        }
    }
    
}
