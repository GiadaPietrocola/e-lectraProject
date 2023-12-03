package com.electra.canbusdemo;

import com.electra.canbusdemo.CANbus.CANbus_Controller;
import com.electra.canbusdemo.CANbus.Notifiable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.text.ChoiceFormat;
import java.util.HexFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.controlsfx.control.*;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;

import static com.electra.canbusdemo.CANbus.CANbus_Controller.getCanBusController;
import static com.electra.canbusdemo.DeviceId.*;
import static java.lang.Integer.parseInt;

/**
 * The MainViewController class controls the main view of the CANbusDEMO application.
 * It handles user interactions and updates the GUI components based on the CANbus data.
 */
public class MainViewController implements Notifiable {
    @FXML
    private  TextField idTextField;
    @FXML
    private  Button connectButton;
    @FXML
    private  Button i_emergencyStopButton;
    @FXML
    private  Label i_reverseLabel;
    @FXML
    private  Label i_velocitaDesiderataLabel;
    @FXML
    private  Label i_coppiaDesiderataLabel;
    @FXML
    private  Label i_nmLabel;
    @FXML
    private  Label i_ampereLabel;
    @FXML
    private  Label i_voltLabel;
    @FXML
    private  Label i_kmLabel;
    @FXML
    private  Label i_correnteLabel;
    @FXML
    private  Label i_tensioneLabel;
    @FXML
    private  Label i_velocitaLabel;
    @FXML
    private  Button sendButton;
    @FXML
    private  TextArea receivedTextArea;
    @FXML
    private  TextArea sentTextArea;

    // INPUT WIDGETS -----------------------------------------------------------

    @FXML
    private  ToggleSwitch i_forwardReverseSwitchButton;
    @FXML
    private  ToggleSwitch i_coppiaVelocitaSwitchButton;
    @FXML
    private  TextField i_coppiaTextField;
    @FXML
    private  TextField i_velocitaTextField;
    @FXML
    private  RadioButton i_sportRadioButton;
    @FXML
    private  RadioButton i_ecoRadioButton;
    @FXML
    private  RadioButton i_parkingModeRadioButton;
    @FXML
    private  RadioButton i_chargingModeRadioButton;
    @FXML
    private  TextField i_correnteTextField;
    @FXML
    private  TextField i_tensioneTextField;
    @FXML
    private  ToggleSwitch i_profiloCaricaSwitchButton;
    @FXML
    private  ToggleSwitch i_caricaSwitchButton;
    @FXML
    private  ToggleSwitch i_gridResSwitchButton;
    @FXML
    private  Label i_customLabel;
    @FXML
    private  Label i_resLabel;
    @FXML
    private  ToggleSwitch i_contattore1SwitchButton;
    @FXML
    private  ToggleSwitch i_contattore2SwitchButton;
    @FXML
    private  Gauge i_setVelocitaCoppiaGauge;
    @FXML
    private  ToggleGroup sportEco;
    @FXML
    private  ToggleGroup parkingCharging;

    // OUTPUT WIDGETS -----------------------------------------------------------

    @FXML
    private  Gauge o_tensioneBatterieGauge;
    @FXML
    private  Gauge o_correnteBatterieGauge;
    @FXML
    private  Gauge o_socGauge;
    @FXML
    private  Gauge o_tensioneCaricatoreGauge;
    @FXML
    private  Gauge o_correnteCaricatoreGauge;
    @FXML
    private  Gauge o_velocitaMotoreGauge;
    @FXML
    private  Gauge o_coppiaMotoreGauge;
    @FXML
    private  Gauge o_temperaturaGauge;
    @FXML
    private  StatusBar o_modalitaCaricatoreStatusButton;
    @FXML
    private  StatusBar o_modalitaTrazioneStatusButton;
    @FXML
    private  StatusBar o_statusButtonEmergencyStop;
    @FXML
    private  StatusBar o_statusButtonContattore1;
    @FXML
    private  StatusBar o_statusButtonContattore2;
    @FXML
    private  Button o_emergencyStopButton;
    @FXML
    private  Button o_saveButton;
    @FXML
    private  TextField o_pathFileTextFied;
    @FXML
    private  TextField data0TextField, data1TextField, data2TextField, data3TextField, data4TextField,
            data5TextField, data6TextField, data7TextField;
    private  RadioButton lastSelectedSportEco;
    private  RadioButton lastSelectedParkingCharging;
    @FXML
    private  ComboBox<String> deviceComboBox;
    private  ObservableList<String> canBusDevice_List = FXCollections.observableArrayList(); //Observable = c'è un osservatore che sa quando viene modificata
    private  CANbus_Controller canBusController;
    private  String canBusDevice;
    private final int  MAX_SHOW_MEX = 500;
    private int  sendID = 0, sendCycle = 0, receiveID = 0, receiveCycle = 0;

