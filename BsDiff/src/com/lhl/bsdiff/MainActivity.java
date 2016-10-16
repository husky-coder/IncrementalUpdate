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

	// ���ؿ�
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
					Toast.makeText(MainActivity.this, "�ɰ汾APK·�����Ϸ���",
							Toast.LENGTH_LONG).show();
					findViewById(R.id.diff).setEnabled(true);
					findViewById(R.id.tipLayout).setVisibility(View.INVISIBLE);
					return;
				}
				if (!isApkPath(newApk)) {
					Toast.makeText(MainActivity.this, "�°汾APK·�����Ϸ���",
							Toast.LENGTH_LONG).show();
					findViewById(R.id.diff).setEnabled(true);
					findViewById(R.id.tipLayout).setVisibility(View.INVISIBLE);
					return;
				}

				// �����߳�ִ�к�ʱ����
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// ���в��
						final int result = BsDiffUtil.genDiff(oldApk, newApk,
								patch);
						// ˢ��UI����
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								findViewById(R.id.diff).setEnabled(true);
								findViewById(R.id.tipLayout).setVisibility(
										View.INVISIBLE);
								if (result == 0) {
									((TextView) findViewById(R.id.patchPath))
											.setText("�ɹ�����������·����\n" + patch);
								} else {
									((TextView) findViewById(R.id.patchPath))
											.setText("ʧ�ܣ�");
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
		intent.setType("*/*");// �������ͣ����������������ͣ������׺�Ŀ�������д��
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
		if (resultCode == Activity.RESULT_OK) {// �Ƿ�ѡ��ûѡ��Ͳ������
			String path = Uri.decode(data.getDataString());
			path = path.substring(7, path.length()); // �ü���ǰ���file//
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
	 * �ж�·���Ƿ�Ϸ�
	 * 
	 * @param path
	 * @return
	 */
	private boolean isApkPath(String path) {
		return path != null && !path.isEmpty() && path.endsWith(".apk");
	}
}
