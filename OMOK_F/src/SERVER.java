import java.net.ServerSocket;
import java.net.*;
import java.io.*;
import java.util.*;

public class SERVER {

	private ServerSocket server;

	private Random rnd = new Random(); // 흑돌 백돌을 정하기 위핸 난수입니다.

	private BManager bMan = new BManager(); // 메시지를 전달하는 관리자입니다.

	public SERVER() {
	}

	void startServer() { // 서버를 실행합니다.

		try {

			server = new ServerSocket(0707); // 서버 소켓을 생성합니다.

			System.out.println("서버소켓이 생성되었습니다.");

			while (true) { // 무한반복합니다.

				// 클라이언트와 연결되는 스레드를 얻습니다.

				Socket socket = server.accept();

				// 스레드를 생성합니다.

				Omok_Thread ot = new Omok_Thread(socket);

				// 스레드를 실행합니다.
				ot.start();

				// Vector를 상속받는 BManager 객체인 bMan에 스레드를 추가한다.

				bMan.add(ot);

				// 접속자 수를 표시해줍니다.

				System.out.println("접속자 수: " + bMan.size());

			}

		} catch (Exception e) {

			System.out.println(e);

		}

	}

	public static void main(String[] args) {

		SERVER server = new SERVER();

		server.startServer();

	}

	// 클라이언트와 통신하는 스레드 클래스를 생성합니다.

	class Omok_Thread extends Thread {

		private int roomNumber = -1; // 방 번호를 설정하는데에 쓰입니다.

		private String userName = null; // 유저 이름을 설정하는데에 쓰입니다.

		private Socket socket; // 소켓

		// 게임이 준비되었는지를 지정합니다, true이면 게임을 시작할 준비가 되었음을 의미합니다.

		private boolean ready = false;

		private BufferedReader rd; // 입력 스트림인 BufferedReader입니다.

		private PrintWriter pw; // 출력 스트림 PrintWrite입니다.

		Omok_Thread(Socket socket) { // 생성자를 만듭니다.

			this.socket = socket;

		}

		Socket getSocket() { // 소켓을 반환하는 메소드입니다.

			return socket;

		}

		int getRoomNumber() { // 해당 쓰레드의 방 번호를 반환하는 메소드입니다.

			return roomNumber;

		}

		String getUserName() { // 유저 이름을 반환하는 메소드입니다.

			return userName;

		}

		boolean isReady() { // 준비가 되어있는지를 반환해줍니다.

			return ready;

		}

