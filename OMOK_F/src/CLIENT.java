import java.awt.*;

import java.net.*;

import java.io.*;

import java.util.*;

import java.awt.event.*;

class BOARD extends Canvas { // �������� �����ϴ� Ŭ����

	public static final int BLACK = 1, WHITE = -1; // ��� ���� ��Ÿ���� ���

	// true�̸� ������ ���� ���� �� �ִ� ���¸� �ǹ��ϸ� false�̸� ������ ���� ���� �� ���� ���¸� �ǹ��մϴ�.
	private boolean enable = false;

	private boolean running = false; // ������ ���� ���ΰ��� ��Ÿ���� ����

	private int[][] omokpan; // ������ �迭�� �����մϴ�.

	private int size; // size�� ������ ���� �Ǵ� ���� �����Դϴ�. �� ���ӿ����� 15�� �����մϴ�.

	private int color = BLACK; // ������ �� ����

	private int pixel; // ������ ũ���Դϴ�.

	private String Stat_Text = "���� ������.."; // ������ ���� ��Ȳ�� ��Ÿ���� ���ڿ�

	private PrintWriter pw; // ������� �޽����� �����ϱ� ���� ��Ʈ��

	private Graphics gboard, gbuff; // ĵ������ ���۸� ���� �׷��Ƚ� ��ü

	private Image buff; // ���� ���۸��� ���� ����

	Image BStone_img = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\images\\Black.png");
	Image WStone_img = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\images\\White.png");

