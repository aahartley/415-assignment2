/**
 * ***
 **
 ** USCA ACSC415 *
//Austin Hartley
 */
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


class TCPServer2 {

    public static void main(String argv[]) throws Exception {
        String clientSentence = " ";
        String serverSentence = " ";
       
        ServerSocket welcomeSocket=null;
        HashMap<String,String> map = new HashMap<>();{
        	map.put("png", "image/png\r\n");
        	map.put("html", "text/html\r\n");
        	map.put("pdf","application/pdf\r\n");
        	map.put("txt","text/plain\r\n");
        	
        };

        // Create Server Socket
        try {
            welcomeSocket= new ServerSocket(80);
            welcomeSocket.setSoTimeout(0);

            while (true) {
                // create client socket, blocking if no incoming request.
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Accept connection from" + connectionSocket.getRemoteSocketAddress());

                // input stream
                Scanner inFromClient = new Scanner(connectionSocket.getInputStream());
             
 
                // ouput stream
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                // get the message from client      
         
                if(!inFromClient.hasNextLine()) {
                	
                }else {
                clientSentence =inFromClient.nextLine();
         
                
                
                System.out.println("From Client:" + clientSentence);
                

                //Tokenize this line, check whether it is a valid request.
                String[] temp = clientSentence.split(" ");
                boolean valid =false;
                System.out.println("Http Request Method:" + temp[0]);
                if(temp[0].equals("GET"))
                	valid = true;
                System.out.println("File path: "+ temp[1]);
                if(!valid) {
                	 serverSentence = "HTTP/1.1 400 Bad Request\r\nContent-Type: text/html\r\nContent-Length: 0\r\n\r\n"; //clientSentence.toUpperCase();  //change to uppercase 
                     outToClient.writeBytes(serverSentence);
                }
                else {
                	String responseLine="";
                	String type="";
                	String length="";
                	String end="\r\n";
                	String body="";
                
                if(temp[1].equals("/favicon.ico")) {
                 	responseLine ="HTTP/1.1 418 I'm a teapot\r\n";
                	type="Content-Type: text/html\r\n";
                	body = "";
                	byte[] buffer = body.getBytes();
                	length="Content-Length: "+buffer.length+"\r\n";
                	serverSentence= responseLine+type+length+end+body;
                    outToClient.writeBytes(serverSentence);                }
       
                else if(temp[1].equals("/")) {
        			Path inFilePath = Paths.get("index.html");
                	File myFile = new File(inFilePath.toString());
                	responseLine ="HTTP/1.1 200 OK\r\n";
                	type="Content-Type: text/html\r\n";
                	byte[] buffer = Files.readAllBytes(inFilePath);
                	length="Content-Length: "+buffer.length+"\r\n";
                	serverSentence= responseLine+type+length+end;
                	outToClient.writeBytes(serverSentence);
                	outToClient.write(buffer,0,buffer.length);

                }
              
                else {
                	//game.html from https://www.w3schools.com/graphics/tryit.asp?filename=trygame_default_gravity
                	Path inFilePath = Paths.get(temp[1].substring(1, temp[1].length()));
                	try {
                		File myFile = new File(inFilePath.toString());
                    	byte[] buffer = Files.readAllBytes(inFilePath);
                    	responseLine ="HTTP/1.1 200 OK\r\n";
                    	String itype = temp[1].substring(temp[1].lastIndexOf('.')+1);
                    	if(map.containsKey(itype))
                    		type="Content-Type: "+map.get(itype);
                    	else //if file type is added without updating map, put it as html
                    		type="Content-Type: "+"text/html\r\n";
                    	length="Content-Length: "+buffer.length+"\r\n";
                    	serverSentence= responseLine+type+length+end;
                        outToClient.writeBytes(serverSentence);
                        outToClient.write(buffer,0,buffer.length);

                	}catch(IOException e){
                		responseLine ="HTTP/1.1 404 Not Found\r\n";
                    	type="Content-Type: text/html\r\n";
                    	body = "";
                    	byte[] buffer = body.getBytes();
                    	length="Content-Length: "+buffer.length+"\r\n";
                    	serverSentence= responseLine+type+length+end+body;
                        outToClient.writeBytes(serverSentence);
                	}
                   	
                }}}


                // close stream and socket
                inFromClient.close();
                outToClient.close();
                connectionSocket.close();

            }
        }catch (IOException e) {
            System.err.println("Caught Exception " + e.getMessage());
        }finally{
            if (welcomeSocket !=null ) welcomeSocket.close();  // no need in java 7 above
        }

    }

}