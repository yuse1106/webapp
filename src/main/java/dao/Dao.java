package dao;
import java.util.*;
import java.util.Date;

import model.Comment;

import java.sql.*;

/**
 * <h3>simple sample class for database access object</h3>
 * @author nakano@cc.kumamoto-u.ac.jp
 * @version 2021-07-07 (from 2019-05)
 */
public class Dao {
	private String dbDriver = null;
	private String dbUri = null;
	private Properties dbProps = null;

	/**
	 * <h5>Constructor</h5>
	 * read parameters for database access from external file sys.properties /
	 * 外部ファイル sys.properties からパラメータを得る
	 */
	public Dao() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("sys");
			dbDriver = bundle.getString("driver");
			dbUri = bundle.getString("uri");
			dbProps = new Properties();
			dbProps.put("user", bundle.getString("user"));
			dbProps.put("password", bundle.getString("password"));
			dbProps.put("characterEncoding", "UTF8"); // need for multibyte chars. / 日本語等ではこれが必要
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getAll() method
	 * get all data from "comment" table
	 */
	/**
	 * <h5>Get all data / 全てのデータを取得</h5>
	 * @return all data / 全データ
	 */
	public ArrayList<Comment> getAll() {
		ArrayList<Comment> ret = new ArrayList<Comment>();
		final String queryStr = "select name, atime, btime, com, `date` from comment order by `date` desc;";
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		try {
			// Instantiation of mySQL jdbc driver. / mySQLのjdbcドライバのインスタンス化
			Class.forName(dbDriver).getDeclaredConstructor().newInstance();
//			System.out.println("Class.forName(dbDriver).newInstance().toString()="+Class.forName(dbDriver).newInstance().toString());
			conn = DriverManager.getConnection(dbUri, dbProps);
			conn.setAutoCommit(true);
			state = conn.createStatement();
			rs = state.executeQuery(queryStr); // executing query / queryの実行
			// obtain query results / queryの結果を得る
			while (rs.next()) {
//				ret.add(new Comment(rs.getNString(1), rs.getNString(2), rs.getString(3), rs.getTimestamp(4)));
				ret.add(new Comment(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5)));
			}
			//for exception
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				rs.close();
				state.close();
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * <h5>insert a record to "comment" table / "comment"テーブルに1行データを追加</h5>
	 * @param cmt
	 * @return ret number of added lines / 追加行数
	 */
	public int insert(Comment cmt) {
		int ret = -1;
		Connection conn = null;
		Statement state = null;
		try {
			// Instantiation of mySQL jdbc driver. / mySQLのjdbcドライバのインスタンス化
			Class.forName(dbDriver).getDeclaredConstructor().newInstance(); 
//			System.out.println("Class.forName(dbDriver).newInstance().toString()="+Class.forName(dbDriver).newInstance().toString());
			conn = DriverManager.getConnection(dbUri, dbProps);
			conn.setAutoCommit(true);
			state = conn.createStatement();
			PreparedStatement pStr = null;
			final String sStr = "insert into comment (name, atime, btime, com, `date`) values (?, ?, ?, ?, ?);";
			pStr = conn.prepareStatement(sStr);
			pStr.setString(1, cmt.getName());
			pStr.setString(2, cmt.getAtime());
			pStr.setString(3, cmt.getBtime());
			pStr.setString(4, cmt.getCom());
			pStr.setTimestamp(5, new Timestamp(cmt.getDate().getTime()));
			pStr.execute();
			ret = pStr.getUpdateCount();
			//for exception
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				state.close();
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * <h5>insert erase all data of "comment" table / "comment"テーブルのデータを全て消去</h5>
	 * @return if succeed 0, error -1 / 成功すれば0 エラーなら-1
	 */
	public int deleteAll() {
		int ret = -1;
		Connection conn = null;
		Statement state = null;
		final String executeStr = "truncate comment;";
		try {
			// Instantiation of mySQL jdbc driver. / mySQLのjdbcドライバのインスタンス化
			Class.forName(dbDriver).getDeclaredConstructor().newInstance(); 
			conn = DriverManager.getConnection(dbUri, dbProps);
			conn.setAutoCommit(true);
			state = conn.createStatement();
			PreparedStatement pStr = null;
			ret = state.executeUpdate(executeStr);
			//for exception
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				state.close();
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * <h5>main method for check / チェック用mainメソッド</h5>
	 * @param args
	 */
	public static void main(String[] args) {
		Dao dao = new Dao();
		for(int i = 0; i < 4; i++)
			dao.insert(new Comment("なまえ"+i, "テキスト"+i, "タイム", "コメント", new Date()));
		dao.getAll().forEach(s -> System.out.println(s.getName()+","+s.getAtime()+","+s.getBtime()+","+s.getCom()+","+s.getDate()));
		System.out.println(dao.deleteAll());
	}
	
}