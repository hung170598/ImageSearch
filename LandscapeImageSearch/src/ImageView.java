import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hung1 on 6/19/2020.
 */
public class ImageView extends JFrame{
    private JButton btnChoseFile;
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JPanel imagePanel;
    private JLabel lbFileName;
    private JLabel imageLabel;
    private JFileChooser openFileChooser;

    public ImageView(String title){
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        this.pack();

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("..\\dataset\\"));
        btnChoseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = openFileChooser.showOpenDialog(mainPanel.getParent());
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = openFileChooser.getSelectedFile();
                    lbFileName.setText(file.getName());
                    Mat image = loadImage(file.getPath());
                    showImage(image);
                } else {
                    lbFileName.setText("No File Choosen!");
                }
            }
        });
    }

    public Mat loadImage(String url){
        Imgcodecs imageCodecs = new Imgcodecs();
        Mat matrix = imageCodecs.imread(url);
        return matrix;
    }

    public void showImage(Mat matrix){
        MatOfByte matOfByte = new MatOfByte();

        Imgcodecs.imencode(".jpg", matrix, matOfByte);

        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        try{
            BufferedImage bufImage = ImageIO.read(in);
            imageLabel.setIcon(new ImageIcon(bufImage));
            imageLabel.setSize(SV.IMG_WIDTH, SV.IMG_HEIGHT);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        JFrame frame = new ImageView("Imgae View");
        frame.setSize(SV.GUI_WIDTH,SV.GUI_HEIGHT);
        frame.setVisible(true);

        //DBProcesser dbProcesser = new DBProcesser();
        //dbProcesser.process();

        ImageSearcher.retrieveImage("E:\\Ky2_4\\CSDLDPT\\ImageSearch\\dataset\\00000000.jpg");


    }
}
