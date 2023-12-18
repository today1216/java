import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        String ipAddress = "8.8.8.8";

        try {
            // Build the command
            ProcessBuilder processBuilder = new ProcessBuilder("ping", ipAddress);
            
            // Start the process
            Process process = processBuilder.start();
            
            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            
            // Print the output
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            
            // Print the exit code
            System.out.println("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
