class BufferPool {
    byte[][] buffers;
    boolean[] dirty;
    boolean[] active;
    Elevator e;
    public BufferPool(Disk d) {
        buffers = new byte[Library.getDiskBlockCount()][Library.getDiskBlockSize()];
        dirty = new boolean[buffers.length];
        active = new boolean[buffers.length];
        e = new Elevator(d);
    }
    
    public int read(int blockNumber, byte[] buffer) {
        Library.output("BufferPool : Read");
        if (active[blockNumber]) {
            Library.output(" active block "+blockNumber+"\n");
            swap(buffers[blockNumber],buffer);
        }
        else {
            Library.output(" inactive block "+blockNumber+"\n");
            markActive(blockNumber);
            return doRead(blockNumber,buffer);
        }
        return 0;
    }
    
    private synchronized int doRead(int blockNumber, byte[] buffer) {
        int result = e.read(blockNumber,buffer);
        swap(buffer,buffers[blockNumber]);
        return result;
    }
    
    public int write(int blockNumber, byte[] buffer) {
        swap(buffer,buffers[blockNumber]);
        markDirty(blockNumber);
        return 0;
    }
    
    private synchronized int doWrite(int blockNumber, byte[] buffer) {
        return e.write(blockNumber,buffer);
    }
    
    private synchronized void markDirty(int blockNumber) {
        dirty[blockNumber] = true;
    }
    
    private synchronized void markClean(int blockNumber) {
        dirty[blockNumber] = false;
    }
    
    private synchronized void markActive(int blockNumber) {
        active[blockNumber] = true;
    }
    
    private synchronized void swap(byte[] buff1, byte[] buff2) {
        Library.output("Swap\n");
        System.arraycopy(buff1,0,buff2,0,buff1.length);
    }
    
    public int endIO() {
        return e.endIO();
    }
    
    public void flush() {
        for (int i = 0; i < buffers.length; i++) {
            if (dirty[i]) {
                doWrite(i,buffers[i]);
                markClean(i);
            }
        }
        e.flush();
    }
}