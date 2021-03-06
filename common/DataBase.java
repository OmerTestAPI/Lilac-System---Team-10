package common;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.mysql.cj.jdbc.Blob;

import server.OStatus;
import server.Permissions;
import server.Size;

import java.awt.image.BufferedImage;
import java.io.*;
//

public class DataBase {
	private static DataBase _instance;

//static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

	// update USER, PASS and DB URL according to credentials provided by the
	// website:
	// https://remotemysql.com/
	// in future move these hard coded strings into separated config file or even
	// better env variables
	static private final String DB = "SmFGAHPAE1";
//	static private final String DB_URL = "https://remotemysql.com/phpmyadmin/" + DB + "?useSSL=false";
	static private final String DB_URL = "jdbc:mysql://remotemysql.com/" + DB + "?useSSL=false";
	static private final String LOCAL_DB_URL = "jdbc:MySql://localhost:3306/";
	static private final String LOCAL_DB_URL2 = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

	static private final String USER = "SmFGAHPAE1";
	static private final String PASS = "PrfUzkZFEX";
	static private final String LOCAL_USER = "root";
	static private final String LOCAL_PASS = "gunr1111";
	static private Connection conn;
	public static boolean workFlag = true;

	private DataBase() {
	}

	public static DataBase getRemoteInstance() {
		boolean printflag = true;
		if (_instance == null) {
			_instance = new DataBase();
			try {
				Class.forName(JDBC_DRIVER);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				printflag = false;
				workFlag = false;
			}
			if (printflag)
				System.out.println("<<<DATABASE>> CONNECTED TO DB");
			else
				System.out.println("<<<DATABASE>> ERROR WHILE CONNECTING");

		}
		return _instance;
	}

