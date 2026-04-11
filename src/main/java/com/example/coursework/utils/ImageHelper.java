package com.example.coursework.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageHelper {
    // in project root so images can be edited at runtime
    private static final String BASE_IMAGE_DIR = "images/products/";

    // create category folder if it doesn't exist
    public static void createCategoryFolder(String category) {
        File categoryDir = new File(BASE_IMAGE_DIR + category);
        if (!categoryDir.exists()) {
            categoryDir.mkdirs();
        }
    }

    // save image for a product (in inventory page)
    public static boolean saveProductImage(String productName, String category, File sourceImage) {
        try {
            createCategoryFolder(category);

            // remove special char
            String filename = cleanFilename(productName);
            File destFile = new File(BASE_IMAGE_DIR + category + "/" + filename + ".png");

            // read and save image as png
            BufferedImage image = ImageIO.read(sourceImage);
            ImageIO.write(image, "png", destFile);

            return true;
        } catch (IOException e) {
            System.out.println("Failed to save image: " + e.getMessage());
            return false;
        }
    }

    // load image for a product (in admin inventory and cashier dashboard)
    public static ImageView loadProductImage(String productName, String category, int width, int height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);

        String filename = cleanFilename(productName);
        File imageFile = new File(BASE_IMAGE_DIR + category + "/" + filename + ".png");

        try {
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
            } else {
                setDefaultImage(imageView, category);
            }
        } catch (Exception e) {
            setDefaultImage(imageView, category);
        }

        return imageView;
    }

    // delete product image
    public static boolean deleteProductImage(String productName, String category) {
        String filename = cleanFilename(productName);
        File imageFile = new File(BASE_IMAGE_DIR + category + "/" + filename + ".png");

        if (imageFile.exists()) {
            return imageFile.delete();
        }
        return true; // Return true if file doesn't exist
    }

    // Update image when product name or category changes
    public static boolean updateProductImage(String oldName, String newName, String oldCategory, String newCategory) {
        String oldFilename = cleanFilename(oldName);
        File oldFile = new File(BASE_IMAGE_DIR + oldCategory + "/" + oldFilename + ".png");

        if (oldFile.exists()) {
            // If category changed, move to new category folder
            if (!oldCategory.equals(newCategory)) {
                createCategoryFolder(newCategory);
                String newFilename = cleanFilename(newName);
                File newFile = new File(BASE_IMAGE_DIR + newCategory + "/" + newFilename + ".png");
                return oldFile.renameTo(newFile);
            }
            // If only name changed, rename the file
            else if (!oldName.equals(newName)) {
                String newFilename = cleanFilename(newName);
                File newFile = new File(BASE_IMAGE_DIR + oldCategory + "/" + newFilename + ".png");
                return oldFile.renameTo(newFile);
            }
        }
        return true;
    }

    private static void setDefaultImage(ImageView imageView, String category) {
        try {
            File defaultImage = new File("images/default.png");
            if (defaultImage.exists()) {
                Image image = new Image(defaultImage.toURI().toString());
                imageView.setImage(image);
            }
        } catch (Exception e) {
            // No default image, leave empty
        }
    }

    private static String cleanFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\s-]", "").trim().replace(" ", "_");
    }

    // file chooser for image selection
    public static File chooseImage(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        // only filter png, jpg and jpeg
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(owner);
    }
}