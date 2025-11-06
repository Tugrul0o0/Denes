package com.desktoppet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ControlPanel {

    private final Stage stage;
    private final Consumer<String> characterCallback;
    private final Path notesDir;

    private final ObservableList<String> noteTitles = FXCollections.observableArrayList();
    private final ListView<String> notesListView = new ListView<>(noteTitles);
    private final TextField titleField = new TextField();
    private final TextArea contentArea = new TextArea();

    public ControlPanel(Consumer<String> characterCallback) {
        this.stage = new Stage();
        this.characterCallback = characterCallback;
        this.notesDir = Paths.get(System.getProperty("user.home"), "DesktopPetNotes");

        setupUI();
        ensureNotesDirectoryExists();
        loadNoteTitles();
    }

    private void setupUI() {
        stage.setTitle("Kontrol Paneli");

        Label characterLabel = new Label("Karakter Seç:");
        ListView<String> characterList = new ListView<>();
        characterList.setItems(FXCollections.observableArrayList("Küp", "Küre", "Silindir"));
        characterList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                characterCallback.accept(newVal);
            }
        });
        characterList.setPrefHeight(100);

        Label notesLabel = new Label("Not Defteri");
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadNoteContent(newVal);
            }
        });

        Button btnNew = new Button("Yeni");
        btnNew.setOnAction(e -> clearEditor());

        Button btnSave = new Button("Kaydet");
        btnSave.setOnAction(e -> saveNote());

        Button btnDelete = new Button("Sil");
        btnDelete.setOnAction(e -> deleteNote());

        HBox noteButtons = new HBox(10, btnNew, btnSave, btnDelete);

        VBox notesSection = new VBox(10, notesLabel, notesListView, titleField, contentArea, noteButtons);

        VBox mainLayout = new VBox(20, characterLabel, characterList, new Separator(), notesSection);
        mainLayout.setPadding(new Insets(15));

        Scene scene = new Scene(mainLayout, 400, 600);

        // CSS dosyasını sahneye ekle
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
    }

    // --- Not İşlevleri ---
    private void ensureNotesDirectoryExists() {
        if (!Files.exists(notesDir)) {
            try {
                Files.createDirectories(notesDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadNoteTitles() {
        noteTitles.clear();
        try {
            noteTitles.addAll(Files.list(notesDir)
                .filter(p -> p.toString().endsWith(".txt"))
                .map(p -> p.getFileName().toString().replace(".txt", ""))
                .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNoteContent(String title) {
        Path notePath = notesDir.resolve(title + ".txt");
        try {
            String content = new String(Files.readAllBytes(notePath), StandardCharsets.UTF_8);
            titleField.setText(title);
            contentArea.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNote() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showAlert("Hata", "Not başlığı boş olamaz.");
            return;
        }
        Path notePath = notesDir.resolve(title + ".txt");
        try {
            Files.write(notePath, contentArea.getText().getBytes(StandardCharsets.UTF_8));
            loadNoteTitles();
            notesListView.getSelectionModel().select(title);
        } catch (IOException e) {
            showAlert("Hata", "Not kaydedilirken bir sorun oluştu.");
            e.printStackTrace();
        }
    }

    private void deleteNote() {
        String selectedTitle = notesListView.getSelectionModel().getSelectedItem();
        if (selectedTitle == null) {
            showAlert("Uyarı", "Lütfen silmek için bir not seçin.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "'" + selectedTitle + "' başlıklı notu silmek istediğinizden emin misiniz?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            Path notePath = notesDir.resolve(selectedTitle + ".txt");
            try {
                Files.delete(notePath);
                clearEditor();
                loadNoteTitles();
            } catch (IOException e) {
                showAlert("Hata", "Not silinirken bir sorun oluştu.");
                e.printStackTrace();
            }
        }
    }

    private void clearEditor() {
        notesListView.getSelectionModel().clearSelection();
        titleField.clear();
        contentArea.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        if (!stage.isShowing()) {
            stage.show();
        }
        stage.toFront();
    }

    public Stage getStage() {
        return stage;
    }
}
