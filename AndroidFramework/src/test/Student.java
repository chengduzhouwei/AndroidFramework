package test;

import java.util.Date;
import java.util.List;

import org.zw.android.framework.db.ColumnBinary;
import org.zw.android.framework.db.ColumnBoolean;
import org.zw.android.framework.db.ColumnByte;
import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnLong;
import org.zw.android.framework.db.ColumnShort;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.ColumnTimeStamp;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName = "Student")
public class Student extends Person {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id ;
	
	@ColumnByte
	private byte m ;
	
	@ColumnShort
	private short Sex ;
	
	@ColumnLong
	private long Height ;
	
	@ColumnFloat
	private float Score ;
	
	@ColumnDouble
	private double Score2 ;
	
	@ColumnDouble
	private double Score3 ;
	
	@ColumnDate
	private Date Birtherday ;
	
	@ColumnTimeStamp
	private Date Birtherday2 ;
	
	@ColumnBinary
	private byte[] Icon ;
	
	@ColumnText
	private String Info ;
	
	@ColumnBoolean
	private boolean used;
	
	private List<Person> Friend ;
	
	@ColumnBoolean
	private boolean Acheck ;
	
	@ColumnBoolean
	private boolean Buseddd ;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public byte getM() {
		return m;
	}

	public void setM(byte m) {
		this.m = m;
	}

	public short getSex() {
		return Sex;
	}

	public void setSex(short sex) {
		Sex = sex;
	}

	public long getHeight() {
		return Height;
	}

	public void setHeight(long height) {
		Height = height;
	}

	public float getScore() {
		return Score;
	}

	public void setScore(float score) {
		Score = score;
	}

	public double getScore2() {
		return Score2;
	}

	public void setScore2(double score2) {
		Score2 = score2;
	}

	public double getScore3() {
		return Score3;
	}

	public void setScore3(double score3) {
		Score3 = score3;
	}

	public Date getBirtherday() {
		return Birtherday;
	}

	public void setBirtherday(Date birtherday) {
		Birtherday = birtherday;
	}

	public Date getBirtherday2() {
		return Birtherday2;
	}

	public void setBirtherday2(Date birtherday2) {
		Birtherday2 = birtherday2;
	}

	public byte[] getIcon() {
		return Icon;
	}

	public void setIcon(byte[] icon) {
		Icon = icon;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public List<Person> getFriend() {
		return Friend;
	}

	public void setFriend(List<Person> friend) {
		Friend = friend;
	}

	public boolean isAcheck() {
		return Acheck;
	}

	public void setAcheck(boolean acheck) {
		Acheck = acheck;
	}

	public boolean isBuseddd() {
		return Buseddd;
	}

	public void setBuseddd(boolean buseddd) {
		Buseddd = buseddd;
	}
	
}
