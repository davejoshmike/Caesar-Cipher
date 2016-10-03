//import java.util.Random; //Random
import java.net.Socket; //socket
import java.net.UnknownHostException; //socket
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException; //socket
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CaesarCipherClient {
	//private static char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' }; => also didn't work well
	//private static alphabet = "abcdefghijklmnopqrstuvwxyz"; //=>indicies of String are not constant. sometimes a is index 24

	public static void main(String[] args) {
		if(!(args.length>0 && args.length<3)){
			System.err.println("Correct Use: \n\tjava CaesarCipherClient [hostname] [port]");
			return;
		}
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		Socket MyClient = null;
		BufferedReader in = null;
		BufferedReader stdIn = null;
		DataOutputStream output = null;
		DataInputStream input = null;
		PrintWriter out;
		int n = 0; //default (has to be initialized)
		boolean firstMsg = true;
		String inputStr = "";
		String encryptedMsg = "";
		String unencryptedMsg = "";
		System.out.println("Welcome to Caesar Chavez's Super Cipher Service!");

		//CONNECT TO SOCKET
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
			//handle io exception
			inputStr = null;
			while((inputStr = stdIn.readLine()) != null){ //read user input from command line
				out.println(inputStr); //send user input to the server
				encryptedMsg = in.readLine(); //receive message back from the server
				if(firstMsg){
					//set the rotation number, n
					try{
					n = Integer.parseInt(encryptedMsg);
					} catch(NumberFormatException e){
						System.err.println("Error: Invalid rotation value");
						System.exit(-1);
					}
					if(n<=0 && n>=25){
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

		////////////////////////////////////////////////////////

		//Get input from user

		//do cipher

		//server should replay with that same number i.e. if(!(n = serverresponse.n)) drop; //or resend??

		//close sockets
		try {
			output.close();
			input.close();
			MyClient.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static String decodeMsg(String encryptedMsg, int weight){
		String unencryptedMsg = "";
		int index;
		for(int i = 0; i<encryptedMsg.length();i++){
			/*
			//attempt 1 w/ alphabetStr
			if(alphabet.contains(encryptedMsg.substring(i,i+1))){ //charAt() and isLetter()  giving me errors
				if(verbose){
					System.out.println("Index (before): " + alphabet.indexOf(encryptedMsg.substring(i,i+1)));
				}
				index = Math.abs(alphabet.indexOf(encryptedMsg.substring(i,i+1))-weight%25);
				if(verbose){
					System.out.println("Index (after): "+ index);
				}
				unencryptedMsg += alphabet.substring(index,index+1);
			} else {
				unencryptedMsg += encryptedMsg.charAt(i);
			}

			//attempt 2 with char[] alphabet
			if(new String(alphabet).contains(encryptedMsg.substring(i,i+1))){ //charAt() and isLetter()  giving me errors
				index = new String(alphabet).indexOf(encryptedMsg.substring(i,i+1));
				if(verbose){
					System.out.println("Index (before): " + index);
				}
				//if(index<0){
					//index = 25-Math.abs(index);
				//}else {
					index = Math.abs((index-weight));
				//}
				if(verbose){
					System.out.println("Index (after): "+ index);
				}
				unencryptedMsg += alphabet[index];
			} else {
				unencryptedMsg += encryptedMsg.charAt(i);
			}
			 */

			//attempt 3
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
			//System.out.print("From: " + encryptedMsg.charAt(i) + ": " + (int)encryptedMsg.charAt(i));
			//System.out.println(" To: " + unencryptedMsg.charAt(i) + ": " + (int)unencryptedMsg.charAt(i));
		}
		return unencryptedMsg;
	}
}

