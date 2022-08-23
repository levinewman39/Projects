import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import javafx.stage.FileChooser;

import static javafx.application.Application.launch;

/**
 *
 */
public class ImageManipulator extends Application implements ImageManipulatorInterface {
    Stage primaryStage = null;
    Scene scene = null;
    Group root = null;
    Label label = null;
    BorderPane borderPane = null;
    WritableImage image = null;
    int width = 0;
    int height = 0;


    /**
     * Loads image into user interface
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    @Override
    public WritableImage loadImage(String filename) throws FileNotFoundException {
        File imageFile = new File(filename);
        Scanner fileScanner = new Scanner(imageFile);



        while(!fileScanner.hasNextInt()){
            fileScanner.nextLine();
        }
        width = fileScanner.nextInt();
        height = fileScanner.nextInt();
        image = new WritableImage(width, height);

        if (primaryStage != null) {
            if (width < 600 && height < 600)
            {
                primaryStage.setWidth(600);
                primaryStage.setHeight(600);
            }
            else {
                primaryStage.setWidth(width + 50);
                primaryStage.setHeight(height + 100);
            }
            primaryStage.setTitle(filename);
        }


        fileScanner.next();
        WritableImage image = new WritableImage(width, height);
        //Pixel coordinates
        int x = 0;
        int y = 0;
        while(fileScanner.hasNextInt()){
            while(y < height) {
                while (x < width) {
                    //Color values
                    int red = fileScanner.nextInt();
                    int green = fileScanner.nextInt();
                    int blue = fileScanner.nextInt();
                    //The pixel color
                    Color pixelColor = Color.rgb(red, green, blue);
                    image.getPixelWriter().setColor(x, y, pixelColor);
                    x++;
                }
                y++;
                x = 0;
            }
        }
        return image;
    }

    /**
     * Opens file explore and allows user to select where they would like to save their ppm file
     * in which then copies the contents of the image into the file
     * @param filename
     * @param image
     * @throws FileNotFoundException
     */
    @Override
    public void saveImage(String filename, WritableImage image) throws FileNotFoundException{

        File savedFile = new File(filename);
        PrintWriter printWriter = new PrintWriter(savedFile);

        if (savedFile.delete()) {
            try {
                savedFile.createNewFile();
            } catch (IOException e) {
                System.out.println("File could not be created!");
            }
        }


        if(savedFile == null){
            System.out.print("File is null");
            return;
        }

        printWriter.println("P3");
        printWriter.println(filename);
        printWriter.println((int) image.getWidth() + " " + (int) image.getHeight());
        printWriter.println(255);
        int x = 0;
        int y = 0;

        while (y < image.getHeight() ) {
            while(x < image.getWidth() ) {
                Color pixelColor = image.getPixelReader().getColor(x, y);
                int red = (int) (pixelColor.getRed()*255);
                int green = (int) (pixelColor.getGreen()*255);
                int blue = (int) (pixelColor.getBlue()*255);
                printWriter.println(red);
                printWriter.println(green);
                printWriter.println(blue);

                x++;
            }
            y++;
            x=0;
        }

        printWriter.close();


    }


    /**
     * Changes the pixel colors to their luminescent counterparts
     * @param image - the image to be inverted, do not modify!
     * @return
     */
    @Override
    public WritableImage invertImage(WritableImage image) {
        WritableImage image2 = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        int y =0;
        int x =0;
        while (y < image.getHeight() ) {
            while(x < image.getWidth() ) {
                Color pixelColor = image.getPixelReader().getColor(x, y);
                int red = (255 - (int)(pixelColor.getRed() * 255));
                int green = (255 - (int)(pixelColor.getGreen() * 255));
                int blue = (255 - (int)(pixelColor.getBlue() * 255));
                pixelColor = Color.rgb(red, green, blue);
                image2.getPixelWriter().setColor(x, y, pixelColor);
                x++;
            }
            y++;
            x=0;
        }
        return image2;
    }

    /**
     * Changes each pixel color to their gray counterpart
     * @param image - the image to be converted to grayscale, do not modify!
     * @return
     */
    @Override
    public WritableImage grayifyImage(WritableImage image) {
        WritableImage image2 = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        int y =0;
        int x =0;
        while (y < image.getHeight() ) {
            while(x < image.getWidth() ) {
                Color pixelColor = image.getPixelReader().getColor(x, y);
                int grayColor = (int) (pixelColor.getRed()*255*0.2989 +
                        pixelColor.getGreen()*255*0.5870 +
                        pixelColor.getBlue()*255*0.1140);
                pixelColor = Color.rgb(grayColor, grayColor, grayColor);
                image2.getPixelWriter().setColor(x, y, pixelColor);
                x++;
            }
            y++;
            x=0;
        }
        return image2;
    }

