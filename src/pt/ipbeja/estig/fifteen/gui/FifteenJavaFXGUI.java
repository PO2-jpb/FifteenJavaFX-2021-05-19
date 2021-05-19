package pt.ipbeja.estig.fifteen.gui;

import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pt.ipbeja.estig.fifteen.model.Direction;
import pt.ipbeja.estig.fifteen.model.FifteenModel;
import pt.ipbeja.estig.fifteen.model.Move;
import pt.ipbeja.estig.fifteen.model.Position;

/**
 * The fifteen main view
 * 
 * @author João Paulo Barros e Rui Pais
 * @version 2014/05/19 - 2016/04/03 - 2017/04/19 - 2019/05/06 - 2021/05/18
 */
public class FifteenJavaFXGUI extends Application implements View {
	private final String ICON_FILE = "/resources/images/puzzle15.jpg";
	private FifteenModel model;

	private List<PieceJFXButton> buttons;
	private Button solveButton;
	private GridPane panBtns;
	private Label timeLabel;
	private static Map<KeyCode, Direction> directionMap = new HashMap<>();
	static {
		directionMap.put(KeyCode.UP, Direction.UP);
		directionMap.put(KeyCode.DOWN, Direction.DOWN);
		directionMap.put(KeyCode.LEFT, Direction.LEFT);
		directionMap.put(KeyCode.RIGHT, Direction.RIGHT);
	}

	/**
	 * Create window with board
	 */
	public FifteenJavaFXGUI() {
		this.model = null;
		this.buttons = null;
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.createModel();
		this.mixModel();

		Scene scnMain = this.createScene();

		stage.setTitle("Fifteen Puzzle");
		this.setAppIcon(stage, ICON_FILE);
		stage.setScene(scnMain);
		stage.show();

		this.model.startTimer();
	}

	/**
	 * Executed on exit to stop all threads
	 */
	@Override
	public void stop() {
		System.out.println("END");
		System.exit(0);
	}

	private void setAppIcon(Stage stage, String filename) {
		try {
			Image ico = new Image(filename);
			stage.getIcons().add(ico);
		} catch (Exception ex) {
		}
	}

	private Pane createButtonsUI() {
		int nRows = FifteenModel.N_LINES;
		int nCols = FifteenModel.N_COLS;
		this.panBtns = new GridPane();
		this.panBtns.setAlignment(Pos.CENTER);

		this.buttons = new ArrayList<>();
		for (int row = 0; row < nRows; row++) {
			for (int col = 0; col < nCols; col++) {
				Position pos = new Position(row, col);
				String text = this.model.pieceTextAt(pos);
				PieceJFXButton b = new PieceJFXButton(text, pos);
				this.panBtns.add(b, col, row);
				this.buttons.add(b);
				b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				//b.setPadding(new Insets(5));
				b.setPrefHeight(Double.MAX_VALUE);
				b.setPrefSize(100, 100);
				b.setOnMouseClicked(this::handle);
				GridPane.setVgrow(b, Priority.ALWAYS);
				GridPane.setHgrow(b, Priority.ALWAYS);
			}
		}
		return panBtns;
	}

	/**
	 * Handle button press by asking the model to execute the respective actions
	 * The model is then responsible to notify this (and other) views
	 */
	public void handle(MouseEvent e) {
		PieceJFXButton btn = (PieceJFXButton) e.getSource();
		Position pos = btn.getPosition();
		model.pieceSelected(pos); // inform model
	}

	void setKeyHandle(Scene scnMain) {
		scnMain.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				model.keyPressed(directionMap.get(event.getCode()));
			};
		});
	}

	private Scene createScene() {
		VBox vbxMain = new VBox();
		this.solveButton = new Button("Solve!");
		this.solveButton.setMaxWidth(Integer.MAX_VALUE);
		this.solveButton.setStyle("-fx-background-color: #ffff33; ");
		this.solveButton.setOnAction(event -> {
			panBtns.setDisable(true);
			this.solveButton.setDisable(true);
			model.solve();
		});
		this.timeLabel = new Label(this.model.getTimerValue() + "");
		vbxMain.getChildren().addAll(solveButton, this.timeLabel);
		vbxMain.getChildren().addAll(this.createButtonsUI());
		Scene scnMain = new Scene(vbxMain);
		this.setKeyHandle(scnMain);

		return scnMain;
	}

	private void createModel() {
		this.model = new FifteenModel(this);
	}

	/**
	 * Makes a number of moves to mix the puzzle. The mix is not very smart as
	 * the moves can be symmetric in consecutive moments.
	 */
	private void mixModel() {
		this.model.mix(5, 10);
	}

	/**
	 * Updates the pieces content by asking the model
	 */
	private void updateAllLayout() {
		for (PieceJFXButton b : this.buttons) {
			String btnText = this.model.pieceTextAt(b.getPosition());
			b.setTextAndImage(btnText);
		}
		this.timeLabel.setText(this.model.getTimerValue() + "");
		this.solveButton.setDisable(false);
	}

	public void updateLayoutAfterMove(Move lastMove) {
		if (lastMove != null) {
			int line1 = lastMove.getBegin().getLine();
			int col1 = lastMove.getBegin().getCol();
			PieceJFXButton b1 = this.buttons.get(line1 * FifteenModel.N_COLS + col1);
			String text1 = b1.getText();

			int line2 = lastMove.getEnd().getLine();
			int col2 = lastMove.getEnd().getCol();
			PieceJFXButton b2 = this.buttons.get(line2 * FifteenModel.N_COLS + col2);
			String text2 = b2.getText();

			b1.setTextAndImage(text2);
			b2.setTextAndImage(text1);
		}
	}

	@Override
	public void notifyView(Move lastMove, Boolean wins, int timerValue) {
		Platform.runLater(() -> {


			if (lastMove != null) {
				this.updateLayoutAfterMove(lastMove);
			}


			if (wins) {
				this.model.stopTimer();
				new Alert(AlertType.INFORMATION, "You win! ").showAndWait();
				this.mixModel();
				this.panBtns.setDisable(false);
				this.model.startTimer();
				this.updateAllLayout();
			}

			this.timeLabel.setText(timerValue + "");
		});
	}

	/**
	 * Start program
	 * 
	 * @param args
	 *            currently not used
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}



}
