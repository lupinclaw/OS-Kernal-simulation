//vertual file system for OS
public class VFS implements Device
{
    //paralelle arrays
    Device m_Devices[] = new Device[10];
    int m_ID[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

    FakeFileSystem m_FileSystem = new FakeFileSystem();
    RandomDevice m_RandomDevice = new RandomDevice();

    // given @s, create a device based on string input 
    public int Open(String s)
    {
        String[] temp = s.split(" ");
        for(int i = 0; i < m_ID.length && i < m_Devices.length; i++)
        {
            if(m_ID[i] == -1 && m_Devices[i] == null)
            {
                if(temp[0].equals("file"))
                {
                    m_Devices[i] = m_FileSystem;
                    m_ID[i] = m_Devices[i].Open(temp[1]);
                    return i;
                }
                else if(temp[0].equals("random"))
                {
                    m_Devices[i] = m_RandomDevice;
                    m_ID[i] = m_Devices[i].Open(temp[1]);
                    return i;
                }
            }
        }
        //no spot
        return -1;

    }
    //closes device associated with id
    public void Close(int id)
    {
        m_Devices[id] = null;
        m_ID[id] = -1;
    }
    //reads from device associated with id @size bytes
    public byte[] Read(int id,int size)
    {
        return m_Devices[id].Read(m_ID[id], size);
    }
    //seeks on device associated with id for @to bytes
    public void Seek(int id,int to)
    {
        m_Devices[id].Seek(m_ID[id], to);
    }
    //writes @data to device associated with id 
    public int Write(int id, byte[] data)
    { 
        return m_Devices[id].Write(m_ID[id], data);
    }
    
}
