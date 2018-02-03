package com.pescaworks.ned.expenses.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.controlsfx.control.textfield.TextFields;

import com.pescaworks.ned.expenses.model.Product;
import com.pescaworks.ned.expenses.model.TableEntry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class MainController implements EventHandler<MouseEvent> {
	@FXML
	private TextField itemText;

	@FXML
	private TextField vendorText;

	@FXML
	private Spinner<Integer> quantitySpinner;

	@FXML
	private TextField unitPriceText;

	@FXML
	private Button addButton;

	@FXML
	private TableView<TableEntry> table;

	@FXML
	private TableColumn<Product, String> itemCol;

	@FXML
	private TableColumn<Product, String> vendorCol;

	@FXML
	private TableColumn<Product, String> unitPriceCol;

	@FXML
	private TableColumn<Product, String> quantityCol;

	@FXML
	private TableColumn<Product, String> totalPriceCol;

	@FXML
	private Label totalExpensesText;

	@FXML
	private DatePicker datePicker;

	@FXML
	private Button saveBtn;

	@FXML
	private Button clearBtn;

	private ObservableList<TableEntry> tableEntries;

	Connection connect = null;
	
	private TreeSet<String> items = new TreeSet<>();
	
	private TreeSet<String> vendors = new TreeSet<>();

	@FXML
	private void initialize() {
		tableEntries = FXCollections.observableArrayList();

		// Link columns to objects
		itemCol.setCellValueFactory(new PropertyValueFactory<Product, String>("item"));
		vendorCol.setCellValueFactory(new PropertyValueFactory<Product, String>("vendor"));
		unitPriceCol.setCellValueFactory(new PropertyValueFactory<Product, String>("unitPrice"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<Product, String>("quantity"));
		totalPriceCol.setCellValueFactory(new PropertyValueFactory<Product, String>("totalPrice"));

		table.setItems(tableEntries);

		addButton.setOnMouseClicked(this);

		saveBtn.setOnMouseClicked(this);
		clearBtn.setOnMouseClicked(this);

		table.setOnMouseClicked(this);

		// Set-up quantity spinner
		SpinnerValueFactory<Integer> quantityValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1);

		quantitySpinner.setValueFactory(quantityValue);

		// Set-up date picker
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		};

		datePicker.setConverter(converter);
		datePicker.setValue(LocalDate.now());

		// Set-up total sales text
		totalExpensesText.setText("0");

		try {
			// Setup DB connection
			Class.forName("com.mysql.jdbc.Driver");

			connect = DriverManager.getConnection("jdbc:mysql://localhost/tribu?" + "user=root&password=root");

			populateAutoComplete();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void populateAutoComplete() {

		Statement stateItem = null;
		ResultSet rsItem = null;

		Statement stateVendor = null;
		ResultSet rsVendor = null;

		try {
			stateItem = connect.createStatement();

			// Query distinct items
			rsItem = stateItem.executeQuery("SELECT DISTINCT ITEM FROM tribu.expenses");

			// Save to array list
			while (rsItem.next()) {
				items.add(rsItem.getString("ITEM"));
			}

			TextFields.bindAutoCompletion(itemText, items);

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}

			stateVendor = connect.createStatement();

			// Query distinct vendor
			rsVendor = stateVendor.executeQuery("SELECT DISTINCT VENDOR FROM tribu.expenses");

			// Save to array list
			while (rsVendor.next()) {
				vendors.add(rsVendor.getString("VENDOR"));
			}

			TextFields.bindAutoCompletion(vendorText, vendors);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stateItem != null) {
				try {
					stateItem.close();
				} catch (SQLException e) {
				}
			}

			if (rsItem != null) {
				try {
					rsItem.close();
				} catch (SQLException e) {
				}
			}

			if (stateVendor != null) {
				try {
					stateVendor.close();
				} catch (SQLException e) {
				}
			}

			if (rsVendor != null) {
				try {
					rsVendor.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public void handle(MouseEvent event) {
		Object source = event.getSource();
		if (source == addButton) {
			if (event.getButton() == MouseButton.PRIMARY) {
				String item = itemText.getText();
				String vendor = vendorText.getText();
				int quantity = quantitySpinner.getValue();
				String unitPrice = unitPriceText.getText();

				itemText.setStyle(null);
				unitPriceText.setStyle(null);

				// Check if item and unit price are accomplished
				if (item.isEmpty()) {
					itemText.setStyle("-fx-text-box-border: red");
					event.consume();
				}

				double dblUnitPrice = 0;

				if (unitPrice.isEmpty()) {
					unitPriceText.setStyle("-fx-text-box-border: red");
					event.consume();
				} else {
					try {
						dblUnitPrice = Double.parseDouble(unitPrice);
					} catch (NumberFormatException e) {
						unitPriceText.setStyle("-fx-text-box-border: red");
						event.consume();
					}

				}

				if (event.isConsumed()) {
					return;
				}

				TableEntry entry = new TableEntry();
				entry.setItem(item);
				entry.setVendor(vendor);
				entry.setQuantity(quantity);
				entry.setUnitPrice(dblUnitPrice);

				double totalPrice = quantity * dblUnitPrice;
				entry.setTotalPrice(totalPrice);

				tableEntries.add(entry);
				
				items.add(item);
				TextFields.bindAutoCompletion(itemText, items);
				
				if (!vendor.isEmpty()) {
					vendors.add(vendor);
					TextFields.bindAutoCompletion(vendorText, vendors);
				}

				double totalExpenses = Double.parseDouble(totalExpensesText.getText());
				totalExpenses += totalPrice;
				totalExpensesText.setText("" + totalExpenses);
			}
		} else if (source == saveBtn) {
			System.out.println("Save button");
			if (event.getButton() == MouseButton.PRIMARY) {
				if (connect == null) {
					System.err.println("Connection is null");
					return;
				}

				PreparedStatement psQuery = null;
				ResultSet rsQuery = null;

				PreparedStatement psInsert = null;

				try {

					// Check database for same date and order number
					String query = "SELECT * FROM  tribu.expenses WHERE DATE = ?";
					psQuery = connect.prepareStatement(query);

					psQuery.setDate(1, Date.valueOf(datePicker.getValue()));

					rsQuery = psQuery.executeQuery();

					if (rsQuery.isBeforeFirst()) {
						System.out.println("With duplicate date");
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Existing Record Detected");
						alert.setHeaderText(null);
						alert.setContentText("Record exist with same date. Continue Saving?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() != ButtonType.OK) {
							return;
						}
					}

					System.out.println("Without duplicate date");
					String insert = "INSERT INTO tribu.expenses (DATE, ITEM, VENDOR, QUANTITY,UNIT_PRICE, TOTAL_PRICE) VALUES (?, ?, ?, ?, ?, ?)";
					psInsert = connect.prepareStatement(insert);

					for (TableEntry entry : tableEntries) {
						psInsert.setDate(1, Date.valueOf(datePicker.getValue()));
						psInsert.setString(2, entry.getItem());
						psInsert.setString(3, entry.getVendor());
						psInsert.setInt(4, entry.getQuantity());
						psInsert.setDouble(5, entry.getUnitPrice());
						psInsert.setDouble(6, entry.getTotalPrice());

						psInsert.addBatch();
					}

					int[] results = psInsert.executeBatch();
					System.out.println("Processed " + results.length + " rows.");

					boolean success = true;
					for (int result : results) {
						success = success && (result >= 0);
					}

					Alert alert;
					if (success) {
						alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Success");
						alert.setHeaderText(null);
						alert.setContentText("Successfully saved!");
					} else {
						alert = new Alert(AlertType.ERROR);
						alert.setTitle("Fail");
						alert.setHeaderText(null);
						alert.setContentText("Failed to save data!");
					}

					alert.showAndWait();

					totalExpensesText.setText("0");
					tableEntries.clear();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (psQuery != null) {
						try {
							psQuery.close();
						} catch (SQLException e) {
						}
					}
					if (rsQuery != null) {
						try {
							rsQuery.close();
						} catch (SQLException e) {
						}
					}
					if (psInsert != null) {
						try {
							psInsert.close();
						} catch (SQLException e) {
						}
					}
				}
			}
		} else if (source == clearBtn) {
			if (event.getButton() == MouseButton.PRIMARY) {
				tableEntries.clear();
				totalExpensesText.setText("0");
			}
		} else if (source == table) {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() == 2) {
					TableEntry entry = table.getSelectionModel().getSelectedItem();

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Remove Row");
					alert.setHeaderText(null);
					alert.setContentText("Do you wish to remove row?\n" + entry.toString());

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						double totalExpenses = Double.parseDouble(totalExpensesText.getText());
						totalExpenses -= entry.getTotalPrice();
						totalExpensesText.setText("" + totalExpenses);

						tableEntries.remove(entry);
					}
				}
			}
		}
	}

	public TextField getItemText() {
		return itemText;
	}

	public void setItemText(TextField itemText) {
		this.itemText = itemText;
	}

	public TextField getVendorText() {
		return vendorText;
	}

	public void setVendorText(TextField vendorText) {
		this.vendorText = vendorText;
	}

	public Spinner<Integer> getQuantitySpinner() {
		return quantitySpinner;
	}

	public void setQuantitySpinner(Spinner<Integer> quantitySpinner) {
		this.quantitySpinner = quantitySpinner;
	}

	public TextField getUnitPriceText() {
		return unitPriceText;
	}

	public void setUnitPriceText(TextField unitPriceText) {
		this.unitPriceText = unitPriceText;
	}

	public Button getAddButton() {
		return addButton;
	}

	public void setAddButton(Button addButton) {
		this.addButton = addButton;
	}

	public TableView<TableEntry> getTable() {
		return table;
	}

	public void setTable(TableView<TableEntry> table) {
		this.table = table;
	}

	public TableColumn<Product, String> getItemCol() {
		return itemCol;
	}

	public void setItemCol(TableColumn<Product, String> itemCol) {
		this.itemCol = itemCol;
	}

	public TableColumn<Product, String> getVendorCol() {
		return vendorCol;
	}

	public void setVendorCol(TableColumn<Product, String> vendorCol) {
		this.vendorCol = vendorCol;
	}

	public TableColumn<Product, String> getUnitPriceCol() {
		return unitPriceCol;
	}

	public void setUnitPriceCol(TableColumn<Product, String> unitPriceCol) {
		this.unitPriceCol = unitPriceCol;
	}

	public TableColumn<Product, String> getQuantityCol() {
		return quantityCol;
	}

	public void setQuantityCol(TableColumn<Product, String> quantityCol) {
		this.quantityCol = quantityCol;
	}

	public TableColumn<Product, String> getTotalPriceCol() {
		return totalPriceCol;
	}

	public void setTotalPriceCol(TableColumn<Product, String> totalPriceCol) {
		this.totalPriceCol = totalPriceCol;
	}

	public Label getTotalExpensesText() {
		return totalExpensesText;
	}

	public void setTotalExpensesText(Label totalExpensesText) {
		this.totalExpensesText = totalExpensesText;
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	public Button getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(Button saveBtn) {
		this.saveBtn = saveBtn;
	}

	public Button getClearBtn() {
		return clearBtn;
	}

	public void setClearBtn(Button clearBtn) {
		this.clearBtn = clearBtn;
	}

	public ObservableList<TableEntry> getTableEntries() {
		return tableEntries;
	}

	public void setTableEntries(ObservableList<TableEntry> tableEntries) {
		this.tableEntries = tableEntries;
	}

	public Connection getConnect() {
		return connect;
	}

	public void setConnect(Connection connect) {
		this.connect = connect;
	}
}