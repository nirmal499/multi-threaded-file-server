
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import redis.clients.jedis.JedisPooled;

public class ClientHandler implements Runnable {
    private Socket client;
    private DataInputStream input;
    private DataOutputStream output;
    private JedisPooled jedis;

    public ClientHandler(Socket socket, JedisPooled sjedis) throws IOException {
        client = socket;
        input = new DataInputStream(client.getInputStream());
        output = new DataOutputStream(client.getOutputStream());
        jedis = sjedis;

    }

    public void saveUsersToRedis(String to_users, String fileName) {
        // Saving it to redis
        String[] to_users_arr = to_users.split(":");

        for (String user : to_users_arr) {
            // System.out.println("Added " + user);
            jedis.sadd(fileName, user);
        }

    }

    public boolean checkPermission(String fileName, String username) {
        return jedis.sismember(fileName, username);
    }

    public void run() {
        try {
            /*
             * First thing we receive from the client is the integer:
             * if it is 1 then it means that the client will upload something
             * if it is 2 then it means that the client will request for some file
             * 
             */
            int option = input.readInt();

            if (option == 1) {

                /* First thing we receive from the client is the length of the fileName */
                // int fileNameLength = input.readInt();
                String username = input.readUTF();

                Integer works = input.readInt();
                for (int i = 1; i <= works; i++) {

                    String to_users = input.readUTF();
                    // System.out.println("to_users: " + to_users);

                    Integer files = input.readInt();
                    // System.out.println("Files " + files);

                    for (int j = 1; j <= files; j++) {
                        String fileName = input.readUTF();
                        // System.out.println("fileName " + fileName);
                        saveUsersToRedis(to_users, fileName);

                        long fileSize = input.readLong();

                        // Integer fileContentLength = input.readInt();
                        // if (fileContentLength > 0) {}

                        String pathName = "/home/nbaskey/Desktop/clgPresent/clgProject/version/server_resources/";

                        File fileDownoad = new File(pathName + fileName);
                        long startTime = System.currentTimeMillis();

                        // byte[] fileContentBytes = new byte[fileContentLength];
                        // input.readFully(fileContentBytes, 0, fileContentBytes.length);

                        try (FileOutputStream fout = new FileOutputStream(fileDownoad)) {

                            int count;
                            byte[] buffer = new byte[8192]; // 8KB
                            // while ((count = input.read(buffer)) > 0) {
                            // fout.write(buffer, 0, count);
                            // }
                            while (fileSize > 0
                                    && (count = input.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                                fout.write(buffer, 0, count);
                                fileSize -= count;
                                // System.out.println("FileSize is " + fileSize + " and Count is " + +count);
                            }

                            // fout.write(fileContentBytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        long stopTime = System.currentTimeMillis();

                        /* milliseconds to seconds divide it by 1000 */
                        System.out.println(
                                "The user " + username + " uploaded " + fileName + " Recieved. "
                                        + " Time taken to recieve is "
                                        + ((stopTime - startTime) / 1000) + " ms");

                    }

                }

            } else if (option == 2) {

                String username = input.readUTF();
                Integer files = input.readInt();
                System.out.println("User " + username + " Tried to download " + files + " files");

                for (int i = 1; i <= files; i++) {
                    /*
                     * We recieve from client is the name of file client wants to
                     * download
                     */
                    String fileName = input.readUTF();
                    // System.out.println("FileName " + fileName);

                    String pathToResources = "/home/nbaskey/Desktop/clgPresent/clgProject/version/server_resources/";
                    File fileToSend = new File(pathToResources + fileName);
                    // System.out.println("File " + fileToSend);
                    int responseCode = 100;

                    if (fileToSend.exists()) {
                        // output.writeInt(200);// Response saying that the file exists
                        responseCode += 100; // responseCode = 200

                        if (checkPermission(fileName, username)) {
                            // Given username is allowed to download the file
                            responseCode += 100;

                            // Response saying that the file exists and permitted to download
                            output.writeInt(responseCode); // responseCode = 300

                            output.writeLong(fileToSend.length());
                            try (FileInputStream fin = new FileInputStream(fileToSend);) {
                                // byte[] fileContentBytes = new byte[(int) fileToSend.length()];
                                // fin.read(fileContentBytes);

                                /* Here we are sending file data bytes */
                                int count;
                                byte[] buffer = new byte[8192];
                                while ((count = fin.read(buffer)) > 0) {
                                    output.write(buffer, 0, count);
                                }

                                /* ******************************** */
                                // output.writeInt(fileContentBytes.length);
                                // output.write(fileContentBytes);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            output.writeInt(responseCode); // 200
                        }

                    } else {
                        // 100
                        output.writeInt(responseCode); // Response showing that file does not exists
                    }
                }

            } else {
                output.writeInt(400); // Response showing that INVALID Integer was provided
            }
        } catch (IOException e) {
            System.out.println("IO exception happened in client handler");
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
