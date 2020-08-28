import java.net.ServerSocket;
import java.net.*;
import java.io.*;
import java.util.*;

public class SERVER {

	private ServerSocket server;

	private Random rnd = new Random(); // �浹 �鵹�� ���ϱ� ���� �����Դϴ�.

	private BManager bMan = new BManager(); // �޽����� �����ϴ� �������Դϴ�.

	public SERVER() {
	}

	void startServer() { // ������ �����մϴ�.

		try {

			server = new ServerSocket(0707); // ���� ������ �����մϴ�.

			System.out.println("���������� �����Ǿ����ϴ�.");

			while (true) { // ���ѹݺ��մϴ�.

				// Ŭ���̾�Ʈ�� ����Ǵ� �����带 ����ϴ�.

				Socket socket = server.accept();

				// �����带 �����մϴ�.

				Omok_Thread ot = new Omok_Thread(socket);

				// �����带 �����մϴ�.
				ot.start();

				// Vector�� ��ӹ޴� BManager ��ü�� bMan�� �����带 �߰��Ѵ�.

				bMan.add(ot);

				// ������ ���� ǥ�����ݴϴ�.

				System.out.println("������ ��: " + bMan.size());

			}

		} catch (Exception e) {

			System.out.println(e);

		}

	}

	public static void main(String[] args) {

		SERVER server = new SERVER();

		server.startServer();

	}

	// Ŭ���̾�Ʈ�� ����ϴ� ������ Ŭ������ �����մϴ�.

	class Omok_Thread extends Thread {

		private int roomNumber = -1; // �� ��ȣ�� �����ϴµ��� ���Դϴ�.

		private String userName = null; // ���� �̸��� �����ϴµ��� ���Դϴ�.

		private Socket socket; // ����

		// ������ �غ�Ǿ������� �����մϴ�, true�̸� ������ ������ �غ� �Ǿ����� �ǹ��մϴ�.

		private boolean ready = false;

		private BufferedReader rd; // �Է� ��Ʈ���� BufferedReader�Դϴ�.

		private PrintWriter pw; // ��� ��Ʈ�� PrintWrite�Դϴ�.

		Omok_Thread(Socket socket) { // �����ڸ� ����ϴ�.

			this.socket = socket;

		}

		Socket getSocket() { // ������ ��ȯ�ϴ� �޼ҵ��Դϴ�.

			return socket;

		}

		int getRoomNumber() { // �ش� �������� �� ��ȣ�� ��ȯ�ϴ� �޼ҵ��Դϴ�.

			return roomNumber;

		}

		String getUserName() { // ���� �̸��� ��ȯ�ϴ� �޼ҵ��Դϴ�.

			return userName;

		}

		boolean isReady() { // �غ� �Ǿ��ִ����� ��ȯ���ݴϴ�.

			return ready;

		}

