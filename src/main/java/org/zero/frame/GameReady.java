package org.zero.frame;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import org.zero.common.CommonUtil;
import org.zero.db.ConnectionMgr;
import org.zero.db.DB;

import static java.util.Arrays.asList;
import static org.zero.common.CommonUtil.*;
import static org.zero.db.ConnectionMgr.closeConnection;
import static org.zero.db.ConnectionMgr.getConnection;

public class GameReady extends JFrame{
	private JPanel backgroundPanel;
	private Image background = new ImageIcon(Main.class.getResource("/static/img/backGround.png")).getImage();
	private String content="";
	private String name="";
	private int categoryIndex;
	public static Connection conn = null;
	public static Statement stmt = null;
	private int user_id;// primary key
	public GameReady() {
		// 시작기본세팅 메서드
		settings(this);

		//배경 패널 생성
		backgroundPanel = CommonUtil.makeBackground(backgroundPanel, background);
		add(backgroundPanel);

		JLabel idText = new JLabel("아이디");
		idText.setBounds(110,200,100,50);
		idText.setFont(midFont);
		backgroundPanel.add(idText);

		JTextField idTf = new JTextField();
		idTf.setBounds(188, 200, 360,50);
		idTf.setFont(midFont);
		backgroundPanel.add(idTf);

		JButton idBtn = new JButton("확인");
		idBtn.setBounds(570,200,80,50);
		idBtn.setBackground(new Color(255,228,131));
		idBtn.setFont(midFont);
		backgroundPanel.add(idBtn);

		//test
		JButton gamestartBtn = new JButton("게임시작");
		gamestartBtn.setBounds(500,383, 170, 45);
		gamestartBtn.setBackground(new Color(255,228,131));
		gamestartBtn.setBorder(new RoundBorder(30, new Color(255, 228, 131), 2.0f));
		gamestartBtn.setForeground(new Color(168, 131, 0));
		gamestartBtn.setFont(midFont);
		backgroundPanel.add(gamestartBtn);

		idBtn.addActionListener(event->{
			name = idTf.getText();
			if(name.equals("")){
				JOptionPane.showMessageDialog(null, "아이디를 입력하세요.");
			}
			else {
				JOptionPane.showMessageDialog(null, "아이디가 정상적으로 등록 되었습니다.");
				idBtn.setBackground(new Color(198,198,198));
				idTf.setEnabled(false);
				saveUserName(name);// 사용자 이름 db에 저장
			}
		});

		setVisible(true);


	}

	public void changeBtn(JButton btn, int index, ArrayList<Integer> flag, JButton button[]){
		content = btn.getText();
		if (flag.get(index) == 0) {
			btn.setBackground(new Color(255, 228, 131));
			if (flag.contains(1)) {
				int changeColor = flag.indexOf(1);
				button[changeColor].setBackground(new Color(255, 255, 255));
				flag.set(changeColor, 0);
			}
			flag.set(index, 1);
		} else {
			btn.setBackground(new Color(255, 255, 255));
			flag.set(index, 0);
		}
	}

	private void saveUserName(String userId) {
		// 데이터베이스 연결을 설정하고 사용자 ID를 삽입합니다.
		try {
			conn = getConnection(DB.MySQL.JDBC_URL);
			String query = "INSERT INTO user (name) VALUES ('"+name+"')";
			stmt = conn.createStatement();
			stmt.executeUpdate(query);

			// 현재 사용자의 id(primary key)값 알기
			ResultSet rs;
			rs = stmt.executeQuery("SELECT * FROM user");
			while ( rs.next()) {
				this.user_id = Integer.parseInt(rs.getString("id"));
			}

			System.out.println(this.user_id);

			// 사용 후 close
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("사용자 이름 저장 실패");
		}
	}

	private void saveGameCategory(int categoryIndex) {
		String category = "";
		// 카테고리 찾기
		switch (categoryIndex) {
			case 0: category = "동물"; break;
			case 1: category = "음식"; break;
			case 2: category = "사물"; break;
			case 3: category = "장소"; break;
		}

		// 데이터베이스 연결을 설정하고 사용자 ID를 삽입합니다.
		try {
			String query = "UPDATE user SET category = '"+category+"' WHERE id = "+this.user_id;
			ResultSet rs;
			stmt.executeUpdate(query);
			rs = stmt.executeQuery("SELECT * FROM user");

			// 테스트
			while ( rs.next()) {
				System.out.print(rs.getString("id")+" ");
				System.out.print(rs.getString("name")+" ");
				System.out.print(rs.getString("team_id")+" ");
				System.out.print(rs.getString("captain")+" ");
				System.out.println(rs.getString("category")+" ");
			}

			// 사용 후 close
			stmt.close();
			rs.close();
			closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("게임 카테고리 저장 실패");
		}
	}

	private void saveUserTopicToDatabase(String userId, String topic) {
		// 데이터베이스 연결을 설정하고 사용자가 선택한 주제를 업데이트합니다.
		try {
			conn = getConnection(DB.MySQL.JDBC_URL);
			String query = "UPDATE user SET selected_topic = ? WHERE user_id = ?";
			stmt = conn.prepareStatement(query);

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GameReady();
	}
}
