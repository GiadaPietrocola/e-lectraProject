package com.electra.canbusdemo.CANbus;

import peak.can.basic.TPCANMsg;

public class InstructionCANbus {
    TPCANMsg frame;

    public InstructionCANbus(int id, byte[] data, byte length){
        frame = new TPCANMsg();
        frame.setID(id);
        frame.setData(data, length);
        frame.setLength(length);
    }

    public TPCANMsg getFrame(){
        return frame;
    }

}
