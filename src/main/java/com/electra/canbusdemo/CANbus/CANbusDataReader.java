package com.electra.canbusdemo.CANbus;



import peak.can.basic.TPCANMsg;

import java.util.HexFormat;
import java.util.concurrent.BlockingQueue;

public class CANbusDataReader extends Thread{

    private BlockingQueue<TPCANMsg> queue;
    private CANbus_Controller controller;
    double startTime;
    boolean stopReaderThread = false;
    boolean stoppedReaderThread = true;

    public CANbusDataReader(CANbus_Controller controller, BlockingQueue<TPCANMsg> q) {
        queue = q;
        this.controller = controller;
        this.setName("CANBusDataReaderThread");
        this.start();
    }

    /**
     * If this thread was constructed using a separate
     * {@code Runnable} run object, then that
     * {@code Runnable} object's {@code run} method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of {@code Thread} should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see Thread#Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        stoppedReaderThread = false;
        while(!stopReaderThread){
            try {
                TPCANMsg data = queue.take();
                processData(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stopped!!!");
        stoppedReaderThread = true;
    }

    private void processData(TPCANMsg data) {
//        System.out.println("ID: " + data.getID() + " data: " +
//                HexFormat.of().formatHex(data.getData()).toUpperCase());
        //TODO: process received data.
        controller.newData("ID: " + Integer.toHexString(data.getID()) + " data: " +
                HexFormat.of().formatHex(data.getData()).toUpperCase());
//        if (data.getID() == CanBusMessage.CrankshaftMessage.ID){
//            controller.readCrankshaftMeasure(data.getData());
//        } else if (data.getID() == CanBusMessage.DriverMessage.ID){
//            controller.readDriverMeasure(data.getData());
//        } else if(data.getID() == CanBusMessage.ErrorMessage.ID){
//            controller.readError(data.getData());
//        }
    }
}
