package com.electra.canbusdemo;

/**
 * Class containing static final String constants representing device IDs for CANbus communication.
 *
 * <p>
 * The {@code DeviceId} class provides a set of public constants that represent unique identifiers
 * for different devices in a CANbus network. These constants are used to distinguish between
 * various messages and commands exchanged between components of the CANbus system.
 * </p>
 *
 */
public final class DeviceId {
    public static final String VCU_Velocity = "222";
    public static final String VCU_Pair = "224";
    public static final String VCU_Charging = "226";
    public static final String Charger = "187";
    public static final String Charger_Battery = "207";
    public static final String Inverter_Battery = "208";
    public static final String ChargingMode = "210";
    public static final String Inverter = "290";
}