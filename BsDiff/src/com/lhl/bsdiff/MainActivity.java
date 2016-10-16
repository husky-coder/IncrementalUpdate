package com.lhl.bsdiff;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

	// 加载库
	static {
		System.loadLibrary("BsDiff");
	}

	public static final int OLD_APK_PATH_REQUST_CODE = 1;
	public static final int NEW_APK_PATH_REQUST_CODE = 2;

	private EditText oldApkEditText;
	private EditText newApkEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		oldApkEditText = (EditText) findViewById(R.id.oldApkPath);
		newApkEditText = (EditText) findViewById(R.id.newApkPath);

		findViewById(R.id.oldApkPathBtn).setOnClickListener(this);
		findViewById(R.id.newApkPathBtn).setOnClickListener(this);

		findViewById(R.id.diff).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findViewById(R.id.diff).setEnabled(false);
				findViewById(R.id.tipLayout).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.patchPath)).setText("");

				String sdCardPath = Environment.getExternalStorageDirectory()
						.getPath();

				final String oldApk = oldApkEditText.getEditableText()
						.toString();
				final String newApk = newApkEditText.getEditableText()
						.toString();
				final String patch = sdCardPath + "/patch";

				if (!isApkPath(oldApk)) {
					Toast.makeText(MainActivity.this, "旧版本APK路径不合法！",
							Toast.LENGTH_LONG).show();
					findViewById(R.id.diff).setEnabled(true);
					findViewById(R.id.tipLayout).setVisibility(View.INVISIBLE);
					return;
				}
				if (!isApkPath(newApk)) {
					Toast.makeText(MainActivity.this, "新版本APK路径不合法！",
							Toast.LENGTH_LONG).show();
					findViewById(R.id.diff).setEnabled(true);
					findViewById(R.id.tipLayout).setVisibility(View.INVISIBLE);
					return;
				}

				// 另起线程执行耗时操作
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 进行差分
						final int result = BsDiffUtil.genDiff(oldApk, newApk,
								patch);
						// 刷新UI界面
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								findViewById(R.id.diff).setEnabled(true);
								findViewById(R.id.tipLayout).setVisibility(
										View.INVISIBLE);
								if (result == 0) {
									((TextView) findViewById(R.id.patchPath))
											.setText("成功！差异包存放路径：\n" + patch);
								} else {
									((TextView) findViewById(R.id.patchPath))
											.setText("失败！");
								}
							}
						});
					}
				}).start();
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		switch (v.getId()) {
		case R.id.oldApkPathBtn:
			startActivityForResult(intent, OLD_APK_PATH_REQUST_CODE);
			break;
		case R.id.newApkPathBtn:
			startActivityForResult(intent, NEW_APK_PATH_REQUST_CODE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {// 是否选择，没选择就不会继续
			String path = Uri.decode(data.getDataString());
			path = path.substring(7, path.length()); // 裁剪掉前面的file//
			switch (requestCode) {
			case OLD_APK_PATH_REQUST_CODE:
				oldApkEditText.setText(path);
				oldApkEditText.setSelection(path.length());
				break;
			case NEW_APK_PATH_REQUST_CODE:
				newApkEditText.setText(path);
				newApkEditText.setSelection(path.length());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 判断路径是否合法
	 * 
	 * @param path
	 * @return
	 */
	private boolean isApkPath(String path) {
		return path != null && !path.isEmpty() && path.endsWith(".apk");
	}
}
