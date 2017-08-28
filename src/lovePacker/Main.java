package lovePacker;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("lovePacker.fxml"));
    primaryStage.setTitle("LÖVE2D Project File Packer");

    root.setOnDragOver(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
          event.acceptTransferModes(TransferMode.COPY);
        } else {
          event.consume();
        }
      }
    });

    root.setOnDragDropped(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
          success = true;
          String filePath = null;
          try (FileSystem loveFs = openLove(Paths.get("newGame.love"))) {
            for (File file : db.getFiles()) {
              filePath = file.getAbsolutePath();
              System.out.println(filePath);

              String fileName = "/" + file.getName();

              copyToLove(loveFs, filePath, fileName);

            }
          } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + " - " + e.getMessage());
          }


        }
        event.setDropCompleted(success);
        event.consume();
      }
    });

    primaryStage.setScene(new Scene(root, 500, 500));
    primaryStage.show();

  }


  public static void main(String[] args) {
    launch(args);
  }

  private static FileSystem openLove(Path path) throws IOException, URISyntaxException {
    Map<String, String> properties = new HashMap<>();
    properties.put("create", "true");

    URI uri = new URI("jar:file", path.toUri().getPath(), null);
    FileSystem loveFs = FileSystems.newFileSystem(uri, properties);

    return loveFs;
  }

  private static void copyToLove(FileSystem loveFs, String source, String dest) throws IOException {
    Path sourceFile = Paths.get(source);
    Path destFile = loveFs.getPath(dest);

    Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
  }

}


