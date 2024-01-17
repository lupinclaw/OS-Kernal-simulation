public class MemProcess extends UserlandProcess
{
    public void run()
    {
        byte value = (byte)'h';
        int size = 1024;
        System.out.println("I am memory test process");
        int pointer = OS.AllocateMemory(size);
        System.out.println("I allocated: " + size + " bytes of memory, returned pointer: " + pointer);

        Write(pointer, value);
        System.out.println("I Wrote: " + (char)value + " to " + pointer);
        byte val = Read(pointer);
        System.out.println("I Read: " + (char)val + " from " + pointer);
        OS.FreeMemory(pointer, 1024);
        System.out.println("I freed: " + size + " bytes of memory at: " + pointer);

        pointer = OS.AllocateMemory(size);
        System.out.println("I allocated: " + size + " bytes of memory, returned pointer: " + pointer);
        int pointer2 = OS.AllocateMemory(size);
        System.out.println("I allocated: " + size + " bytes of memory, returned pointer: " + pointer2);
        OS.FreeMemory(pointer, 1024);
        System.out.println("I freed: " + size + " bytes of memory at: " + pointer);
        OS.FreeMemory(pointer2, 1024);
        System.out.println("I freed: " + size + " bytes of memory at: " + pointer2);
    }

}
