package au.com.xprime.webapp.paypalstub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionSupport;

public class PayNowAction extends ActionSupport {
	protected static final Log LOG = LogFactory.getLog(PayNowAction.class);

	private String cmd;
	private String custom;
	private String item_name;
	private String item_number;
	private String charset;
	private String currency_code;
	private String amount;
	private String business;
	private String first_name;
	private String last_name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String zip;
	private String notify_url;
	private String _return;
	private String cancel_return;
	private String rm;
	private String no_note;
	private String no_shipping;

	@Override
	public String execute() throws Exception {
		LOG.debug("PayNowAction.execute()");
		LOG.debug("cmd: " + cmd);
		LOG.debug("custom: " + custom);
		LOG.debug("item_name: " + item_name);
		LOG.debug("item_number: " + item_number);
		LOG.debug("charset: " + charset);
		LOG.debug("currency_code: " + currency_code);
		LOG.debug("amount: " + amount);
		LOG.debug("business: " + business);
		LOG.debug("first_name: " + first_name);
		LOG.debug("last_name: " + last_name);
		LOG.debug("address1: " + address1);
		LOG.debug("address2: " + address2);
		LOG.debug("city: " + city);
		LOG.debug("state: " + state);
		LOG.debug("country: " + country);
		LOG.debug("zip: " + zip);
		LOG.debug("notify_url: " + notify_url);
		LOG.debug("return: " + _return);
		LOG.debug("cancel_return: " + cancel_return);
		LOG.debug("rm: " + rm);
		LOG.debug("no_note: " + no_note);
		LOG.debug("no_shipping: " + no_shipping);
		return SUCCESS;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_number() {
		return item_number;
	}

	public void setItem_number(String item_number) {
		this.item_number = item_number;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getReturn() {
		return _return;
	}

	public void setReturn(String _return) {
		this._return = _return;
	}

	public String getCancel_return() {
		return cancel_return;
	}

	public void setCancel_return(String cancel_return) {
		this.cancel_return = cancel_return;
	}

	public String getRm() {
		return rm;
	}

	public void setRm(String rm) {
		this.rm = rm;
	}

	public String getNo_note() {
		return no_note;
	}

	public void setNo_note(String no_note) {
		this.no_note = no_note;
	}

	public String getNo_shipping() {
		return no_shipping;
	}

	public void setNo_shipping(String no_shipping) {
		this.no_shipping = no_shipping;
	}
}