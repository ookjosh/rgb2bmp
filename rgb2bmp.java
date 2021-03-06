/**

 MIT License

 Copyright (c) 2016 Josh Carty

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * This program takes the output of raspiyuv -rgb and converts it into a
 * regular bitmap file. It currently assumes that the extension of the files
 * is '.data' but this will be made flexible in the future. This will create
 * an 'output' folder in the specified directory, where the bitmaps will be
 * placed.
 */
public class rgb2bmp {

    final String outFolder = "/output/";

    public rgb2bmp(final String args[]) {

        if (args.length > 1) {
            System.out.println("Using extension: '" + args[1] + "'");
        }

        File dir = new File("."); // Default to current location
        try {
            dir = new File(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) { // If arguments weren't passed.
            printUsage();
            System.exit(0);
        }

        // Get list of files with specified extension.
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!(args.length > 1)) {   // If nothing specified default to '.data'
                    return name.endsWith(".data");
                }
                else {
                    return name.endsWith(args[1]);
                }
            }
        });


        // If list is zero length, exit
        if (files.length == 0) {
            System.out.println("No files with matching extension. Exiting.");
            System.exit(0);
        }


        // Create an output folder.

        // This prevents issues if args[0] ends in "/" or not.
        Path check = Paths.get(dir.getPath() + "/" + "output");
     
        if (Files.exists(check)) {
            System.out.println("This: " + check.toString());
            System.out.println("directory exists! Please delete it or rename.");
            System.out.println("We don't want to overwrite anything important!");
            System.exit(3);
        }

        try {
            Files.createDirectory(check);
            System.out.println("Created output directory");
        } catch (IOException e) {
            System.out.println("Failed to create directory");
            System.exit(2);

        }

        // Cycle through files and convert them.
        for (File f : files) {
            convert(f.getPath());
        }

        System.exit(0);
    }


    /**
     * Converts an RGB file to a bitmap.
     * Note: The file must be a sequence of 8 bit bytes in RGB order (24bpp);
     *
     * @param fpath the path to the file to be converted.
     */

    private void convert(String fpath) {

        Path path = Paths.get(fpath);
        byte[] data = null;

        // Packs RGB into an Int (which are 32 bits wide). Since we are using 8+8+8 (24), last 8 bits are 0
        // (Aside: those bits would be used in TYPE_INT_ARGB for alpha)
        BufferedImage image = new BufferedImage(2592, 1952, BufferedImage.TYPE_INT_RGB);

        // Try to load the file to our buffer.
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("Error reading file.");
            System.exit(1);
        }


        System.out.println("Converting " + path.getFileName());


        // Write to our buffer
        int x = 0; int y = 0;
        for (int i = 0; i < data.length; i+=3) {

            byte r = data[i];
            byte g = data[i+1];
            byte b = data[i+2];

            int rgb = r;
            rgb = (rgb << 8) + g;
            rgb = (rgb << 8) + b;
            image.setRGB(x,y, rgb);
            x++;
            if (x > 2592 - 1) {
                y++;
                x = 0;
            }

        }

        // Generate a path to our new file
        String outpath = path.getParent().toString() + outFolder;
        String filename = path.getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf(".")); // Strip extension
        filename = filename + ".bmp"; // Append new extension
        outpath += filename; // Full path to file



        // Attempt to write an image file.
        File output = new File(outpath);
        try {
            ImageIO.write(image, "bmp", output);
            System.out.println("Successfully converted " + path.getFileName() + " to " + "bmp");
        } catch (IOException e) {
            System.out.println("Error writing file: " + path.getFileName());
        }

    }



    public void printUsage() {

        System.out.println("RGB to BMP Conversion Tool");
        System.out.println("Usage for class: java rgb2bmp /FULL/PATH/TO/IMAGES/ [.extension]");
        System.out.println("Usage for Jar: java -jar rgb2bmp.jar /FULL/PATH/TO/IMAGES/ [.extension]");
        System.out.println("Example: java -jar rgb2bmp.jar /home/bob/rawimages/ .rgb");
        System.out.println();
        System.out.println("It will place images in a new subfolder called 'output'");
        System.out.println("In the image directory, with extension '.bmp'");
        System.out.println();
        System.out.println();
        System.out.println("Note: This assumes input files are of type '.data'");
        System.out.println("and are a sequence of bytes in RGB order.");
        System.out.println("Use the optional parameter to define the extension.");
    }






    public static void main (String args[]) {

        new rgb2bmp(args);

    }
}

