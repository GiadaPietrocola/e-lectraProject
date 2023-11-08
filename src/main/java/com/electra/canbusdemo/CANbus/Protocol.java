package com.electra.canbusdemo.CANbus;

import java.util.List;

/**
 * LEVEL 1 contains all the methods in order to control the interaction with the given device connected
 * to the host via one of the supported hardware peripheral.<br>
 * Two known Protocol are used at moment: the RS232 and the CANbus.<br>
 * This Protocol is an abstraction layer that allows to interact with any of the supported device via the given
 * hardware peripheral. Adapt the general format of an instruction into a suitable send and receive byte
 * commands available on the Driver services.
 * <p>
 * This file contains the abstraction of the protocol.
 * <p>
 * Property: E-Lectra s.r.l.
 */

public abstract class Protocol {
    protected Driver driver;

    /**
     * Adapt and send the general format of an instruction into a suitable
     * byte commands available on the {@link #driver} services.
     *
     * @param instruction the instruction to be sent to the connected device
     **/
    public abstract <T> void sendInstruction(T instruction) throws Exception;

    /**
     * Adapt and send a list of general format instructions.
     *
     * @param instructionList list of instructions to be sent to the connected device
     **/
    public <T> void sendInstructionList(List<T> instructionList) throws Exception {
        for(T instruction: instructionList){
            sendInstruction(instruction);
        }
    }

    /**
     * Get the Driver ({@link #driver}) associated with the Protocol
     *
     * @return the {@link #driver} variable
     **/
    public Driver getDriver(){
        return driver;
    }

}
