
import java.util.ArrayList;

/**
 * Elevator Class
 * @author Zach Souser
 * @author Alan Peters
 * CS 3600
 */
public class Elevator {
    
    /**
     * The Disk object
     */
    
    private Disk disk;
    
    /**
     * The current and last requests
     */
    
    private Request currentRequest, lastRequest;
    
    /**
     * The queue of requests, in an ArrayList
     */
    
    private ArrayList<Request> requests;
    
    /**
     * Constructor
     * @param disk 
     */
    
    public Elevator(Disk disk){
        this.disk = disk;
        lastRequest = new Request(0, new byte[6], false);
        requests = new ArrayList();
    }
    
    /**
     * Read operation
     * @param blockNumber
     * @param buffer
     * @return 
     */
    
    public int read(int blockNumber, byte[] buffer){
        Request newRequest = new Request(blockNumber,buffer,false);
        addToList(newRequest);
        process();
        newRequest.await();
        return 0;
    }
    
    /**
     * Process the next disk IO operation
     * @return true for success, false for failure
     */
    
    private synchronized boolean process(){
        if(currentRequest != null || requests.isEmpty()){
            return false;
        }
        currentRequest = requests.remove(0);
        for(int i = 0; i < requests.size(); i++){
            if(Math.abs(requests.get(i).blockNumber - lastRequest.blockNumber) < Math.abs(currentRequest.blockNumber - lastRequest.blockNumber)){
                requests.add(currentRequest);
                currentRequest = requests.remove(i);
            }
        }
        if(currentRequest.isWrite){
            Library.output("Start Write");
            disk.beginWrite(currentRequest.blockNumber, currentRequest.buffer);
        }else {
            Library.output("Start Read");
            disk.beginRead(currentRequest.blockNumber, currentRequest.buffer);
        }
        return true;
    }
    
    /**
     * Safely add a request to the list.
     * @param r 
     */
    private synchronized void addToList(Request r){
        requests.add(r);
    }
    
    /**
     * Write operation
     * @param blockNumber
     * @param buffer
     * @return 
     */
    
    public int write(int blockNumber, byte[] buffer){
        Request newRequest = new Request(blockNumber,buffer,true);
        addToList(newRequest);
        process();
        newRequest.await();
        return 0;
    }
    
    /**
     * End the IO operation safely
     * @return 
     */
    public synchronized int endIO(){
        Library.output("End IO\n");
        currentRequest.finish();
        lastRequest = currentRequest;
        currentRequest = null;
        process();
        return 0;
    }
    
    /**
     * Flush on shutdown
     */
    
    public void flush() {
        Library.output("Flush");
        while(!requests.isEmpty()){
            process();
        }
        while(currentRequest != null);
        disk.flush();
    }
    
    /** 
     * Class Request
     */
    
    private class Request{
        
        /**
         * Block Number
         */
        
        public int blockNumber;
        
        /**
         * Buffer contents
         */
        
        public byte[] buffer;
        
        /**
         * Whether the operation is a read or a write
         */
        
        public boolean isWrite;
        
        /**
         * Constructor for Request
         * @param blockNumber
         * @param buffer
         * @param isWrite 
         */
        
        public Request(int blockNumber, byte[] buffer, boolean isWrite){
            this.blockNumber = blockNumber;
            this.buffer = buffer;
            this.isWrite = isWrite;
        }
        
        /**
         * Await the interrupt
         */
        
        public synchronized void await(){
            try{ 
                wait(); 
            }catch(Exception e){
            }
        }
        
        /**
         * Receive the interrupt
         */
        
        public synchronized void finish () {
            notify();
        }
    }
}