	BOARD(int s, int c) { // �������� �������Դϴ�. (s=15, c=30)

		this.size = s;
		this.pixel = c;

		omokpan = new int[size + 2][]; // �������� ũ�⸦ ���մϴ�.

		for (int i = 0; i < omokpan.length; i++)

			omokpan[i] = new int[size + 2];

		setBackground(new Color(184, 255, 176)); // �������� ������ ���մϴ�.

		setSize(size * (pixel + 1) + size, size * (pixel + 1) + size); // �������� ũ�⸦ ����Ѵ�.

		// GUI������ �������� ���콺 �̺�Ʈ ó���� ���� �κ��Դϴ�.

		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent me) { // ���콺�� ������

				if (!enable)
					return; // ������ ���� �� ���� �����̸� �������ɴϴ�.

				// ���콺 ��ǥ�� omokpan�� ��ǥ�� ����մϴ�.

				int x = (int) Math.round(me.getX() / (double) pixel);

				int y = (int) Math.round(me.getY() / (double) pixel);

				// ���� ���� �� ���� ��ġ�� Ŭ���ߴٸ� �̺�Ʈ���� �������ɴϴ�.

				if (x == 0 || y == 0 || x == size + 1 || y == size + 1)
					return;

				// �̹� �ش� ��ǥ�� ���� ������ �ִٸ� �̺�Ʈ���� �������ɴϴ�.

				if (omokpan[x][y] == BLACK || omokpan[x][y] == WHITE)
					return;

				// ������� ���� ���� ��ǥ�� �����մϴ�.

				pw.println("[STONE]" + x + " " + y);

				omokpan[x][y] = color;

				// ���ӿ��� �̰������ �Ǻ��ϴ� �κ��Դϴ�.

				if (check(new Point(x, y), color)) {

					Stat_Text = "�¸�!";

					pw.println("[WIN]");

				} // �̱�� �ƴ϶�� ����ؼ� ������ �����մϴ�.

				else
					Stat_Text = "������ ���Դϴ�..";

				repaint(); // �������� �ٽ� ���� �׸��ϴ�.

				// ������ ���̱� ������ ������ �� �� ���� ���·� ����ϴ�.

				// ������� �θ� enable�� true�� �Ǿ� ������ �� �� �ְ� �˴ϴ�.

				enable = false;

			}

		});

	}

	public boolean isRunning() { // ������ ���� ���� ���¸� ��ȯ�ϴ� �޼ҵ��Դϴ�.

		return running;

	}

	public void startGame(String col) { // ���� ������ ����ϴ� �޼ҵ��Դϴ�.

		running = true;

		if (col.equals("BLACK")) { // �浹�� ���õǾ��� �� ������ �ݴϴ�.

			enable = true;
			color = BLACK;

			Stat_Text = "������  ���۵Ǿ����ϴ�. �浹�̹Ƿ� ������ �����մϴ�.";

		}

		else { // �鵹�� ���õǾ��� �� �İ����� ��븦 ��ٸ��ϴ�.

			enable = false;
			color = WHITE;

			Stat_Text = "������ ���۵Ǿ����ϴ�. �鵹�̹Ƿ� ������ ��ٸ��ϴ�.";

		}

	}

	public void stopGame() { // ���� �ߴ��� ����ϴ� �޼ҵ��Դϴ�.

		reset(); // �������� �����մϴ�.

		pw.println("[STOPGAME]"); // ���濡�� ������ ����ٴ� ���ڿ��� �����ϴ�.

		enable = false; // ���� �� �� �����ϴ�.

		running = false; // ������ �ߴܵǾ����� �ǹ��մϴ�.

	}

	public void putOpponent(int x, int y) { // ������ ���� ���� ���� ����ϴ� �޼ҵ��Դϴ�.

		omokpan[x][y] = -color; // �Ű������� �޾ƿ� x, y ��ǥ�� -color���� �����մϴ�.

		Stat_Text = "������ ���� �ξ����ϴ�. ����� �����Դϴ�, ���� �����ּ���.";

		repaint(); // ���� �ٽ� �׸��ϴ�.

	}

	public void setEnable(boolean enable) {

		this.enable = enable;

	}

	public void setWriter(PrintWriter pw) {

		this.pw = pw;

	}

	public void update(Graphics g) { // repaint�� ȣ���ϸ� �ڵ����� ȣ��˴ϴ�.

		paint(g); // paint�� ȣ���մϴ�.

	}

	public void paint(Graphics g) { // ������ ȭ���� �׸��� �޼ҵ��Դϴ�.

		if (gbuff == null) { // ���۰� ���ٸ� ���۸� �����մϴ�.

			buff = createImage(getWidth(), getHeight());

			gbuff = buff.getGraphics();

		}

		drawBoard(g); // �������� �׸��� �޼ҵ带 ȣ���մϴ�..

	}

	public void reset() { // �������� �ʱ�ȭ�ϴ� �޼ҵ��Դϴ�.

		for (int i = 0; i < omokpan.length; i++)

			for (int j = 0; j < omokpan[i].length; j++)

				omokpan[i][j] = 0; // ������ 2���� �迭������ 0���� �ʱ�ȭ��ŵ�ϴ�.

		Stat_Text = "���� ����"; // ���� ������� �ؽ�Ʈ�� ȭ�鿡 ���ϴ�.

		repaint(); // �������� �ٽ� �׸��ϴ�.

	}

	private void drawLine() { // �����ǿ� ���� �׾��ִ� �޼ҵ��Դϴ�.

		gbuff.setColor(Color.black); // ���������� �߽��ϴ�.

		for (int i = 1; i <= size; i++) {

			gbuff.drawLine(pixel, i * pixel, pixel * size, i * pixel);

			gbuff.drawLine(i * pixel, pixel, i * pixel, pixel * size);

		} // pixel �����ŭ ������ �־� ���� �߽��ϴ�.

	}

	private void drawBlack(int x, int y) { // �� ���� �׸��� �޼ҵ��Դϴ�.

		Graphics2D gbuff = (Graphics2D) this.gbuff;

		gbuff.drawImage(BStone_img, x * pixel - pixel / 2, y * pixel - pixel / 2, pixel, pixel, this);
		// �̹����� �Ű����� x, y�� �޾ƿͼ� x,y ��ǥ�� �����մϴ�.
	}

	private void drawWhite(int x, int y) { // �� ���� �׸��� �޼ҵ��Դϴ�.

		gbuff.drawImage(WStone_img, x * pixel - pixel / 2, y * pixel - pixel / 2, pixel, pixel, this);
		// �̹����� �Ű����� x, y�� �޾ƿͼ� x,y ��ǥ�� �����մϴ�.
	}

	private void drawStones() { // �����ǿ� ������ ������ ���� �׷��ִ� �޼ҵ��Դϴ�.

		for (int x = 1; x <= size; x++)

			for (int y = 1; y <= size; y++) {

				if (omokpan[x][y] == BLACK) // �ش� ��ǥ�� 1[��]�̸� ������ �׸��⸦ ȣ���մϴ�.

					drawBlack(x, y);

				else if (omokpan[x][y] == WHITE) // �ش� ��ǥ�� -1[ȭ��Ʈ]�̸� �� �׸��⸦ ȣ���մϴ�.

					drawWhite(x, y);

			}

	}

	synchronized private void drawBoard(Graphics g) { // �������� �׸��� �޼ҵ��Դϴ�.

		// ���ۿ��� �������� ���� �׸��� ���ۿ� �׸� �̹����� �����ǿ� �Ű� �׸��ϴ�.

		gbuff.clearRect(0, 0, getWidth(), getHeight());

		drawLine(); // ���� �׸��ϴ�.

		drawStones(); // ���� �׸��ϴ�.

		gbuff.setColor(Color.red); // ���������� �����մϴ�.

		gbuff.drawString(Stat_Text, 20, 15); // ���� �޽����� ���ϴ�.

		g.drawImage(buff, 0, 0, this); // ���ۿ� �׸� ���� �Ű� �׸��ϴ�.

	}

	private boolean check(Point p, int col) { // ��������� �¸� �˰����Դϴ�.

		if (count(p, 1, 0, col) + count(p, -1, 0, col) == 4)

			return true;

		if (count(p, 0, 1, col) + count(p, 0, -1, col) == 4)

			return true;

		if (count(p, -1, -1, col) + count(p, 1, 1, col) == 4)

			return true;

		if (count(p, 1, -1, col) + count(p, -1, 1, col) == 4)

			return true;

		return false;

	}

	private int count(Point p, int dx, int dy, int col) { // ��������� ���� ���� �˰����Դϴ�.

		int i = 0;

		for (; omokpan[p.x + (i + 1) * dx][p.y + (i + 1) * dy] == col; i++)
			;

		return i;

	}

} // �������� �����ϴ� Ŭ������ ���Դϴ�.

