package com.electra.canbusdemo.CANbus;

import peak.can.MutableInteger;
import peak.can.basic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This Class implements an abstraction layer that allows to
 * send/receive bytes to/from the connected devices via CANbus.
 * <p>
 * This file contains the implementation of CANbus driver.
 * <p>
 * Property: E-Lectra s.r.l.
 */

public class CANbus_Driver implements Driver {
    private static CANbus_Driver canBusDriver =null;
    PCANBasic canbusCom;
    TPCANMsg msg;
    TPCANHandle handle;
    private boolean connected;
    private BlockingQueue<TPCANMsg> queue;

    public CANbus_Driver(BlockingQueue q){
        super();
        connected = false;
        queue = q;
        init();
    }

    /**
     * Set the Message Type
     *
     * @param type The type of {@link #msg} message
     */
    public void setMessageType(TPCANMessageType type){
        msg.setType(type);
    }

    /**
     * Set the Message ID
     *
     * @param id The ID of {@link #msg} message
     */
    public void setMessageID(int id){
        msg.setID(id);
    }

    /**
     * Initialize all local variables ({@link #canbusCom}, {@link #msg})
     * needed in order to use the PEAK USB/CAN Adapter
     */
    private void init(){
        canbusCom = new PCANBasic();
        canbusCom.initializeAPI();
        msg = new TPCANMsg();
        setMessageType(TPCANMessageType.PCAN_MESSAGE_STANDARD);
        setMessageID(0);
        connected = false;
    }

    @Override
    public boolean connect(){
        TPCANStatus res = canbusCom.Initialize(handle, TPCANBaudrate.PCAN_BAUD_500K, TPCANType.PCAN_TYPE_NONE, 100, (short) 3);
        if(res == TPCANStatus.PCAN_ERROR_OK) {
            connected = true;
        }
        else
        {
            try {
                getError(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            connected = false;
        }

//        connected = true;
        return connected;
    }

    private void getError(TPCANStatus status) throws Exception {
        StringBuffer error;

        if (status == null) {
            throw new Exception("Invalid status value (null).");
        }
        // filter only errors
        else if (status != TPCANStatus.PCAN_ERROR_OK)
        {
            error = new StringBuffer(TPCANParameterValue.MIN_LENGTH_ERROR_STRING);
            status = canbusCom.GetErrorText(status, (short)0, error);
            // Display Error
            if (status == TPCANStatus.PCAN_ERROR_OK)
            {
                throw new Exception(error.toString());
            }
            // Unable To Retrieve Error
            else
            {
                throw new Exception("Unable to call GetErrorText Function !");
            }
        }
    }

    @Override
    public void closeConnection() {
        canbusCom.Uninitialize(handle);
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Reset the {@link #msg} data and length field values.
     */
    public void restMessage() {
        byte[] data = {(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        msg.setData(data, (byte)8);
        msg.setLength((byte)0);
    }

    @Override
    public synchronized <T> void write(T data) {
        canbusCom.Write(handle, ((InstructionCANbus)data).getFrame());
    }

    @Override
    public synchronized void read() {
        TPCANMsg rcvd = new TPCANMsg(0, TPCANMessageType.PCAN_MESSAGE_STANDARD.getValue(), (byte)8, new byte[8]);
        rcvd.setLength((byte)0);
        canbusCom.Read(handle, rcvd, null);

        if(rcvd.getLength() <= 0){
            return;
        }

        //System.out.println("Received: " + HexFormat.of().formatHex(rcvd.getData()).toUpperCase());

        queue.add(rcvd);
    }

    /**
     * Set the {@link #handle} for the CANbus communication in
     * order to initialize the PCANBasic object ({@link #canbusCom})
     *
     * @param val the CAN handle value to be set
     */
    @Override
    public void setPort(String val) {
        this.handle = TPCANHandle.valueOf(val);
    }

    /**
     * Get the available Handlers list
     *
     * @return a String array with port names values
     **/
    public String[] getHandlerNames(){
        canbusCom = new PCANBasic();
        canbusCom.initializeAPI();
        MutableInteger integerBuffer = new MutableInteger(0);
        TPCANStatus status = canbusCom.GetValue(TPCANHandle.PCAN_NONEBUS, TPCANParameter.PCAN_ATTACHED_CHANNELS_COUNT, integerBuffer, Integer.SIZE);

        TPCANChannelInformation[] chInfos = new TPCANChannelInformation[integerBuffer.getValue()];
        List<String> outList = new ArrayList<>();

        canbusCom.GetValue(TPCANHandle.PCAN_NONEBUS, TPCANParameter.PCAN_ATTACHED_CHANNELS, chInfos, chInfos.length);

        for(int i = 0; i < integerBuffer.getValue(); i++){
            if(!chInfos[i].getChannelHandle().name().equals("PCAN_NONEBUS")){
                outList.add(chInfos[i].getChannelHandle().name());
            }
        }

        return integerBuffer.getValue() == 0 ? new String[]{""} : outList.toArray(String[]::new);
    }

    /**
     * A ready to Run function in order to test the CANbus_Driver features.
     */
    public static void main(String[] args) {
        byte[] data = {(byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88};
        CANbus_Driver driver = new CANbus_Driver(new LinkedBlockingQueue());
        driver.connect();
//        int i = 0;
//        while (i<150) {
//            driver.restMessage();
//            driver.read();
//            try {
//                System.out.println("" + i + ") Wait...");
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            i++;
//
//        }

        for (int i = 0; i < 10; i++) {
            driver.setMessageID(i);
            driver.write(data);
        }

       driver.closeConnection();
    }

}
