package common;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


import client.LClient;
import server.Server;

public class Main {

	public static void main(String[] args) {
	/*	Complaint complaint1 = new Complaint(new Date(2002,12,12),"not okay",1,1,1);
		Complaint complaint2 = new Complaint(new Date(2002,12,12),"not okay",2,2,1);
		Complaint complaint3 = new Complaint(new Date(2002,12,12),"not okay",3,1,1);
		Complaint complaint4 = new Complaint(new Date(2002,12,12),"not okay",4,3,4);
		Complaint complaint5 = new Complaint(new Date(2002,12,12),"not okay",2,1,4);
		DataBase myDB = DataBase.getInstance();
		myDB.add_to_DB(complaint5);
		myDB.add_to_DB(complaint4);
		myDB.add_to_DB(complaint3);
		myDB.add_to_DB(complaint2);
		myDB.add_to_DB(complaint1);
		ArrayList<Complaint> search = new ArrayList<Complaint>();
		search=myDB.get_complaints();
		System.out.println("ALL");
		for(int i=0; i<search.size();i++)
		{
			String id =search.get(i).toString();
			System.out.println(id);
		}
		search=myDB.get_complaints("ClientID","2");
		System.out.println("ClientID");
		for(int i=0; i<search.size();i++)
		{
			String id =search.get(i).toString();
			System.out.println(id);
		}
		search=myDB.get_complaints("Status","1");
		System.out.println("ClientID");
		for(int i=0; i<search.size();i++)
		{
			String id =search.get(i).toString();
			System.out.println(id);
		}
		search=myDB.get_complaints("OrderID","1");
		System.out.println("ClientID");
		for(int i=0; i<search.size();i++)
		{
			String id =search.get(i).toString();
			System.out.println(id);
		}
		*/
		DataBase myDB = DataBase.getInstance();
		myDB.table_delete("Item");
		Item lilac =new Item("lilac",10,"lilac","purple","big");
		myDB.add_to_DB(lilac);
		try {
			BufferedImage imm = ImageIO.read(new File("C:\\Users\\user\\Pictures\\a.jpg"));


			myDB.add_image_to_item(Integer.parseInt(lilac.getId()), imm);
			imm = myDB.get_imageDB(Integer.parseInt(lilac.getId()));
			File outputfile = new File("C:\\Users\\user\\Pictures\\imageResize4.jpg");
			ImageIO.write(imm, "jpg", outputfile);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Server myServer= new Server(5555);
		try {
			myServer.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		LClient client =new LClient("127.0.0.1",5555);
		try {
			client.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Person p = new Person("omer","lerman",2,"gmail",11,"22",11,"male","haifa","omer","lerman");
		Person p1 = new Person("omer","lerman",2,"gmail",11,"22",11,"male","haifa","omer","lerman");
		Client c  = new Client("omer","lerman",3,"gmail",333,"fs",22,"male","sss","omer","lerman");

		Massage m = new Massage (null,Commands.GETCATALOG);
		try {
			client.sendToServer(m);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		Massage msg = client.getReturnMassage();
		System.out.println("ALL");
	
		
		
		

		
	}

}
