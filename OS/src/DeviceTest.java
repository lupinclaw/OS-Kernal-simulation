//userland process to test devices uses open, close, read and write

public class DeviceTest extends UserlandProcess
{
    public void run()
    {
        //tests fakefilesytem device
        int fd = OS.Open("file src\\source.txt");
        System.out.println("opened file device: " + fd);
        if(fd != -1)
        {
            byte[] data = OS.Read(fd, 16);
            

            int fd_destination = OS.Open("file src\\test.txt");
            System.out.println("opened file device: " + fd_destination);
            if(fd_destination != -1)
            {
                OS.Write(fd_destination, data);
                System.out.println("wrote data from device " + fd + " to device " + fd_destination);
            }
            OS.Close(fd_destination);
        }
        OS.Close(fd);    
        System.out.println("closed all open devices ");    

        //tests random device
        int fd_random = OS.Open("random 100");
        System.out.println("opened random device: " + fd_random);
        if(fd_random != -1)
        {
            byte[] temp = OS.Read(fd_random, 30);
            System.out.println("read data from random device " + fd_random);
            OS.Write(fd_random, temp);
            System.out.println("wrote data to random device " + fd_random);
        }
        OS.Close(fd_random);
        System.out.println("closed all open devices "); 

    }
}
