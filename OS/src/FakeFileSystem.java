//mimics a file system called thru VFS

import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device
{
    private RandomAccessFile[] fileArray = new RandomAccessFile[10];
    //given a filename @s create a new randomaccessfile device
    public int Open(String s)
    {
        if(s.isEmpty() || s == null)
        {
            throw new IllegalArgumentException("Invalid Filename");
        }
        
        for(int i = 0; i < fileArray.length; i++)
        {
            try 
            {
                if(fileArray[i] == null)
                {
                    fileArray[i] = new RandomAccessFile(s, "rw");
                    return i;
                }
            }
            catch (IOException e) 
            {
                System.out.println("Failed to make random access file");
                return -1;
            }
        } 

        
        //if no spot in array
        return -1;
        
    }
    //closes device associated with id
    public void Close(int id)
    {
        try 
        {
            fileArray[id].close();
        } 
        catch (IOException e) {}
    }
    //reads from device associated with id @size bytes
    public byte[] Read(int id,int size)
    {
        byte[] temp = new byte[size];

        try 
        {
            fileArray[id].read(temp);
        } 
        catch (IOException e) {}

        return temp;
    }
    //seeks on device associated with id for @to bytes
    public void Seek(int id,int to)
    {
        try
        {
            fileArray[id].seek((long)to);
        }
        catch (IOException e) {}
    }
    //writes @data to device associated with id 
    public int Write(int id, byte[] data)
    {
        try 
        {
            fileArray[id].write(data);
        } 
        catch (IOException e) {return -1;}
        return 0;
    }
    
}
