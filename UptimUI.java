/**
 * Created by n00897619 on 9/29/2017.
 */

import java.io.*;
import java.net.*;
import java.net.Socket;

public class UptimUI implements Runnable{

    private boolean shutdown = false;

    public static void main (String[] args ){

        System.out.println("Welcome to UI!");


        try{
            //
            ServerSocket service = new ServerSocket(4444);
            Socket sock = service.accept();
            run(sock, service);
        } catch (Exception e){
            e.printStackTrace();
        }

        return;

    }
    // This program will be put in as a method the run method takes in the socket and serverSocket
    private void run (Socket sock, ServerSocket service){

        try{
            System.out.println("[Server] Connection received from " + sock.getInetAddress().getHostName());

            //Get Stream from Client
            DataInputStream in = new DataInputStream(sock.getInputStream());
            PrintStream out = new PrintStream(sock.getOutputStream());

            //Client Option Process
            try{
                switch (in.read()){
                    case 1:
                        System.out.println("[Server] Finding Current Date and Time from " + service.getInetAddress().getHostAddress());
                        //Send back the response to client
                        respondToClient(out,"Host Current Date and Time: " + commandOutput(runCommand("date")));
                        break;
                    case 2:
                        System.out.println("[Server] Received Request for Host's Uptime from " + service.getInetAddress().getHostAddress());
                        respondToClient(out, "Host Current Uptime: " + commandOutput(runCommand("uptime")));
                        break;
                    case 3:
                        System.out.println("[Server] Received Request for Host's Memory Use " + service.getInetAddress().getHostAddress());
                        respondToClient(out, "Host Current Memory Use: " + commandOutput(runCommand("free")));
                        break;
                    case 4:
                        System.out.println("[Server] Received Request for Host's Network Statistics from " + service.getInetAddress().getHostAddress());
                        respondToClient(out, "Host Current Network Statistics: " + commandOutput(runCommand("netstat")));
                        break;
                    case 5:
                        System.out.println("[Server] Received Request for Host's Current Users from " + service.getInetAddress().getHostAddress());
                        respondToClient(out, "Host Current User: " + commandOutput(runCommand("who")));
                        break;
                    case 6:
                        System.out.println("[Server] Received Request for Current Running Processes from " + service.getInetAddress().getHostAddress());
                        respondToClient(out, "Host Current Running Process: " + commandOutput(runCommand("ps -c")));
                        break;
                    case 7:
                        System.out.println("[Server] Received Request to shutdown... ");
                        shutdown = true;
                        break;

                }
            } catch (Exception e){
                System.err.println("Data received in unknown format or did not receive.");
                e.printStackTrace();
            } finally {
                try {
                    if (shutdown){
                        in.close();
                        out.close();
                        service.close();
                        sock.close();

                        System.exit(0);
                    }
                } catch (IOException e){
                    System.err.println("Error Shutting Down Server");
                    e.printStackTrace();
                }
            }



        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
    private void respondToClient(PrintStream output, String text){
        try {
            output.println("[Server] Response: " + text);
            output.println("[Server] OK");
            output.close();                 // <------------ LOOK HERE???
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String commandOutput(Process process){

        String x;
        StringBuilder strbld = new StringBuilder();
        //
        try {
            BufferedReader strOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((x = strOutput.readLine()) != null){
                strbld.append(x).append("\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return strbld.toString();
    }
    /*
    * This method will return a process by executing
    * the linux cmd that will return all the method
    * */
    private Process runCommand(String command){

        try{
            return Runtime.getRuntime().exec(command);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
