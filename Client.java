
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/* 

javac -cp '.:jar_files/client/*' Client.java && java -cp '.:jar_files/client/*' Client clientmain1.json
javac -cp '.:jar_files/client/*' Client.java && java -cp '.:jar_files/client/*' Client clientmain2.json

*/

public class Client {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    static char[] animationChars = new char[] { '|', '/', '-', '\\' };

    public static void main(String[] args) {
        File fileDownoad = null;

        if (args.length < 1) {
            System.out.println("Provide the json file");
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(args[0]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket("localhost", 9000);
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());) {

            /* Send the operation you want to perform to the client */
            Integer operation = Integer.parseInt((String) jsonObject.get("operation"));
            if (operation == 1) {
                /* Client is suppose to upload */

                /* ******************************** */
                output.writeInt(operation); // Telling server that the client wants to upload

                /* ******************************** */
                String username = (String) jsonObject.get("username");
                output.writeUTF(username); // write the message we want to send

                JSONArray works = (JSONArray) jsonObject.get("works");
                /* ************************* */
                output.writeInt(works.size());

                Iterator<?> works_iterator = works.iterator();

                while (works_iterator.hasNext()) {
                    JSONObject userjsonObject = (JSONObject) works_iterator.next();

                    /*
                     * The List is an interface, and the ArrayList is a class of Java Collection
                     * framework. The List creates a static array, and the ArrayList creates a
                     * dynamic array for storing the objects. So the List can not be expanded once
                     * it is created but using the ArrayList, we can expand the array when needed.
                     */
                    List<?> to_users = (List<?>) userjsonObject.get("to_users");
                    StringBuilder sb = new StringBuilder();
                    sb.append(username + ":");
                    for (int i = 0; i < to_users.size(); i++) {
                        sb.append(to_users.get(i));
                        if (i != to_users.size() - 1) {
                            sb.append(":");
                        }
                    }
                    /* *********************************** */
                    output.writeUTF(sb.toString());

                    List<?> files = (List<?>) userjsonObject.get("files");
                    Iterator<?> files_iterator = files.iterator();
                    /* *********************************** */
                    output.writeInt(files.size());
                    while (files_iterator.hasNext()) {
                        String file = (String) files_iterator.next();
                        File fileToSend = new File(file);
                        if (fileToSend.isAbsolute() && fileToSend.isFile()) {
                            try (FileInputStream fin = new FileInputStream(fileToSend);) {

                                UUID uuid = UUID.randomUUID();
                                String fileName = fileToSend.getName();
                                String final_fileName = uuid.toString() + '-' + fileName;

                                /* *********************************** */
                                output.writeUTF(final_fileName);// write the message we want to send
                                output.writeLong(fileToSend.length());

                                /* Here we are sending file data bytes */
                                int count;
                                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                                while ((count = fin.read(buffer)) > 0) {
                                    output.write(buffer, 0, count);
                                }
                                System.out.println("OK ::" + fileName + " renamed to " + final_fileName);

                                /* ******************************** */
                                // output.writeInt(fileContentBytes.length);
                                // output.write(fileContentBytes);

                            } catch (IOException e) {

                                e.printStackTrace();
                            }

                        } else {
                            System.out.println(
                                    "Given file path " + file + "should be absolute and it must exists");
                        }
                    }

                }

            } else if (operation == 2) {
                /* Client is suppose to download */

                output.writeInt(operation); // Telling server that the client wants to download

                /* ******************************** */
                String username = (String) jsonObject.get("username");
                // output.writeChars(username); // Telling server the name of the client
                output.writeUTF(username);

                JSONArray files = (JSONArray) jsonObject.get("files");
                /* ************************* */
                output.writeInt(files.size());

                Iterator<?> files_iterator = files.iterator();
                while (files_iterator.hasNext()) {

                    String file = (String) files_iterator.next();
                    output.writeUTF(file);

                    int response = input.readInt();
                    if (response == 300) {
                        // It means the file exists

                        // Integer fileContentLength = input.readInt();
                        // if (fileContentLength > 0) {}
                        long fileSize = input.readLong();

                        String pathToSave = "/home/nbaskey/Desktop/clgPresent/clgProject/version/client_resources/";
                        fileDownoad = new File(pathToSave + file);
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
                            }
                            // fout.write(fileContentBytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        long stopTime = System.currentTimeMillis();
                        /* milliseconds to seconds divide it by 1000 */
                        System.out.println(
                                file + " Downloaded. "
                                        + " Time taken to download is "
                                        + ((stopTime - startTime) / 1000) + " ms");

                    } else if (response == 200) {
                        System.out.println(
                                "File " + file + " does exists in the server.But you are not permitted to download it");
                    } else if (response == 100) {
                        System.out.println("File " + file + " does not exists in the server.");

                    }
                }

            } else {
                System.out.println("INVALID operation is provided...");
                return;
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
