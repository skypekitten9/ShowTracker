package showtracker.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import showtracker.Envelope;

public class Connection {

	private String ip;
	private int port;

	public Connection(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public Object packEnvelope(Object o, String type) {
		Envelope enSend = new Envelope(o, type);
		Envelope enReturn = sendEnvelope(enSend);
		if (enReturn != null)
			return enReturn.getContent();
		else
			return null;
	}

	private Envelope sendEnvelope(Envelope envelope) {
		Envelope returnEnvelope = null;
		try {
			Socket socket = null;

			try {
				socket = new Socket(ip, port);
			} catch (IOException e) {
				System.out.println("Connection: " + e);
			}
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(envelope);
			oos.flush();
			System.out.println("Connection: Envelope sent.");
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());  
			returnEnvelope = (Envelope) ois.readObject();
			System.out.println("Connection: Envelope received.");

		} catch (Exception e) {
			System.out.println("Connection: " + e);
		}
		return returnEnvelope;
	}
}