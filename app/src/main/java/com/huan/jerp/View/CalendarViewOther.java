package com.huan.jerp.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.huan.jerp.R;
import com.huan.jerp.adapter.CalendarAdapter;
import com.huan.jerp.data.Sign;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

//次日历通过Id获得考勤记录

public class CalendarViewOther extends LinearLayout implements OnClickListener {

	private final String TAG = CalendarViewOther.class.getSimpleName();
	private int year_c = 0;// 今天的年份
	private int month_c = 0;// 今天的月份
	private int day_c = 0;// 今天的日期
	private String currentDate = "";
	private Context mContext;
	private TextView currentMonth;// 显示日期
	private ImageView prevMonth;// 去上一个月
	private ImageView nextMonth;// 去下一个月
	private int gvFlag = 0;
	private GestureDetector gestureDetector = null;
	private CalendarAdapter calV = null;
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private ClickDataListener clickDataListener;
	private Integer[] t={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //31

	private String selected_id;

	public CalendarViewOther(Context context,String id) {
		this(context, null,id);
	}

	public CalendarViewOther(Context context, AttributeSet attrs,String id) {
		super(context, attrs);
		mContext = context;
		selected_id=id;
		initView();
	}

	private void initView() {
		View view = View.inflate(mContext, R.layout.calen_calendar, this);
		currentMonth = (TextView) view.findViewById(R.id.currentMonth);
		prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
		nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
		setListener();
		setCurrentDay();
		gestureDetector = new GestureDetector(mContext, new MyGestureListener());
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();

		final Handler myHandler=new Handler(){
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				try {
					// ----
					if (msg.what == 1) {
						List<Sign> list=(List<Sign>)msg.obj;
						Integer[] p={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
						if(null!=list&&!list.isEmpty()){
							for (Sign s:list) {
								int i=Integer.parseInt(s.getCreatedAt().substring(8,10));//提取日期
								p[i-1]=s.getGrade();
							}
						}
						calV = new CalendarAdapter(mContext, getResources(), jumpMonth,
								jumpYear, year_c, month_c, day_c,p);
						addGridView();
						gridView.setAdapter(calV);
						flipper.addView(gridView, 0);
						addTextToTopTextView(currentMonth);

					}
				} catch (Exception e) {
				}
			};

		};
		getData(year_c,month_c,0,0,myHandler,selected_id);
	}

	private void setListener() {
		prevMonth.setOnClickListener(this);
		nextMonth.setOnClickListener(this);

	}

	public void setClickDataListener(ClickDataListener clickDataListener) {
		this.clickDataListener = clickDataListener;
	}

	private void setCurrentDay() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
		currentDate = sdf.format(date); // 当期日期

		System.out.println("currentDate:"+currentDate);
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
	}