		public void run() { // 스레드가 실행 시 실제로 실행되는 부분입니다.

			try {
				// 소켓에서 값을 읽어올 때 쓰이는 부분입니다.
				rd = new BufferedReader(

						new InputStreamReader(socket.getInputStream()));
				// 소켓에 값을 보낼 때 쓰이는 부분입니다.
				pw = new PrintWriter(socket.getOutputStream(), true);

				String msg; // 클라이언트의 메시지입니다.

				while ((msg = rd.readLine()) != null) {

					// msg를 읽어 왔는데 "[NAME]"으로 시작된다면

					if (msg.startsWith("[NAME]")) {

						userName = msg.substring(6); // 앞부분을 자르고 userName을 정한다.

					}

					// msg를 읽어 왔는데 "[ROOM]"으로 시작되면

					else if (msg.startsWith("[ROOM]")) {

						int roomNum = Integer.parseInt(msg.substring(6));
						// 방 번호를 앞부분을 자르고 나머지를 정수로 전환하여 대입합니다.

						if (!bMan.isFull(roomNum)) { // 방이 꽉 차있지 않다면(2명이 꽉찬 상태입니다)

							// 현재 방의 다른 유저에게 유저의 퇴장을 알립니다.

							if (roomNumber != -1) // 방의 번호가 초기값이 아니라면

								bMan.sendToOthers(this, "[EXIT]" + userName); // sendToOthers 메소드를 호출합니다.

							// 유저의 방 번호를 위에서 읽어온 새로운 방 번호로 지정합니다.

							roomNumber = roomNum;

							// 유저에게 입장이 가능하다는 것을 알려줍니다.

							pw.println(msg); // msg를 소켓을 통해 전송합니다.

							// 유저에게 새로 입장한 방에 있는 유저 이름 리스트를 전송합니다.

							pw.println(bMan.getNamesInRoom(roomNumber));

							// 새 방에 있는 다른 유저에게 유저의 입장을 알려줍니다.

							bMan.sendToOthers(this, "[ENTER]" + userName);

						}

						else
							pw.println("[FULL]"); // 유저에게 방이 사람이 꽉찼음을 알려줍니다.

					}

					// "[STONE]" 메시지를 상대에게 전송합니다.

					else if (roomNumber >= 1 && msg.startsWith("[STONE]"))

						bMan.sendToOthers(this, msg);

					// 대화 메시지를 방에 전송합니다.

					else if (msg.startsWith("[MSG]"))

						bMan.sendToRoom(roomNumber,

								"[" + userName + "] : " + msg.substring(5));

					// "[START]" 메시지라면

					else if (msg.startsWith("[START]")) {

						ready = true; // 게임을 시작할 준비를 의미하는 ready를 true로 바꿉니다.

						// 다른 유저도 게임을 시작한 준비가 되었으면

						if (bMan.isReady(roomNumber)) {

							// 흑과 백을 정하고 유저와 상대편에게 전송합니다.

							int a = rnd.nextInt(2); // 1~2사이의 정수 난수롤 a에 대입합니다.

							if (a == 0) {

								pw.println("[COLOR]BLACK"); // 0 이면 해당 문자를 전송합니다.

								bMan.sendToOthers(this, "[COLOR]WHITE");

							}

							else {

								pw.println("[COLOR]WHITE"); // 1이면 해당 문자를 전송합니다.

								bMan.sendToOthers(this, "[COLOR]BLACK");

							}

						}

					}

					// 유저가 게임을 중지하는 메시지를 보내면

					else if (msg.startsWith("[STOPGAME]"))

						ready = false;

					// 유저가 게임을 기권하는 메시지를 보내면

					else if (msg.startsWith("[DROPGAME]")) {

						ready = false;

						// 상대편에게 유저의 기권을 알립니다.

						bMan.sendToOthers(this, "[DROPGAME]");

					}

					// 유저가 이겼다는 메시지를 보내면

					else if (msg.startsWith("[WIN]")) {

						ready = false;

						// 유저에게 메시지를 보냅니다.

						pw.println("[WIN]");

						// 상대편에는 졌음을 알립니다.

						bMan.sendToOthers(this, "[LOSE]");

					}

				}

			} catch (Exception e) {

			} finally {
				// 연결을 닫아주는 부분입니다.
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

					System.out.println(userName + "님이 접속을 끊었습니다.");

					System.out.println("접속자 수: " + bMan.size());

					// 유저가 접속을 끊었음을 같은 방에 알려줍니다..

					bMan.sendToRoom(roomNumber, "[DISCONNECT]" + userName);

				} catch (Exception e) {
				}

			}

		}

	}

	class BManager extends Vector { // 메시지를 전달하는 클래스입니다. Vector를 상속받는 중요한 클래스입니다.

		BManager() {
		}

		void add(Omok_Thread ot) { // 스레드를 Vector에 추가합니다.

			super.add(ot);

		}

		void remove(Omok_Thread ot) { // 스레드를 Vector에서 제거합니다.

			super.remove(ot);

		}

		Omok_Thread getOT(int i) { // i번째 스레드를 반환합니다.

			return (Omok_Thread) elementAt(i);

		}

		Socket getSocket(int i) { // i번째 스레드의 소켓을 반환합니다.

			return getOT(i).getSocket();

		}

		// i번째 스레드와 연결된 클라이언트에게 메시지를 전송합니다.

		void sendTo(int i, String msg) {

			try {

				PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);

				pw.println(msg); // msg를 보냅니다.

			} catch (Exception e) {
			}

		}

		int getRoomNumber(int i) { // i번째 스레드의 방 번호를 반환합니다.

			return getOT(i).getRoomNumber();

		}

		synchronized boolean isFull(int roomNum) { // 방이 찼는지 알아보는 메소드입니다.

			if (roomNum == 0)
				return false; // 대기실은 인원 제한이 없기 때문에 대기실이면 그냥 빠져나옵니다.

			// 다른 방은 2명 이상 입장할 수 없습니다.

			int count = 0; // int형의 count를 초기값 0으로 선언합니다.

			for (int i = 0; i < size(); i++) // 반복문을 메소드의 개수 (Vector의 요소 개수)만큼 반복합니다.

				if (roomNum == getRoomNumber(i)) // 만약 방 번호와 i번 스레드의 방 번호가 같다면
					count++; // count를 1씩 증가시킵니다.

			if (count >= 2) // 만약 count가 2보다 크다면 true를 반환합니다.
				return true;

			return false; // 아니면 false를 반환합니다.

		}

		// 해당하는 번호의 방에 msg를 전송합니다.

		void sendToRoom(int roomNum, String msg) {

			for (int i = 0; i < size(); i++)

				if (roomNum == getRoomNumber(i))

					sendTo(i, msg);

		}

		// ot와 같은 방에 있는 다른 유저에게 msg를 전달합니다.

		void sendToOthers(Omok_Thread ot, String msg) {

			for (int i = 0; i < size(); i++)

				if (getRoomNumber(i) == ot.getRoomNumber() && getOT(i) != ot) // i번 스레드의 방번호와 ot의 방번호가 같으며 ot 자기 자신이 아닐 경우

					sendTo(i, msg); // i번 스레드에게 msg를 보내는 메소드를 실행합니다.

		}

		// 게임을 시작할 준비가 되었는가를 반환합니다.

		// 두 명의 유저 모두 준비된 상태이면 true를 반환합니다.

		synchronized boolean isReady(int roomNum) {

			int count = 0; // count를 선언합니다.

			for (int i = 0; i < size(); i++) // 반복문을 스레드의 개수만큼 반복합니다.

				if (roomNum == getRoomNumber(i) && getOT(i).isReady()) // i번 스레드와 roomNum이 같으며 i번스레드가 준비가 되어있다면

					count++; // count를 1 증가시킵니다.

			if (count == 2) // count가 2라면 true를 반환합니다.
				return true;

			return false;

		}

		// roomNum방에 있는 유저들의 이름을 반환합니다.

		String getNamesInRoom(int roomNum) {

			StringBuffer sb = new StringBuffer("[PLAYERS]"); // 문자열 버퍼를 만듭니다.

			for (int i = 0; i < size(); i++) // 메소드의 개수만큼 반복합니다.

				if (roomNum == getRoomNumber(i)) // i번 메소드와 roomNum이 같다면 sb에 i번 메소드의 유저이름과 "\t"를 문자열 뒤에 붙여줍니다.

					sb.append(getOT(i).getUserName() + "\t");

			return sb.toString(); // 문자열 버퍼를 문자열로 변환하여 반환합니다.

		}

	}

}