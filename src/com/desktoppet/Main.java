package com.desktoppet;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application implements NativeKeyListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private StackPane root;
    private Group group3D;
    private TranslateTransition jumpAnimation;
    private ControlPanel controlPanel;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        group3D = new Group();

        // Başlangıç karakterini ayarla
        changeCharacter("Küp");

        SubScene subScene = new SubScene(group3D, WIDTH, HEIGHT);
        subScene.setFill(Color.TRANSPARENT);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500);
        subScene.setCamera(camera);

        root.getChildren().add(subScene);

        // --- Sağ Tıklama Menüsü ---
        setupContextMenu(primaryStage);

        Scene scene = new Scene(root, WIDTH, HEIGHT, true);
        scene.setFill(Color.TRANSPARENT);

        // CSS dosyasını sahneye ekle
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Desktop Pet");
        primaryStage.show();

        initGlobalKeyListener();
    }

    private void changeCharacter(String characterName) {
        Shape3D character;
        PhongMaterial material = new PhongMaterial();

        switch (characterName) {
            case "Küre":
                character = new Sphere(50);
                material.setDiffuseColor(Color.DODGERBLUE);
                break;
            case "Silindir":
                character = new Cylinder(40, 120);
                material.setDiffuseColor(Color.INDIANRED);
                break;
            case "Küp":
            default:
                character = new Box(100, 100, 100);
                material.setDiffuseColor(Color.LIMEGREEN);
                break;
        }
        character.setMaterial(material);

        // Zıplama animasyonunu yeni karaktere ata
        jumpAnimation = new TranslateTransition(Duration.millis(300), character);
        jumpAnimation.setByY(-100);
        jumpAnimation.setCycleCount(2);
        jumpAnimation.setAutoReverse(true);

        // Sahneyi güncelle
        group3D.getChildren().clear();
        group3D.getChildren().add(character);
    }

    private void setupContextMenu(Stage stage) {
        MenuItem animateItem = new MenuItem("Animasyon Oynat");
        animateItem.setOnAction(event -> {
            if (jumpAnimation != null && jumpAnimation.getStatus() != javafx.animation.Animation.Status.RUNNING) {
                jumpAnimation.playFromStart();
            }
        });

        MenuItem clickThroughItem = new MenuItem("Tıklamayı Kapat (Geri almak için Ctrl+R+T)");
        clickThroughItem.setOnAction(event -> root.setMouseTransparent(true));

        MenuItem panelItem = new MenuItem("Kontrol Paneli");
        panelItem.setOnAction(event -> {
            if (controlPanel == null) {
                // Karakter değiştirme isteğini işleyecek bir lambda fonksiyonu ilet
                controlPanel = new ControlPanel(this::changeCharacter);
                // Panel kapandığında referansı temizle
                controlPanel.getStage().setOnHidden(e -> controlPanel = null);
            }
            controlPanel.show();
        });

        ContextMenu contextMenu = new ContextMenu(animateItem, clickThroughItem, panelItem);

        root.setOnContextMenuRequested(event -> {
            contextMenu.show(stage, event.getScreenX(), event.getScreenY());
        });
    }

    // --- JNativeHook Metodları ---
    private void initGlobalKeyListener() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException e) {
            System.err.println("Native hook'u kaydederken bir sorun oluştu: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        GlobalScreen.unregisterNativeHook();
        super.stop();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        if (pressedKeys.contains(NativeKeyEvent.VC_CONTROL) &&
            pressedKeys.contains(NativeKeyEvent.VC_R) &&
            pressedKeys.contains(NativeKeyEvent.VC_T)) {
            Platform.runLater(() -> {
                if (root != null) root.setMouseTransparent(false);
            });
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    public static void main(String[] args) {
        launch(args);
    }
}
