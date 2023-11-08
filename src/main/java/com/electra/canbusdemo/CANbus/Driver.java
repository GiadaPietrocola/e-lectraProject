package com.electra.canbusdemo.CANbus;

/**
 * This Interface represent an abstraction layer that allows to
 * send/receive bytes to/from the connected devices.<br>
 * The Driver manage the interaction with the given hardware
 * peripheral connected to the host device.<br>
 * <p>
 * This file contains the abstraction of the driver.
 * <p>
 * Property: E-Lectra s.r.l.
 */
public interface Driver {

    /**
     * Open the connection with the given hardware peripheral
     *
     * @return a boolean: false in case of error, true otherwise
     **/
    boolean connect();

    /**
     * Close the connection with the given hardware peripheral
     **/
    void closeConnection();

    /**
     * Check if the host is connected to the given hardware peripheral
     *
     * @return a boolean: false if the host is not connected, true otherwise
     **/
    boolean isConnected();

    /**
     * Write data on the given hardware peripheral
     *
     * @param data the data to be written on the given peripheral
     **/
    <T> void write(T data);

    /**
     * Set the port where open the connection
     *
     * @param val the port name where the hardware peripheral is connected
     */
    void setPort(String val);

    /**
     * Read te data received from the given hardware peripheral
     **/
    void read();
}
