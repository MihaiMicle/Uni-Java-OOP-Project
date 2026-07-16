package ui;

import domain.Client;
import domain.Session;
import filter.*;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import service.ClientService;
import service.SessionService;
import ui.command.ClientCommands;
import ui.command.SessionCommands;
import ui.command.UndoRedoManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class GymUI extends Application {
    public static ClientService clientService;
    public static SessionService sessionService;

    private final UndoRedoManager undoManager = new UndoRedoManager();

    private TableView<Client> clientTable = new TableView<>();
    private TableView<Session> sessionTable = new TableView<>();
    private TextArea reportArea = new TextArea();

    // Initializes the layout and starts the application
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        Button btnUndo = new Button("Undo");
        Button btnRedo = new Button("Redo");

        btnUndo.setOnAction(e -> {
            try {
                undoManager.undo();
                refreshTables();
            } catch (Exception ex) { showAlert("Undo Error", ex.getMessage()); }
        });

        btnRedo.setOnAction(e -> {
            try {
                undoManager.redo();
                refreshTables();
            } catch (Exception ex) { showAlert("Redo Error", ex.getMessage()); }
        });

        topMenu.getChildren().addAll(btnUndo, btnRedo);
        root.setTop(topMenu);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createClientTab(), createSessionTab(), createReportsTab());
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Gym Management System");
        stage.setScene(scene);
        stage.show();

        refreshTables();
    }

    // Builds the tab for managing Client data
    private Tab createClientTab() {
        Tab tab = new Tab("Clients");
        tab.setClosable(false);
        BorderPane pane = new BorderPane();

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(10));

        TextField txtId = new TextField(); txtId.setPromptText("ID");
        TextField txtFn = new TextField(); txtFn.setPromptText("First Name");
        TextField txtLn = new TextField(); txtLn.setPromptText("Last Name");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Phone");

        form.addRow(0, new Label("ID:"), txtId, new Label("First Name:"), txtFn);
        form.addRow(1, new Label("Last Name:"), txtLn, new Label("Email:"), txtEmail);
        form.addRow(2, new Label("Phone:"), txtPhone);

        Button btnAdd = new Button("Add Client");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnClear = new Button("Clear Fields");

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        form.add(buttons, 1, 3, 3, 1);

        Separator separator = new Separator();
        form.add(separator, 0, 4, 4, 1);

        ComboBox<String> comboFilter = new ComboBox<>();
        comboFilter.getItems().addAll("ID", "First Name", "Last Name", "Email", "Phone");
        comboFilter.setValue("First Name");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Search terms...");

        Button btnFilter = new Button("Filter");
        Button btnReset = new Button("Reset / Show All");

        HBox filterBox = new HBox(10, new Label("Filter By:"), comboFilter, txtSearch, btnFilter, btnReset);
        form.add(filterBox, 0, 5, 5, 1);

        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getID()).asObject());

        TableColumn<Client, String> colFn = new TableColumn<>("First Name");
        colFn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstname()));

        TableColumn<Client, String> colLn = new TableColumn<>("Last Name");
        colLn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastname()));

        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<Client, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));

        clientTable.getColumns().clear();
        clientTable.getColumns().addAll(colId, colFn, colLn, colEmail, colPhone);

        btnAdd.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                undoManager.executeCommand(new ClientCommands.AddCommand(
                        clientService, id, txtFn.getText(), txtLn.getText(), txtEmail.getText(), txtPhone.getText()
                ));
                refreshTables();
                clearFields(txtId, txtFn, txtLn, txtEmail, txtPhone);
            } catch (NumberFormatException ex) { showAlert("Input Error", "ID must be a number!"); }
            catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        btnDelete.setOnAction(e -> {
            Client selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Selection Error", "Please select a client to delete."); return; }
            try {
                undoManager.executeCommand(new ClientCommands.DeleteCommand(clientService, selected));
                refreshTables();
                clearFields(txtId, txtFn, txtLn, txtEmail, txtPhone);
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        btnUpdate.setOnAction(e -> {
            Client selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Selection Error", "Select a client to update.");
                return;
            }
            showClientUpdateDialog(selected);
        });

        btnClear.setOnAction(e -> {
            clearFields(txtId, txtFn, txtLn, txtEmail, txtPhone);
            clientTable.getSelectionModel().clearSelection();
        });

        clientTable.setOnMouseClicked(e -> {
            Client c = clientTable.getSelectionModel().getSelectedItem();
            if (c != null) {
                txtId.setText(String.valueOf(c.getID()));
                txtFn.setText(c.getFirstname());
                txtLn.setText(c.getLastname());
                txtEmail.setText(c.getEmail());
                txtPhone.setText(c.getPhone());
            }
        });

        btnFilter.setOnAction(e -> {
            String criteria = comboFilter.getValue();
            String text = txtSearch.getText();
            ArrayList<Client> filteredList = new ArrayList<>();

            try {
                if (criteria.equals("ID")) {
                    int id = Integer.parseInt(text);
                    filteredList = clientService.filter(new FilterByID<>(id));
                } else if (criteria.equals("First Name")) {
                    filteredList = clientService.filter(new FilterByFirstname(text));
                } else if (criteria.equals("Last Name")) {
                    filteredList = clientService.filter(new FilterByLastname(text));
                } else if (criteria.equals("Email")) {
                    filteredList = clientService.filter(new FilterByEmail(text));
                } else if (criteria.equals("Phone")) {
                    filteredList = clientService.filter(new FilterByPhone(text));
                }
                clientTable.setItems(FXCollections.observableArrayList(filteredList));
                clientTable.refresh();
            } catch (NumberFormatException ex) {
                showAlert("Filter Error", "ID must be a number.");
            }
        });

        btnReset.setOnAction(e -> {
            txtSearch.clear();
            refreshTables();
            clearFields(txtId, txtFn, txtLn, txtEmail, txtPhone);
        });

        pane.setTop(form);
        pane.setCenter(clientTable);
        tab.setContent(pane);
        return tab;
    }

    // Builds the tab for managing Session data
    private Tab createSessionTab() {
        Tab tab = new Tab("Sessions");
        tab.setClosable(false);
        BorderPane pane = new BorderPane();

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(10));

        TextField txtId = new TextField(); txtId.setPromptText("ID");
        TextField txtClientId = new TextField(); txtClientId.setPromptText("Client ID");
        TextField txtDate = new TextField(); txtDate.setPromptText("dd.mm.yyyy");
        TextField txtTime = new TextField(); txtTime.setPromptText("hh:mm");
        TextField txtDesc = new TextField(); txtDesc.setPromptText("Workout Description");

        form.addRow(0, new Label("ID:"), txtId, new Label("Client ID:"), txtClientId);
        form.addRow(1, new Label("Date:"), txtDate, new Label("Time:"), txtTime);
        form.addRow(2, new Label("Description:"), txtDesc);

        Button btnAdd = new Button("Add Session");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnClear = new Button("Clear Fields");

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        form.add(buttons, 1, 3, 3, 1);

        Separator separator = new Separator();
        form.add(separator, 0, 4, 4, 1);

        ComboBox<String> comboFilter = new ComboBox<>();
        comboFilter.getItems().addAll("ID", "Client ID", "Date", "Time", "Workout");
        comboFilter.setValue("Workout");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Search terms...");

        Button btnFilter = new Button("Filter");
        Button btnReset = new Button("Reset / Show All");

        HBox filterBox = new HBox(10, new Label("Filter By:"), comboFilter, txtSearch, btnFilter, btnReset);
        form.add(filterBox, 0, 5, 5, 1);

        TableColumn<Session, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getID()).asObject());

        TableColumn<Session, Integer> colCId = new TableColumn<>("Client ID");
        colCId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getClientID()).asObject());

        TableColumn<Session, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDate()));

        TableColumn<Session, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTime()));

        TableColumn<Session, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getWorkoutDescription()));

        sessionTable.getColumns().clear();
        sessionTable.getColumns().addAll(colId, colCId, colDate, colTime, colDesc);

        btnAdd.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                int clientId = Integer.parseInt(txtClientId.getText());

                undoManager.executeCommand(new SessionCommands.AddCommand(
                        sessionService, id, clientId, txtDate.getText(), txtTime.getText(), txtDesc.getText()
                ));
                refreshTables();
                clearFields(txtId, txtClientId, txtDate, txtTime, txtDesc);
            } catch (NumberFormatException ex) { showAlert("Input Error", "ID and Client ID must be numbers!"); }
            catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        btnDelete.setOnAction(e -> {
            Session selected = sessionTable.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Selection Error", "Please select a session to delete."); return; }
            try {
                undoManager.executeCommand(new SessionCommands.DeleteCommand(sessionService, selected));
                refreshTables();
                clearFields(txtId, txtClientId, txtDate, txtTime, txtDesc);
            } catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        btnUpdate.setOnAction(e -> {
            Session selected = sessionTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Selection Error", "Select a session to update.");
                return;
            }
            showSessionUpdateDialog(selected);
        });

        btnClear.setOnAction(e -> {
            clearFields(txtId, txtClientId, txtDate, txtTime, txtDesc);
            sessionTable.getSelectionModel().clearSelection();
        });

        sessionTable.setOnMouseClicked(e -> {
            Session s = sessionTable.getSelectionModel().getSelectedItem();
            if (s != null) {
                txtId.setText(String.valueOf(s.getID()));
                txtClientId.setText(String.valueOf(s.getClientID()));
                txtDate.setText(s.getDate());
                txtTime.setText(s.getTime());
                txtDesc.setText(s.getWorkoutDescription());
            }
        });

        btnFilter.setOnAction(e -> {
            String criteria = comboFilter.getValue();
            String text = txtSearch.getText();
            ArrayList<Session> filteredList = new ArrayList<>();

            try {
                if (criteria.equals("ID")) {
                    int id = Integer.parseInt(text);
                    filteredList = sessionService.filter(new FilterByID<>(id));
                } else if (criteria.equals("Client ID")) {
                    int id = Integer.parseInt(text);
                    filteredList = sessionService.filter(new FilterByClientID(id));
                } else if (criteria.equals("Date")) {
                    filteredList = sessionService.filter(new FilterByDate(text));
                } else if (criteria.equals("Time")) {
                    filteredList = sessionService.filter(new FilterByTime(text));
                } else if (criteria.equals("Workout")) {
                    filteredList = sessionService.filter(new FilterByWorkout(text));
                }
                sessionTable.setItems(FXCollections.observableArrayList(filteredList));
                sessionTable.refresh();
            } catch (NumberFormatException ex) {
                showAlert("Filter Error", "ID fields must be numeric.");
            }
        });

        btnReset.setOnAction(e -> {
            txtSearch.clear();
            refreshTables();
            clearFields(txtId, txtClientId, txtDate, txtTime, txtDesc);
        });

        pane.setTop(form);
        pane.setCenter(sessionTable);
        tab.setContent(pane);
        return tab;
    }

    // Builds the tab for viewing statistical Reports
    private Tab createReportsTab() {
        Tab tab = new Tab("Reports");
        tab.setClosable(false);
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Button btn1 = new Button("Most Active Client");
        Button btn2 = new Button("Busiest Day");
        Button btn3 = new Button("Workout Popularity");
        Button btn4 = new Button("Inactive Clients");
        Button btn5 = new Button("Sessions per Month");

        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);

        btn1.setOnAction(e -> {
            Client c = sessionService.getMostActiveClient();
            reportArea.setText(c != null ? "Most Active Client:\n" + c : "No data found.");
        });

        btn2.setOnAction(e -> reportArea.setText("Busiest Day: " + sessionService.getBusiestDay()));

        btn3.setOnAction(e -> {
            Map<String, Integer> pop = sessionService.getWorkoutPopularity();
            reportArea.setText("Workout Popularity:\n" + pop.toString());
        });

        btn4.setOnAction(e -> reportArea.setText("Inactive Clients:\n" + sessionService.getInactiveClients()));

        btn5.setOnAction(e -> reportArea.setText("Sessions Per Month:\n" + sessionService.getSessionsPerMonth()));

        vbox.getChildren().addAll(new Label("Available Reports:"), new HBox(10, btn1, btn2, btn3, btn4, btn5), reportArea);
        tab.setContent(vbox);
        return tab;
    }

    // Opens a popup dialog to update Client details
    private void showClientUpdateDialog(Client client) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Update Client");
        dialog.setHeaderText("Update details for Client ID: " + client.getID());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fn = new TextField(client.getFirstname());
        TextField ln = new TextField(client.getLastname());
        TextField em = new TextField(client.getEmail());
        TextField ph = new TextField(client.getPhone());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(fn, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(ln, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(em, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(ph, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new Client(client.getID(), fn.getText(), ln.getText(), em.getText(), ph.getText());
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();

        result.ifPresent(newClient -> {
            try {
                undoManager.executeCommand(new ClientCommands.UpdateCommand(
                        clientService, client, client.getID(),
                        newClient.getFirstname(), newClient.getLastname(),
                        newClient.getEmail(), newClient.getPhone()
                ));
                refreshTables();
            } catch (Exception e) {
                showAlert("Update Error", e.getMessage());
            }
        });
    }

    // Opens a popup dialog to update Session details
    private void showSessionUpdateDialog(Session session) {
        Dialog<Session> dialog = new Dialog<>();
        dialog.setTitle("Update Session");
        dialog.setHeaderText("Update details for Session ID: " + session.getID());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cId = new TextField(String.valueOf(session.getClientID()));
        TextField date = new TextField(session.getDate());
        TextField time = new TextField(session.getTime());
        TextField desc = new TextField(session.getWorkoutDescription());

        grid.add(new Label("Client ID:"), 0, 0);
        grid.add(cId, 1, 0);
        grid.add(new Label("Date (dd.mm.yyyy):"), 0, 1);
        grid.add(date, 1, 1);
        grid.add(new Label("Time (hh:mm):"), 0, 2);
        grid.add(time, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(desc, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    int newClientId = Integer.parseInt(cId.getText());
                    return new Session(session.getID(), newClientId, date.getText(), time.getText(), desc.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Session> result = dialog.showAndWait();

        result.ifPresent(newSession -> {
            try {
                undoManager.executeCommand(new SessionCommands.UpdateCommand(
                        sessionService, session, session.getID(),
                        newSession.getClientID(), newSession.getDate(),
                        newSession.getTime(), newSession.getWorkoutDescription()
                ));
                refreshTables();
            } catch (Exception e) {
                showAlert("Update Error", e.getMessage());
            }
        });
    }

    // Reloads data and refreshes both tables
    private void refreshTables() {
        clientTable.setItems(FXCollections.observableArrayList(clientService.getAllClients()));
        clientTable.refresh();
        sessionTable.setItems(FXCollections.observableArrayList(sessionService.getAllSessions()));
        sessionTable.refresh();
    }

    // Clears all input fields provided
    private void clearFields(TextField... fields) {
        for(TextField f : fields) f.clear();
    }

    // Displays an error alert dialog
    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}