    /**
     * Checks and filters the input for a TextField based on specific criteria.
     *
     * <p>
     * This method is designed to be used as an event handler for KeyEvent on a TextField.
     * The method ensures that the entered character is among the allowed characters.
     * </p>
     *
     * @param keyEvent The KeyEvent triggered by user input.
     * @param textField The TextField for which the input is being checked.
     */
    private void checkTextFieldInput(KeyEvent keyEvent, TextField textField){
        // Converts the input character to uppercase for consistency
        String newChar = keyEvent.getCharacter().toUpperCase();

        // String containing the allowed characters
        String keyFilter = "0123456789ABCDEF";

        // Verifies that the new character is among the allowed characters
        // or checks that there are not more than two characters
        if (!keyFilter.contains(newChar) || textField.getText().length() > 1) {
            // If the character is not valid or the length exceeds 1, consume the event to discard the input
            keyEvent.consume();
        }
    }

    /**
     * Checks and filters the input for a TextField intended for numeric values.
     *
     * <p>
     * This method is designed to be used as an event handler for KeyEvent on a TextField.
     * The method ensures that the entered numeric character is among the allowed characters.
     * </p>
     *
     * @param keyEvent The KeyEvent triggered by user input.
     * @param textField The TextField for which the input is being checked.
     */
    private void checkNumberFieldInput(KeyEvent keyEvent, TextField textField){
        // Converts the input character to uppercase for consistency
        String newChar = keyEvent.getCharacter().toUpperCase();

        // String containing the allowed numeric characters
        String keyFilter = "0123456789";

        // Verifies that the new character is among the allowed numeric characters
        // or checks that there are not more than two characters, except for the special case "10" followed by "0"
        if (!keyFilter.contains(newChar) || (textField.getText().length() > 1 && !(textField.getText().equals("10") && newChar.equals("0")))  ) {
            // If the character is not valid or the length exceeds 1 (except for "10" followed by "0"), consume the event to discard the input
            keyEvent.consume();
        }
    }

