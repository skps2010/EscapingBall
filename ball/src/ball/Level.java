package ball;

public class Level {
	String name;
	int x;
	int y;
	char[] level;
	
	public Level(String n,int x,int y,String l)
	{
		name=n.replaceAll(".txt", "");
		this.x=x;
		this.y=y;
		level=l.toCharArray();
	}
}