	/**
	 * 移动到下一个月
	 *
	 * @param gvFlag
	 */
	private int gv;
	private void enterNextMonth(int gvFlag) {
		gv=gvFlag;
		addGridView(); // 添加一个gridView
		jumpMonth++; // 下一个月

		final Handler myHandler=new Handler(){
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				try {
					// ----
					if (msg.what == 1) {
						List<Sign> list=(List<Sign>)msg.obj;
						Integer[] p={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
						if(null!=list && !list.isEmpty()){
							for (Sign s:list) {
								int i=Integer.parseInt(s.getCreatedAt().substring(8,10));//提取日期
								p[i-1]=s.getGrade();
								System.out.println("i="+i);
							}
						}
						calV = new CalendarAdapter(mContext, getResources(), jumpMonth,
								jumpYear, year_c, month_c, day_c,p);
						gridView.setAdapter(calV);

						addTextToTopTextView(currentMonth); // 移动到下一月后，将当月显示在头标题中
						gv++;
						flipper.addView(gridView, gv);
						flipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
								R.anim.push_left_in));
						flipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
								R.anim.push_left_out));
						flipper.showNext();
						flipper.removeViewAt(0);

					}
				} catch (Exception e) {

				}
			};

		};

		getData(year_c,month_c,jumpYear,jumpMonth,myHandler,selected_id);
	}

	/**
	 * 移动到上一个月
	 *
	 * @param gvFlag
	 */
	private void enterPrevMonth(int gvFlag) {
		gv=gvFlag;
		addGridView(); // 添加一个gridView
		jumpMonth--; // 上一个月

		final Handler myHandler=new Handler(){
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				try {
					// ----
					if (msg.what == 1) {
						List<Sign> list=(List<Sign>)msg.obj;
						Integer[] p={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
						if(null!=list&&!list.isEmpty()){
							for (Sign s:list) {
								int i=Integer.parseInt(s.getCreatedAt().substring(8,10));//提取日期
								p[i-1]=s.getGrade();
							}
						}
						calV = new CalendarAdapter(mContext, getResources(), jumpMonth,
								jumpYear, year_c, month_c, day_c,p);
						gridView.setAdapter(calV);

						gv++;
						addTextToTopTextView(currentMonth); // 移动到上一月后，将当月显示在头标题中
						flipper.addView(gridView, gv);

						flipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
								R.anim.push_right_in));
						flipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
								R.anim.push_right_out));
						flipper.showPrevious();
						flipper.removeViewAt(0);

					}
				} catch (Exception e) {
				}
			};

		};

		getData(year_c,month_c,jumpYear,jumpMonth,myHandler,selected_id);
	}

	/**
	 * 添加头部的年份 闰哪月等信息
	 *
	 * @param view
	 */
	private void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		// draw = getResources().getDrawable(R.drawable.top_day);
		// view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("年")
				.append(calV.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
	}

	private void addGridView() {
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(mContext);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(40);
		// gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if (Width == 720 && Height == 1280) {
			gridView.setColumnWidth(40);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 去除gridView边框
		gridView.setVerticalSpacing(0);
		gridView.setHorizontalSpacing(0);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position + 7
						&& position <= endPosition - 7) {
					String scheduleDay = calV.getDateByClickItem(position)
							.split("\\.")[0]; // 这一天的阳历
					String scheduleYear = calV.getShowYear();
					String scheduleMonth = calV.getShowMonth();
					((CalendarAdapter) arg0.getAdapter())
							.setColorDataPosition(position);
					if (clickDataListener != null) {
						clickDataListener.clickData(scheduleYear,
								scheduleMonth, scheduleDay);
					}
				}
			}
		});
		gridView.setLayoutParams(params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextMonth: // 下一个月
			enterNextMonth(gvFlag);
			Log.d(TAG, "gvFlag=" + gvFlag);
			break;
		case R.id.prevMonth: // 上一个月
			enterPrevMonth(gvFlag);
			break;

		}

	}

	//查询符合时间和用户id条件的记录
	public void getData(int year_c,int month_c,int jumpYear,int jumpMonth, final Handler handler,String id){

		String dateString=String.valueOf(year_c+jumpYear).concat("-").concat(String.valueOf(month_c+jumpMonth)).concat("-").concat("31");
		String dateString2 = String.valueOf(year_c+jumpYear).concat("-").concat(String.valueOf(month_c+jumpMonth)).concat("-").concat("01");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1  = null;
		Date date2  = null;
		try {
			date1 = sdf.parse(dateString);
			date2=sdf.parse(dateString2);
		} catch (ParseException e) {
		}

		BmobQuery<Sign> my_sign=new BmobQuery<Sign>();
		my_sign.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));

		BmobQuery<Sign> my_sign2=new BmobQuery<Sign>();
		my_sign2.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date2));

		BmobQuery<Sign> my_sign3=new BmobQuery<Sign>();
		my_sign3.addWhereEqualTo("user_id",id);

		List<BmobQuery<Sign>> andQuerys = new ArrayList<BmobQuery<Sign>>();
		andQuerys.add(my_sign);
		andQuerys.add(my_sign2);
		andQuerys.add(my_sign3);

		BmobQuery<Sign> sign=new BmobQuery<Sign>();
		sign.and(andQuerys);
		sign.findObjects(this.mContext, new FindListener<Sign>() {
			@Override
			public void onSuccess(List<Sign> list) {

				Message message = handler.obtainMessage();
				message.what = 1;
				message.obj = list;
				handler.sendMessage(message);
			}

			@Override
			public void onError(int i, String s) {
			}
		});
	}

	//左右滑动的监听器
	class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 像左滑动
				enterNextMonth(gvFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				enterPrevMonth(gvFlag);
				return true;
			}
			return false;
		}
	}
}
