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
    /**
     * ID: 222
     * -
     * Position:
     * 0 Stadio Potenza;
     * 1 Marcia avanti;
     * 2 Marcia indietro;
     * 3 Setpoint Velocità;
     * 4 Main Contactor;
     */
    public static final String VCU_Velocity = "222";
    /**
     * ID: 224
     * -
     * Position:
     * 0 Setpoint Coppia;
     * 1 Contattore 2;
     * 2 Emergency Stop;
     */
    public static final String VCU_Pair = "224";
    /**
     * ID: 226
     * -
     * Position:
     * 0 Parking mode;
     * 1 Charging mode;
     * 2 Grid mode;
     * 3 Res mode;
     * 4 Setpoint corrente;
     * 5 Setpoint tensione;
     * 6 Corrente costante;
     * 7 Corrente custom;
     */
    public static final String VCU_Charging = "226";
    /**
     * ID: 187
     * -
     * Position:
     * 0 Valore corrente charger;
     * 2-3 Valore tensione charger;
     */
    public static final String Charger = "187";
    /**
     * ID: 207
     * -
     * Position:
     * 2 Betteria SoC;
     */
    public static final String Charger_Battery = "207";
    /**
     * ID: 208
     * -
     * Position:
     * 0 Modalità Charging;
     */
    public static final String Inverter_Battery = "208";
    /**
     * ID: 210
     * -
     * Position:
     * 2 Temperatura del motore;
     * 5 Valore corrente batteria;
     * 6 Valore tensione matteria;
     */
    public static final String ChargingMode = "210";
    /**
     * ID: 290
     * -
     * Position:
     * 0 Valore Velocità;
     * 1 Valore Coppia;
     * 2 Stato contattore 1;
     * 3 Stato contattore 2;
     * 4 Stato Emergency stop;
     * 6 Modalità trazione;
     */
    public static final String Inverter = "290";
}