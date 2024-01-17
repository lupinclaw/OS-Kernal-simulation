public class Backround extends UserlandProcess
{
    public void run()
    {
        while(true)
        {
            System.out.println("I a backround process who sleeps itself for 1000ms and loops");
            OS.Sleep(1000);
        }
    }
}