		public void run() { // �����尡 ���� �� ������ ����Ǵ� �κ��Դϴ�.

			try {
				// ���Ͽ��� ���� �о�� �� ���̴� �κ��Դϴ�.
				rd = new BufferedReader(

						new InputStreamReader(socket.getInputStream()));
				// ���Ͽ� ���� ���� �� ���̴� �κ��Դϴ�.
				pw = new PrintWriter(socket.getOutputStream(), true);

				String msg; // Ŭ���̾�Ʈ�� �޽����Դϴ�.

				while ((msg = rd.readLine()) != null) {

					// msg�� �о� �Դµ� "[NAME]"���� ���۵ȴٸ�

					if (msg.startsWith("[NAME]")) {

						userName = msg.substring(6); // �պκ��� �ڸ��� userName�� ���Ѵ�.

					}

					// msg�� �о� �Դµ� "[ROOM]"���� ���۵Ǹ�

					else if (msg.startsWith("[ROOM]")) {

						int roomNum = Integer.parseInt(msg.substring(6));
						// �� ��ȣ�� �պκ��� �ڸ��� �������� ������ ��ȯ�Ͽ� �����մϴ�.

						if (!bMan.isFull(roomNum)) { // ���� �� ������ �ʴٸ�(2���� ���� �����Դϴ�)

							// ���� ���� �ٸ� �������� ������ ������ �˸��ϴ�.

							if (roomNumber != -1) // ���� ��ȣ�� �ʱⰪ�� �ƴ϶��

								bMan.sendToOthers(this, "[EXIT]" + userName); // sendToOthers �޼ҵ带 ȣ���մϴ�.

							// ������ �� ��ȣ�� ������ �о�� ���ο� �� ��ȣ�� �����մϴ�.

							roomNumber = roomNum;

							// �������� ������ �����ϴٴ� ���� �˷��ݴϴ�.

							pw.println(msg); // msg�� ������ ���� �����մϴ�.

							// �������� ���� ������ �濡 �ִ� ���� �̸� ����Ʈ�� �����մϴ�.

							pw.println(bMan.getNamesInRoom(roomNumber));

							// �� �濡 �ִ� �ٸ� �������� ������ ������ �˷��ݴϴ�.

							bMan.sendToOthers(this, "[ENTER]" + userName);

						}

						else
							pw.println("[FULL]"); // �������� ���� ����� ��á���� �˷��ݴϴ�.

					}

					// "[STONE]" �޽����� ��뿡�� �����մϴ�.

					else if (roomNumber >= 1 && msg.startsWith("[STONE]"))

						bMan.sendToOthers(this, msg);

					// ��ȭ �޽����� �濡 �����մϴ�.

					else if (msg.startsWith("[MSG]"))

						bMan.sendToRoom(roomNumber,

								"[" + userName + "] : " + msg.substring(5));

					// "[START]" �޽������

					else if (msg.startsWith("[START]")) {

						ready = true; // ������ ������ �غ� �ǹ��ϴ� ready�� true�� �ٲߴϴ�.

						// �ٸ� ������ ������ ������ �غ� �Ǿ�����

						if (bMan.isReady(roomNumber)) {

							// ��� ���� ���ϰ� ������ ������� �����մϴ�.

							int a = rnd.nextInt(2); // 1~2������ ���� ������ a�� �����մϴ�.

							if (a == 0) {

								pw.println("[COLOR]BLACK"); // 0 �̸� �ش� ���ڸ� �����մϴ�.

								bMan.sendToOthers(this, "[COLOR]WHITE");

							}

							else {

								pw.println("[COLOR]WHITE"); // 1�̸� �ش� ���ڸ� �����մϴ�.

								bMan.sendToOthers(this, "[COLOR]BLACK");

							}

						}

					}

					// ������ ������ �����ϴ� �޽����� ������

					else if (msg.startsWith("[STOPGAME]"))

						ready = false;

					// ������ ������ ����ϴ� �޽����� ������

					else if (msg.startsWith("[DROPGAME]")) {

						ready = false;

						// ������� ������ ����� �˸��ϴ�.

						bMan.sendToOthers(this, "[DROPGAME]");

					}

					// ������ �̰�ٴ� �޽����� ������

					else if (msg.startsWith("[WIN]")) {

						ready = false;

						// �������� �޽����� �����ϴ�.

						pw.println("[WIN]");

						// ������� ������ �˸��ϴ�.

						bMan.sendToOthers(this, "[LOSE]");

					}

				}

			} catch (Exception e) {

			} finally {
				// ������ �ݾ��ִ� �κ��Դϴ�.
				try {

					bMan.remove(this);

					if (rd != null)
						rd.close();

					if (pw != null)
						pw.close();

					if (socket != null)
						socket.close();

					rd = null;
					pw = null;
					socket = null;

					System.out.println(userName + "���� ������ �������ϴ�.");

					System.out.println("������ ��: " + bMan.size());

					// ������ ������ �������� ���� �濡 �˷��ݴϴ�..

					bMan.sendToRoom(roomNumber, "[DISCONNECT]" + userName);

				} catch (Exception e) {
				}

			}

		}

	}

	class BManager extends Vector { // �޽����� �����ϴ� Ŭ�����Դϴ�. Vector�� ��ӹ޴� �߿��� Ŭ�����Դϴ�.

		BManager() {
		}

		void add(Omok_Thread ot) { // �����带 Vector�� �߰��մϴ�.

			super.add(ot);

		}

		void remove(Omok_Thread ot) { // �����带 Vector���� �����մϴ�.

			super.remove(ot);

		}

