
public class userinfo {
	public String name;
	public int logindays;
	userinfo(){
		name="";
		logindays=0;
	}
	userinfo(String n,int d){
		name=n;
		logindays=d;
	}
	public userinfo(userinfo another){
		this.name=another.name;
		this.logindays=another.logindays;
	}
}
