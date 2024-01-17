public class RealTimeDemotable extends UserlandProcess
{
    public void run()
    {
        System.out.println("I am the evil Process: DEMOTE ME IF U CAN HAHAHAH!!!!! I'll wait (literally)");
        while(true)
        {
            try {
                System.out.println("I am the evil Process: DEMOTE ME IF U CAN HAHAHAH!!!!! I'll wait (literally)");
                Thread.sleep(5000); // sleep for 50 ms
            } catch (Exception e) { }
            
        }
    }
    
}