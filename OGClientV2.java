import java.util.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.io.*;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class OGClientV2 {

    private static Scanner scanner = new Scanner(System.in);
    private static ArrayList<Thread> threads = new ArrayList<>();
    private static ArrayList<Long> responseTime = new ArrayList<>(); //is time taken as a Long?
    private static String serverIP;     //May change the serverIP to be read as a int
    public int req2Server = 0;
    //private static int req2Server;


    public static void main (String args[]){

        getIP(scanner);
        //getInput();
        getInput(serverIP);
        //do {} while (req2Server != 7);


        System.out.println("Type in Thread Count: ");
        int threadCount = scanner.nextInt();

        for (int i = 0; i < threadCount; i++){
            ClientThread thread = new ClientThread(serverIP, responseTime);
            threads.add(new Thread(thread));
            //thread.run();
        }
        threads.forEach(Thread::run);

        System.out.println("Mean Response Time: " + mean(responseTime));

        threads.clear();

        return;
    }

    private static Long mean(ArrayList<Long> time) {
        int n = time.size();
        long sum = 0;

        for (int i = 0; i < n; i++){
            sum += time.get(i);
        }

        return sum / n;
    }

    //Sets the IP Address to talk to Server
    public static void getIP (Scanner s){

        System.out.println("====================================================================");
        System.out.println("========================= WELCOME ==================================");
        System.out.println("====================================================================");

        System.out.println("Type the Server IP Address:");
        serverIP = s.nextLine();

        while(!isValidIP(serverIP)){
            System.err.println("[ERROR] Invalid IP Address");
            serverIP = s.nextLine();
        }

    }

    //Used to check if the IP Address is Valid
    public static boolean isValidIP(String IP){

        String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        if (pattern.matcher(IP).matches())
            return true;


        return false;

    }

    private void getInput(String serverIP){


        System.out.println("The IP is " + serverIP);
        System.out.println("\n====================================================================");
        System.out.println("========================== Request =================================");
        System.out.println("====================================================================");
        System.out.println("\nPlease Enter A Number 1 - 7:\n" );

        LinkedList<String> inputs = new LinkedList<>();

        inputs.add("1 - Current Date & Time");
        inputs.add("2 - Uptime");
        inputs.add("3 - Memory Use");
        inputs.add("4 - Network Statistics");
        inputs.add("5 - Current Users");
        inputs.add("6 - Hosts Running Processes");
        inputs.add("7 - Quit");

        inputs.forEach(System.out::println);

        req2Server = scanner.nextInt();


        while (!isValidRequest(req2Server)){
            System.err.println("[ERROR] Invalid Server Request!");
            req2Server = scanner.nextInt();
        }


    }



}

class ClientThread implements Runnable {


    //private int id = 0;
    private long endingTime;

    private String serverIP;
    private ArrayList<Long> responseTime = new ArrayList<>();
    //private HashMap<String, Float> currentTime = new HashMap<>();



    public ClientThread (String serverIP, ArrayList<Long> responseTime){

        this.serverIP = serverIP;
        this.responseTime = responseTime;

    }

    @Override
    public void run (){
        ClientToServerConnection(serverIP, responseTime, endingTime); //currentTime,
    }



    //Check to see if the request is valid
    private static boolean isValidRequest(int input){

        Integer [] valid = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        List<Integer> list = Arrays.asList(valid);

        return list.contains(input);

    }


    private void ClientToServerConnection(String serverIP, ArrayList<Long> responseTime, Long endingTime){ //, HashMap<String, Float> currentTime
        Socket requestingSocket = null;
        DataOutputStream out = null;
        BufferedReader in = null;

        try{
            System.out.println("req2Server is " + req2Server);
            System.out.println("[CLIENT] Making connection to Server with IP Address " + serverIP);
            requestingSocket = new Socket(serverIP, 43595);
            System.out.println("[CLIENT] Connecting to Server on port 43595");

            out = new DataOutputStream(requestingSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(requestingSocket.getInputStream(), "UTF-8"));

            System.out.println("[CLIENT] Attempting to retrieve input data from Server...");


            if (requestingSocket != null && out != null && in != null){

                try{
                    Long startTime = System.currentTimeMillis();

                    out.writeByte(req2Server);
                    System.out.println("[CLIENT] Data Sent Successfully. Started Timer");

                    String serverResponse;

                    while ((serverResponse = in.readLine()) != null){
                        System.out.println(serverResponse);

                        if (serverResponse.contains("[500] OK")){
                            endingTime = System.currentTimeMillis();
                            break;
                        }
                    }

                    responseTime.add(startTime - endingTime); //switched to avoid negative number.
                    System.out.println("[CLIENT] Response Time: " + (startTime - endingTime));

                } catch(Exception e){
                    System.err.println("[ERROR] Data Received in a Unknown Format");
                }
            }

        } catch(UnknownHostException UnknownHostIP){
            System.err.println("[ERROR] Connecting to Server or Unknown Server IP");

        } catch (IOException ioexcept){
            System.err.println("[ERROR] The Server selected is not running or responding");
            System.exit(0);
        } finally {         //May omit to make it into a while loop that would work until the value is 7.

            try{

                System.out.println("[CLIENT] Closing Connection");

                in.close();
                out.close();
                requestingSocket.close();

            } catch (IOException ioExcept){
                System.exit(0);
            } catch (NullPointerException ne){
                System.err.println("Hostname Unknown");
            }

        }
    }



}
