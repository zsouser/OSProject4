
import java.util.PriorityQueue;
class Elevator {
    private Disk disk;
    private static int WRITE = 1;
    private static int READ = 0;
    private static int index = 0;
    private static Request currentRequest = null;
    public static boolean increasing = true,busy = false;
    private static PriorityQueue<Request> requests;
    public Elevator(Disk d) {
        disk = d;
        requests = new PriorityQueue<Request>();
    }
    
    private synchronized void await() {
        try { wait(); } catch (Exception e) { } 
    }
    
    public int read(int blockNumber,byte[] buffer) {
        Request r = new Request(READ,blockNumber,buffer);
        requests.offer(r);
        if (!busy) process();
        else await();
        return 0;
    }
    
    public int write(int blockNumber,byte[] buffer) {
        Request r = new Request(WRITE,blockNumber,buffer);
        requests.offer(r);
        if (!busy) process();
        else await();
        return 0;
    }
    
    public void process() {
        busy = true;
        Request r = requests.peek();
        Library.output(requests.size()+"PRocessing "+r+"\n");
        
        
        r.process();
        r.await();
    }
    
    public synchronized int endIO() {
        if (requests.isEmpty()) {
            busy = false;
            increasing = !increasing;
        } else {
            Request r = requests.poll();
            Library.output("Finishing "+r+"\n");
            r.finish();
        }
        notifyAll();
        return 0;
    }
    
    public void flush() {
        while (!requests.isEmpty()) process();
        disk.flush();
    }

    private class Request implements Comparable {
        private int type;
        private int blockNumber;
        private byte[] buffer;
        public Request(int type, int blockNumber, byte[] buffer) {
            this.type = type;
            this.blockNumber = blockNumber;
            this.buffer = buffer;
        }
        
        protected synchronized void await() {
            try { wait(); }
            catch (java.lang.InterruptedException e) {
                Library.output("Could not wait");
            }
        }

        protected void process() {
            Elevator.busy = true;
            if (type == READ) disk.beginRead(blockNumber,buffer);
            else if (type == WRITE) disk.beginWrite(blockNumber,buffer);
	}

        protected synchronized void finish() {
            Elevator.busy = false;
            notify();
        } 
        
        protected int type() {
            return type;
        }
        
        protected int blockNumber() { 
            return blockNumber;
        }
        
        protected byte[] buffer() {
            return buffer;
        }
        
        public int compareTo(Object o) {
            return ((Request)o).blockNumber - blockNumber;
        }
        
        public String toString() {
            return "("+type+","+blockNumber+")";
        }
    }
}