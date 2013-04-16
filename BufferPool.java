
import java.util.LinkedList;

/**
 * Class BufferPool
 * @author Zach Souser
 * @author Alan Peters
 * CS 3600 Fall 2013
 */
public class BufferPool {
    
    /**
     * The elevator for the disk
     */
    
    private Elevator elevator;
    
    /**
     * The list of buffers
     */
    
    private LinkedList<Buffer> buffers;
    
    /**
     * Constructor for BufferPool
     * @param disk 
     */
    
    public BufferPool(Disk disk){
        elevator = new Elevator(disk);
        buffers = new LinkedList<Buffer>();
    }
    
    /** 
     * Read operation
     * @param blockNumber
     * @param buffer
     * @return 
     */
    
    public int read(int blockNumber, byte[] buffer){
        Buffer b = new Buffer(blockNumber, buffer);
        if(!hasInCache(b, false)){
            elevator.read(blockNumber, buffer);
            swap(buffer, b.buffer);
            add(b);
        }
        
        return 0;
    }

    /**
     * Add a new buffer to the cache
     * @param newCache 
     */
    private synchronized void add(Buffer newCache){
        if(buffers.size() > Library.getDiskCacheSize()){
            Buffer b = buffers.removeLast();
            if(b.dirty){
                elevator.write(b.blockNumber, b.buffer);
            }
        }
        buffers.addFirst(newCache);
    }
    
    /**
     * Test whether target is in the cache
     * @param target
     * @param isWrite
     * @return 
     */
    
    private synchronized boolean hasInCache(Buffer target, boolean isWrite){
        if(!buffers.contains(target)){
            return false;
        }
        if(isWrite){
            swap(target.buffer, buffers.get(buffers.indexOf(target)).buffer);
            buffers.get(buffers.indexOf(target)).dirty = true;
        }else{
            swap(buffers.get(buffers.indexOf(target)).buffer, target.buffer);
        }
        return true;
    }
   
    /**
     * Write operation
     * @param blockNumber
     * @param buffer
     * @return 
     */
    
    public int write(int blockNumber, byte[] buffer){
        Buffer b = new Buffer(blockNumber, buffer);
        if(!hasInCache(b, true)){
            swap(buffer,b.buffer);
            b.dirty = true;
            add(b);
        }
        return 0;
    }
    
    /**
     * End IO operation
     * @return 
     */
    public int endIO(){
        return elevator.endIO();
    }
    
    /**
     * Flush the buffer to disk
     */
    
    public void flush(){
        while(!buffers.isEmpty()){
            Buffer b = buffers.removeFirst();
            if(b.dirty){    
                elevator.write(b.blockNumber, b.buffer);
            }
        }
        elevator.flush();
    }
    
    /**
     * Safely swap buff1 for buff2
     * @param buff1
     * @param buff2 
     */
    
    
    private synchronized void swap(byte[] buff1, byte[] buff2) {
        System.arraycopy(buff1,0,buff2,0,buff1.length);
    }
    
    /**
     * Buffer class
     */
    
    private class Buffer{
        
        /** 
         * The block number
         */
        
        public int blockNumber;
        
        /**
         * The buffer contents
         */
        
        public byte[] buffer;
        
        /**
         * Dirty flag
         */
        
        public boolean dirty;
        
        /** 
         * The block size
         */
        
        private final int blockSize = Library.getDiskBlockSize();
        
        /**
         * Constructor for class Buffer
         * @param blockNumber
         * @param buffer 
         */
        
        public Buffer(int blockNumber, byte[] buffer){
            this.buffer = buffer;
            this.blockNumber = blockNumber;
            dirty = false;
        }
        
        /** 
         * Equals comparator
         * @param O
         * @return 
         */
        public boolean equals(Object O){
            return ((Buffer)O).blockNumber == blockNumber;
        }
    }
}
