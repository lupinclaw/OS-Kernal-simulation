public class piggy extends UserlandProcess
{
    public void run()
    {
        System.out.println("I am Piggy I eat memory");
        int Fd = OS.AllocateMemory(100 * 1024);
        System.out.println("I have eaten " + 100 * 1024 + " memeory");
    }
}
