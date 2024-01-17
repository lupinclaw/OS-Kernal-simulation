import javax.swing.text.html.StyleSheet;

public abstract class UserlandProcess implements Runnable
{
    //mem structures
    static byte[] PhysicalMemory = new byte[1024 * 1024];
    static int[][] TLB =  new int[2][2];

    //reads from mem addy
    byte Read(int address)
    {
        int virtual_page = address/1024;
        int page_offset = address%1024;
        int physical_page = -1;

        if (TLB[0][0] == virtual_page) 
        {
            physical_page = TLB[1][0];
        } 
        else if(TLB[0][1] == virtual_page) 
        {
            physical_page = TLB[1][1];
        }

        if(physical_page != -1)
        {
            int physical_addy = physical_page * 1024 + page_offset;
            return PhysicalMemory[physical_addy];
        }
        else
        {
            OS.GetMapping(virtual_page);
            Read(address);
        }

        return -1;
    }
    //writes to mem addy
    void Write(int address, byte value)
    {
        int virtual_page = address/1024;
        int page_offset = address%1024;
        int physical_page = -1;

        if (TLB[0][0] == virtual_page) 
        {
            physical_page = TLB[1][0];
            int physical_addy = physical_page * 1024 + page_offset;
            PhysicalMemory[physical_addy] = value;
        } 
        else if (TLB[0][1] == virtual_page) 
        {
            physical_page = TLB[1][1];
            int physical_addy = physical_page * 1024 + page_offset;
            PhysicalMemory[physical_addy] = value;
        }
        else if (physical_page == -1)
        {
            OS.GetMapping(virtual_page);
            Write(address, value);
        }
    }

    //debugging prints tlb
    public void ptlb()
    {
        System.out.println(TLB[0][0] + " " + TLB[1][0]);
        System.out.println(TLB[0][1] + " " + TLB[1][1]);
    } 
}
