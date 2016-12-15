import java.net.Socket; //socket
import java.net.UnknownHostException; //socket
import java.io.IOException; //socket
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/** The Caesar Cipher Client Class uses the Java Sockets and Java IO libraries to create two-way encrypted traffic.
 *
 * After taking a rotational number between 0 and 25 'n' from the server the client will:
 * 	1. read input from the user, and encrypt the message before sending it off to the server
 * 	2. listen for a response from the server, unencrypting and displaying the server's message
 *
 * Caesar Cipher Client Algorithm:
 * 	1.Read user input (stdIn)
 *	2.Send input to server (out)
 * 	3.Receive message back from server (in.readLine())
 * 	4a.For the first message,
 *     		Set Cipher rotation value (n) from the received message
 *			(Note: n must be between 1 and 25)
 * 	4b.For all messages after,
 * 		Unencrypt and display the received message
 * 	5.Repeat Steps 1-4b
 * 	6a.If errors,
 * 		print stack trace
 * 	6b.If user or server exit,
 * 		close all sockets
 *
 * Correct usage (hostname, port of server):
 *      java CaesarCipherClient [hostname] [port]
 * i.e. java CaesarCipherClient 127.0.0.1 49152
 *
 * Note: Must be running the Caesar Cipher Server
 *
 * @author David Michel djm43
 * @date 11 May 2016
 *
 */
public class CaesarCipherClient {
	public static void main(String[] args) {
		//Read in hostname and port of which to connect
		if(!(args.length>0 && args.length<3)){
			System.err.println("Correct Use: \n\tjava CaesarCipherClient [hostname] [port]");
			return;
		}
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);

		//initialize variables
		Socket MyClient = null;
		BufferedReader in = null;
		BufferedReader stdIn = null;
		DataOutputStream output = null;
		DataInputStream input = null;
		PrintWriter out;
		int n = 0; //default (needs to be initialized)
		boolean firstMsg = true;
		String inputStr = "";
		String encryptedMsg = "";
		String unencryptedMsg = "";
		System.out.println("Welcome to Caesar's Super Cipher Service!");

		//connect to socket
		try {
			MyClient = new Socket(hostname, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Connected to " + hostname);

		System.out.print("Please enter a rotation number between 1-25: ");
		try {
			out = new PrintWriter(MyClient.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(MyClient.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			inputStr = null;

			while((inputStr = stdIn.readLine()) != null){
				out.println(inputStr);
				encryptedMsg = in.readLine();
				if(firstMsg){
					try{
					n = Integer.parseInt(encryptedMsg);
					} catch(NumberFormatException e){
						System.err.println("Error: Invalid rotation value");
						System.exit(-1);
					}
					if(n<=0 && n>25){
						System.err.println("Error: Rotation value must be between 1 and 25");
						System.exit(-1);
					}
					firstMsg = false;
				}else{
					unencryptedMsg = CaesarCipherClient.decodeMsg(encryptedMsg, n);
					System.out.println("Server: " + encryptedMsg + "(Encrypted) "+ unencryptedMsg + "(Unencrypted)"); //display messages
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			output.close();
			input.close();
			MyClient.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/* decodeMsg() will decrypt a message encoded with Casear-Cipher type encryption,
	 * given the encrypted message and its rotational value of which to decrypt with
	 *
	 * @param: encryptedMsg, the message to decrypt
	 * @param: weight, the rotational value (n)
	 * @return: unencryptedMsg
	 */
	public static String decodeMsg(String encryptedMsg, int weight){
		String unencryptedMsg = "";
		int index;
		for(int i = 0; i<encryptedMsg.length();i++){
			if(Character.isLetter(encryptedMsg.charAt(i))){
				index = (int)(encryptedMsg.charAt(i));

				//for lowercase (ASCII characters a:97 to z:122)
				if((index-weight) <= 96 && index > 96 && index < 123){
					unencryptedMsg += (char)(122 - (96-(index-weight)));
				}
				else if(index > 96 && index < 123){
					unencryptedMsg += (char)(index-weight);
				}

				//for uppercase (ASCII characters A:65 to Z:90)
				if((index-weight) <= 64 && index > 64 && index < 91){
					unencryptedMsg += (char)(90 - (64 -(index-weight)));
				}
				else if(index > 64 && index < 91){
					unencryptedMsg += (char)(index-weight);
				}
			} else
				unencryptedMsg += encryptedMsg.charAt(i);
		}
		return unencryptedMsg;
	}
}