	public static DataBase getLocalInstance(String scheme) {
		boolean printflag = true;
		if (_instance == null) {
			_instance = new DataBase();
			try {
				Class.forName(JDBC_DRIVER);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(LOCAL_DB_URL + scheme + LOCAL_DB_URL2, LOCAL_USER, LOCAL_PASS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				printflag = false;
				workFlag = false;
			}
			if (printflag)
				System.out.println("<<<DATABASE>> CONNECTED TO DB");
			else
				System.out.println("<<<DATABASE>> ERROR WHILE CONNECTING");

		}
		return _instance;
	}

	public int getLastID(String object) {
		int maxID = 0;
		try {
			ResultSet rs = this.get_TableResultSet(object);
			while (rs.next()) {
				int id = (rs.getInt("ID"));
				if (maxID < id)
					maxID = id;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxID;
	}

	public ArrayList<Item> get_flowers() {
		ArrayList<Item> catalog = new ArrayList<Item>();
		try {
			ResultSet rs = this.get_TableResultSet("Item");

			while (rs.next()) {
				String Name = rs.getString("Name");
				int Price = rs.getInt("Price");
				String Kind = rs.getString("Kind");
				String Color = rs.getString("Color");
				int id = (rs.getInt("ID"));
				int Size = (rs.getInt("Size"));
				if (Size > server.Size.values().length - 1) {
					Size = server.Size.values().length - 1;
				}
				Item newitem = new Item(Name, Price, Kind, Color, server.Size.values()[Size], String.valueOf(id));
				System.out.println(newitem.to_String());
				catalog.add(newitem);
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}
		return catalog;

	}

	public Item get_flower(int iid) {

		try {
			ResultSet rs = this.get_TableResultSet("Item");

			while (rs.next()) {
				int id = (rs.getInt("ID"));
				if (iid == id) {
					String Name = rs.getString("Name");
					int Price = rs.getInt("Price");
					String Kind = rs.getString("Kind");
					String Color = rs.getString("Color");
					int Size = (rs.getInt("Size"));
					Item newitem = new Item(Name, Price, Kind, Color, server.Size.values()[Size], String.valueOf(id));
					return newitem;
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}
		return null;

	}

	public ArrayList<Item> get_flowers(String criteria, String wanted) {

		ArrayList<Item> catalog = new ArrayList<Item>();
		try {
			ResultSet rs = this.get_TableResultSet("Item");

			while (rs.next()) {
				String Name = rs.getString("Name");
				int Price = rs.getInt("Price");
				String Kind = rs.getString("Kind");
				String Color = rs.getString("Color");
				int Size = (rs.getInt("Size"));
				String id = (rs.getString("ID"));
				if (criteria.equals("Price")) {
					if (rs.getInt(criteria) == Integer.parseInt(wanted)) {
						Item newitem = new Item(Name, Price, Kind, Color, server.Size.values()[Size], id);
						catalog.add(newitem);
					}

				} else {
					if (rs.getString(criteria).equals(wanted)) {
						Item newitem = new Item(Name, Price, Kind, Color, server.Size.values()[Size], id);
						catalog.add(newitem);
					}
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}
		return catalog;

	}

	public ItemInOrder getItemsInOrder(int id) {
		Item removedItem = new Item("Item Removed", 0, "-", "-", Size.SMALL,"-1");

		ItemInOrder iio = new ItemInOrder(id, -1);
		try {
			ResultSet rs = this.get_TableResultSet("ItemInOrder");
			while (rs.next()) {
				int orderID = rs.getInt("OrderID");
				if (orderID == id) {
					int clientID = rs.getInt("ClientID");
					iio.setClientID(clientID);
					int iioID = rs.getInt("ID");
					int itemID = rs.getInt("ItemID");
					int amount = rs.getInt("Amount");
					Item item = get_flower(iioID);
					if (item == null) {
						System.out.println("NO ITEM IN DB");
						removedItem.setId(String.valueOf(iioID));
						item = removedItem;
					}
					for (int i = 0; i < amount; i++) {
						iio.addToList(item);
					}

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iio;

	}

	public ArrayList<Orders> get_orders() {
		ArrayList<Orders> orders = new ArrayList<Orders>();
		try {
			ResultSet rs = this.get_TableResultSet("Orders");

			while (rs.next()) {
				int id = rs.getInt("ID");
				int clientid = rs.getInt("ClientID");
				Timestamp time = rs.getTimestamp("Time");
				Timestamp orderdate = rs.getTimestamp("OrderDate");
				Timestamp deliverytime = rs.getTimestamp("DeliveryTime");
				int shipment = rs.getInt("Shipment_Method");
				String address = rs.getString("Address");
				int receiverPone = rs.getInt("ReciverPhone");
				String recivername = rs.getString("ReciverName");
				int totalCost = rs.getInt("TotalCost");
				int status = rs.getInt("Status");
				String greeting = rs.getString("Greeting");
				Orders order = new Orders(clientid, time, orderdate, shipment, address, receiverPone, recivername,
						deliverytime, totalCost, OStatus.values()[status], greeting);
				order.setID(id);
				order.setItemList(this.getItemsInOrder(id));
				System.out.println("from DB order id " + order.getID());
				orders.add(order);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return orders;
	}

	public ArrayList<Complaint> get_complaints() {

		ArrayList<Complaint> complaints = new ArrayList<Complaint>();
		try {
			ResultSet rs = this.get_TableResultSet("Complaint");

			while (rs.next()) {
				Timestamp date = rs.getTimestamp("Date");
				String text = rs.getString("TextField");
				int clientID = rs.getInt("ClientID");
				int Status = rs.getInt("Status");
				int OrderID = (rs.getInt("OrderID"));
				int cid = (rs.getInt("ID"));
				Complaint complaint = new Complaint(date, text, clientID, Status, OrderID, cid);
				complaints.add(complaint);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return complaints;

	}

	public ArrayList<Complaint> get_complaints(int id) {

		ArrayList<Complaint> complaints = new ArrayList<Complaint>();
		try {
			ResultSet rs = this.get_TableResultSet("Complaint");

			while (rs.next()) {
				Timestamp date = rs.getTimestamp("Date");
				String text = rs.getString("TextField");
				int clientID = rs.getInt("ClientID");
				int Status = rs.getInt("Status");
				int OrderID = (rs.getInt("OrderID"));
				int cid = (rs.getInt("ID"));
				Complaint complaint = new Complaint(date, text, clientID, Status, OrderID, cid);
				if (complaint.getClientID() == id)
					complaints.add(complaint);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return complaints;

	}

	public ArrayList<Complaint> get_complaints(String criteria, String wanted) {

		ArrayList<Complaint> complaints = new ArrayList<Complaint>();
		try {
			ResultSet rs = this.get_TableResultSet("Complaint");

			while (rs.next()) {
				Timestamp date = rs.getTimestamp("Date");
				String text = rs.getString("TextField");
				int clientID = rs.getInt("ClientID");
				int Status = rs.getInt("Status");
				int OrderID = (rs.getInt("OrderID"));
				int cid = (rs.getInt("ID"));
				if (criteria.equals("ClientID")) {
					if (rs.getInt(criteria) == Integer.parseInt(wanted)) {
						Complaint complaint = new Complaint(date, text, clientID, Status, OrderID, cid);

						complaints.add(complaint);
					}

				} else {
					if (rs.getString(criteria).equals(wanted)) {
						Complaint complaint = new Complaint(date, text, clientID, Status, OrderID, cid);
						complaints.add(complaint);

					}
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}
		return complaints;

	}

	public Person get_person(int id) {
		Person person = null;
		ResultSet rs;
		try {
			rs = this.get_TableResultSet("Person");

			while (rs.next()) {
				int _id = rs.getInt("ID");
				if (id == _id) {
					String firstname = rs.getString("FirstName");
					String lastname = rs.getString("LastName");
					String mail = rs.getString("EMail");
					int phone = rs.getInt("PhoneNumber");
					String credit = rs.getString("CreditCard");
					int age = rs.getInt("Age");
					String gender = rs.getString("Gender");
					String address = rs.getString("Address");
					String username = rs.getString("Username");
					String password = rs.getString("Password");
					person = new Person(firstname, lastname, _id, mail, phone, credit, age, gender, address, username,
							password);
					Permissions p = Permissions.valueOf(rs.getString("Permission"));
					System.out.println(p);
					person.setPermission(p);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return person;

	}

	public ArrayList<Person> get_persons() {
		ArrayList<Person> persons = new ArrayList<Person>();
		ResultSet rs;
		try {
			rs = this.get_TableResultSet("Person");

			while (rs.next()) {
				int _id = rs.getInt("ID");
				String firstname = rs.getString("FirstName");
				String lastname = rs.getString("LastName");
				String mail = rs.getString("EMail");
				int phone = rs.getInt("PhoneNumber");
				String credit = rs.getString("CreditCard");
				int age = rs.getInt("Age");
				String gender = rs.getString("Gender");
				String address = rs.getString("Address");
				String username = rs.getString("Username");
				String password = rs.getString("Password");
				Person person = new Person(firstname, lastname, _id, mail, phone, credit, age, gender, address,
						username, password);
				Permissions p = Permissions.valueOf(rs.getString("Permission"));
				person.setPermission(p);
				persons.add(person);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return persons;

	}

	public ArrayList<Employee> get_employees() {
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try {
			ResultSet rs = this.get_TableResultSet("Employee");

			while (rs.next()) {
				int id = rs.getInt("ID");
				Person person = get_person(id);
				int branchid = rs.getInt("BranchID");
				int rank = rs.getInt("Rank");
				employees.add(new Employee(branchid, person.getUsername(), person.getPassword(), person));

			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return employees;
	}

	public Client get_client(int id) {
		Client client = null;

		ResultSet rs;
		try {
			rs = this.get_TableResultSet("Client");

			while (rs.next()) {
				int _id = rs.getInt("ID");
				if (_id == id) {
					String user = rs.getString("Username");
					String password = rs.getString("Password");
					client = new Client(user, password, get_person(_id));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return client;
	}

	public int checkLogin_user(String username, String password) {
		ResultSet rs;
		try {
			rs = get_TableResultSet("Person");

			while (rs.next()) {
				String _password = rs.getString("Password");
				String _username = rs.getString("Username");
				if (_username.equals(username)) {
					if (_password.equals(password)) {
						return rs.getInt("ID");
					} else {
						return 0;
					}
				}

			}
			return -1;
		} catch (SQLException e) {
			System.out.println("DB ERROR");
			e.printStackTrace();
			return -2;

		}

	}

	public int checkLogin_user(String username) {
		ResultSet rs;
		try {
			rs = get_TableResultSet("Person");

			while (rs.next()) {
				String _username = rs.getString("Username");
				if (_username.equals(username)) {
					return rs.getInt("ID");

				}

			}
			return -1;
		} catch (SQLException e) {
			System.out.println("DB ERROR");
			e.printStackTrace();
			return -2;

		}

	}

	public DBERRORS add_to_DB(Object object) {

		try {
			String table = object.getClass().getSimpleName();
			if (this.exists_in_DB(object) > 0)//
				return DBERRORS.IDEXIST;
			Class cls = Person.class;
			boolean isAflag = cls.isInstance(object);
			boolean isntPerson = !(object.getClass().getSimpleName().equals(cls.getSimpleName()));
			if (isAflag && isntPerson) {
				Person p = new Person((Person) object);
				DBERRORS output = add_to_DB(p);
				if (output == DBERRORS.IDEXIST) {
					return DBERRORS.IDEXIST;
				}
				System.out.println(output);

			}

			ResultSet rs = this.get_TableResultSet(table);

			boolean id_flag = false;
//
			switch (table) {
			case "Item": {
				Item item = (Item) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO Item(Name, Price, Kind,Color,Size,ID,Image,URL) VALUES (?, ?, ?,?,?,?,?,?)");
				stmt1.setString(1, item.getName());
				stmt1.setInt(2, (int) item.getPrice());
				stmt1.setString(3, item.getKind());
				stmt1.setString(4, item.getColor());
				stmt1.setInt(5, item.getSize().ordinal());
				stmt1.setInt(6, Integer.valueOf(item.getId()));
				stmt1.setString(7, "NOTYET");
				stmt1.setString(8, "NOTYET");

				stmt1.executeUpdate();
				break;
			}
			case "Person": {
				Person person = (Person) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO Person(FirstName,LastName,ID,Email,PhoneNumber,CreditCard,Age,Gender,Address,Username,Password,Permission) VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?)");
				stmt1.setString(1, person.getFirstName());
				stmt1.setString(2, person.getLastName());
				stmt1.setInt(3, person.getId());
				stmt1.setString(4, person.getMail());
				stmt1.setInt(5, person.getPhone_number());
				stmt1.setString(6, person.getCredit_card());
				stmt1.setInt(7, person.getAge());
				stmt1.setString(8, person.getGender());
				stmt1.setString(9, person.getAddress());
				stmt1.setString(10, person.getUsername());
				stmt1.setString(11, person.getPassword());
				stmt1.setString(12, person.getPermission().toString());
				stmt1.executeUpdate();
				break;
			}
			case "Client": {
				Client client = (Client) object;
				PreparedStatement stmt1 = conn
						.prepareStatement("INSERT INTO Client(Username,Password,ID) VALUES (?, ?, ?)");
				stmt1.setString(1, client.getUsername());
				stmt1.setString(2, client.getPassword());
				stmt1.setInt(3, client.getId());
				stmt1.executeUpdate();

				break;
			}
			case "Employee": {
				Employee employee = (Employee) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO Employee(`Username`, `Password`, `BranchID`, `Rank`, `ID` ) VALUES (?, ?, ?, ?, ?)");
				stmt1.setString(1, employee.getUsername());
				stmt1.setString(2, employee.getPassword());
				stmt1.setInt(3, employee.getBranchID());
				stmt1.setInt(4, employee.getRank());
				stmt1.setInt(5, employee.getId());
				stmt1.executeUpdate();
				break;
			}
			case "ShopManager": {
				ShopManager shopManager = (ShopManager) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO ShopManager(`Username`, `Password`, `BranchID`, `ID` ) VALUES (?, ?, ?, ?)");
				stmt1.setString(1, shopManager.getUsername());
				stmt1.setString(2, shopManager.getPassword());
				stmt1.setInt(3, shopManager.getBranchID());
				stmt1.setInt(4, shopManager.getId());
				stmt1.executeUpdate();
				break;

			}
			case "ChainManager": {
				ChainManager cm = (ChainManager) object;
				PreparedStatement stmt1 = conn
						.prepareStatement("INSERT INTO ChainManager(Username,Password,ID) VALUES (?, ?, ?)");
				stmt1.setString(1, cm.getUsername());
				stmt1.setString(2, cm.getPassword());
				stmt1.setInt(3, cm.getId());
				stmt1.executeUpdate();
				break;
			}
			case "Orders": {
				Orders order = (Orders) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO Orders(`ID`, `ClientID`, `Time`, `OrderDate`, `Shipment_Method`, `Address`, `ReciverPhone`, `ReciverName`, `DeliveryTime`, `DeliveryCost`, `TotalCost`,`Status`,`Greeting`) VALUES(?, ?, ?,?,?,?,?,?,?,?,?,?,?)");
				stmt1.setInt(1, order.getID());
				stmt1.setInt(2, order.getClientID());
				stmt1.setTimestamp(3, order.getTime());
				stmt1.setTimestamp(4, order.getOrderDate());
				stmt1.setInt(5, order.getShipment_Method());
				stmt1.setString(6, order.getAddress());
				stmt1.setInt(7, order.getReciverPhone());
				stmt1.setString(8, order.getReciverName());
				stmt1.setTimestamp(9, order.getDeliveryTime());
				stmt1.setInt(10, order.getDeliveryCost());
				stmt1.setInt(11, order.getItemList().getSumOfitems() + order.getDeliveryCost());
				stmt1.setInt(12, order.getStatus().ordinal());
				stmt1.setString(13, order.getGreeting());
				System.out.println(add_to_DB(order.getItemList()));
				stmt1.executeUpdate();
				break;
			}
			case "ItemInOrder": {
				ItemInOrder items = (ItemInOrder) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO ItemInOrder(`ItemID`, `Amount`, `OrderID`, `ClientID`, `ID`) VALUES (?, ?, ?,?,?)");
				ArrayList<Item> itemList = new ArrayList(items.getItemList());
				stmt1.setInt(3, items.getOrderID());
				stmt1.setInt(4, items.getClientID());
				stmt1.setInt(5, items.getID());
				for (int i = 0; i < itemList.size(); i++) {
					int amount = 1;
					Item current = itemList.get(i);
					for (int j = itemList.size() - 1; j > i; j--) {
						if (itemList.get(j).getId() == current.getId()) {
							amount++;
							itemList.remove(j);
						}
					}
					stmt1.setInt(1, Integer.valueOf(current.getId()));
					stmt1.setInt(2, amount);
					stmt1.executeUpdate();

				}
				break;

			}
			case "Complaint": {
				int id = getLastID("Complaint"); // auto id setting
				Complaint complaint = (Complaint) object;
				PreparedStatement stmt1 = conn.prepareStatement(
						"INSERT INTO Complaint(`Date`, `TextField`, `ClientID`, `Status`, `OrderID`, `ID`) VALUES (?, ?, ?,?,?,?)");
				stmt1.setTimestamp(1, complaint.getDate());
				stmt1.setString(2, complaint.getText());
				stmt1.setInt(3, complaint.getClientID());
				stmt1.setInt(4, complaint.getStatus());
				stmt1.setInt(5, complaint.getOrderID());
				stmt1.setInt(6, id);
				stmt1.executeUpdate();
				break;

			}

			}

			if (this.exists_in_DB(object) > 0)
				return DBERRORS.COMPLETE;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DBERRORS.UNKNOWN;

	}

	public DBERRORS delete_from_DB(Object object) {
		try {
			String table = object.getClass().getSimpleName();
			if (table.equals("Orders")) { // to delete item in order.
				Orders order = (Orders) object;
				System.out.println("hey");
				System.out.println("Sending to delete" + order.getItemList().getClass().getSimpleName());
				System.out.println(delete_from_DB(order.getItemList()));
			}
			Class cls = Person.class;
			boolean isAflag = cls.isInstance(object);
			boolean isntPerson = !(object.getClass().getSimpleName().equals(cls.getSimpleName()));
			ResultSet rs = this.get_TableResultSet(table);
			if (isAflag && isntPerson) { // if its is Client, emp , sm, cm etc..
				Person p = new Person((Person) object);
				DBERRORS out = delete_from_DB(p);
				if (out == DBERRORS.NOID)
					return out;
			}
			boolean id_flag = false;
			while (rs.next()) { // should do something faster then this , mabye search see if not empty then
								// delete in 2 commands in stead of while on everthing.
				int id = (rs.getInt("ID"));
				if (String.valueOf(id).equals((object.toString()))) {
					id_flag = true;
					PreparedStatement st = conn.prepareStatement("DELETE FROM " + table + " WHERE ID = ?");
					st.setInt(1, Integer.valueOf(object.toString()));
					st.executeUpdate();
					break;
				}
			}
			if (!id_flag)
				return DBERRORS.NOID;
			if (this.exists_in_DB(object) == 0)
				return DBERRORS.COMPLETE;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DBERRORS.UNKNOWN;

	}

	public DBERRORS update_in_DB(Object object) {
		DBERRORS delete = delete_from_DB(object);
		DBERRORS add = add_to_DB(object);
		boolean deleteF = (delete == DBERRORS.COMPLETE);
		boolean addF = (add == DBERRORS.COMPLETE);
		if (addF && deleteF)
			return DBERRORS.COMPLETE;
		else
			return DBERRORS.UNKNOWN;

	}

	public int exists_in_DB(Object object) {
		String table = object.getClass().getSimpleName();
		try {
			Class cls = Person.class;
			boolean isAccountflag = cls.isInstance(object);
			if (isAccountflag) {
				ResultSet rs = this.get_TableResultSet("Person");
				Person p = (Person) object;
				while (rs.next()) {
					String username = (rs.getString("Username"));
					if (username.equals(p.getUsername()))
						return 2;
				}
			}
			ResultSet rs = this.get_TableResultSet(table);
			while (rs.next()) {
				String id = (rs.getString("ID"));
				if (id.equals(object.toString()))
					return 1;
			}
			return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public int add_image_to_item(int itemID, BufferedImage imm) {
		byte[] immAsBytes;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// use another encoding if JPG is innappropriate for you
		try {
			ImageIO.write(imm, "jpg", baos);

			baos.flush();
			immAsBytes = baos.toByteArray();
			baos.close();
			PreparedStatement pstmt = conn.prepareStatement("UPDATE Item set `Image`=(?) WHERE `ID` = (?)");
			ByteArrayInputStream bais = new ByteArrayInputStream(immAsBytes);
			pstmt.setBinaryStream(1, bais, immAsBytes.length);
			pstmt.setInt(2, itemID);
			pstmt.executeUpdate();
			pstmt.close();
			return 1;
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public BufferedImage get_imageDB(int itemID) {
		Statement stmt;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			PreparedStatement prep_stmt = conn.prepareStatement("SELECT Image FROM Item where id = (?)");
			prep_stmt.setInt(1, itemID);
			ResultSet rs = get_TableResultSet("Item");
			while (rs.next()) {
				String id = (rs.getString("ID"));
				if (id.equals(String.valueOf(itemID)))
					break;
			}
			Blob immAsBlob = (Blob) rs.getBlob("Image");
			byte[] immAsBytes = immAsBlob.getBytes(1, (int) immAsBlob.length());
			InputStream in = new ByteArrayInputStream(immAsBytes);
			BufferedImage imgFromDb = ImageIO.read(in);
			return imgFromDb;

		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public byte[] get_imageDBasByte(int itemID) {
		Statement stmt;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			PreparedStatement prep_stmt = conn.prepareStatement("SELECT Image FROM Item where id = (?)");
			prep_stmt.setInt(1, itemID);
			ResultSet rs = get_TableResultSet("Item");
			while (rs.next()) {
				String id = (rs.getString("ID"));
				if (id.equals(String.valueOf(itemID)))
					break;
			}
			Blob immAsBlob = (Blob) rs.getBlob("Image");
			byte[] immAsBytes = immAsBlob.getBytes(1, (int) immAsBlob.length());
			return immAsBytes;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private ResultSet get_TableResultSet(String table) throws SQLException {
		Statement stmt;
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		PreparedStatement prep_stmt = conn.prepareStatement("SELECT * FROM " + table);
		ResultSet rs = prep_stmt.executeQuery();
		return rs;
	}

	private ResultSet get_TableResultSet(String table, String criteria) throws SQLException {
		Statement stmt;
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		PreparedStatement prep_stmt = conn.prepareStatement("SELECT " + criteria + "FROM " + table);
		ResultSet rs = prep_stmt.executeQuery();
		return rs;
	}

	public void table_delete(String table) {
		try {
			PreparedStatement st = conn.prepareStatement("TRUNCATE " + table);
			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void finalize() throws SQLException {
		System.out.println("<<<DATABASE>> Closing Connection");
		conn.close();
	}
}
