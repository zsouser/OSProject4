import java.util.LinkedList;
import java.util.Collections;
import java.util.PriorityQueue;
class Elevator {
    private Disk disk;
    private static int WRITE = 1;
    private static int READ = 0;
    private static int index = 0;
    private static Request currentRequest = null;
    public static boolean increasing = true,busy = false;
    private LinkedList<Request> requests;
    public Elevator(Disk d) {
        disk = d;
        requests = new LinkedList<Request>();
    }
    
    private synchronized void await() {
        try { wait(); } catch (Exception e) { } 
    }
    
    public int read(int blockNumber,byte[] buffer) {
        Request r = new Request(READ,blockNumber,buffer);
        if (busy) {
            requests.addLast(r);
        } else {
            busy = true;
            currentRequest = r;
            currentRequest.process();
            currentRequest.await();
        }
        return 0;
    }
    
    public int write(int blockNumber,byte[] buffer) {
        Request r = new Request(WRITE,blockNumber,buffer);
        requests.addLast(r);
        while(busy);
        currentRequest = requests.removeFirst();
        currentRequest.process();
        currentRequest.await();
        
        return 0;
    }
    
    public synchronized int endIO() {
        currentRequest.finish();
        if (requests.isEmpty()) {
            Library.output("Empty\n");
            currentRequest = null;
            busy = false;
        }
        else {
            currentRequest = requests.removeFirst();
            Library.output(currentRequest+"\n");
            currentRequest.process();
            currentRequest.await();
        }
        notifyAll();
        return 0;
        /*notifyAll();
        Library.output("Ending");
        Library.output("Closing request " + currentRequest + "\n");
        currentRequest.finish();
        requests.remove(currentRequest);
            
        
        Request pointer = requests.getFirst();
        int minDiff = pointer.compareTo(currentRequest);
        boolean negative = true;
        Library.output(""+requests.size());
        for (Request r : requests) {
            Library.output("-");
            int diff = increasing ? r.compareTo(currentRequest) :
                                        currentRequest.compareTo(r);
            Library.output(""+diff);
            negative &= diff < 0;
            if (diff > 0 && diff < minDiff) {
                minDiff = diff;
                pointer = r;
            }
        }
        
        if (negative) {
            Library.output("Negative\n\n");
            increasing = !increasing;
        }
        
        
        Library.output("Choosing request " + currentRequest + "\n");
        return 0;*/
    }
    
    public void flush() {
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

        protected synchronized void process() {
            if (type == READ) disk.beginRead(blockNumber,buffer);
            else if (type == WRITE) disk.beginWrite(blockNumber,buffer);
	}

        protected synchronized void finish() {
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
            return blockNumber;/*
            int diff = Elevator.index - ((Request)o).blockNumber();
            if (diff < 0) return Integer.MAX_VALUE;
            return diff;
            * */
        }
        
        public String toString() {
            return "("+type+","+blockNumber+")";
        }
    }
}