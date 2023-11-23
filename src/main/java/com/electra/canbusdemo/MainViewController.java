package com.electra.canbusdemo;

import com.electra.canbusdemo.CANbus.CANbus_Controller;
import com.electra.canbusdemo.CANbus.Notifiable;
import eu.hansolo.medusa.Clock;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.*;
import eu.hansolo.medusa.Gauge;

import java.util.HexFormat;

import static com.electra.canbusdemo.CANbus.CANbus_Controller.getCanBusController;

public class MainViewController implements Notifiable {
    @FXML
    private TextField idTextField;
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextArea receivedTextArea;
    @FXML
    private TextArea sentTextArea;

    // INPUT WIDGETS -----------------------------------------------------------
    @FXML
    private ToggleSwitch i_forwardReverseSwitchButton;
    @FXML
    private ToggleSwitch i_coppiaVelocitaSwitchButton;
    @FXML
    private TextField i_coppiaTextField;
    @FXML
    private TextField i_velocitaTextField;
    @FXML
    private RadioButton i_sportRadioButton;
    @FXML
    private RadioButton i_ecoRadioButton;
    @FXML
    private RadioButton i_parkingModeRadioButton;
    @FXML
    private RadioButton i_chargingModeRadioButton;
    @FXML
    private TextField i_correnteTextField;
    @FXML
    private TextField i_tensioneTextField;
    @FXML
    private ToggleSwitch i_caricaSwitchButton;
    @FXML
    private ToggleSwitch i_contattore1SwitchButton;
    @FXML
    private ToggleSwitch i_contattore2SwitchButton;
    @FXML
    private  Gauge i_velocitaGauge;


    // OUTPUT WIDGETS -----------------------------------------------------------
    @FXML
    private  Gauge o_tensioneBatterieGauge;
    @FXML
    private  Gauge o_correnteBatterieGauge;
    @FXML
    private  Gauge o_batteryGauge;
    @FXML
    private  Gauge o_tensioneCaricatoreGauge;
    @FXML
    private  Gauge o_correnteCaricatoreGauge;
    @FXML
    private  Gauge o_velocitaMotoreGauge;
    @FXML
    private  Gauge o_coppiaMotoreGauge;
    @FXML
    private Gauge o_temperaturaMotoreGauge;
    @FXML
    private  StatusBar o_modalitaStatusButton;
    @FXML
    private StatusBar o_statusButtonEmergencyStop;
    @FXML
    private  StatusBar o_statusButtonContattore1;
    @FXML
    private  StatusBar o_statusButtonContattore2;
    @FXML
    private Button o_emergencyStopButton;
    @FXML
    private Button o_saveButton;
    @FXML
    private TextField o_pathFileTextFied;
    @FXML
    private TextField data0TextField, data1TextField, data2TextField, data3TextField, data4TextField,
            data5TextField, data6TextField, data7TextField;
    @FXML
    private ComboBox<String> deviceComboBox;

    private ObservableList<String> canBusDevice_List = FXCollections.observableArrayList(); //Observable = c'è un osservatore che sa quando viene modificata

    private CANbus_Controller canBusController;
    private String canBusDevice;
    private final int MAX_SHOW_MEX = 500;
    private int sendID = 0, sendCycle = 0, receiveID = 0, receiveCycle = 0;

    private void checkTextFieldInput(KeyEvent keyEvent, TextField textField){ //Verifica che il carattere sia tra quelli concessi
        String newChar = keyEvent.getCharacter().toUpperCase();
        String keyFilter = "0123456789ABCDEF";

        if (!keyFilter.contains(newChar) || textField.getText().length() > 1) { //Verifica che non ci siano più di due caratteri
            keyEvent.consume(); //elimina il carattere
        }
    }

    //Aggiunto Giada
    private void checkNumberFieldInput(KeyEvent keyEvent, TextField textField){ //Verifica che il carattere sia tra quelli concessi
        String newChar = keyEvent.getCharacter().toUpperCase();
        String keyFilter = "0123456789";

        if (!keyFilter.contains(newChar)) { //Verifica che non ci siano più di due caratteri
            keyEvent.consume(); //elimina il carattere
        }
    }