public class CLIENT extends Frame implements Runnable, ActionListener {

	// �濡 ������ �ο��� ���� �����ִ� ���̺��Դϴ�

	private Label pInfo = new Label(" > ����"); // ���� �̸��� ǥ���մϴ�.

	private java.awt.List pList = new java.awt.List(); // ���� ���� ��(����)�� �ο� ����� �����ִ� ����Ʈ�Դϴ�.

	private Button startButton = new Button("���� ����"); // ������ �����ϴ� ��ư�Դϴ�.

	private Button stopButton = new Button("���"); // ����� �ϴ� ��ư�Դϴ�.

	private Button enterButton = new Button("���� ����"); // ���� ���� ��ư�Դϴ�.

	private Button exitButton = new Button("���� ����"); // ���Ƿ� �����ϴ� ��ư�Դϴ�.

	// ���� ������Ʈ�Դϴ�.

	private TextArea msgView = new TextArea("", 1, 1, 1); // �޽����� ǥ�����ִ� ������ TextArea ������Ʈ�Դϴ�.

	private TextField sendBox = new TextField(""); // �۽��� �޽����� ���� �� �ִ� ������ TextField ������Ʈ�Դϴ�.

	private TextField nameBox = new TextField(); // ������ �̸��� ���� �� �ִ� ������ TextField ������Ʈ�Դϴ�.

	private TextField roomBox = new TextField("0"); // �� ��ȣ�� ���� �� �ִ� ������ TextField ������Ʈ�Դϴ�.

	// �������� ������ �����ִ� ���̺��Դϴ�.

	private Label infoView = new Label("< <  OMG  > >", 1);

	private BOARD board = new BOARD(15, 30); // ������ ��ü�� �����մϴ�.

	private BufferedReader br; // ���Ͽ��Լ� �޾ƿ� �� ���̴� �Է� ��Ʈ���Դϴ�.

	private PrintWriter pw; // �������� �������� �� ���̴� ��� ��Ʈ���Դϴ�.

	private Socket socket; // ����

	private int roomNumber = -1; // �� ��ȣ�Դϴ�.

	private String userName = null; // ���� �̸��Դϴ�.

	private Font f1 = new Font("NanumGothic", Font.BOLD, 20); // �������, ����, 20����Ʈ�� ��Ʈ�Դϴ�.

	private Font f2 = new Font("NanumGothic", Font.BOLD, 13); // �������, ����, 13����Ʈ�� ��Ʈ�Դϴ�.

	private Font f3 = new Font("NanumGothic", Font.BOLD, 11); // �������, ����, 11����Ʈ�� ��Ʈ�Դϴ�.

