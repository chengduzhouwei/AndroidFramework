package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.IBitmapDownloader;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.xml.XMLFactory;
import org.zw.android.framework.xml.XMLParserCallback;

import test.service.AppHanlder;
import test.service.IMessageDefine;
import test.service.WebServiceImpl;
import android.os.Bundle;
import android.os.Message;

import com.google.gson.Gson;

// @InjectLayout(layout=R.layout.activity_main)
public class MainActivity extends BaseActivity {
	
	static String url = "http://img10.3lian.com/d0214/file/2011/11/25/89a92fa59782aa6eb6bf106912a29fab.jpg" ;
	static String url2 = "http://i8.hexunimg.cn/2011-12-06/136028951.jpg" ;
	
	// @InjectView(layout=R.layout.test_layout)
	//LinearLayout layout ;
	
	// @InjectView(id=R.id.bitmap_view)
	//RecyclingImageView view ;
	
	// @InjectView(id=R.id.bitmap_view_2)
	//RecyclingImageView view2 ;

	//@InjectView(parent="layout",id=R.id.test_txt)
	//TextView name ;
	
	static final String dbName_A = "zhouwei_1.db" ;
	static final String dbName_B = "zhouwei_2.db" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// orm 实现
		IAccessDatabase db 			= mFramework.openDefaultDatabase() ;
		IAccessDatabase db2 		= mFramework.openDatabaseByName(dbName_A, 0);
		IAccessDatabase db3 		= mFramework.openDatabaseByName(dbName_B, 0);
		
		// 图片下载实现
		IBitmapDownloader downloader = mFramework.getBitmapDownloader() ;
		
		// 下载图片
		//downloader.downloadBitmap("mnt/sdcard/journey_time.png", view) ;
		//downloader.downloadBitmap(url2, view2, R.drawable.ic_launcher) ;
		
		// 调用业务层实现
		WebServiceImpl.getWebService().login("zhouwei", "123456", mHandler);
		
//		TextView tv = new TextView(this);
//		tv.setText("dd");
//		tv.invalidate() ;
		
		// 
		db.deleteTable(Student.class);
		db.createTable(Student.class);
		
		db2.deleteTable(Student.class);
		db3.deleteTable(Student.class);
		
//		mFramework.getAsyncExecutor().executeTask(new IAsyncTask() {
//			
//			@Override
//			public Object onProcessing() {
//				System.out.println("-----------------");
//				
//				return null;
//			}
//		}) ;
		
//		db.deleteTable(Student.class);
//		db.execute("select * from student", null);
//		db.updateTable(Student.class);
//		
//		
//		// 查询数据库
		List<Student> list = db.queryObjects("select * from Student", Student.class);
//		
		List<Student> l = new ArrayList<Student>();
		byte[] icon = new byte[]{0,1,2,3,4};
//		
		List<Person> fs= new ArrayList<Person>();
//		Person f = new Person() ;
//		f.setName("朋友1");
//		f.setAddress("北京");
//		fs.add(f);
//		f = new Person() ;
//		f.setName("朋友2");
//		f.setAddress("上海");
//		fs.add(f);
//		f = new Person() ;
//		f.setName("朋友3");
//		f.setAddress("成都");
//		fs.add(f);
//		f = new Person() ;
//		f.setName("朋友4");
//		f.setAddress("广州");
//		fs.add(f);
//		
		Student stu = new Student() ;
		stu.setName("周伟");
		stu.setM((byte)128);// byte
		stu.setSex((short)2);// short
		stu.setHeight(185);// long
		stu.setScore(98.2f);// float
		stu.setScore2(100.9012);// double
		stu.setScore3(10000.99909);// real
		stu.setBirtherday(new Date());// date
		stu.setBirtherday2(DateUtils.toDate("1986-10-27 10:00:00",DateUtils.DATE_TIME_FORMAT));// timestamp
		stu.setIcon(icon) ;// byte
		stu.setAddress("成都市高新区新乐北巷2号");// varchar
		stu.setInfo("这是orm 映射--------------------------------------");
		stu.setUsed(true);
		stu.setAcheck(true);
		stu.setFriend(fs);
		
		db.saveObject(stu);
		
		db2.saveObject(stu);
		db3.saveObject(stu);
		
//		l.add(stu);
		
		String xml = XMLFactory.toXML(stu);
		
		Student s = new Student() ;
		
		XMLFactory.toObjectFromXml(xml, s,new XMLParserCallback<Student>() {

			@Override
			public void startTag(Student t, String tag, XmlPullParser parser) {
				
				if(tag.equals("")){
					
				}
				
				System.out.println(tag + "=" + parser);
			}

			@Override
			public void endTag(Student t, String tag, XmlPullParser parser) {
				System.out.println("end" + tag );
			}

			@Override
			public void startDocument() {
				
			}
		});
		
		stu = new Student() ;
		stu.setName("周伟2");
		stu.setSex((short)2);
		stu.setBirtherday(new Date());
		stu.setIcon(icon) ;
		stu.setM((byte)128);
		stu.setAddress("成都市高新区新乐北巷2号");
		l.add(stu);
		
		stu = new Student() ;
		stu.setName("周伟3");
		stu.setSex((short)2);
		stu.setBirtherday(new Date());
		stu.setIcon(icon) ;
		stu.setM((byte)128);
		stu.setAddress("成都市高新区新乐北巷2号");
		l.add(stu);
		
//		db.saveObjectList(l);
////		
		list = db.queryObjects("select * from Student", Student.class);
		
		List<Student> listA = db2.queryObjects("select * from Student", Student.class);
		
		List<Student> listB = db3.queryObjects("select * from Student", Student.class);
//		
		System.out.println("--------" + list);
		
		File file = new File(db2.getDatabasePath()) ;
		
		System.out.println(">>" + file.exists());
		
		db2.removeDatabse() ;
		db3.removeDatabse() ;
		db.removeDatabse() ;
//		
//		list = db.queryObjects("select * from Student where sex = ?", new String[]{String.valueOf(2)}, Student.class);
		
//		List<Student> list = new ArrayList<Student>();
//		
//		for(int i = 0 ; i < 1000 ; i++){
//			Student stu = new Student();
//			stu.setName("zhouwei id = " + i);
//			list.add(stu);
//		}
//		
//		long enter = System.currentTimeMillis() ;
//		db.saveObjectList(list);
//		System.out.println(" 消耗时间 >> " + (System.currentTimeMillis() - enter));
//		
//		int index = 0 ;
////		
//		for(Student su : list){
//			
////			db.deleteObject(su);
//			
////			su.setName("insert " + su.getName() + " id = " + index);
////			
////			db.saveObject(su);
////			
////			index++ ;
//		}
//		
//		list = db.queryObjects("select * from Student", Student.class);
//		
//		for(Student su : list){
//			
//			if(su == null || su.getName() == null){
//				System.out.println("----------");
//			} else {
//				System.out.println(su.getName());
//			}
//		}
		
		
		String jsonData = "{\"username\":\"arthinking\",\"userId\":1}"; 
		
		Gson gson = new Gson(); 
		User user = gson.fromJson(jsonData, User.class); 
		
		
		System.out.println(user.getUsername()); 
		System.out.println(user.getUserId()); 
	}

	@Override
	protected AppHanlder getAppHanlder() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				// 登陆成功
				case IMessageDefine.MSG_TASK_LOGIN_SUCCESS :
					
					break ;
				// 登陆失败
				case IMessageDefine.MSG_TASK_LOGIN_FAILED :
					
					break ;
				}
			}
			
		};
	}

}
