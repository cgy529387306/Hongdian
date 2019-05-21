package com.android.mb.hd.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.mb.hd.MainActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;



public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, "wxf3fb9dfe2ff4c6ec");
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}


	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0) {
				Toast.makeText(this, "支付成功", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(MainActivity.PAY_RESULT_ACTION);
				intent.putExtra("result",1);
				LocalBroadcastManager.getInstance(WXPayEntryActivity.this).sendBroadcast(intent);
			} else {
				Log.e("java", "onResp: " + resp.errCode);
				Intent intent = new Intent(MainActivity.PAY_RESULT_ACTION);
				intent.putExtra("result",0);
				LocalBroadcastManager.getInstance(WXPayEntryActivity.this).sendBroadcast(intent);
				Toast.makeText(this, "支付失败", Toast.LENGTH_LONG).show();
			}
			finish();
		}
	}

	@Override
	public void onReq(BaseReq baseReq) {

	}
}