    /**
     * Divides the pixels into groups of 5 x 5 and changes the pixel colors the color of the pixel that is directly in the
     * middle of each group
     * @param image - the image to be converted to pixel, do not modify!
     * @return
     */
    @Override
    public WritableImage pixelateImage(WritableImage image) {
        //Pixel coordinates
        int x = 0;
        int y = 0;

        while (y < image.getHeight() ) {
            while(x < image.getWidth() ) {
                //If x nor y step out of bounds of image, it will pixelize normally
                try {
                    Color pixelColor = image.getPixelReader().getColor(x + 2, y + 2);
                    //System.out.println("(" + (x+3) + ", " + (y+3) + ")");

                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            image.getPixelWriter().setColor(x + j, y + i, pixelColor);
                            //System.out.println("(" + (x+j) + ", " + (y+i) + ")");
                        }
                    }
                    x = x + 5;
                } catch (IndexOutOfBoundsException e) {
                    //If x or y step out of bounds, it will first try to pixelize with x being assumed to be
                    //the variable out of bounds
                    try {
                        //The amount of x coords to the edge
                        int xLimit = (int)image.getWidth() - x;
                        Color pixelColor = image.getPixelReader().getColor((int) image.getWidth() - 1, y + 3);
                        for (int i = 0; i < 5; i++) {
                            for (int j = 0; j < xLimit; j++) {
                                image.getPixelWriter().setColor(x + j, y + i, pixelColor);
                                //System.out.println("(" + (x+j) + ", " + (y+i) + ")");
                            }
                        }
                        x = (int) image.getWidth();
                    } catch (IndexOutOfBoundsException f) {
                        //If there is still an Index out of bounds exception, then we know that y steps out of bounds
                        //in which, this tries to run with the assumption that y is the only variable that is out of bounds
                        try {
                            Color pixelColor = image.getPixelReader().getColor( x + 3, (int) image.getHeight() - 1);
                            //the amount of y coordinates to the bottom edge
                            int yLimit = (int) image.getHeight() - y;

                            for (int i = 0; i < yLimit; i++) {
                                for (int j = 0; j < 5; j++) {
                                    image.getPixelWriter().setColor(x + j, y + i, pixelColor);

                                }
                            }
                            y = (int) image.getHeight();
                        } catch (IndexOutOfBoundsException g){
                            //If there is still an out of bounds exception, we know that both variables step out of bounds
                            //This runs with the proper pixel dimensions.
                            Color pixelColor = image.getPixelReader().getColor((int) image.getWidth()-1, (int) image.getHeight() - 1);
                            //The amount of x coordinates and y coordinates to the bottom right corner
                            int xLimit = (int) image.getWidth() - x;
                            int yLimit = (int) image.getHeight() - y;

                            for (int i = 0; i < yLimit; i++) {
                                for (int j = 0; j < xLimit; j++) {
                                    image.getPixelWriter().setColor(x + j, y + i, pixelColor);

                                }
                            }
                            y = (int) image.getHeight();
                        }
                    }
                }

            }
            x = 0;
            y = y + 5;
        }

        return image;
    }

    /**
     * Flips each pixels' location to flip the image
     * @param image - the image to be flipped, do not modify!
     * @return
     */
    @Override
    public WritableImage flipImage(WritableImage image) {
        WritableImage image2 = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        //Pixel coordinates
        int y =0;
        int x =0;
        while (y < image.getHeight() ) {
            while(x < image.getWidth() ) {
                Color pixelColor1 = image.getPixelReader().getColor(x, y);
                Color pixelColor2 = image.getPixelReader().getColor((int)image2.getWidth()-x-1, (int)image2.getHeight()-y-1);
                image2.getPixelWriter().setColor(x, y, pixelColor2);
                image2.getPixelWriter().setColor((int)image2.getWidth()-x-1, (int)image2.getHeight()-y-1, pixelColor1);
                x++;
            }
            y++;
            x=0;
        }
        return image2;
    }

    /**
     * Creates the bottomBar containing the buttons and gives the buttons function
     * @return
     */
    private HBox bottomBar() {
        HBox bottomPane = new HBox();
        ImageView imageView = new ImageView();

        bottomPane.setPadding(new Insets(4,5,4,5));
        bottomPane.setSpacing(10);

        label.setGraphic(imageView);

        Button load = new Button("Load");
        load.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open PPM FIle");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE FILE","*.ppm"));
            File chosen = chooser.showOpenDialog(primaryStage);
            if(chosen != null) {
                try {
                    image = loadImage(chosen.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImage(image);
            }
        });
        Button save = new Button("Save");
        save.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save ppm file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE FILE","*.ppm"));
            File returnedFile = chooser.showSaveDialog(primaryStage);
            try {
                saveImage(returnedFile.toString(), image);
            } catch (FileNotFoundException e) {
                System.out.print("Cannot Save: " + e);
            }
        });
        Button flip = new Button("Flip");
        flip.setOnAction(event -> {
            image = (flipImage(image));
            imageView.setImage(image);
        });
        Button grey = new Button("Greyscale");
        grey.setOnAction(event -> {
            image = grayifyImage(image);
            imageView.setImage(image);
        });
        Button invert = new Button("Invert");
        invert.setOnAction(event -> {
            image = invertImage(image);
            imageView.setImage(image);
        });
        Button pixel = new Button("Pixelize");
        pixel.setOnAction(event -> {
            image = (pixelateImage(image));
            imageView.setImage(image);
        });
        bottomPane.getChildren().addAll(load, save, flip, grey, invert, pixel);
        return bottomPane;
    }

    public void start( Stage stage ) throws FileNotFoundException{
        primaryStage = stage;

        root = new Group( );
        label = new Label();
        borderPane = new BorderPane();
        scene = new Scene( borderPane, stage.getWidth(), stage.getHeight() );
        stage.setTitle("My JavaFX Application");



        borderPane.setCenter(label);
        borderPane.setBottom(bottomBar());
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) throws FileNotFoundException {
        launch(args);
    }
}
