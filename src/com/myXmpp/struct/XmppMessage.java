package com.myXmpp.struct;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import android.util.Xml;

import com.myXmpp.asyctask.ChatAsyclTask.Status;

public class XmppMessage implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private String messageFrom;
	private String messageTo;
	private String messageBody;
	private boolean isSend = false;
	private int messageType = 1;
	private int messageStatus;
	private Long createTime = System.currentTimeMillis();
	public final static int STATUS_PENDING = 0;
	public final static int STATUS_SENDED = 1;
	public final static int STATUS_ERROR = 2;
	public final static int IS_READ = 0;
	public final static int NOT_READ = 1;
	private int messageSend = -1;
	private int isMsgRead = NOT_READ;

	public final static int MESSAGE_Type_CHAT_MSG = 10;
    public final static int MESSAGE_Type_SYSTEM_MSG = 11;

//	public XmppMessage(Message msg) {
//		// TODO Auto-generated constructor stub
//		this.messageBody = msg.getBody();
//		this.messageFrom = msg.getFrom();
//		this.messageTo = msg.getTo();
//	}

	public XmppMessage(String body, String from, String to, boolean isSend) {
		// TODO Auto-generated constructor stub
		this.messageBody = body;
		this.messageFrom = from;
		this.messageTo = to;
		this.isSend = isSend;
	}

	public XmppMessage(String xml) {
		// TODO Auto-generated constructor stub
//		readXML(new ByteArrayInputStream(xml.getBytes()));
		try {
			parseXml(new StringReader("<?xml version='1.0'?>" + xml));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public XmppMessage() {
		// TODO Auto-generated constructor stub
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
		this.messageSend = isSend ? 1 : -1;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getMessageTo() {
		return messageTo;
	}

	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(Status stauts) {

		switch (stauts) {
		case pending:
			this.messageStatus = STATUS_PENDING;
			break;
		case sended:
			this.messageStatus = STATUS_SENDED;
			break;
		case error:
			this.messageStatus = STATUS_ERROR;
			break;

		default:
			break;
		}
	}

	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public int getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(int messageSend) {
		this.messageSend = messageSend;
		this.isSend = messageSend > 0 ? true : false;
	}

	public int getIsMsgRead() {
		return isMsgRead;
	}

	public void setIsMsgRead(int isMsgRead) {
		this.isMsgRead = isMsgRead;
	}

//	public void readXML(InputStream inStream) {
//
//		XmlPullParser parser = Xml.newPullParser();
//		try {
//			parser.setInput(inStream, "UTF-8");
//			int eventType = parser.getEventType();
//			// ArrayList<T> list = new ArrayList<T>(4);
//			// String[] nodes = obj.getNodes();
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				switch (eventType) {
//				case XmlPullParser.START_DOCUMENT:
//					break;
//				case XmlPullParser.START_TAG:
//					String name = parser.getName();
//					System.out.println(name + "   "   +  parser.nextText());
//					break;
//				case XmlPullParser.END_TAG:
//					break;
//				}
//				eventType = parser.next();
//			}
//		} catch (XmlPullParserException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public void parseXml(Reader reader)

	        throws XmlPullParserException, IOException {

	    XmlPullParser parser = Xml.newPullParser();

	    parser.setInput(reader);


	    parser.nextTag();

	    parser.require(XmlPullParser.START_TAG, null, "menu");

	    while (parser.nextTag() == XmlPullParser.START_TAG) {

	        parser.require(XmlPullParser.START_TAG, null, "item");

	        String itemText = parser.nextText();

	        parser.nextTag(); // this call shouldn’t be necessary!

	        parser.require(XmlPullParser.END_TAG, null, "item");

	        System.out.println("menu option: " + itemText);

	    }

	    parser.require(XmlPullParser.END_TAG, null, "menu");
	}

}