    @FXML
    public void initialize (){ //Called automatically by the framework
     //   sendButton.setDisable(true);  //Il pulsante di invio è disabilitato perché non è aperta la connessione
        canBusDevice_List.addAll(); //Lista di dispositivi collegati
        deviceComboBox.setItems(canBusDevice_List); //menu a tendina in alto a sinistra con dispositivi collegati
        canBusController = getCanBusController();  //CanBUS viene inizializzata come singleton
        canBusController.setNotifiable(this); //Se succede qualcosa notificalo a quest'oggetto

        //Aggiunto Giada
        i_velocitaTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkNumberFieldInput(keyEvent, i_velocitaTextField);
        });

        i_coppiaTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkNumberFieldInput(keyEvent, i_coppiaTextField);
        });

        /*
        data0TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> { //Invocato ogni volta che viene premuto un tasto in quella casella di testo
           checkTextFieldInput(keyEvent, data0TextField);  //evita di scrivere lettere oltre 0123456789abcdef
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
*/
        deviceComboBox.setOnMouseClicked(event -> { //set = solo un listener per l'evento
            canBusDevice_List.removeAll(canBusDevice_List); //Pulisce la lista  (menu in alto a sinistra
            canBusDevice_List.addAll(canBusController.getAvailableHandlers()); //aggiorna la lista con i dispositivi disponibili
        });

        //Aggiunto gGiada
        i_forwardReverseSwitchButton.setOnMouseClicked(event -> {
            if(i_forwardReverseSwitchButton.isSelected()){
                i_sportRadioButton.setDisable(true);
                i_ecoRadioButton.setDisable(true);
            }
            else{
                i_sportRadioButton.setDisable(false);
                i_ecoRadioButton.setDisable(false);
            }

        });
    }

    @FXML
    void connectButtonAction() { //quando si preme sul pulsante connect
        if(connectButton.getText().equals("Connect")){ //Se la scritta sul pulsante è connect
            canBusDevice = deviceComboBox.getValue(); //Assegna il dispositivo
            if (!canBusController.isConnected()) { //Se il dispositivo non è connesso
                if (canBusDevice == null || canBusDevice.equals("")) { //Controlla se nella combobox è null o una stringa vuota
                        fireAlarm(Alert.AlertType.ERROR, "Warning", "Please select a valid CAN bus adapter Device.");
                        return;
                    }
                }

            if (!canBusController.connect(canBusDevice)) { //true se la connessione va a buon fine, false se ci sono problemi con la connessione
                String mex = "Connection with the CANbus failed!!\n";
                mex += "Possible causes:\n";
                mex += "  - the selected device is wrong;\n" +
                        "  - the device is not connected to the USB port;\n\n";
                mex += "If the problem persist, try to unplug and replug the device.";

                fireAlarm(Alert.AlertType.ERROR, "Connection ERROR!!", mex);
                return;
            }

         //   sendButton.setDisable(false); //abilita il pulsante di invio del messaggio
            connectButton.setText("Disconnect"); //La scritta sul pulsante diventa disconnect
        } else { //Se si preme sul pulsante e la scritta non è "connect" (è disconnect)
            canBusController.disconnect();  //disconnette il dispositivo
         //   sendButton.setDisable(true);
            connectButton.setText("Connect");
        }

    }

    @FXML
    public void clearAllButtonAction(){ //Azione del pulsante clear all
        receiveID = receiveCycle = sendID = sendCycle = 0; //reinizializza tutte le variabili
        receivedTextArea.clear(); //pulisce le text area
        sentTextArea.clear();
    }

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
        sentTextArea.appendText("[" + sendID + "]: " + "ID: " + idTextField.getText().toUpperCase() + " data: " +   //il messaggio viene aggiunto alla text area
                HexFormat.of().formatHex(data).toUpperCase() + "\n");
    }

    public void send(String id) throws Exception {
        byte[] data;
       switch (id) {
           case "0x222": {
               data = new byte[]
                       {
                               (byte) HexFormat.fromHexDigits("0"),
                               (byte) HexFormat.fromHexDigits(i_forwardReverseSwitchButton.isSelected() ? "0" : "1"),
                               (byte) HexFormat.fromHexDigits(i_forwardReverseSwitchButton.isSelected() ? "1" : "0"),
                               (byte) HexFormat.fromHexDigits(i_velocitaTextField.getText()),
                               (byte) HexFormat.fromHexDigits(i_contattore1SwitchButton.isSelected() ? "0" : "1"),
                               (byte) HexFormat.fromHexDigits("0"),
                               (byte) HexFormat.fromHexDigits("0"),
                               (byte) HexFormat.fromHexDigits("0")
                       };
           }
               break;
           default:
               data = new byte[]{};

              break;
       }
           canBusController.sendCommand(HexFormat.fromHexDigits(id), data);

    }

    private void fireAlarm(Alert.AlertType type, String title, String contentText){ //Genera una finestra di "allarme"
        Alert alert = new Alert(type); //type cambia l'icona, title il nome della finestra, contentText il testo di allarme
        alert.initOwner(MainApplication.stage); //Evita che l'utente possa interagire con l'applicazione finchè la finestra dell allarme è aperta
        alert.setTitle(title);
        alert.setContentText(contentText);
        //alert.setX(owner.getX());
        alert.showAndWait();
    }

    @Override
    public void _notify(String data) { //override del metodo notify dell'interfaccia notifiable
        Platform.runLater(() -> { //run later perché ci sono due thread diversi per inviare e ricevere
            receiveID++;
            if (receiveID-(receiveCycle*MAX_SHOW_MEX) > MAX_SHOW_MEX){
                receiveCycle++;
                receivedTextArea.clear();
            }
            receivedTextArea.appendText("[" + receiveID + "]: " + data + "\n"); //ID e data da utilizzare
        });
    }
}