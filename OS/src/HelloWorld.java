public class HelloWorld extends UserlandProcess
{
    public void run()
    {
        while(true)
        {
            System.out.println("Hello World: I am a real time process loop that sleeps itself for 500ms");
            OS.Sleep(500);
        }
    }
    
}
