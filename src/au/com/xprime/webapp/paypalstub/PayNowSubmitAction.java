package au.com.xprime.webapp.paypalstub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.xprime.misc.TokenCreator;

public class PayNowSubmitAction extends PayNowAction {
	private static final String PAYMENT_STATUS = "Completed";//"Pending"
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss MMM dd, yyyy z");
	private String redirectResultUrl;
	private String subType;

	@Override
	public String execute() throws Exception {
		super.execute();
		LOG.debug("PayNowAction.execute()");
		LOG.debug("submitType: " + getSubType());
		if (getSubType() == null || getReturn() == null || getCancel_return() == null)
			return ERROR;
		boolean cancel = getSubType().toLowerCase().indexOf("cancel") != -1;
		setRedirectResultUrl(cancel ? getCancel_return() : getReturn());
		if (!cancel)
			new Thread(new Runnable() {
				public void run() {
					try {
						sendIPN();
					} catch (IOException e) {
						LOG.error("bad sendIPN()", e);
					}
				}
			}).start();
		return "redirect";
	}

	/*
	cmd: _xclick
	38 - custom: user=211,accessPlan=26
	39 - item_name: FeedXL Deluxe Pro Plan: Monthly Subscription
	40 - item_number: 26
	41 - charset: utf-8
	42 - currency_code: AUD
	43 - amount: 90.0
	44 - business: rod.va_1212909304_biz@xprime.com.au
	45 - first_name: Roger
	46 - last_name: Vagg
	47 - address1: 
	48 - address2: 
	49 - city: 
	50 - state: 
	51 - country: AU
	52 - zip: 
	53 - notify_url: http://localhost:8081/FeedXLWebapp/_paypal/ipn.fxl
	54 - return: http://localhost:8081/FeedXLWebapp/auth/paymentComplete.fxl?uid=211
	55 - cancel_return: http://localhost:8081/FeedXLWebapp/auth/paymentCancel.fxl
	56 - rm: 1
	57 - no_note: 1
	58 - no_shipping: 1
	42 - PayNowAction.execute()
	43 - submitType: submit

	373, 20, 'business', 'rod.va_1212909304_biz@xprime.com.au'
	391, 20, 'charset', 'windows-1252'
	388, 20, 'custom', '"user=200,accessPlan=16"'
	376, 20, 'first_name', 'Test'
	394, 20, 'item_name', 'FeedXL Trainer Plan: 1-Day Access'
	369, 20, 'item_number', '16'
	395, 20, 'last_name', 'User'
	385, 20, 'mc_currency', 'AUD'
	378, 20, 'mc_fee', '1.07'
	386, 20, 'mc_gross', '32.00'
	382, 20, 'notify_version', '2.4'
	377, 20, 'payer_email', 'rod.va_1215669904_per@xprime.com.au'
	374, 20, 'payer_id', '9CHK83JV3YN38'
	384, 20, 'payer_status', 'unverified'
	389, 20, 'payment_date', '"23:13:05 Jul 15, 2008 PDT"'
	390, 20, 'payment_fee', ''
	392, 20, 'payment_gross', ''
	372, 20, 'payment_status', 'Completed'
	396, 20, 'payment_type', 'instant'
	381, 20, 'quantity', '1'
	380, 20, 'receiver_email', 'rod.va_1212909304_biz@xprime.com.au'
	397, 20, 'receiver_id', '27LAMK39MDXU2'
	370, 20, 'residence_country', 'AU'
	375, 20, 'shipping', '0.00'
	393, 20, 'tax', '0.00'
	387, 20, 'test_ipn', '1'
	379, 20, 'txn_id', '6DH23395RV0980700'
	                    
	383, 20, 'txn_type', 'web_accept'
	371, 20, 'verify_sign', 'AFcWxV21C7fd0v3bYYYRCpSSRl31AYN2Dus8QelwiVmc2KoUVits7Y6V'
	                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	*/

	protected void sendIPN() throws IOException {
		String str = new AppendableQS(null, "test_ipn", 1).append("business", getBusiness()).append("charset", "windows-1252")
				.append("custom", getCustom()).append("first_name", getFirst_name()).append("item_name", getItem_name())
				.append("item_number", getItem_number()).append("last_name", getLast_name()).append("mc_currency", getCurrency_code())
				.append("mc_fee", "1.00").append("mc_gross", getAmount()).append("notify_version", "2.4").append("payer_email", "unknown@feedxl.com")
				.append("payer_id", "XXXXXXXXXXXXX").append("payer_status", "unverified")
				.append("payment_date", DATE_FORMAT.format(new Date(System.currentTimeMillis()))).append("payment_fee", "")
				.append("payment_gross", "").append("payment_status", PAYMENT_STATUS).append("pending_reason", "echeck")
				.append("payment_type", "instant").append("quantity", "1").append("receiver_email", getBusiness())
				.append("receiver_id", "XXXXXXXXXXXXX").append("residence_country", getCountry()).append("shipping", "0.00").append("tax", "0.00")
				.append("txn_id", TokenCreator.createToken(17)).append("txn_type", "web_accept")
				.append("verify_sign", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").toString();
		URL u = new URL(getNotify_url());
		URLConnection uc = u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.print(str);
		pw.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		in.close();
		LOG.debug("Returned from IPN: " + res);
	}

	private static class AppendableQS {
		private final String qs;

		public AppendableQS(AppendableQS appendable, String key, Object val) {
			qs = (appendable != null ? appendable.toString() + '&' : "") + gen(key, val);
		}

		public AppendableQS append(String key, Object val) {
			return new AppendableQS(this, key, val);
		}

		private String gen(String key, Object val) {
			return key + '=' + val.toString();
		}

		public String toString() {
			return qs;
		}
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getRedirectResultUrl() {
		return redirectResultUrl;
	}

	public void setRedirectResultUrl(String redirectResultUrl) {
		this.redirectResultUrl = redirectResultUrl;
	}
}