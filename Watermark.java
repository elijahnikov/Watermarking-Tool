package watermark;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Watermark extends JFrame implements ActionListener {

    JTextArea txtNewNote = new JTextArea();
    JScrollPane txtScroll = new JScrollPane(txtNewNote);
    Font fnt = new Font("Helvetica", Font.PLAIN, 20);
    FileFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
    public JTextField browsePhotoText;
    public ButtonGroup group;
    public JTextField browseWtrmarkText;
    public JButton btnBrowsePhoto; 
    public JButton btnBrowseWtrmark;
    public JButton btnOutput;
    public JButton run;
    public JFileChooser chooser;
    public String photoFileID;
    public String wtrmarkFileID;
    public String outputLocID;
    public JRadioButton lower;
    public JRadioButton upper;
    public JRadioButton center;
    final String dir = System.getProperty("user.dir");
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if ("browsePhoto".equals(ae.getActionCommand())) {
            
            //calls upon ChooseFile method to open the File Chooser
            ChooseFile();
            chooser.setFileFilter(filter);
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                photoFileID = chooser.getSelectedFile().getPath();
                browsePhotoText.setText(photoFileID);
                System.out.println("Photo location = " + photoFileID);
                
            }
        }
        
        if ("browseWtrmark".equals(ae.getActionCommand())) {
            
            ChooseFile();
            chooser.setFileFilter(filter);
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                wtrmarkFileID = chooser.getSelectedFile().getPath();
                browseWtrmarkText.setText(wtrmarkFileID);
                System.out.println("Watermark location = " + wtrmarkFileID);
                
            }
        }
        
        if ("runWatermark".equals(ae.getActionCommand())) {
            
            //validation in the form of if/else to ensure all user inputs are selected
            if (browseWtrmarkText.getText().trim().isEmpty() || browsePhotoText.getText().trim().isEmpty() || group.getSelection() == null) {
                JOptionPane.showMessageDialog(null, "Please select a photo and/or watermark and ensure a placement choice is selected");
            } else {
                File folderOutput = new File("C:\\Users\\Elijah\\Desktop\\final.png");
                File watermarkOverlay = new File(wtrmarkFileID);
                File photoInput = new File(photoFileID);
                //folderOutput.getParentFile().mkdirs();
            
                try {
                    watermarkOnImage(watermarkOverlay, "png", photoInput, folderOutput);
                } catch (IOException ex) {
                
                }
            }
            
        }
    }
    
    public static void main(String[] args) throws IOException {
        Watermark openMain = new Watermark();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            
        }
    }
    
    public Watermark() throws FileNotFoundException, IOException {
        view();
    }

    private void watermarkOnImage(File watermark, String fileType, File photo, File output) throws IOException {
        BufferedImage image = ImageIO.read(photo);
        BufferedImage watermarkImage = ImageIO.read(watermark);

        // determine image type
        int imageType = "png".equalsIgnoreCase(fileType) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage watermarked = new BufferedImage(image.getWidth(), image.getHeight(), imageType);

        Graphics2D w = (Graphics2D) watermarked.getGraphics();
        w.drawImage(image, 0, 0, null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        w.setComposite(alphaChannel);

        // calculates the coordinate where the String is painted
        int coordXUpper = image.getWidth();
        int coordYUpper = image.getWidth();
        int coordHeight = image.getHeight();
        int watermarkCoordX = watermarkImage.getWidth() / 2;
        int watermarkCoordY = watermarkImage.getHeight();
        
        //width and height values divided by 2 to get center coord position
        int imageXCoordHalf = image.getWidth() / 2;
        int imageYCoordHalf = image.getHeight() / 2;
        int wtrmarkXCoordHalf = watermarkImage.getWidth() / 2;
        int wtrmarkYCoordHalf = watermarkImage.getHeight() / 2;
        
        //initialised coordinate values set to 0
        int offsetX = 0;
        int offsetY = 0;
        
        //changes offsetX/Y values depending on what radio button user has selected
        if (upper.isSelected()){
           offsetX = coordXUpper / 2 - watermarkCoordX;
           offsetY = 0;
        } else if (lower.isSelected()){
           offsetX = coordXUpper / 2 - watermarkCoordX;
           offsetY = coordHeight - watermarkCoordY;
        } else if (center.isSelected()){
            offsetX = imageXCoordHalf - wtrmarkXCoordHalf;
            offsetY = imageYCoordHalf - wtrmarkYCoordHalf;
        }

         int finalCoordX = offsetX;
         int finalCoordY = offsetY;
        
        // add watermark to the image
        w.drawImage(watermarkImage, finalCoordX, finalCoordY, null);
        ImageIO.write(watermarked, fileType, output);
        w.dispose();
    }

    private void view() {
      
        JPanel panel1 = new JPanel();
        btnBrowsePhoto = new JButton("Photo");
        btnBrowsePhoto.setActionCommand("browsePhoto");
        btnBrowsePhoto.addActionListener(this);
        browsePhotoText = new JTextField();
        browsePhotoText.setPreferredSize(new Dimension(300, 25));
        browsePhotoText.setEditable(false);
        browsePhotoText.addActionListener(this);
        panel1.add(browsePhotoText);
        panel1.add(btnBrowsePhoto);
        
        JPanel panel2 = new JPanel();
        btnBrowseWtrmark = new JButton("Watermark");
        btnBrowseWtrmark.setActionCommand("browseWtrmark");
        btnBrowseWtrmark.addActionListener(this);
        browseWtrmarkText = new JTextField();
        browseWtrmarkText.setPreferredSize(new Dimension(300, 25));
        browseWtrmarkText.setEditable(false);
        browseWtrmarkText.addActionListener(this);
        panel2.add(browseWtrmarkText);
        panel2.add(btnBrowseWtrmark);
                
        JPanel panel3 = new JPanel();
        upper = new JRadioButton("Top");
        lower = new JRadioButton("Bottom");
        center = new JRadioButton("Center");
        group = new ButtonGroup();
        group.add(upper);
        group.add(lower);
        group.add(center);
        panel3.add(upper);
        panel3.add(lower);
        panel3.add(center);
        
        JPanel panel4 = new JPanel();
        run = new JButton("Run");
        run.setActionCommand("runWatermark");
        run.addActionListener(this);
        panel4.add(run);
        
        JPanel containerPanelOne = new JPanel(new GridLayout(3, 1));
        JPanel containerPanelTwo = new JPanel(new GridLayout(2, 1));
        containerPanelOne.add(panel1);
        containerPanelOne.add(panel2);
        containerPanelOne.add(panel3);
        containerPanelTwo.add(panel4);
        
        add(containerPanelOne, BorderLayout.NORTH);
        add(containerPanelTwo, BorderLayout.SOUTH);

        setSize(600, 230);
        setTitle("Watermarker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setVisible(true);
    }

    private void ChooseFile() {
        
        //method to call upon JFileChooser
        //only needs one line to be called as opposed to repeating the block of code below several times
        chooser = new JFileChooser(new File(System.getProperty("user.home") + "\\Desktop"));
        chooser.setDialogTitle("Select location of watermark...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
    }
    
}
