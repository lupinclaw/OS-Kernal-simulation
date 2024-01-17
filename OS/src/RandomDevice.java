import java.util.Random;

public class RandomDevice implements Device
{
    private Random m_Devices[] = new Random[10];

    //Open() will create a new Random device and put it in an empty spot in the array. 
    //If the supplied string for Open is not null or empty, assume that it is the seed 
    //for the Random class (convert the string to an integer). 
    public int Open(String s)
    {
        int i = 0;
        while(m_Devices[i] != null){i++;}
        if(s != null && !s.isEmpty())
        {
            m_Devices[i] = new Random(Long.parseLong(s));
        }
        else
        {
            m_Devices[i] = new Random();
        }
        return i;
    }

    //closes device associated with id
    public void Close(int id)
    {
        m_Devices[id] = null;
    }

    //reads from device associated with id @size bytes
    //Read will create/fill an array with random values. 
    public byte[] Read(int id,int size)
    {   
        var temp = new Random();
        byte[] randomArray = new byte[size];
        temp.nextBytes(randomArray);
        return randomArray;
    }
    //seeks on device associated with id for @to bytes
    public void Seek(int id,int to)
    {
        //Seek will read random bytes but not return them.
        Read(id, to);
    }
    //writes @data to device associated with id 
    public int Write(int id, byte[] data)
    {
        return 0;
    }
    
}
