package com.lhl.bspatch;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static {
		System.loadLibrary("BsPatch");
	}

	public static final int PATCH_PATH_REQUST_CODE = 1;

	private EditText patchEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		patchEditText = (EditText) findViewById(R.id.patchPath);

		findViewById(R.id.patchPathBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("*/*");// �������ͣ����������������ͣ������׺�Ŀ�������д��
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						startActivityForResult(intent, PATCH_PATH_REQUST_CODE);
					}
				});

		findViewById(R.id.patch).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findViewById(R.id.patch).setEnabled(false);
				findViewById(R.id.tipLayout).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.patchResult)).setText("");

				String sdCardPath = Environment.getExternalStorageDirectory()
						.getPath();

				// Ӧ�ó�������
				String appName = null;
				try {
					appName = MainActivity.this.getPackageManager()
							.getApplicationInfo(getPackageName(), 0)
							.loadLabel(getPackageManager()).toString();
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				final String oldApk = getSourceApkPath(MainActivity.this,
						getPackageName());
				final String newApk = sdCardPath + "/New_" + appName + ".apk";
				final String patch = patchEditText.getEditableText().toString();

				if (!isPatchPath(oldApk)) {
					Toast.makeText(MainActivity.this, "�ɰ汾APK��ȡʧ�ܣ�",
							Toast.LENGTH_SHORT).show();
					findViewById(R.id.patch).setEnabled(true);
					findViewById(R.id.tipLayout).setVisibility(View.INVISIBLE);
					return;
				}
				if (!isPatchPath(patch)) {
					Toast.makeText(MainActivity.this, "��ְ�·�����Ϸ���",
							Toast.LENGTH_SHORT).show();
					findViewById(R.id.patch).setEnabled(true);
					findViewById(R.id.tipLayout).setVisibility(View.INVISIBLE);
					return;
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final int result = BsPatchUtil.patch(oldApk, newApk,
								patch);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								findViewById(R.id.patch).setEnabled(true);
								findViewById(R.id.tipLayout).setVisibility(
										View.INVISIBLE);
								if (result == 0) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW);
									intent.setDataAndType(
											Uri.fromFile(new File(newApk)),
											"application/vnd.android.package-archive");
									startActivity(intent);
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								} else {
									((TextView) findViewById(R.id.patchResult))
											.setText("�ϳ�ʧ�ܣ�");
								}
							}
						});
					}
				}).start();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {// �Ƿ�ѡ��ûѡ��Ͳ������
			String path = Uri.decode(data.getDataString());
			path = path.substring(7, path.length());
			switch (requestCode) {
			case PATCH_PATH_REQUST_CODE:
				patchEditText.setText(path);
				patchEditText.setSelection(path.length());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ��ȡ�˰汾apk·��
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	private static String getSourceApkPath(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName))
			return null;
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(packageName, 0);
			return appInfo.sourceDir;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * �ж�·���Ƿ�Ϸ�
	 * 
	 * @param path
	 * @return
	 */
	private boolean isPatchPath(String path) {
		return path != null && !path.isEmpty();
	}

	/**
	 * ��ȡ��ǰӦ�ð汾�ţ����ڲ����Ƿ�ϳ������ɹ�
	 * 
	 * @param v
	 */
	public void getVersion(View v) {
		try {
			Toast.makeText(
					MainActivity.this,
					getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
					Toast.LENGTH_SHORT).show();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
