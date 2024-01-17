public class GoodbyeWorld extends UserlandProcess
{
    public void run()
    {
        while(true)
        {
            System.out.println("Goodbye World: I am a Interactive process that sleeps itself for 100ms and loops");
            OS.Sleep(100);
        }
    }
}
