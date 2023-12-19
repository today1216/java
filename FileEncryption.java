import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;
import java.security.SecureRandom;
import java.util.*;

public class FileEncryption {

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

    public static void main(String[] args) {
        System.out.println("암호화 키값 16자리를 입력하시오");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String password = scanner.nextLine();
        byte[] key = password.getBytes();

        System.out.println("암호화 키값은 \"" + password + "\" 입니다.");

        System.out.println("암호화 할 위치의 경로를 아래와 같이 입력하시오");
        System.out.println("ex. C:/Users/abcd/Desktop/test/ test폴더 내부");
        System.out.println("ex. //10.100.100.125/spnt공용/test/ test폴더 내부");
        String startPath = scanner.nextLine();

        System.out.println("1: Encrypt(암호화), 2: Decrypt(복호화)");
        int choice = scanner.nextInt();

        try {
            switch (choice) {
                case 1:
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
                    break;

                case 2:
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
                    break;

                default:
                    System.out.println("잘못 입력했다");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
