package org.usd.csci.su15.tmbioc;

/** referred from http://www.mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/ **/

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RESTClientPost {
    public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("\n$ java RESTClientPost [Inputfile] [Trigger] Submit:[E-mail](optional)\n$ java RESTClientPost [Inputfile] GNormPlus [Taxonomy ID]\n		e.g., java RESTClientPost input.PubTator tmChem Submit:[PubTator username](optional)\n		e.g., java RESTClientPost input.PubTator GNormPlus 10090\n\nParameters:\n\n	[Inputfile]:The file you would like to process.\n	[Trigger]:tmChem|DNorm|tmVar|GNormPlus\n	[Taxonomy ID]: NCBI Taxonomy identifier (e.g., 10090 for mouse). The species you would like to focus on. Only avaliable for GNormPlus.\n\n");
		}
		else
		{
			String Inputfile=args[0];
			String Trigger=args[1];
			String Taxonomy="";
			if(args.length > 2)
			{
				Taxonomy=args[2];
			}
			
			try {
				//Submit
				URL url_Submit;
				if(Taxonomy != "")
				{
					url_Submit = new URL("http://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/" + Trigger + "/" + Taxonomy + "/");
				}
				else
				{
					url_Submit = new URL("http://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/" + Trigger + "/Submit/");
				}
				HttpURLConnection conn_Submit = (HttpURLConnection) url_Submit.openConnection();
				conn_Submit.setDoOutput(true);
				conn_Submit.setRequestMethod("POST");
				
				BufferedReader fr= new BufferedReader(new FileReader(Inputfile));
				String input="";
				String str = null;
				while((str = fr.readLine()) != null)
				{
					input=input+str+"\n";
				}
				OutputStream os = conn_Submit.getOutputStream();
				os.write(input.getBytes());
				os.flush();
				BufferedReader br_Sumbit = new BufferedReader(new InputStreamReader(conn_Submit.getInputStream()));
				String SessionNumber = br_Sumbit.readLine();
				conn_Submit.disconnect();

				String sub=Taxonomy.substring(0,7);
				String email=Taxonomy.substring(8,0); // added 1 ... JM
				if(sub.equals("Submit:"))
				{
					System.out.println("Thanks for your submission (Session number: " + SessionNumber + ").\nThe result will be sent to your E-mail: " + email + ".\n");
				}
				else
				{
					System.out.println(SessionNumber);
					
					//Receive
					String outputSTR="";
					String output="Not yet";
					try {
						while (output.equals("Not yet")) 
						{
							try {Thread.sleep(5000);} catch(InterruptedException e) {}
							
							URL url_Receive = new URL("http://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/" + SessionNumber + "/Receive/");
							HttpURLConnection conn_Receive = (HttpURLConnection) url_Receive.openConnection();
							conn_Receive.setDoOutput(true);
							conn_Receive.setRequestMethod("GET");
							BufferedReader br_Receive = new BufferedReader(new InputStreamReader(conn_Receive.getInputStream()));
							output = br_Receive.readLine();
							
							if(!output.equals("Not yet"))
							{
								outputSTR=output+"\n";
								while((output = br_Receive.readLine()) != null)
								{
									outputSTR=outputSTR+output+"\n";
								}
							}
							conn_Receive.disconnect();
						}
					}
					catch(NullPointerException e){}
					
					System.out.println(outputSTR);
				}
			}
			catch (MalformedURLException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
    }
}