    /**
     * Initializes the MainViewController.
     *
     * <p>
     * This method is called automatically by the JavaFX framework
     * when the FXML file is loaded.
     * </p>
     *
     */
    @FXML
    public void initialize (){
        // Disable the sendButton initially because the connection is not open
        // sendButton.setDisable(true);

        // Populate the CANbus device list with the connected devices
        canBusDevice_List.addAll();

        // Set the items in the deviceComboBox (in the top left corner) with the connected CANbus devices
        deviceComboBox.setItems(canBusDevice_List);

        // Initialize the CANbus Controller as a singleton and set the notifiable object
        canBusController = getCanBusController();
        canBusController.setNotifiable(this);

        // Add event filters for validating input in "velocità" TextField
        i_velocitaTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkNumberFieldInput(keyEvent, i_velocitaTextField);
        });

        // Add event filters for validating input in "coppia" TextField
        i_coppiaTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkNumberFieldInput(keyEvent, i_coppiaTextField);
        });

        // Set up ToggleGroups for RadioButtons
        i_sportRadioButton.setToggleGroup(sportEco);
        i_ecoRadioButton.setToggleGroup(sportEco);

        i_parkingModeRadioButton.setToggleGroup(parkingCharging);
        i_chargingModeRadioButton.setToggleGroup(parkingCharging);

        // Initialize input and output Gauges

        // o_tensioneBatterieGauge = new Gauge();
        // o_socGauge = new Gauge();
        // o_coppiaMotoreGauge = new Gauge();
        // o_temperaturaGauge = new Gauge();
        // o_velocitaMotoreGauge = new Gauge();
        // o_correnteCaricatoreGauge = new Gauge();
        // o_tensioneCaricatoreGauge = new Gauge();

        i_caricaSwitchButton = new ToggleSwitch();

        o_correnteBatterieGauge.setAnimated(true);
        o_tensioneBatterieGauge.setAnimated(true);
        o_socGauge.setAnimated(true);
        o_coppiaMotoreGauge.setAnimated(true);
        o_temperaturaGauge.setAnimated(true);
        o_velocitaMotoreGauge.setAnimated(true);
        o_correnteCaricatoreGauge.setAnimated(true);
        o_tensioneCaricatoreGauge.setAnimated(true);

        i_setVelocitaCoppiaGauge.setAnimated(true);

        // Set up the emergency stop button style
        i_emergencyStopButton.getStyleClass().add("button-red");

        /**
         * This method updates the CANbus device list when the deviceComboBox is clicked.
         *
         * <p>
         * The method is triggered by the setOnMouseClicked event handler attached to the
         * deviceComboBox. When the deviceComboBox is clicked, the existing list is cleared,
         * and the updated list of available CANbus device handlers is added to the
         * canBusDevice_List.
         * </p>
         *
         * @param event The MouseEvent representing the click event on the deviceComboBox.
         */
        // Set an event handler for mouse click on the deviceComboBox
        deviceComboBox.setOnMouseClicked(event -> {
            // Remove all elements from the canBusDevice_List
            canBusDevice_List.removeAll(canBusDevice_List);

            // Add all available handlers from canBusController to canBusDevice_List
            canBusDevice_List.addAll(canBusController.getAvailableHandlers());
        });

        /**
         * Event handler for the i_forwardReverseSwitchButton's mouse click event.
         *
         * <p>
         * This method is triggered when the i_forwardReverseSwitchButton is clicked.
         * If i_forwardReverseSwitchButton is selected, it disables the i_sportRadioButton
         * and i_ecoRadioButton; otherwise, it enables them.
         * </p>
         *
         * @param event The MouseEvent representing the click event on the
         *              i_forwardReverseSwitchButton.
         * @throws RuntimeException If an exception occurs while handling received
         *                          messages.
         */
        // Set an event handler for mouse click on i_forwardReverseSwitchButton
        i_forwardReverseSwitchButton.setOnMouseClicked(event -> {
            // Check if i_forwardReverseSwitchButton is selected
            if (i_forwardReverseSwitchButton.isSelected()){
                // Disable i_sportRadioButton and i_ecoRadioButton if i_forwardReverseSwitchButton is selected
                i_sportRadioButton.setDisable(true);
                i_ecoRadioButton.setDisable(true);
            } else {
                // Enable i_sportRadioButton and i_ecoRadioButton if i_forwardReverseSwitchButton is not selected
                i_sportRadioButton.setDisable(false);
                i_ecoRadioButton.setDisable(false);
            }

            // if(connectButton.getText().equals("Connect")){
            //     fireAlarm(Alert.AlertType.ERROR, "Warning", "Please select a valid CAN bus adapter Device.");
            // }
            // else{

            try {
                //send(VCU_Velocity);
                handleReceivedMessages(Inverter, "0000000001000000"); //per testing
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //}

        });

        /**
         * Event handler for the i_coppiaVelocitaSwitchButton's mouse click event.
         *
         * <p>
         * This method is triggered when the i_coppiaVelocitaSwitchButton is clicked.
         * It adjusts the state of associated text fields (i_coppiaTextField and
         * i_velocitaTextField) based on whether the i_coppiaVelocitaSwitchButton is
         * selected or not.
         * </p>
         *
         * @param event The MouseEvent representing the click event on the
         *              i_coppiaVelocitaSwitchButton.
         */
        // Set an event handler for mouse click on i_coppiaVelocitaSwitchButton
        i_coppiaVelocitaSwitchButton.setOnMouseClicked(event -> {
            // Check if i_coppiaVelocitaSwitchButton is selected
            if(i_coppiaVelocitaSwitchButton.isSelected()){
                // If selected, set i_coppiaTextField to "0" and disable it
                i_coppiaTextField.setText("0");
                i_coppiaTextField.setDisable(true);

                i_velocitaTextField.setDisable(false);
            } else {
                // If not selected, enable i_coppiaTextField
                i_coppiaTextField.setDisable(false);

                i_velocitaTextField.setDisable(true);
                i_velocitaTextField.setText("0");
            }
        });

        /**
         * Event filter for key presses on the i_coppiaTextField.
         *
         * <p>
         * This event filter is triggered when a key is pressed in the i_coppiaTextField.
         * It specifically checks if the pressed key is the ENTER key.
         * </p>
         *
         * @param event The KeyEvent representing the key press event on the i_coppiaTextField.
         * @throws RuntimeException If an exception occurs while sending the command.
         */
        // Add an event filter for key presses on i_coppiaTextField
        i_coppiaTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Check if the pressed key is ENTER
            if (event.getCode() == KeyCode.ENTER) {
                // Set the value of i_setVelocitaCoppiaGauge to the parsed integer value of i_coppiaTextField
                i_setVelocitaCoppiaGauge.setValue(parseInt(i_coppiaTextField.getText()));

                try {
                    send(VCU_Pair);
                    System.out.println("a");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // Consume the event to prevent other handlers from receiving it
                event.consume();
            }
        });

        /**
         * Event filter for key presses on the i_correnteTextField.
         *
         * <p>
         * This event filter is triggered when a key is pressed in the i_correnteTextField.
         * It specifically checks if the pressed key is the ENTER key.
         * </p>
         *
         * @param event The KeyEvent representing the key press event on the i_correnteTextField.
         * @throws RuntimeException If an exception occurs while sending the command.
         */
        // Add an event filter for key presses on i_correnteTextField
        i_correnteTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Check if the pressed key is ENTER
            if (event.getCode() == KeyCode.ENTER) {
                // Set the value of i_setVelocitaCoppiaGauge to the parsed integer value of i_velocitaTextField
                i_setVelocitaCoppiaGauge.setValue(parseInt(i_velocitaTextField.getText()));

                try {
                    send(VCU_Charging);
                    System.out.println("a");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // Consume the event to prevent other handlers from receiving it
                event.consume();
            }
        });

        /**
         * Event filter for key presses on the i_tensioneTextField.
         *
         * <p>
         * This event filter is triggered when a key is pressed in the i_tensioneTextField.
         * It specifically checks if the pressed key is the ENTER key.
         * </p>
         *
         * @param event The KeyEvent representing the key press event on the i_tensioneTextField.
         * @throws RuntimeException If an exception occurs while sending the command.
         */
        // Add an event filter for key presses on i_tensioneTextField
        i_tensioneTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Check if the pressed key is ENTER
            if (event.getCode() == KeyCode.ENTER) {

                try {
                    send(VCU_Charging);
                    System.out.println("a");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // Consume the event to prevent other handlers from receiving it
                event.consume();
            }
        });

        /**
         * Event handler for the i_parkingModeRadioButton's mouse click event.
         *
         * <p>
         * This method is triggered when the i_parkingModeRadioButton is clicked. It
         * disables various GUI components associated with charging and vehicle velocity
         * control when the parking mode is activated.
         * </p>
         *
         * @param event The MouseEvent representing the click event on the
         *              i_parkingModeRadioButton.
         * @throws RuntimeException If an exception occurs while sending the commands.
         */
        // Set an event handler for mouse click on i_parkingModeRadioButton
        i_parkingModeRadioButton.setOnMouseClicked(event -> {
            // Disable various GUI components when i_parkingModeRadioButton is clicked
            i_gridResSwitchButton.setDisable(true);
            i_profiloCaricaSwitchButton.setDisable(true);
            i_customLabel.setDisable(true);
            i_resLabel.setDisable(true);
            i_tensioneTextField.setDisable(true);
            i_correnteTextField.setDisable(true);
            i_ampereLabel.setDisable(true);
            i_voltLabel.setDisable(true);
            i_correnteLabel.setDisable(true);
            i_tensioneLabel.setDisable(true);

            try {
                send(VCU_Charging);
                send(VCU_Velocity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        /**
         * Event handler for the i_chargingModeRadioButton's mouse click event.
         *
         * <p>
         * This method is triggered when the i_chargingModeRadioButton is clicked. It
         * enables various GUI components associated with charging and vehicle velocity
         * control when the charging mode is activated.
         * </p>
         *
         * @param event The MouseEvent representing the click event on the
         *              i_chargingModeRadioButton.
         * @throws RuntimeException If an exception occurs while sending the commands.
         */
        // Set an event handler for mouse click on i_chargingModeRadioButton
        i_chargingModeRadioButton.setOnMouseClicked(event -> {
            // Enable various UI components when i_chargingModeRadioButton is clicked
            i_gridResSwitchButton.setDisable(false);
            i_profiloCaricaSwitchButton.setDisable(false);
            i_customLabel.setDisable(false);
            i_resLabel.setDisable(false);
            i_tensioneTextField.setDisable(false);
            i_correnteTextField.setDisable(false);
            i_ampereLabel.setDisable(false);
            i_voltLabel.setDisable(false);
            i_correnteLabel.setDisable(false);
            i_tensioneLabel.setDisable(false);

            try {
                send(VCU_Charging);
                send(VCU_Velocity);
                System.out.println("a");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        i_gridResSwitchButton.setOnMouseClicked(event -> {

            try {
                send(VCU_Charging);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        i_profiloCaricaSwitchButton.setOnMouseClicked(event -> {

            try {
                send(VCU_Charging);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        i_contattore1SwitchButton.setOnMouseClicked(event -> {

            try {
                send(VCU_Velocity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        i_contattore2SwitchButton.setOnMouseClicked(event -> {

            try {
                send(VCU_Pair);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

    }

    /**
     * Handles the action triggered by clicking the connectButton.
     *
     * <p>
     * This method is called when the connectButton is clicked. It manages the connection
     * and disconnection to/from the CAN bus device. If the current button text is "Connect,"
     * it attempts to connect to the selected CAN bus device, and if successful, it changes
     * the button text to "Disconnect." If the current button text is "Disconnect," it
     * disconnects from the CAN bus device and changes the button text back to "Connect."
     * If there are issues during the connection attempt or disconnection, it displays
     * relevant error messages using the fireAlarm() method.
     * </p>
     *
     */
    @FXML
    void connectButtonAction() {
        // Check if the button text is "Connect"
        if (connectButton.getText().equals("Connect")){
            // Assign the selected device from the combo box
            canBusDevice = deviceComboBox.getValue();

            // If the device is not already connected
            if (!canBusController.isConnected()) {
                // Check if the selected device is null or an empty string
                if (canBusDevice == null || canBusDevice.equals("")) {
                    // Display an error message if the device selection is invalid
                    fireAlarm(Alert.AlertType.ERROR, "Warning", "Please select a valid CAN bus adapter Device.");
                    return;
                }
            }

            // Try to connect to the CAN bus device ("true" if connection is successful, "false" if connection fails)
            if (!canBusController.connect(canBusDevice)) {
                // Display an error message if the connection fails, providing possible causes
                String mex = "Connection with the CANbus failed!!\n";
                mex += "Possible causes:\n";
                mex += "  - the selected device is wrong;\n" +
                        "  - the device is not connected to the USB port;\n\n";
                mex += "If the problem persist, try to unplug and replug the device.";

                fireAlarm(Alert.AlertType.ERROR, "Connection ERROR!!", mex);
                return; // Exit the method
            }

            // Enables the massage sendButton
            // sendButton.setDisable(false);

            // Change the button text to "Disconnect"
            connectButton.setText("Disconnect");

            // If the button text is not "Connect" (it is "Disconnect")
        } else {
            // Disconnect the CAN bus device
            canBusController.disconnect();

            // Enables the massage sendButton
            // sendButton.setDisable(true);

            // Change the button text back to "Connect"
            connectButton.setText("Connect");
        }
    }

    /*
    @FXML
    public void clearAllButtonAction(){ //Azione del pulsante clear all
        receiveID = receiveCycle = sendID = sendCycle = 0; //reinizializza tutte le variabili
        receivedTextArea.clear(); //pulisce le text area
        sentTextArea.clear();
    }
    */

    /*
    @FXML
    public void sendButtonAction() throws Exception {  // Può generare eccezioni se il parametro di fromHexDigits ha più di 8 byte o se contiene caratteri non esadecimali ma siamo sicuri che non è così quindi non serve il try catch
        byte data[] =
                {
                        (byte) HexFormat.fromHexDigits(data0TextField.getText()), //i dati dalle textfield sono trasformati in byte dll'esadecimale
                        (byte) HexFormat.fromHexDigits(data1TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data2TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data3TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data4TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data5TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data6TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data7TextField.getText())
                };

        canBusController.sendCommand(HexFormat.fromHexDigits(idTextField.getText()), data); //manda l'id in esadecimale e il dato
        sendID++;
        if(sendID-(sendCycle*MAX_SHOW_MEX) > MAX_SHOW_MEX){ //evita che troppe linee di testo vengano inviate alla text area di invio e la pulisce dpo un po'
            sendCycle++;
            sentTextArea.clear();
        }
       // sentTextArea.appendText("[" + sendID + "]: " + "ID: " + idTextField.getText().toUpperCase() + " data: " +   //il messaggio viene aggiunto alla text area
       //         HexFormat.of().formatHex(data).toUpperCase() + "\n");
    }
    */

    /**
     * Sends a CANbus command based on the provided id and associated data.
     *
     * <p>
     * The method constructs a byte array of data based on the specified CAN bus command
     * id and associated GUI component values. It performs checks for valid
     * inputs and displays error messages using the fireAlarm() method if necessary.
     * Finally, it sends the constructed command using the sendCommand() method of the
     * canBusController.
     * </p>
     *
     * @param id The id of the CAN bus command (VCU_Velocity, VCU_Pair, or VCU_Charging).
     * @throws Exception If an exception occurs during the command sending process.
     */
    public void send(String id) throws Exception {
        // Initialize an empty byte array for data
        byte[] data={};

        switch (id) {
            case VCU_Velocity: {
                if (i_forwardReverseSwitchButton.isDisabled()){
                    // Construct data array for VCU_Velocity when forwardReverseSwitchButton is disabled
                    data = new byte[]{
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits(i_contattore1SwitchButton.isSelected() ? "1" : "0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0")
                    };
                } else {
                    // Construct data array for VCU_Velocity when forwardReverseSwitchButton is enabled
                    data = new byte[]{
                            (byte) HexFormat.fromHexDigits(i_forwardReverseSwitchButton.isSelected() ? "0" : "1"),
                            (byte) HexFormat.fromHexDigits(i_forwardReverseSwitchButton.isSelected() ? "0" : "1"),
                            (byte) HexFormat.fromHexDigits(i_forwardReverseSwitchButton.isSelected() ? "1" : "0"),
                            (byte) HexFormat.fromHexDigits(i_velocitaTextField.getText()),
                            (byte) HexFormat.fromHexDigits(i_contattore1SwitchButton.isSelected() ? "1" : "0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0"),
                            (byte) HexFormat.fromHexDigits("0")
                    };

                    // Additional checks for valid inputs
                    if (data[3]>(byte)100){
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Please insert a valid input for velocity.");
                        return;
                    } else if (data[1]==(byte)1&&data[2]==(byte)1){
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Forward and Reverse modes cannot be activated at the same time");
                        return;
                    }
                }
            }
            break;

            case VCU_Pair: {
                if (i_forwardReverseSwitchButton.isDisabled()){
                    // Construct data array for VCU_Pair when forwardReverseSwitchButton is disabled
                    data = new byte[]
                            {
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits(i_contattore2SwitchButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_emergencyStopButton.getText().equals("STOP") ? "0" : "1"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0")
                            };
                } else {
                    // Construct data array for VCU_Pair when forwardReverseSwitchButton is enabled
                    data = new byte[]
                            {
                                    (byte) HexFormat.fromHexDigits(i_coppiaTextField.getText()),
                                    (byte) HexFormat.fromHexDigits(i_contattore2SwitchButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_emergencyStopButton.getText().equals("STOP") ? "0" : "1"),
                                    (byte) HexFormat.fromHexDigits(i_sportRadioButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_ecoRadioButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0")
                            };

                    // Additional checks for valid inputs
                    if (data[0]>(byte)100){
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Please insert a valid input for pair.");
                        return;
                    } else if (data[3]==(byte)1&&data[4]==(byte)1){
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Sport and Eco modes cannot be activated at the same time");
                        return;
                    }
                }
            }
            // Ci manca un break??

            case VCU_Charging: {
                if (i_gridResSwitchButton.isDisabled()){
                    // Construct data array for VCU_Charging when forwardReverseSwitchButton is disabled
                    data = new byte[]
                            {
                                    (byte) HexFormat.fromHexDigits(i_parkingModeRadioButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_chargingModeRadioButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                                    (byte) HexFormat.fromHexDigits("0"),
                            };
                } else {
                    // Construct data array for VCU_Charging when forwardReverseSwitchButton is enabled
                    data = new byte[]
                            {
                                    (byte) HexFormat.fromHexDigits(i_parkingModeRadioButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_chargingModeRadioButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_gridResSwitchButton.isSelected() ? "0" : "1"),
                                    (byte) HexFormat.fromHexDigits(i_gridResSwitchButton.isSelected() ? "1" : "0"),
                                    (byte) HexFormat.fromHexDigits(i_correnteTextField.getText()),
                                    (byte) HexFormat.fromHexDigits(i_tensioneTextField.getText()),
                                    (byte) HexFormat.fromHexDigits(i_profiloCaricaSwitchButton.isSelected() ? "0" : "1"),
                                    (byte) HexFormat.fromHexDigits(i_profiloCaricaSwitchButton.isSelected() ? "1" : "0"),
                            };

                    // Additional checks for valid inputs
                    if (data[0]==(byte)1&&data[1]==(byte)1) {
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Parking and Charging modes cannot be activated at the same time");
                        return;
                    } else if (data[2]==(byte)1&&data[3]==(byte)1) {
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Grid and Res modes cannot be activated at the same time");
                        return;
                    } else if (data[6]==(byte)1&&data[7]==(byte)1) {
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Cost and Custom modes cannot be activated at the same time");
                        return;
                    }
                }
            }
            break;

            default:
                System.err.println("Id errato");
                break;
        }

        canBusController.sendCommand(HexFormat.fromHexDigits(id), data);
    }

    /**
     * Handles received messages from the CAN bus, updating the user interface accordingly.
     *
     * <p>
     * This method is responsible for processing received CAN bus messages based on the provided
     * id and associated data. It updates various user interface elements such as
     * Gauges, Labels, and Buttons to reflect the current state of the vehicle components.
     * </p>
     *
     * @param id   The id of the received CAN bus message (Charger_Battery, Charger,
     *             Inverter_Battery, ChargingMode, Inverter).
     * @param data The data associated with the received message.
     * @throws Exception If an exception occurs during the message handling process.
     */
    public void handleReceivedMessages(String id, String data) throws Exception {
        System.out.println(data);

        switch (id) {
            case Charger_Battery:
                // Update State of Charge (SoC) Gauge based on received data
                o_socGauge.setValue(HexFormat.fromHexDigits(data.substring(2,4)));
                break;

            case Charger:
                // Update Charger Current and Voltage Gauges based on received data
                o_correnteCaricatoreGauge.setValue(HexFormat.fromHexDigits(data.substring(0,2)));
                o_tensioneCaricatoreGauge.setValue(HexFormat.fromHexDigits(data.substring(2,6)));
                break;

            case Inverter_Battery:
                // Update Inverter Temperature, Battery Current, and Battery Voltage Gauges based on received data
                o_temperaturaGauge.setValue(HexFormat.fromHexDigits(data.substring(2,4)));
                o_correnteBatterieGauge.setValue(HexFormat.fromHexDigits(data.substring(8,10)));
                o_tensioneBatterieGauge.setValue(HexFormat.fromHexDigits(data.substring(10,12)));
                break;

            case ChargingMode:
                // Update Charging Mode status based on received data
                if (HexFormat.fromHexDigits(data.substring(0,2))==0)
                    o_modalitaCaricatoreStatusButton.setText("");
                else if (HexFormat.fromHexDigits(data.substring(0,2))==1)
                    o_modalitaCaricatoreStatusButton.setText("   GRID");
                else if (HexFormat.fromHexDigits(data.substring(0,2))==2)
                    o_modalitaCaricatoreStatusButton.setText("     RES");
                break;

            case Inverter:
                // Update Inverter Speed, Torque, and Contactor Status based on received data
                o_velocitaMotoreGauge.setValue(HexFormat.fromHexDigits(data.substring(0,2)));
                o_coppiaMotoreGauge.setValue(HexFormat.fromHexDigits(data.substring(2,4)));

                // Update Contactor 1 status based on received data
                if (HexFormat.fromHexDigits(data.substring(4,6))==0) {
                    o_statusButtonContattore1.getStyleClass().remove("status-bar-red");
                    o_statusButtonContattore1.getStyleClass().add("status-bar-green");
                } else if (HexFormat.fromHexDigits(data.substring(4,6))==1) {
                    o_statusButtonContattore1.getStyleClass().remove("status-bar-green");
                    o_statusButtonContattore1.getStyleClass().add("status-bar-red");
                }

                // Update Contactor 2 status based on received data
                if (HexFormat.fromHexDigits(data.substring(6,8))==0) {
                    o_statusButtonContattore2.getStyleClass().remove("status-bar-red");
                    o_statusButtonContattore2.getStyleClass().add("status-bar-green");
                } else if (HexFormat.fromHexDigits(data.substring(6,8))==1) {
                    o_statusButtonContattore2.getStyleClass().remove("status-bar-green");
                    o_statusButtonContattore2.getStyleClass().add("status-bar-red");
                }

                // Update Traction Mode status based on received data
                if (HexFormat.fromHexDigits(data.substring(8,10))==0)
                    o_modalitaTrazioneStatusButton.setText("");
                else if (HexFormat.fromHexDigits(data.substring(8,10))==1)
                    o_modalitaTrazioneStatusButton.setText(" SPORT");
                else if (HexFormat.fromHexDigits(data.substring(8,10))==2)
                    o_modalitaTrazioneStatusButton.setText("  ECO");
                break;

            default:
                break;
        }
    }

    /**
     * Displays an alert with the specified type, title, and content text.
     *
     * <p>
     * This method creates an instance of the JavaFX Alert class with the specified alert type,
     * title, and content text. It sets the owner of the alert to the main stage of the
     * {@link MainApplication}, preventing user interaction with the application until the alert
     * window is closed. The method then sets the title and content text of the alert and displays
     * it, waiting for the user's response before continuing.
     * </p>
     *
     * @param type         The type of the alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title        The title of the alert window.
     * @param contentText  The content text or message displayed in the alert.
     */
    private void fireAlarm(Alert.AlertType type, String title, String contentText){
        // Create a new Alert with the specified type (determines the icon), title, and content text
        Alert alert = new Alert(type);

        // Initialize the owner of the alert to MainApplication's stage
        // Prevents the user from interacting with the application while the alarm window is open.
        alert.initOwner(MainApplication.stage);

        // Set the title and content text of the alert
        alert.setTitle(title);
        alert.setContentText(contentText);

        //alert.setX(owner.getX());

        // Display the alert and wait for the user's response (the window is blocked until the user closes the alert)
        alert.showAndWait();
    }

    @Override
    public void _notify(String data) {

        // Using Platform.runLater to ensure that UI updates are performed on two different threads for sending and receiving.
        Platform.runLater(() -> {

            receiveID++; // Increment the receive ID for each received message

            // Check if it's time to clear the receivedTextArea based on the receiveID and MAX_SHOW_MEX
            if (receiveID-(receiveCycle*MAX_SHOW_MEX) > MAX_SHOW_MEX){
                receiveCycle++;
                receivedTextArea.clear(); // Clear the receivedTextArea to avoid excessive content
            }

            // receivedTextArea.appendText("[" + receiveID + "]: " + data + "\n");

            // Split the received data into an array based on space (" ") as a delimiter
            String[] data1=data.split(" ");

            try {
                handleReceivedMessages(data1[1], data1[3]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    // Funzione di esempio, triggerata dall'emergency stop, per verificare l'animazione delle gauge
    @FXML
    public void EmercencyStopAction(){
        if (i_emergencyStopButton.getText().equals("STOP")) {
            i_emergencyStopButton.getStyleClass().remove("button-red");
            i_emergencyStopButton.setText("RUN");
            i_emergencyStopButton.getStyleClass().add("button-green");
            i_contattore1SwitchButton.setSelected(false);
            i_contattore2SwitchButton.setSelected(false);
            setDisableWidgets(true);
            try{
                send(VCU_Pair);
                send(VCU_Velocity);
                send(VCU_Charging);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            i_emergencyStopButton.getStyleClass().remove("button-green");
            i_emergencyStopButton.setText("STOP");
            i_emergencyStopButton.getStyleClass().add("button-red");
            setDisableWidgets(false);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    double randomArray [] = {0,0,0,0,0,0,0,0,0};
                    for(int i = 0; i< 9;i++){
                        randomArray[i]= 10 + (Math.random() * (100 - 10));
                    }

                    o_correnteBatterieGauge.setValue(randomArray[0]);
                    o_tensioneBatterieGauge.setValue(randomArray[1]);
                    o_socGauge.setValue(randomArray[2]);
                    o_coppiaMotoreGauge.setValue(randomArray[3]);
                    o_temperaturaGauge.setValue(randomArray[4]);
                    o_velocitaMotoreGauge.setValue(randomArray[5]);
                    o_correnteCaricatoreGauge.setValue(randomArray[6]);
                    o_tensioneCaricatoreGauge.setValue(randomArray[7]);

                    i_setVelocitaCoppiaGauge.setValue(randomArray[8]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 1000);
    }

    /**
     * Sets the 'disable' property for each widget in the user interface.
     *
     * <p>
     * This method takes a boolean parameter, 'disable,' and sets the 'disable' property for each
     * widget in the user interface accordingly. The widgets include text fields, radio buttons,
     * buttons, labels, and switches related to various aspects of the vehicle control and monitoring.
     * </p>
     *
     * @param disable If true, sets the 'disable' property to true for all widgets; if false, sets
     *                the 'disable' property to false for all widgets.
     */
    public void setDisableWidgets(boolean disable){
        // Set the 'disable' property for each widget
        i_coppiaTextField.setDisable(disable);
        i_ecoRadioButton.setDisable(disable);
        i_chargingModeRadioButton.setDisable(disable);
        i_parkingModeRadioButton.setDisable(disable);
        i_sportRadioButton.setDisable(disable);
        i_correnteTextField.setDisable(disable);
        i_tensioneTextField.setDisable(disable);
        i_caricaSwitchButton.setDisable(disable);
        i_velocitaTextField.setDisable(disable);
        i_coppiaVelocitaSwitchButton.setDisable(disable);
        i_contattore1SwitchButton.setDisable(disable);
        i_contattore2SwitchButton.setDisable(disable);
        i_forwardReverseSwitchButton.setDisable(disable);
        i_gridResSwitchButton.setDisable(disable);
        i_profiloCaricaSwitchButton.setDisable(disable);
        i_customLabel.setDisable(disable);
        i_resLabel.setDisable(disable);
        i_reverseLabel.setDisable(disable);
        i_coppiaDesiderataLabel.setDisable(disable);
        i_velocitaDesiderataLabel.setDisable(disable);
        i_nmLabel.setDisable(disable);
        i_kmLabel.setDisable(disable);
        i_ampereLabel.setDisable(disable);
        i_voltLabel.setDisable(disable);
        i_correnteLabel.setDisable(disable);
        i_velocitaLabel.setDisable(disable);
        i_tensioneLabel.setDisable(disable);
    }

    /**
     * Handles the selection and deselection of RadioButtons in the Sport/Eco group.
     *
     * <p>
     * This method is invoked when a RadioButton in the Sport/Eco group is selected or deselected.
     * It keeps track of the last selected RadioButton to toggle its selection state upon the next click.
     * If the same RadioButton is clicked again, it is deselected; otherwise, the new RadioButton is selected.
     * </p>
     *
     * @throws RuntimeException If an exception occurs during the command sending process.
     */
    @FXML
    private void handleRadioButtonSportEco() {

        // Get the currently selected RadioButton in the Sport/Eco group
        RadioButton currentRadioButton = (RadioButton) sportEco.getSelectedToggle();

        // Check if the current RadioButton is the same as the previously selected one
        if (currentRadioButton == lastSelectedSportEco) {
            // If it is then deselect it
            currentRadioButton.setSelected(false);
            // Reset the last selected RadioButton
            lastSelectedSportEco = null;

            // If it is not then select the current RadioButton
        } else {
            lastSelectedSportEco = currentRadioButton;
        }

        try {
            send(VCU_Pair);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the selection and deselection of RadioButtons in the Parking/Charging group.
     *
     * <p>
     * This method is invoked when a RadioButton in the Parking/Charging group is selected or deselected.
     * It keeps track of the last selected RadioButton to toggle its selection state upon the next click.
     * If the same RadioButton is clicked again, it is deselected; otherwise, the new RadioButton is selected.
     * </p>
     *
     * @throws RuntimeException If an exception occurs during the command sending process.
     */
    @FXML
    private void handleRadioButtonParkingCharging() {

        // Get the currently selected RadioButton in the Parking/Charging group
        RadioButton currentRadioButton = (RadioButton) sportEco.getSelectedToggle();

        // Check if the current RadioButton is the same as the previously selected one
        if (currentRadioButton == lastSelectedParkingCharging) {
            // If it is, deselect it
            currentRadioButton.setSelected(false);
            // Reset the last selected RadioButton
            lastSelectedParkingCharging = null;

            // If it is not then select the current RadioButton
        } else {
            lastSelectedParkingCharging = currentRadioButton;
        }

        try {
            send(VCU_Charging);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
