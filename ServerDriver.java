
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.JedisPooled;

/* 

javac -cp '.:jar_files/server/*' ClientHandler.java;
javac -cp '.:jar_files/server/*' ServerDriver.java && java -cp '.:jar_files/server/*' ServerDriver

*/

/* 
clientmain1.json
{
  "operation": "1",
  "username": "user0",
  "works": [
    {
      "to_users": ["user1", "user2"],
      "files": ["/home/nbaskey/Desktop/books/Computer Systems.pdf"]
    },
    {
      "to_users": ["user2", "user3"],
      "files": [
        "/home/nbaskey/Desktop/books/Packt.Advanced.Node.js.Development.1788393937.pdf"
      ]
    }
  ]
} */

/* 
clientmain2.json
{
  "operation": "2",
  "username": "user4",
  "files": [
    "Computer Systems.pdf",
    "Ahmet Bindal - Fundamentals of Computer Architecture and Design-Springer (2019).pdf"
  ]
}
 */

public class ServerDriver {
  static final int PORT = 9000;
  private static ArrayList<ClientHandler> clients = new ArrayList<>();
  private static ExecutorService threadPool = Executors.newFixedThreadPool(4);

  public static void main(String[] args) throws IOException {
    ServerSocket listener = new ServerSocket(PORT);
    JedisPooled jedis = new JedisPooled("127.0.0.1", 6379);

    while (true) {
      /*
       * The accept() call is used by a server to accept a connection request from a
       * client. When a connection is available, the socket created is ready for use
       * to read data from the process that requested the connection. The call accepts
       * the first connection on its queue of pending connections for the given socket
       * socket.
       */
      Socket socket = listener.accept();

      ClientHandler client = new ClientHandler(socket, jedis);
      clients.add(client);

      threadPool.execute(client);

    }

  }
}
