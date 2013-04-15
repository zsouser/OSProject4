import java.util.LinkedList;
class BufferPool {
    private LinkedList<Buffer> buffers;
//    boolean[] dirty;
//    boolean[] active;
    Elevator e;
    public BufferPool(Disk d) {
        buffers = new LinkedList<Buffer>();
        e = new Elevator(d);
    }
    
    
    public int read(int blockNumber, byte[] buffer) {
        Library.output("BufferPool : Read");
        Buffer b = new Buffer(blockNumber,buffer);
        if (buffers.contains(b)) {
            Buffer target = buffers.remove(buffers.indexOf(b));
            buffers.addFirst(target);
            swap(target.buffer, buffer);
        } else {
            if(buffers.size() > Library.getDiskBufferSize()){
                Buffer bufferLast = buffers.removeLast();
                if(bufferLast.isDirty){
                    doWrite(bufferLast.blockNumber,bufferLast.buffer);
                }
            }
            doRead(blockNumber,b.buffer);
            buffers.addFirst(b);
            swap(b.buffer,buffer);
        }
        return 0;
    }
    
    
    private int doRead(int blockNumber, byte[] buffer) {
        return e.read(blockNumber,buffer);
    }
    
    public int write(int blockNumber, byte[] buffer) {
        Buffer b = new Buffer(blockNumber,buffer);
        if(buffers.contains(b)){
            Buffer target = buffers.remove(buffers.indexOf(b));
            buffers.addFirst(target);
            swap(buffer,target.buffer);
            target.isDirty = true;
        }else{
            if(buffers.size() > Library.getDiskBufferSize()){
                Buffer bufferLast = buffers.removeLast();
                if(bufferLast.isDirty){
                    doWrite(bufferLast.blockNumber, bufferLast.buffer);
                }
            }
            swap(buffer,b.buffer);
            b.isDirty = true;
            buffers.addFirst(b);
            
        }
        return 0;
    }
    
    
    private int doWrite(int blockNumber, byte[] buffer) {
        return e.write(blockNumber,buffer);
    }
    
    
    
    private synchronized void swap(byte[] buff1, byte[] buff2) {
        Library.output("Swap\n");
        System.arraycopy(buff1,0,buff2,0,buff1.length);
        Library.output("Swapped\n");
    }
    
    public int endIO() {
        return e.endIO();
    }
    
    public void flush() {
        while (!buffers.isEmpty()) {
            Buffer b = buffers.removeFirst();
            if (b.isDirty) {
                doWrite(b.blockNumber,b.buffer);
            }
        }
        e.flush();
    }
    
    private class Buffer {
        public boolean isDirty;
        public int blockNumber;
        public byte[] buffer;
        public Buffer(int blockNumber, byte[] buffer) {
            this.blockNumber = blockNumber;
            this.buffer = buffer;
            isDirty = false;
        }
        
        public boolean equals(Object o) {
            return ((Buffer)o).blockNumber == blockNumber;
        }
    }
}