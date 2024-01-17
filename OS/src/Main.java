//main, Calls Startup() with a
//new HelloWorld then CreateProcess() with a new GoodbyeWorld(). 
public class Main 
{
    public static void main(String[] args)
    {

        OS.Startup(new Ping());
        OS.CreateProcess(new Pong());

        OS.CreateProcess(new MemProcess()); //old meme test
        OS.CreateProcess(new MemTest2());


        OS.CreateProcess(new piggy()); // new memory test
        OS.CreateProcess(new piggy());
        OS.CreateProcess(new piggy());
        OS.CreateProcess(new piggy());
        OS.CreateProcess(new piggy());
        OS.CreateProcess(new piggy());
        OS.CreateProcess(new piggy());
        OS.CreateProcess(new piggy());

        OS.CreateProcess(new DeviceTest()); //write the text from source.txt to test.txt, relative paths are in DeviceTest.java change /src/ to where u have 
                                      //the source files including the source.txt files test.txt will be create if it doesnt not exist
                                      
        OS.CreateProcess(new HelloWorld()); //real time loop: sleeps, startup is always realtime
        OS.CreateProcess(new GoodbyeWorld(), OS.Priority.Interactive); //interactive
        OS.CreateProcess(new Backround(),  OS.Priority.Backround); //backround
        OS.CreateProcess(new RealTime(),  OS.Priority.RealTime); //real time once
        OS.CreateProcess(new RealTimeDemotable(), OS.Priority.RealTime); //real time demotes

    }  
}
