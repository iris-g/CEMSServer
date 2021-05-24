package gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.CEMSServer;
import server.ServerController;

public class ServerUIController implements Observer {

	private int port;
	private FXMLLoader loader;
	@SuppressWarnings("unused")
	private CEMSServer server;

	public void setServer(CEMSServer server) {
		this.server = server;
		server.addObserver(loader.getController());
	}

    @FXML
    private JFXTextArea serverLog;

    @FXML
    private AnchorPane questionAnchor;

    @FXML
    private AnchorPane insideQuestionAnchor;

    @FXML
    private JFXTextField portTxt;

    @FXML
    private Label connectedLbl;

    @FXML
    private AnchorPane questionAnchor1;

    @FXML
    private AnchorPane insideQuestionAnchor1;

    @FXML
    private JFXTextField ipTxt;

    @FXML
    private JFXTextField schemaTxt;

    @FXML
    private JFXTextField usernameTxt;

    @FXML
    private JFXPasswordField passwordTxt;

    @FXML
    private JFXButton connectBtn;

    @FXML
    private JFXButton disconnectBtn;

	public JFXTextField getIpTxt() {
		return ipTxt;
	}

	public JFXTextField getSchemaTxt() {
		return schemaTxt;
	}

	public JFXTextField getUsernameTxt() {
		return usernameTxt;
	}

	public JFXPasswordField getPasswordTxt() {
		return passwordTxt;
	}

	/**
	 * when clicking on connect the server will start and start listen to
	 * connections
	 * 
	 * @param event
	 */
	@FXML
	void clickConnect(Event event) {
		port = Integer.parseInt(portTxt.getText());
		try {
			ServerController.runServer(port);
		} catch (IOException e) {
			writeToLog("Could not run server");
			disconnectSet();
		}
		try {
			try {
				ServerController.connectToDB(ipTxt.getText(), schemaTxt.getText(), usernameTxt.getText(),
						passwordTxt.getText());
				writeToLog("Driver definition succeed");
				writeToLog("SQL connection succeed");
				connectSet();
			} catch (ClassNotFoundException e) {
				writeToLog("Driver definition failed");
				disconnectSet();
			}
		} catch (SQLException ex) {
			writeToLog("SQLException: " + ex.getMessage());
			writeToLog("SQLState: " + ex.getSQLState());
			writeToLog("VendorError: " + ex.getErrorCode());
			disconnectSet();
		}
	}

	/**
	 * when clicking disconnect it will close the connection to the server
	 * 
	 * @param event
	 */
	@FXML
	void clickDisconnect(MouseEvent event) {
		disconnectSet();
	}

	/**
	 * this function contains the visual changes when clicking connect
	 */
	private void connectSet() {
		connectBtn.setVisible(false);
		disconnectBtn.setVisible(true);
		passwordTxt.setDisable(true);
		usernameTxt.setDisable(true);
		ipTxt.setDisable(true);
		schemaTxt.setDisable(true);
		portTxt.setDisable(true);
		connectedLbl.setText("(Connected)");
		connectedLbl.setStyle("-fx-text-fill: green;");
	}

	/**
	 * this function contains the visual and functional changes when clicking
	 * disconnect
	 */
	private void disconnectSet() {
		try {
			ServerController.closeServer();
		} catch (IOException e) {
			writeToLog(e.getMessage());
		}
		disconnectBtn.setVisible(false);
		connectBtn.setVisible(true);
		passwordTxt.setDisable(false);
		usernameTxt.setDisable(false);
		ipTxt.setDisable(false);
		portTxt.setDisable(false);
		schemaTxt.setDisable(false);
		connectedLbl.setText("(Not Connected)");
		connectedLbl.setStyle("-fx-text-fill: red;");
	}

	/**
	 * @return instance of the fxml loader
	 */
	public FXMLLoader getLoader() {
		return loader;
	}

	public void start(Stage stage) {
		try {
			loader = new FXMLLoader(getClass().getResource("ServerUI.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add("util/style.css");
			stage.setTitle("CEMS Server");
			stage.setResizable(false);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param msg - this message will be written to the server log when called
	 */
	private void writeToLog(String msg) {
		serverLog.appendText("> " + msg + "\n");
	}

	/**
	 * this function will get the messages from the server to write them to the log
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		writeToLog((String) arg1);
	}

}