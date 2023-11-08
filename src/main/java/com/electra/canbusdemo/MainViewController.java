package com.electra.canbusdemo;

import com.electra.canbusdemo.CANbus.CANbus_Controller;
import com.electra.canbusdemo.CANbus.Notifiable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.util.HexFormat;

import static com.electra.canbusdemo.CANbus.CANbus_Controller.getCanBusController;

public class MainViewController implements Notifiable {
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextArea receivedTextArea;
    @FXML
    private TextArea sentTextArea;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField data0TextField, data1TextField, data2TextField, data3TextField, data4TextField,
            data5TextField, data6TextField, data7TextField;
    @FXML
    private ComboBox<String> deviceComboBox;

    private ObservableList<String> canBusDevice_List = FXCollections.observableArrayList();

    private CANbus_Controller canBusController;
    private String canBusDevice;
    private final int MAX_SHOW_MEX = 500;
    private int sendID = 0, sendCycle = 0, receiveID = 0, receiveCycle = 0;

    private void checkTextFieldInput(KeyEvent keyEvent, TextField textField){
        String newChar = keyEvent.getCharacter().toUpperCase();
        String keyFilter = "0123456789ABCDEF";

        if (!keyFilter.contains(newChar) || textField.getText().length() > 1) {
            keyEvent.consume();
        }
    }

    @FXML
    public void initialize (){
        sendButton.setDisable(true);
        canBusDevice_List.addAll();
        deviceComboBox.setItems(canBusDevice_List);
        canBusController = getCanBusController();
        canBusController.setNotifiable(this);
        data0TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
           checkTextFieldInput(keyEvent, data0TextField);
        });

        data1TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data1TextField);
        });

        data2TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data2TextField);
        });

        data3TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data3TextField);
        });

        data4TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data4TextField);
        });

        data5TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data5TextField);
        });

        data6TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data6TextField);
        });

        data7TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data7TextField);
        });

        deviceComboBox.setOnMouseClicked(event -> {
            canBusDevice_List.removeAll(canBusDevice_List);
            canBusDevice_List.addAll(canBusController.getAvailableHandlers());
        });
    }

    @FXML
    void connectButtonAction() {
        if(connectButton.getText().equals("Connect")){
            canBusDevice = deviceComboBox.getValue();
            if (!canBusController.isConnected()) {
                if (canBusDevice == null || canBusDevice.equals("")) {
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Please select a valid CAN bus adapter Device.");
                        return;
                    }
                }

            if (!canBusController.connect(canBusDevice)) {
                String mex = "Connection with the CANbus failed!!\n";
                mex += "Possible causes:\n";
                mex += "  - the selected device is wrong;\n" +
                        "  - the device is not connected to the USB port;\n\n";
                mex += "If the problem persist, try to unplug and replug the device.";

                fireAlarm(Alert.AlertType.ERROR, "Connection ERROR!!", mex);
                return;
            }

            sendButton.setDisable(false);
            connectButton.setText("Disconnect");
        } else {
            canBusController.disconnect();
            sendButton.setDisable(true);
            connectButton.setText("Connect");
        }

    }

    @FXML
    public void clearAllButtonAction(){
        receiveID = receiveCycle = sendID = sendCycle = 0;
        receivedTextArea.clear();
        sentTextArea.clear();
    }

    @FXML
    public void sendButtonAction() throws Exception {
        byte data[] =
                {
                        (byte) HexFormat.fromHexDigits(data0TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data1TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data2TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data3TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data4TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data5TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data6TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data7TextField.getText())
                };

        canBusController.sendCommand(HexFormat.fromHexDigits(idTextField.getText()), data);
        sendID++;
        if(sendID-(sendCycle*MAX_SHOW_MEX) > MAX_SHOW_MEX){
            sendCycle++;
            sentTextArea.clear();
        }
        sentTextArea.appendText("[" + sendID + "]: " + "ID: " + idTextField.getText().toUpperCase() + " data: " +
                HexFormat.of().formatHex(data).toUpperCase() + "\n");
    }

    private void fireAlarm(Alert.AlertType type, String title, String contentText){
        Alert alert = new Alert(type);
        alert.initOwner(MainApplication.stage);
        alert.setTitle(title);
        alert.setContentText(contentText);
        //alert.setX(owner.getX());
        alert.showAndWait();
    }

    @Override
    public void _notify(String data) {
        Platform.runLater(() -> {
            receiveID++;
            if (receiveID-(receiveCycle*MAX_SHOW_MEX) > MAX_SHOW_MEX){
                receiveCycle++;
                receivedTextArea.clear();
            }
            receivedTextArea.appendText("[" + receiveID + "]: " + data + "\n");
        });
    }
}