		Omok_Thread getOT(int i) { // i��° �����带 ��ȯ�մϴ�.

			return (Omok_Thread) elementAt(i);

		}

		Socket getSocket(int i) { // i��° �������� ������ ��ȯ�մϴ�.

			return getOT(i).getSocket();

		}

		// i��° ������� ����� Ŭ���̾�Ʈ���� �޽����� �����մϴ�.

		void sendTo(int i, String msg) {

			try {

				PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);

				pw.println(msg); // msg�� �����ϴ�.

			} catch (Exception e) {
			}

		}

		int getRoomNumber(int i) { // i��° �������� �� ��ȣ�� ��ȯ�մϴ�.

			return getOT(i).getRoomNumber();

		}

		synchronized boolean isFull(int roomNum) { // ���� á���� �˾ƺ��� �޼ҵ��Դϴ�.

			if (roomNum == 0)
				return false; // ������ �ο� ������ ���� ������ �����̸� �׳� �������ɴϴ�.

			// �ٸ� ���� 2�� �̻� ������ �� �����ϴ�.

			int count = 0; // int���� count�� �ʱⰪ 0���� �����մϴ�.

			for (int i = 0; i < size(); i++) // �ݺ����� �޼ҵ��� ���� (Vector�� ��� ����)��ŭ �ݺ��մϴ�.

				if (roomNum == getRoomNumber(i)) // ���� �� ��ȣ�� i�� �������� �� ��ȣ�� ���ٸ�
					count++; // count�� 1�� ������ŵ�ϴ�.

			if (count >= 2) // ���� count�� 2���� ũ�ٸ� true�� ��ȯ�մϴ�.
				return true;

			return false; // �ƴϸ� false�� ��ȯ�մϴ�.

		}

		// �ش��ϴ� ��ȣ�� �濡 msg�� �����մϴ�.

		void sendToRoom(int roomNum, String msg) {

			for (int i = 0; i < size(); i++)

				if (roomNum == getRoomNumber(i))

					sendTo(i, msg);

		}

		// ot�� ���� �濡 �ִ� �ٸ� �������� msg�� �����մϴ�.

		void sendToOthers(Omok_Thread ot, String msg) {

			for (int i = 0; i < size(); i++)

				if (getRoomNumber(i) == ot.getRoomNumber() && getOT(i) != ot) // i�� �������� ���ȣ�� ot�� ���ȣ�� ������ ot �ڱ� �ڽ��� �ƴ� ���

					sendTo(i, msg); // i�� �����忡�� msg�� ������ �޼ҵ带 �����մϴ�.

		}

		// ������ ������ �غ� �Ǿ��°��� ��ȯ�մϴ�.

		// �� ���� ���� ��� �غ�� �����̸� true�� ��ȯ�մϴ�.

		synchronized boolean isReady(int roomNum) {

			int count = 0; // count�� �����մϴ�.

			for (int i = 0; i < size(); i++) // �ݺ����� �������� ������ŭ �ݺ��մϴ�.

				if (roomNum == getRoomNumber(i) && getOT(i).isReady()) // i�� ������� roomNum�� ������ i�������尡 �غ� �Ǿ��ִٸ�

					count++; // count�� 1 ������ŵ�ϴ�.

			if (count == 2) // count�� 2��� true�� ��ȯ�մϴ�.
				return true;

			return false;

		}

		// roomNum�濡 �ִ� �������� �̸��� ��ȯ�մϴ�.

		String getNamesInRoom(int roomNum) {

			StringBuffer sb = new StringBuffer("[PLAYERS]"); // ���ڿ� ���۸� ����ϴ�.

			for (int i = 0; i < size(); i++) // �޼ҵ��� ������ŭ �ݺ��մϴ�.

				if (roomNum == getRoomNumber(i)) // i�� �޼ҵ�� roomNum�� ���ٸ� sb�� i�� �޼ҵ��� �����̸��� "\t"�� ���ڿ� �ڿ� �ٿ��ݴϴ�.

					sb.append(getOT(i).getUserName() + "\t");

			return sb.toString(); // ���ڿ� ���۸� ���ڿ��� ��ȯ�Ͽ� ��ȯ�մϴ�.

		}

	}

}