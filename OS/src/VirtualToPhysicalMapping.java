public class VirtualToPhysicalMapping 
{
    public int m_PhysicalPageNumber;
    public int m_DiskPageNumber;

    public VirtualToPhysicalMapping() 
    {
        this.m_PhysicalPageNumber = -1;
        this.m_DiskPageNumber = -1;
    }

    public void setIndex(int index, int value)
    { 
        if (index < 1024) 
        {
            m_PhysicalPageNumber = value;
        } 
        else
        {
            m_DiskPageNumber = value;
        } 

    }

}   