	public CLIENT(String title) { // �����ڸ� ����ϴ�.

		super(title);

		setLayout(null); // ���̾ƿ��� ������� �ʰ� ��ġ�ϱ� ���� ���Դϴ�.

		// ���� ������Ʈ�� �����ϰ� ���� ��ġ�մϴ�.

		this.setBackground(Color.BLACK); // ����� ���������� �����մϴ�.

		msgView.setEditable(false); // msgView TextArea�� ������ �� �� ������ ��޴ϴ�.

		infoView.setBounds(10, 30, 480, 30); // infoView�� ũ��� ��ġ�Ǵ� ��ġ�� �����մϴ�.

		infoView.setBackground(new Color(252, 247, 142)); // �� �� �ؽ�Ʈ�� ������ �����մϴ�.

		infoView.setFont(f1); // ��Ʈ�� f1���� �����մϴ�.

		board.setLocation(10, 70); // board�� ��ġ�� �����մϴ�.

		add(infoView); // infoView ������Ʈ�� ��ġ�մϴ�.

		add(board); // board ������Ʈ�� ��ġ�մϴ�.

		Panel p = new Panel(); // ������ �г� p�� �����մϴ�.

		p.setBackground(new Color(252, 247, 142)); // �г��� ������ �����մϴ�.

		p.setLayout(new GridLayout(3, 3)); // �г��� ���̾ƿ��� �����մϴ�.

		p.add(new Label("�ڽ��� �г���   :   ", 2)); // �гο� ���̺��� �ϳ� �����մϴ�.
		p.add(nameBox); // �гο� namebox ������Ʈ�� �ϳ� �����մϴ�.

		p.setFont(f2); // �г��� ��Ʈ�� f2�� �����մϴ�.

		p.add(new Label("������ �� ��ȣ   :   ", 2)); // �гο� ���̺��� �ϳ� �����մϴ�.
		p.add(roomBox); // roomBox ������Ʈ�� �����մϴ�.

		p.add(enterButton); // enterButton ������Ʈ�� �����մϴ�.
		p.add(exitButton); // exitButton ������Ʈ�� �����մϴ�.

		enterButton.setEnabled(false); // enterButton�� ��Ȱ��ȭ ��ŵ�ϴ�.

		p.setBounds(500, 30, 290, 70); // �г� p�� ��ġ�� ũ�⸦ �����մϴ�.

		Panel p2 = new Panel(); // �г� p2�� �����մϴ�.

		p2.setBackground((new Color(255, 247, 142))); // �г� p�� ������ �����մϴ�.

		p2.setLayout(new BorderLayout()); // �г� p�� ���̾ƿ��� �����մϴ�.

		Panel p2_1 = new Panel(); // �г� p2_1�� �����մϴ�.
		p2_1.add(startButton); // �г� p2_1�� startButton�� �����մϴ�.
		p2_1.add(stopButton); // ���� ���������� stopButton�� �����մϴ�.

		startButton.setBackground(new Color(184, 255, 176)); // startButton�� ������ �����մϴ�.
		stopButton.setBackground(new Color(184, 255, 176));// stopButton�� ������ �����մϴ�.
		startButton.setForeground(Color.white);// startButton�� ���� �����մϴ�.
		stopButton.setForeground(Color.WHITE); // stopButton�� ���� �����մϴ�.

		enterButton.setBackground(new Color(184, 255, 176)); // EnterButton�� ���� �����մϴ�.
		exitButton.setBackground(new Color(184, 255, 176));// exitButton�� ���� �����մϴ�.

		p2.add(pInfo, "North"); // pInfo ������Ʈ�� ����(��)�� ��ġ�մϴ�.
		p2.add(pList, "Center"); // pList ������Ʋ�� �߾ӿ� ��ġ�մϴ�.
		p2.add(p2_1, "South"); // p2_1 �г��� ����(�Ʒ�)�� ��ġ�մϴ�.

		p2.setFont(f2); // p2�� ��Ʈ�� f2�� �����մϴ�.

		startButton.setEnabled(false); // ��ư�� ��Ȱ��ȭ�մϴ�.
		stopButton.setEnabled(false); // ��ư�� ��Ȱ��ȭ�մϴ�.

		p2.setBounds(500, 110, 290, 125); // ���� Label ũ�� ����

		msgView.setBackground(new Color(255, 247, 147));

		Panel p3 = new Panel();

		p3.setLayout(new BorderLayout());

		p3.add(msgView, "Center");

		p3.add(sendBox, "South");

		p3.setBounds(500, 250, 290, 300); // ä��â TextArea�� ũ�⸦ �����մϴ�.

		p3.setFont(f3);

		add(p);
		add(p2);
		add(p3);

		// ���� ������Ʈ���� �̺�Ʈ �����ʸ� ����Ѵ�.

		sendBox.addActionListener(this);

		enterButton.addActionListener(this);

		exitButton.addActionListener(this);

		startButton.addActionListener(this);

		stopButton.addActionListener(this);

		// ������ �ݱ⸦ ó���մϴ�.

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent we) {

				System.exit(0);

			}

		});

	}

	// ������Ʈ���� �׼� �̺�Ʈ���� ó���ϴ� �κ��Դϴ�.

	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == sendBox) { // ���� �̺�Ʈ�� �Ͼ�� ���� �޽��� �Է� �����̸�

			String msg = sendBox.getText(); // senBox�� ���� ���ڸ� msg�� �����մϴ�.

			if (msg.length() == 0) // msg�� ���̰� 0�̶�� �������ɴϴ�.
				return;

			if (msg.length() >= 30) // msg�� ���̰� 30���� ũ�ٸ� msg�� 30���� ���� �ڸ��ϴ�.
				msg = msg.substring(0, 30);

			try {

				pw.println("[MSG]" + msg); // �ڸ� ���ڿ� [MSG]��� ���ڵ��� �ٿ��� �������� �����ݴϴ�.

				sendBox.setText(""); // �޽��� �Է� ���ڸ� ���ϴ�.

			} catch (Exception ie) {
			}

		}

		else if (ae.getSource() == enterButton) { // ���� �̺�Ʈ�� �Ͼ�� ���� �����ϱ� ��ư�̸�

			try {

				if (Integer.parseInt(roomBox.getText()) < 1) { // roomBox�� ���� ���ڰ� 1���� �۴ٸ�

					infoView.setText("���ȣ�� �߸��Ǿ����ϴ�. 1�̻�"); // infoView�� �ش� ���ڵ��� ����ϰ� �������ɴϴ�.

					return;

				}

				pw.println("[ROOM]" + Integer.parseInt(roomBox.getText())); // �տ� [ROOM]�� ���̰� ������ ���� ���ڸ� ������ ��ȯ�� ���� ���
																			// �������� �����մϴ�.

				msgView.setText(""); // �ؽ�Ʈ ���ڸ� ���ϴ�.

			} catch (Exception ie) {

				infoView.setText("...�Է¿� ������ �����մϴ�..."); // ������ ������ �ش� ���ڸ� ����մϴ�.

			}

		}

		else if (ae.getSource() == exitButton) { // ���� �̺�Ʈ�� �Ͼ�� ���� ���Ƿ� ��ư�̸�

			try {

				goToWaitRoom(); // goToWaitRoom �̶�� �޼ҵ带 �����մϴ�.

				startButton.setEnabled(false); // startButton�� stopButton�� ��Ȱ��ȭ �մϴ�.

				stopButton.setEnabled(false);

			} catch (Exception e) {
			}

		}

		else if (ae.getSource() == startButton) { // ���� �̺�Ʈ�� �Ͼ�� ���� ���� ���� ��ư�̸�

			try {

				pw.println("[START]"); // [START]��� ���ڿ��� �������� �����ϴ�.

				infoView.setText("����� ������ ��ٸ��ϴ�."); // �ش� ���ڸ� ����մϴ�.

				startButton.setEnabled(false); // startButton�� ��Ȱ��ȭ�մϴ�.

			} catch (Exception e) {
			}

		}

		else if (ae.getSource() == stopButton) { // ���� �̺�Ʈ�� �Ͼ�� ���� ��� ��ư�̸�

			try {

				pw.println("[DROPGAME]"); // [DROPGAME]�̶�� ���ڿ��� �������ϴ�.

				endGame("����� ������ ����Ͽ����ϴ�. ����� �й��Ͽ����ϴ�..."); // endGame �޼ҵ带 ȣ���մϴ�.

			} catch (Exception e) {
			}

		}

	}

	void goToWaitRoom() { // ���Ƿ� ��ư�� ������ ȣ��Ǵ� �޼ҵ��Դϴ�.

		if (userName == null) { // userName�� �ʱⰪ(NULL)�̶��

			String name = nameBox.getText().trim(); // nameBox���� ȭ��Ʈ�����̽�(�����̽�, ��, ���� ���� ����)�� ������ ���ڿ��� name�� �����մϴ�.

			if (name.length() <= 2 || name.length() > 10) { // name�� ���̰� 2���� �����̰ų� 10���ڸ� �Ѵ´ٸ�

				infoView.setText("�̸��� 3���� �̻�, 9���� ���Ͽ��� �մϴ�!"); // �ش� ���ڿ��� ����մϴ�.

				nameBox.requestFocus(); // Ű �Է��� ���� ������Ʈ�� ������ �����մϴ�. �� nameBox�� �Է¹޵��� �����մϴ�.

				return;

			}

			userName = name; // userName�� name���� �����մϴ�.

			pw.println("[NAME]" + userName); // �տ� [NAME]�� �߰��� ���ڿ��� ���Ͽ��� �����մϴ�.

			nameBox.setText(userName); // nameBox�� ������ �̸��� ���ϴ�.

			nameBox.setEditable(false); // nameBox�� ������ �� ������ �����ϴ�.

		}

		msgView.setText(""); // msgView�� �������� �ٲߴϴ�.

		pw.println("[ROOM]0"); // [ROOM]0�� �������� �����ϴ�.

		infoView.setText("���ǿ� �����ϼ̽��ϴ�. ��ſ� �Ϸ� �Ǽ���!"); // �ش� �޼����� infoView�� �ٿ�ϴ�.

		roomBox.setText("0"); // roomBox�� ���ڸ� 0���� �ٲߴϴ�.

		enterButton.setEnabled(true); // enterButton�� Ȱ��ȭ�մϴ�.

		exitButton.setEnabled(false); // exitButton�� ��Ȱ��ȭ�մϴ�.

	}

	public void run() {

		String msg; // �����κ����� �޽����� �����մϴ�.

		try {

			while ((msg = br.readLine()) != null) { // msg�� �������κ��� ������ ���� �����ϸ� msg�� null�� �ƴ� ���� �ݺ��մϴ�.

				if (msg.startsWith("[STONE]")) { // �޾ƿ� msg�� ������ ������ [STONE]�̸� ������� ���� ���� ��ǥ�� ���� ������ �ǹ��մϴ�.

					String temp = msg.substring(7); // ���� 7���� [STONE]�� ������ ���� temp�� �����մϴ�. �׷��� ���ڿ� temp�� "A B"�� �����ϴ�.
													// (A, B�� �����Դϴ�.)

					int x = Integer.parseInt(temp.substring(0, temp.indexOf(" "))); // x���� temp�� ó�����ۺ��� " "(�����̽�)��������
																					// ���ڵ��� ������ ��ȯ�Ͽ� �����մϴ�. �� ���� A��
																					// ���Թ޽��ϴ�.

					int y = Integer.parseInt(temp.substring(temp.indexOf(" ") + 1)); // y���� temp�� " "(�����̽�)�� �ִ� ���� +
																						// 1��ŭ�� ��ġ �Ʒ��� ���� ���� �� �� ���� ����ȭ
																						// �Ͽ� �����մϴ� �� ���� B�� �����մϴ�.

					board.putOpponent(x, y); // ������ ���� x, y���� ������ ���� ���� �޼ҵ��� �Ű������� �����Ͽ� ȣ���մϴ�.

					board.setEnable(true); // ������ ���� ���� �� �ֵ��� �մϴ�.

				}

				else if (msg.startsWith("[ROOM]")) { // �޾ƿ� msg�� ������ ������ [ROOM]�̸� �� ���忡 ���� ������ �ǹ��մϴ�.

					if (!msg.equals("[ROOM]0")) { // ����(0)�� �ƴ� ���̶��

						enterButton.setEnabled(false); // enterButton�� ��Ȱ��ȭ�ϰ�

						exitButton.setEnabled(true); // exitButton�� Ȱ��ȭ�մϴ�.

						infoView.setText(msg.substring(6) + "�� �濡 �����ϼ̽��ϴ�."); // �޾ƿ� ���ڿ��� �� 6���� [ROOM]�� ������ ���� ���ڿ��� ����
																				// ����մϴ�. ex) "3�� �濡 �����ϼ̽��ϴ�."

					}

					else
						infoView.setText("���ǿ� �����ϼ̽��ϴ�."); // �ƴϸ� infoView�� �ش� ���ڸ� ����մϴ�.

					roomNumber = Integer.parseInt(msg.substring(6)); // roomNumber�� msg�� �� 6���� [ROOM]�� ������ ���� ������ ����ȯ�Ͽ�
																		// �����մϴ�.

					if (board.isRunning()) { // ������ ������������ boolean������ ��ȯ���ִ� �޼ҵ带 �����մϴ�. �������̸� ture / �ƴ϶�� false�Դϴ�.

						board.stopGame(); // ������ ������ŵ�ϴ�.

					}

				}

				else if (msg.startsWith("[FULL]")) { // �޾ƿ� msg�� ������ ������ [FULL]�̶�� ���� �� ���¸� �ǹ��ϴ� ���Դϴ�.

					infoView.setText("���� ���� ������ �� �����ϴ�."); // infoView�� �ش� ���ڸ� ����մϴ�.

				}

				else if (msg.startsWith("[PLAYERS]")) { // �޾ƿ� msg�� ������ ������ [PLAYERS]��� �濡 �ִ� �������� ����� �ǹ��մϴ�.

					nameList(msg.substring(9)); // msg�� �� 9���� [PLAYERS]�� ������ ���� nameList�� �߰��ϴ� �޼ҵ带 ȣ���մϴ�.

				}

				else if (msg.startsWith("[ENTER]")) { // �޾ƿ� msg�� ������ ������ [ENTER]�̶�� �� ���忡 ���� ���Դϴ�.

					pList.add(msg.substring(7)); // msg�� �� 7���� [ENTER]�� ������ ���� pList�� �߰��մϴ�.

					playersInfo(); // �÷��̾�鿡 ���� ������ ��� �� �����ݴϴ�.

					msgView.append("[" + msg.substring(7) + "]���� �����Ͽ����ϴ�.\n"); // msgView(ä��â)�� �ش� ���ڵ��� ������ �߰��մϴ�.

				}

				else if (msg.startsWith("[EXIT]")) { // �޾ƿ� msg�� ������ ������ [EXIT]��� ������ ���忡 ���� ���Դϴ�.

					pList.remove(msg.substring(6)); // pList���� msg�� �� 6���� [EXIT]�� ������ ���� ���� ����Ʈ�� �����մϴ�.

					playersInfo(); // �ο����� �ٽ� ����Ͽ� �����ݴϴ�.

					msgView.append("[" + msg.substring(6) +

							"]���� �ٸ� ������ �����Ͽ����ϴ�.\n"); // �ش� ���ڸ� msgView(ä��â)�� ������ �߰��մϴ�.

					if (roomNumber != 0) // ���� ������ �ƴҶ����

						endGame("������ ���ӿ��� �������ϴ�."); // endGame�޼ҵ带 ȣ���մϴ�.

				}

				else if (msg.startsWith("[DISCONNECT]")) { // �޾ƿ� msg�� ������ ������ [DISCONNECT]��� ������ ���� ���ῡ ���� ���Դϴ�.

					pList.remove(msg.substring(12)); // msg�� �׿��� 12���� [DISCONNECT]�� ������ ���� ���� pList�� ���� �����մϴ�.

					playersInfo(); // �ο����� ��� �� �����ݴϴ�.

					msgView.append("[" + msg.substring(12) + "]���� ������ �������ϴ�.\n"); // ä��â�� �ش� ���ڸ� ����մϴ�.

					if (roomNumber != 0) // ���� ������ �ƴ϶��

						endGame("��밡 �������ϴ�."); // endGame�޼ҵ带 ȣ���մϴ�.

				}

				else if (msg.startsWith("[COLOR]")) { // �޾ƿ� msg�� ������ ������ [COLOR]��� ���� ���� �ο��ϴ°Ϳ� ���� ���Դϴ�.

					String color = msg.substring(7); // msg�� �տ� 7���� [COLOR]�� ������ ���� color ���ڿ��� �����մϴ�.

					board.startGame(color); // ������ �����ϴ� �޼ҵ忡 �Ű������� color�� �����Ͽ� �����մϴ�.

					if (color.equals("BLACK")) // ���� color���ڿ��� "BLACK"�� ���ٸ�

						infoView.setText("�浹�� �����մϴ�.."); // �ش� ���ڸ� ����մϴ�.

					else

						infoView.setText("�鵹�� �����մϴ�.."); // �ƴ϶�� �ش繮�ڸ� ����մϴ�.

					stopButton.setEnabled(true); // ��� ��ư�� Ȱ��ȭ�մϴ�.

				}

				else if (msg.startsWith("[DROPGAME]")) // �޾ƿ� msg�� ������ ������ [DROPGAME]�̶�� ��밡 ����Ѵٴ� �Ϳ� ���� �� �Դϴ�.

					endGame("��밡 ����Ͽ����ϴ�."); // endGame�� ����մϴ�.

				else if (msg.startsWith("[WIN]")) // �޾ƿ� msg�� ������ ������ [WIN]�̶�� �¸��� ���� ���Դϴ�.

					endGame("����� �̰���ϴ�. ���ϵ帳�ϴ�!");

				else if (msg.startsWith("[LOSE]")) // �޾ƿ� msg�� ������ ������ [LOSE]�̶�� �¸��� ���� ���Դϴ�.

					endGame("����� �����ϴ�. ��������!");

				// ��ӵ� �޽����� �ƴϸ� �޽��� ������ �����ش�.

				else
					msgView.append(msg + "\n"); // ���� �ִ� ��� ��쵵 �ƴ϶�� ä��â�� �״�� ���ϴ�.

			}

		} catch (IOException ie) {

			msgView.append(ie + "\n"); // ������ ������ ä��â�� ���ϴ�.

		}

		msgView.append("������ ������ϴ�.");

	}

	private void endGame(String msg) { // ������ �����Ű�� �޼ҵ��Դϴ�.

		infoView.setText(msg); // infoView�� �޾ƿ� msg�� ����մϴ�.

		startButton.setEnabled(false); // startButton�� ��Ȱ��ȭ�մϴ�.

		stopButton.setEnabled(false); // stopButton�� ��Ȱ��ȭ�մϴ�.

		try {
			Thread.sleep(2000); // 2�ʵ��� �����带 �����ϴ�.
		} catch (Exception e) {
		}

		if (board.isRunning()) // ���� ������ �������̶��, ������ �����ŵ�ϴ�.
			board.stopGame();

		if (pList.getItemCount() == 2) // ���� pList�� ��Ͽ� �ִ� ���� ���� 2���̸�
			startButton.setEnabled(true); // startButton�� Ȱ��ȭ��ŵ�ϴ�.

	}

	private void playersInfo() { // �� �������� ���� �����ִ� �޼ҵ��Դϴ�.

		int count = pList.getItemCount(); // count�� pList�� ���� ������ �����մϴ�.

		if (roomNumber == 0) // ���� ����(0)�̶��

			pInfo.setText("���� ���� �ο� : " + count + "��"); // pInfo�� �ش� ���ڸ� ����մϴ�.

		else
			pInfo.setText(roomNumber + " �� ��: " + count + "��"); // �ƴ϶�� pInfo�� �ش� ���ڸ� ����մϴ�.

		// ���� ���� ��ư�� Ȱ��ȭ ���¸� �����Ѵ�.

		if (count == 2 && roomNumber != 0) // count�� 2�̸�, ���� ����(0)�� �ƴ� ��,

			startButton.setEnabled(true); // ���۹�ư�� Ȱ��ȭ�մϴ�.

		else
			startButton.setEnabled(false); // �ƴ϶�� ���۹�ư�� ��Ȱ��ȭ�մϴ�.

	}

	// ���� ����Ʈ���� �������� �����Ͽ� pList�� �߰��մϴ�.

	private void nameList(String msg) {

		pList.removeAll(); // pList���� ��� ���� �����մϴ�.

		StringTokenizer st = new StringTokenizer(msg, "\t"); // StringTokenizer�� ù��° �Ű������� ���� ���ڿ�(msg)�� �ι�° �Ű������� ����
																// ���ڿ�("\t")�� ��ȹ���ڷ� �Ͽ� ��ū ������ �����ִ� ����� �մϴ�. ��) Hello My
																// Friends�� " "�� ��ȹ���ڷ� �Ͽ� ��ū ������ ������ Hello, My, Friends��
																// ���������ϴ�.

		while (st.hasMoreElements()) // st�� ���� ��ū ��Ұ� �� ������ �ִٸ�

			pList.add(st.nextToken()); // pList�� st�� ���� ��ū�� �߰��ϴ� ���� �ݺ��մϴ�.

		playersInfo(); // ���� ������ ���� �����ִ� �޼ҵ带 ȣ���մϴ�.

	}

	private void connect() { // ���ῡ ���� �޼ҵ��Դϴ�.

		try {

			msgView.append("���� ������ �õ��մϴ�.\n"); // ä��â�� ����մϴ�.

			socket = new Socket("192.168.43.224", 0707); // �ش��ϴ� ���� �����ǿ� �����մϴ�.

			msgView.append("!!!!! ���� ���� !!!!!\n"); // ä��â�� ����մϴ�.

			msgView.append("�г����� �Է� �� ���ǿ� �����ϼ���.\n"); // ä��â�� ����մϴ�.

			br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // ���Ͽ��Լ� ���� �޾ƿ��� bufferedReader�� �����մϴ�.
			
			pw = new PrintWriter(socket.getOutputStream(), true); // ���Ͽ��� ���� �����ϴ� PrintWriter�� �����մϴ�.

			new Thread(this).start(); // ��ü �ڱ��ڽ��� ���ο� �޼ҵ�� �����Ͽ� �����մϴ�.

			board.setWriter(pw); 

		} catch (Exception e) {

			msgView.append(e + "\n\n..... ���� ���� .....\n"); // ä��â�� ����մϴ�.

		}

	}

	public static void main(String[] args) {

		CLIENT client = new CLIENT("��Ʈ��ũ ���� ����");

		client.setSize(800, 560);// Ŭ���̾�Ʈ â�� ũ���Դϴ�
		client.setLocation(175, 50);// Ŭ���̾�Ʈ â ������ ��ġ�Դϴ�
		client.setVisible(true);

		client.connect();

	}

}