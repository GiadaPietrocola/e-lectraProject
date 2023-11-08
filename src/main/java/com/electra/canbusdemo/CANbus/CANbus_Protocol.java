package com.electra.canbusdemo.CANbus;

import peak.can.basic.TPCANMsg;

import java.util.HexFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This Protocol is an abstraction layer that allows to interaction with any
 * of the supported device via the given hardware peripheral.
 * Adapt the general format of an instruction into a suitable send and receive\
 * byte commands available on the Driver services.
 * <p>
 * This file contains the implementation of the CANbus Protocol.
 * <p>
 * Property: E-Lectra s.r.l.
 */
public class CANbus_Protocol extends Protocol {

    private boolean stopReceiverThread = false;
    private boolean stoppedReceiverThread = true;

    private Thread receiverThread;
    private BlockingQueue<TPCANMsg> queue;

    public CANbus_Protocol(){
        super();
        queue = new LinkedBlockingQueue();
        driver = new CANbus_Driver(queue);
//        driver.connect();
//        startReceiverThread();
    }

    private void _receiverTask() {
        while (!stopReceiverThread) {
            driver.read();
        }
        stoppedReceiverThread = true;
    }

    /**
     * This method is called in order to start the {@link #receiverThread}.<br>
     * <p>
     * In order to ensure that only one {@link #receiverThread} is running at time,
     * we check the value of the boolean variable {@link #stoppedReceiverThread} if:
     * <ul>
     *     <li><b>true:</b> we set {@link #stoppedReceiverThread} to <b>false</b> and start the {@link #receiverThread}.</li>
     *     <li><b>false:</b> return.</li>
     * </ul>
     **/
    public void startReceiverThread() {
        if (stoppedReceiverThread) {
            // Only one thread can be active
            stopReceiverThread = false;
            stoppedReceiverThread = false;
            receiverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    _receiverTask();
                }
            });
            receiverThread.setName("CANbusProtocolReceiverThread");
            receiverThread.start();
        }
    }

    /**
     * This method is called in order to stop the running {@link #receiverThread}.
     * <p>
     *  Before return, waits the termination of the current {@link #receiverThread} via the {@link Thread#join()} method.
     */
    public void stopReceiverThread() throws InterruptedException {
        stopReceiverThread = true;
        receiverThread.join();
    }

    @Override
    public synchronized <T> void sendInstruction(T instruction) throws Exception {
        if(driver.isConnected()) {
            driver.write((InstructionCANbus) instruction);
        } else {
            throw new Exception("CanBus peripheral's is not connected. \n" +
                    "Please connect the CanBus in order to send data.");
        }
    }

    public BlockingQueue<TPCANMsg> getQueue() {
        return queue;
    }

    /**
     * Open the connection with the associated driver.
     *
     * @see CANbus_Driver#connect()
     */
    public boolean connect(String handle) {
        driver.setPort(handle);
        return driver.connect();
    }

    /**
     * Close the connection with the associated driver.
     *
     * @see CANbus_Driver#closeConnection()
     */
    public void disconnect() {
        driver.closeConnection();
    }

    public static void main(String[] args){
        CANbus_Protocol protocol = new CANbus_Protocol();
        TPCANMsg msg;
        long count = 1;

        while (true){
            try {

                msg = protocol.getQueue().take();
                System.out.println("Count: " + count + " ID: " + msg.getID() + " data: " +
                        HexFormat.of().formatHex(msg.getData()).toUpperCase());
                count++;


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
