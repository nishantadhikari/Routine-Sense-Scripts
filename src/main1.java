import java.sql.*;
import java.util.HashMap;


public class main1 {

	/**
	 * @param args
	 */
	public static int mindaystoeval=4;
	public static HashMap<Integer,userinfo> user=new HashMap<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MysqlCon ms=new MysqlCon();
		ms.show();
		HashMap<Integer,userinfo> usercopy=new HashMap<>(user);
		for(Object o:user.keySet()){
			System.out.println(o+" "+user.get(Integer.parseInt(o.toString())).name+" -> "+user.get(Integer.parseInt(o.toString())).logindays);
		}
		user.clear();
		for(int o:usercopy.keySet()){
			if(usercopy.get(o).logindays>=mindaystoeval){
				user.put(o,new userinfo(usercopy.get(o)));
			}
		}
		ms.calc();
		//ms.update_rank();
	}

}