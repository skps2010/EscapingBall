package ball;

/**
 * 關卡狀態
 */
public class Way {
	/**
		 * 球的位置
		 */
	int[] ball;
	/**
		 * 移動紀錄
		 */
	his[] way;
	
	public Way(int[] a,his[] b)
	{
		ball=a;
		way=b;
	}
}

/**
 * 歷史紀錄
 */
class his{
	/**
	 *球的編號
	 */
	int ball;
	/**
	 *方向
	 */
	int dir;
	
	his(int b,int d)
	{
		ball=b;
		dir=d;
	}
}
