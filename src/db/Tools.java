package db;
import java.util.Date;
import java.text.SimpleDateFormat;
public class Tools {
	//判断参数message中是否存在敏感词汇
	public static boolean isSensitive(String message){
		return false;
	}
	public static String getNowTime(){
		String nowTime = null;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowTime = dateFormat.format(date);
		return nowTime;
	}
}
