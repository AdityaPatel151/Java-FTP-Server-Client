import java.io.*;
import java.net.*;
import java.util.*;

//server class
public class server {
	public static void main(String args[]) throws IOException{
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter the port number:");
		int port=Integer.parseInt(scan.nextLine());
		
		ServerSocket ss = new ServerSocket(port);
		Socket client = ss.accept();
		System.out.println("Connection estblished!");
		DataInputStream input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		FileInputStream fileInStream;
		FileOutputStream fileOutStream;
		OutputStream outStream=client.getOutputStream();
		InputStream inStream=client.getInputStream();
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        ObjectOutputStream objOutput = new ObjectOutputStream(client.getOutputStream());
        String command[]=null;
        
        do {
        	System.out.println("Waiting for a command...");
        	String response = input.readUTF();
        	command = response.split("\\s+");
        	switch(command[0]) {
        	
            case "get":

            	File sendFile = new File(command[1]);
            	
            	fileInStream=new FileInputStream(sendFile.getAbsolutePath());
            	byte[] getByteArray = new byte[(int)sendFile.length()];

            	fileInStream.read(getByteArray, 0, getByteArray.length);
            	outStream.write(getByteArray, 0, getByteArray.length);
                break;
        	
            case "put":
            	
            	byte[] putByteArray = new byte[1024];
				fileOutStream= new FileOutputStream(System.getProperty("user.dir")+"\\"+command[1]);
				inStream.read(putByteArray, 0, putByteArray.length);
				fileOutStream.write(putByteArray, 0, putByteArray.length);
            	break;
        	
            case "delete":
            	File del = new File(System.getProperty("user.dir")+"\\"+command[1]);
            	if(del.exists()) {
            		del.delete();
                	output.writeUTF(command[1] + " successfully deleted.");
            	}
            	else {
            		output.writeUTF("File does not exist!");
            	}
            	break;
        	
            case "ls":
            	File curDir = new File(System.getProperty("user.dir")); 
            	objOutput.writeObject(curDir.list());
            	break;
        	
            case "cd..":
            	File currentDir=new File(System.getProperty("user.dir"));
            	System.setProperty("user.dir", currentDir.getAbsoluteFile().getParent());
            	output.writeUTF("Directory changed to " + System.getProperty("user.dir"));
            	break;
        	
            case "cd":
            	if(command[1].equals("..")) {
            		File CurrentDir = new File(System.getProperty("user.dir"));
            		File parentDir=new File(System.getProperty("user.dir", CurrentDir.getAbsoluteFile().getParent()));
            		if(parentDir.exists()) {
            			System.setProperty("user.dir", CurrentDir.getAbsoluteFile().getParent());
                    	output.writeUTF("Directory changed to " + System.getProperty("user.dir"));
            		}
            		else {
            			output.writeUTF("No parent directory exists!");
            		}
            	}
            	else {
            		File changeDir = new File(System.getProperty("user.dir")+"\\"+command[1]);
            		if(changeDir.exists()) {
            			System.setProperty("user.dir", changeDir.getAbsoluteFile().getPath());
        				output.writeUTF("Directory changed to " + System.getProperty("user.dir"));
            		}
            		else {
            			output.writeUTF("No such directory exists!");
            		}
            	}
				break;
        	
            case "mkdir":
            	File newFile = new File(System.getProperty("user.dir")+"\\"+command[1]);
            	newFile.mkdir();
            	output.writeUTF("New directory named " + command[1] + " created.");
            	break;
        	
            case "pwd":
            	output.writeUTF(System.getProperty("user.dir"));
            	break;
        	
            case "quit":
            	client.close();
            	break;
        	
            default:	
            	System.out.println("Invalid command!");
            }
        	
        }while(!command[0].equals("quit"));
        	
	}
}
