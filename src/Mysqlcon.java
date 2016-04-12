import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import com.mysql.jdbc.*;
import javax.activation.DataContentHandlerFactory;

class MysqlCon{  
	Connection con;
	public void opencon(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:9990/RoutineSense","nishant1417","Amuc11");  
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void closecon(){
		try{
			con.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void show(){
		try {
			opencon();
			for(int i=28;i>=1;i--){
				showfornthday(i);
			}
			closecon();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void showfornthday(int d){
		try{
			Statement stmt=con.createStatement();
			String query="select distinct(username),userID from SensorDataApp_userinfo where datediff(now(),logintime)="+d;
			ResultSet rs=stmt.executeQuery(query);  
			
			while(rs.next()){  
				if(main1.user.containsKey(rs.getInt(2))){
					//System.out.println("hi");
					main1.user.get(rs.getInt(2)).logindays++;
				}
				else{
					main1.user.put(rs.getInt(2),new userinfo(rs.getString(1),1));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			} 
	}
	public double user_activity(int id,String name,int u,Date s,Date e){
		double time=0.0;
		HashMap<String,String> temp=new HashMap<String, String>();
		try {
			Statement stmt=con.createStatement();
			String query="select min(id) as id, sum(time_to_sec(time)) as tottime, Activity from (select min(id) as id, min(timestamp) as start_time, max(timestamp) as end_time, datediff(max(timestamp), min(timestamp)) as total, timediff(max(timestamp), min(timestamp)) as time, Activity as Activity from (select @r := @r+(@Activity !=Activity) AS gn, @Activity := Activity as sn, s.* from (select @r := 0, @Activity := 0) vars, (select * from activityData_user_"+id+" where DATE(timestamp)>='"+s+"' and DATE(timestamp)<='" +e+"') s order by id) q group by gn) as tt  group by Activity;";
			ResultSet rs=stmt.executeQuery(query);
			temp.clear();
			System.out.println();
			while(rs.next()){
				//System.out.println(id+" -> "+rs.getString(1)+","+rs.getString(2)+","+rs.getString(3));
				temp.put(rs.getString(3),rs.getString(2));
				time=time+tomin(rs.getString(2));
			}
			System.out.print(id+","+name+",User"+u+",");
			printact(temp);
			return time;
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return time;
	}
	public void calc(){
		opencon();
		int start=28;
		int end=1;
		int duration=7;
		int loops=start/duration;
		try {
			Statement stmt=con.createStatement();
			String query="select date(date_sub(now(),interval "+start+" day));";
			ResultSet rs=stmt.executeQuery(query);
			rs.next();
			Date startd=rs.getDate(1);
			Date endd=null;
			for(int i=loops-1;i>=0;i--){
				query="select date(date_sub(now(),interval "+(i*7+1)+" day));";
				rs=stmt.executeQuery(query);
				rs.next();
				endd=rs.getDate(1);
				System.out.println("\n\n\n\n\n\n\n\n\n\n\nWeek"+(loops-i)+","+startd+" ,"+endd);
				double t=0;
				int u=0;
				
				System.out.print("ID,name,user_num,STILL,TILTING,ON_FOOT,ON_BICYCLE,IN_VEHICLE,UNKNOWN");
				for(int o:main1.user.keySet()){
					u++;
					t+=user_activity(o,main1.user.get(o).name,u,startd,endd);
				}
				System.out.println("\n"+t+","+u+","+t/(double)u);
				
				/*
				System.out.print("ID,name,user_num,Academic Building,Lecture Hall,Library,Girls Hostel,Boys Hostel,Residence,Dining Block,Service Block,Badminton Court,Unknown");
				for(int o:main1.user.keySet()){
					u++;
					t+=user_location(o,main1.user.get(o).name,u,startd,endd);
				}
				System.out.println("\n"+t+","+u+","+t/(double)u);
				*/
				query="select date(date_sub(now(),interval "+(i*7)+" day));";
				rs=stmt.executeQuery(query);
				rs.next();
				startd=rs.getDate(1);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closecon();
	}
	void printact(HashMap<String,String> temp){
		if(temp.containsKey("STILL")){
			System.out.print(tomin(temp.get("STILL"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("TILTING")){
			System.out.print(tomin(temp.get("TILTING"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("ON_FOOT")){
			System.out.print(tomin(temp.get("ON_FOOT"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("ON_BICYCLE")){
			System.out.print(tomin(temp.get("ON_BICYCLE"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("IN_VEHICLE")){
			System.out.print(tomin(temp.get("IN_VEHICLE"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("UNKNOWN")){
			System.out.print(tomin(temp.get("UNKNOWN")));
		}
		else{
			System.out.print("0");
		}
	}
	void printloc(HashMap<String,String> temp){
		//AC = Academic Block
		//BH = Boys Hostel
		//OU = Boys Hostel Badminton court
		//DB = Dinning Block
		//GH = Girls Hostel
		//LC = Lecture Hall
		//LB = Library
		//RE = Residence
		//SR = Service Block
		if(temp.containsKey("AC")){
			System.out.print(tomin(temp.get("AC"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("LC")){
			System.out.print(tomin(temp.get("LC"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("LB")){
			System.out.print(tomin(temp.get("LB"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("GH")){
			System.out.print(tomin(temp.get("GH"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("BH")){
			System.out.print(tomin(temp.get("BH"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("RE")){
			System.out.print(tomin(temp.get("RE"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("DB")){
			System.out.print(tomin(temp.get("DB"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("SR")){
			System.out.print(tomin(temp.get("SR"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("OU")){
			System.out.print(tomin(temp.get("OU"))+",");
		}
		else{
			System.out.print("0,");
		}
		if(temp.containsKey("UN")){
			System.out.print(tomin(temp.get("UN")));
		}
		else{
			System.out.print("0");
		}
	}
	
	double tomin(String s){
		double x=0;
		x=Double.parseDouble(s)/60.0;
		x=x/60.0;
		x=Math.round(x*1000.0)/1000d;
		return x;
	}
	
	/*
	int tomin(String s){
		int x=0;
		x=Integer.parseInt(s)/60;
		return x;
	}
	*/
	public double user_location(int id,String name,int u,Date s,Date e){
		double time=0.0;
		HashMap<String,String> temp=new HashMap<String, String>();
		try {
			Statement stmt=con.createStatement();
			String query="select min(id) as id, sum(time_to_sec(time)) as tottime, Name from (select min(id) as id, min(timestamp) as start_time, max(timestamp) as end_time, datediff(max(timestamp), min(timestamp)) as total, timediff(max(timestamp), min(timestamp)) as time, Name as Name from (select @r := @r+(@Name !=Name) AS gn, @Name := Name as sn, s.* from (select @r := 0, @Name := 0) vars, (select * from wifiDataFeatures_user_" + id +" where DATE(timestamp)>='"+ s +"' and DATE(timestamp)<='" + e +"') s order by id) q group by gn) as tt  group by Name;";
 			ResultSet rs=stmt.executeQuery(query);
			temp.clear();
			System.out.println();
			while(rs.next()){
				//System.out.println(id+" -> "+rs.getString(1)+","+rs.getString(2)+","+rs.getString(3));
				temp.put(rs.getString(3),rs.getString(2));
				time+=tomin(rs.getString(2));
			}
			//System.out.print(id+","+name+",");
			System.out.print(id+","+name+",User"+u+",");
			printloc(temp);
			return time;
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return time;
	}
	public void user_rank(int id,String name,Date s,Date e){
		HashMap<String,String> temp=new HashMap<String, String>();
		try {
			Statement stmt=con.createStatement();
			String query="select min(id) as id, sum(time_to_sec(time)) as tottime, Name from (select min(id) as id, min(timestamp) as start_time, max(timestamp) as end_time, datediff(max(timestamp), min(timestamp)) as total, timediff(max(timestamp), min(timestamp)) as time, Name as Name from (select @r := @r+(@Name !=Name) AS gn, @Name := Name as sn, s.* from (select @r := 0, @Name := 0) vars, (select * from wifiDataFeatures_user_" + id +" where DATE(timestamp)>='"+ s +"' and DATE(timestamp)<='" + e +"') s order by id) q group by gn) as tt  group by Name;";
 			ResultSet rs=stmt.executeQuery(query);
			temp.clear();
			System.out.println();
			while(rs.next()){
				System.out.println(id+" -> "+rs.getString(1)+","+rs.getString(2)+","+rs.getString(3));
				temp.put(rs.getString(3),rs.getString(2));
			}
			System.out.print(id+","+name+",");
			//printact(temp);
			
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void update_rank(){
		opencon();
		System.out.println("\n\n\n\n");
		try{
			Statement stmt=con.createStatement();
			String query="select @i:=@i+1 AS rank,a.*,curdate() from (select * from SensorDataApp_rankinfo ORDER BY Totaltime DESC)a,(SELECT @i:=0) r;";
			ResultSet rs=stmt.executeQuery(query);
			while(rs.next()){
				System.out.println(rs.getString(1)+","+rs.getString(2)+","+rs.getString(3)+","+rs.getString(4)+","+rs.getString(5));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		closecon();
	}
}  
