//class to represents a message between processes
public class KernelMessage 
{
    private int m_Sender_pid, m_Receiver_pid, m_MessageType;
    private byte[] m_MessageData;

    public KernelMessage(int p_Sender_pid, int p_Receiver_pid, int p_MessageType, byte[] p_MessageData)
    {
        m_MessageData = p_MessageData;
        m_Sender_pid = p_Sender_pid;
        m_Receiver_pid = p_Receiver_pid;
        m_MessageType = p_MessageType;
    }
    //copy conbtructor
    public KernelMessage(KernelMessage p_Message)
    {
        this.m_MessageData = p_Message.m_MessageData;
        this.m_Sender_pid = p_Message.m_Sender_pid;
        this.m_Receiver_pid = p_Message.m_Receiver_pid;
        this.m_MessageType = p_Message.m_MessageType;
    }

    //toString method
    public String toString()
    {
        StringBuilder retString = new StringBuilder();
        retString.append("Receiver_pid: "); retString.append(m_Receiver_pid);
        retString.append("\nSender_pid: "); retString.append(m_Sender_pid);
        retString.append("\nMessageTpye: : "); retString.append(m_MessageType);
        retString.append("\nReceiver_pid: "); retString.append(m_MessageData);
        return retString.toString();
    }

    //getters and setters
    public int getSenderPid() 
    {
        return m_Sender_pid;
    }
    public int getReceiverPid() 
    {
        return m_Receiver_pid;
    }
    public int getMessageType() 
    {
        return m_MessageType;
    }
    public byte[] getData() 
    {
        return m_MessageData;
    }
    public void setSenderPid(int p_Pid) 
    {
        this.m_Sender_pid = p_Pid;
    }
    public void setReceiverPid(int p_Pid) 
    {
        this.m_Receiver_pid = p_Pid;
    }
    public void setMessageType(int p_MessageType) 
    {
        this.m_MessageType = p_MessageType;
    }
    public void setData(byte[] p_MessageData) 
    {
        this.m_MessageData = p_MessageData;
    }

}
