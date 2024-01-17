public class MemTest2 extends UserlandProcess
{
    public void run()
    {
        byte value = (byte)'h';
        int size = 1024;
        System.out.println("I am memory test process 2, call me Twomem");
        int pointer = OS.AllocateMemory(size);
        System.out.println("Twomem allocated: " + size + " bytes of memory, returned pointer: " + pointer);

        Write(pointer, value);
        System.out.println("Twomem Wrote: " + (char)value + " to " + pointer);
        byte val = Read(pointer);
        System.out.println("Twomem Read: " + (char)val + " from " + pointer);
        OS.FreeMemory(pointer, 1024);
        System.out.println("Twomem freed: " + size + " bytes of memory at: " + pointer);

        pointer = OS.AllocateMemory(size);
        System.out.println("Twomem allocated: " + size + " bytes of memory, returned pointer: " + pointer);
        int pointer2 = OS.AllocateMemory(size);
        System.out.println("Twomem allocated: " + size + " bytes of memory, returned pointer: " + pointer2);
        OS.FreeMemory(pointer, 1024);
        System.out.println("Twomem freed: " + size + " bytes of memory at: " + pointer);
        OS.FreeMemory(pointer2, 1024);
        System.out.println("Twomem freed: " + size + " bytes of memory at: " + pointer2);
    }

}
