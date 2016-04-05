package com.open.im.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.activity.ChatActivity;
import com.open.im.activity.FriendInfoActivity;
import com.open.im.activity.MainActivity;
import com.open.im.app.MyApp;
import com.open.im.baidumap.BaiduMapActivity;
import com.open.im.bean.MessageBean;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.utils.MyDateUtils;
import com.open.im.utils.MyFileUtils;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyMediaPlayerUtils;
import com.open.im.view.ZoomImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * cursorAdapter填充listView
 * 
 * @author Administrator
 * 
 */
public class ChatLVAdapter extends BaseAdapter {

	private final String friendJid;
	private ChatActivity act;
	private MyBitmapUtils myBitmapUtils;
	private List<MessageBean> data;
	private SimpleDateFormat sdf;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private long lastTime;

	public ChatLVAdapter(Context ctx, List<MessageBean> data,String friendJid) {
		this.act = (ChatActivity) ctx;
		this.data = data;
		this.friendJid = friendJid;
		myBitmapUtils = new MyBitmapUtils(act);
		sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm", Locale.CHINA);
		sdf2 = new SimpleDateFormat("MM月dd日  HH:mm",Locale.CHINA);
		sdf3 = new SimpleDateFormat(" HH:mm",Locale.CHINA);

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// 界面
		View view;
		ViewHolder vh;
		if (convertView == null) {
			view = View.inflate(act, R.layout.list_item_chat_detail, null);
			vh = new ViewHolder();
			TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
			LinearLayout ll_receive = (LinearLayout) view.findViewById(R.id.ll_receive);
			ImageView chatfrom_icon = (ImageView) view.findViewById(R.id.chatfrom_icon);
			TextView tv_receive_body = (TextView) view.findViewById(R.id.tv_receive_body);
			ImageView iv_receive_image = (ImageView) view.findViewById(R.id.iv_receive_image);
			ImageView iv_receive_audio = (ImageView) view.findViewById(R.id.iv_receive_audio);
			ImageView iv_receive_location = (ImageView) view.findViewById(R.id.iv_receive_location);

			RelativeLayout rl_send = (RelativeLayout) view.findViewById(R.id.rl_send);
			ImageView chatto_icon = (ImageView) view.findViewById(R.id.chatto_icon);
			TextView tv_send_body = (TextView) view.findViewById(R.id.tv_send_body);
			ImageView iv_send_image = (ImageView) view.findViewById(R.id.iv_send_image);
			ImageView iv_send_audio = (ImageView) view.findViewById(R.id.iv_send_audio);
			ImageView iv_send_location = (ImageView) view.findViewById(R.id.iv_send_location);

			TextView tv_receipt = (TextView) view.findViewById(R.id.tv_receipt);

			vh.date = tv_date;
			vh.receive = ll_receive;
			vh.receiveBody = tv_receive_body;
			vh.receiveImage = iv_receive_image;
			vh.receiveAudio = iv_receive_audio;
			vh.receiveLocation = iv_receive_location;
			vh.receiveAvatar = chatfrom_icon;

			vh.send = rl_send;
			vh.sendBody = tv_send_body;
			vh.sendImage = iv_send_image;
			vh.sendAudio = iv_send_audio;
			vh.sendLocation = iv_send_location;
			vh.sendAvatar = chatto_icon;

			vh.receipt = tv_receipt;

			view.setTag(vh);
		} else {
			view = convertView;
			vh = (ViewHolder) view.getTag();
		}

		vh.sendAvatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(act, MainActivity.class);
				intent.putExtra("selection",3);
				act.startActivity(intent);
				act.finish();
			}
		});

		vh.receiveAvatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(act, FriendInfoActivity.class);
				intent.putExtra("friendJid",friendJid);
				intent.putExtra("type",2);
				act.startActivity(intent);
				act.finish();
			}
		});

		// 数据
		MessageBean bean = data.get(position);
		final String msgBody = bean.getMsgBody();
		String msgImg = bean.getMsgImg();
		String msgFrom = bean.getFromUser();
		final int msgType = bean.getType();
		String msgReceipt = bean.getMsgReceipt();
		if ("1".equals(msgReceipt)){
			vh.receipt.setTextColor(Color.BLACK);
			vh.receipt.setText("发送中");
		} else if ("2".equals(msgReceipt)){
			vh.receipt.setTextColor(Color.BLUE);
			vh.receipt.setText("已发送");
		}else if ("3".equals(msgReceipt)){
			vh.receipt.setTextColor(Color.GREEN);
			vh.receipt.setText("已送达");
		}else if ("4".equals(msgReceipt)){
			vh.receipt.setTextColor(Color.RED);
			vh.receipt.setText("失败");
		}
		/**
		 * 设置日期
		 */
		Long msgDateLong = bean.getMsgDateLong();
		if (Math.abs(msgDateLong - lastTime) > 60000) {  //两条消息相隔1分钟以上才显示时间 否则不显示时间
			vh.date.setVisibility(View.VISIBLE);
		} else {
			vh.date.setVisibility(View.GONE);
		}
		lastTime = msgDateLong;
		String msgDate;
		if (DateUtils.isToday(msgDateLong)) { // 判断是否是今天
			msgDate = sdf3.format(new Date(msgDateLong));
		} else if (MyDateUtils.isThisYear(msgDateLong)) {
			msgDate = sdf2.format(new Date(msgDateLong));
		} else {
			msgDate = sdf.format(new Date(msgDateLong));
		}
		vh.date.setText(msgDate);

		if (!msgFrom.equals(MyApp.username)) { // 0表示收到消息 1表示发出消息
			vh.send.setVisibility(View.GONE);
			vh.receive.setVisibility(View.VISIBLE);
			if (msgType == 1) { // 1表示图片
				vh.receiveImage.setTag(position);
				vh.receiveBody.setVisibility(View.GONE);
				vh.receiveAudio.setVisibility(View.GONE);
				vh.receiveLocation.setVisibility(View.GONE);

				if (msgImg != null) {
					String imgPath = msgImg.substring(msgImg.indexOf("h"));
					myBitmapUtils.display(vh.receiveImage, imgPath);
				}
				vh.receiveImage.setVisibility(View.VISIBLE);
				vh.receiveImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (msgBody == null) {
							MyLog.showLog("路径为null");
						} else {
							showImgDialog(msgBody);
						}
					}
				});

			} else if (msgType == 2) { // 2表示语音
				vh.receiveBody.setVisibility(View.GONE);
				vh.receiveAudio.setVisibility(View.VISIBLE);
				vh.receiveImage.setVisibility(View.GONE);
				vh.receiveLocation.setVisibility(View.GONE);
				vh.receiveAudio.setImageResource(R.drawable.voice_from_icon);

				final AnimationDrawable an = (AnimationDrawable) vh.receiveAudio.getDrawable();
				// 设置动画初始状态
				an.stop();
				an.selectDrawable(2);
				final String audioPath = msgBody.substring(msgBody.indexOf("h"));
				vh.receiveAudio.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 开始播放声音
						MyMediaPlayerUtils.play(act, audioPath, (ImageView) v);
					}
				});

			} else if (msgType == 3) { // 3表示位置
				vh.receiveLocation.setTag(position);
				vh.receiveBody.setVisibility(View.GONE);
				vh.receiveImage.setVisibility(View.GONE);
				vh.receiveAudio.setVisibility(View.GONE);
				vh.receiveLocation.setVisibility(View.VISIBLE);
				final String[] split = msgBody.split("#");
				String snapShotPath = split[5];
				myBitmapUtils.display(vh.receiveLocation, snapShotPath);
				vh.receiveLocation.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						double latitude = Double.parseDouble(split[1]);
						double longitude = Double.parseDouble(split[2]);
						String locationAddress = split[3];
						Intent intent = new Intent(act, BaiduMapActivity.class);
						intent.putExtra(BaiduMapActivity.LATITUDE, latitude);
						intent.putExtra(BaiduMapActivity.LONGITUDE, longitude);
						intent.putExtra(BaiduMapActivity.ADDRESS, locationAddress);
						act.startActivity(intent);
					}
				});
			} else {
				vh.receiveImage.setVisibility(View.GONE);
				vh.receiveAudio.setVisibility(View.GONE);
				vh.receiveLocation.setVisibility(View.GONE);
				vh.receiveBody.setVisibility(View.VISIBLE);
				vh.receiveBody.setText(msgBody);
			}
		} else {
			// adapter定理 有if必有else 不然会乱跳
			vh.send.setVisibility(View.VISIBLE);
			vh.receive.setVisibility(View.GONE);
			if (msgType == 1) { // 1表示图片
				vh.sendImage.setTag(position);
				vh.sendBody.setVisibility(View.GONE);
				vh.sendAudio.setVisibility(View.GONE);
				vh.sendLocation.setVisibility(View.GONE);
				if (msgImg != null) {
					String imgPath = msgImg.substring(msgImg.indexOf("h"));
					myBitmapUtils.display(vh.sendImage, imgPath);
				}
				vh.sendImage.setVisibility(View.VISIBLE);
				vh.sendImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String picPath = msgBody.substring(msgBody.indexOf("h"));
						showImgDialog(picPath);
					}

				});

			} else if (msgType == 2) { // 2表示语音
				vh.sendBody.setVisibility(View.GONE);
				vh.sendAudio.setVisibility(View.VISIBLE);
				vh.sendImage.setVisibility(View.GONE);
				vh.sendLocation.setVisibility(View.GONE);
				vh.sendAudio.setImageResource(R.drawable.voice_to_icon);

				final AnimationDrawable an = (AnimationDrawable) vh.sendAudio.getDrawable();
				// 设置动画初始状态
				an.stop();
				an.selectDrawable(2);
				final String audioPath = msgBody.substring(msgBody.indexOf("h"));
				vh.sendAudio.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 开始播放声音
						MyMediaPlayerUtils.play(act, audioPath, (ImageView) v);
					}
				});

			} else if (msgType == 3) { // 3表示位置
				vh.sendLocation.setTag(position);
				vh.sendBody.setVisibility(View.GONE);
				vh.sendAudio.setVisibility(View.GONE);
				vh.sendImage.setVisibility(View.GONE);
				vh.sendLocation.setVisibility(View.VISIBLE);
				final String[] split = msgBody.split("#");
				String snapShotPath = split[5];
				myBitmapUtils.display(vh.sendLocation, snapShotPath);

				vh.sendLocation.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						double latitude = Double.parseDouble(split[1]);
						double longitude = Double.parseDouble(split[2]);
						String locationAddress = split[3];
						Intent intent = new Intent(act, BaiduMapActivity.class);
						intent.putExtra(BaiduMapActivity.LATITUDE, latitude);
						intent.putExtra(BaiduMapActivity.LONGITUDE, longitude);
						intent.putExtra(BaiduMapActivity.ADDRESS, locationAddress);
						act.startActivity(intent);
					}
				});

			} else {
				vh.sendImage.setVisibility(View.GONE);
				vh.sendAudio.setVisibility(View.GONE);
				vh.sendLocation.setVisibility(View.GONE);
				vh.sendBody.setVisibility(View.VISIBLE);
				vh.sendBody.setText(msgBody);
			}
		}
		return view;
	}

	/**
	 * 使用viewHolder减少findviewbyid的次数 缩短显示条目的时间
	 * 
	 * @author Administrator
	 * 
	 */
	private class ViewHolder {

		public ImageView sendLocation;
		public ImageView receiveLocation;
		public ImageView sendAudio;
		public ImageView receiveAudio;
		public ImageView sendImage;
		public ImageView receiveImage;
		public TextView date;
		public TextView sendBody;
		public TextView receiveBody;
		public RelativeLayout send;
		public LinearLayout receive;
		public ImageView receiveAvatar;
		public ImageView sendAvatar;
		public TextView receipt;
	}

	/**
	 * 方法 点击小图时，加载大图片
	 * 
	 * @param picPath
	 */
	private void showImgDialog(final String picPath) {
		final AlertDialog dialog = new AlertDialog.Builder(act, R.style.Lam_Dialog_FullScreen).create();
		Window win = dialog.getWindow();
		win.setGravity(Gravity.FILL);
		// 隐藏手机最上面的状态栏
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.show();

		View view = View.inflate(act, R.layout.dialog_big_image, null);
		ZoomImageView imgView = (ZoomImageView) view.findViewById(R.id.iv_image);

		imgView.setTag(-2);

		myBitmapUtils.display(imgView, picPath);
		MyFileUtils.scanFileToPhotoAlbum(act, picPath);
		imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		win.setContentView(view);
	}
}