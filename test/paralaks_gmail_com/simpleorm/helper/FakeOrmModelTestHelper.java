package paralaks_gmail_com.simpleorm.helper;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import paralaks_gmail_com.simpleorm.model.FakeOrmModel;

public class FakeOrmModelTestHelper {
	private final static ComboPooledDataSource pool;

	protected boolean testTableReady = false;

	protected FakeOrmModel model;
	protected FakeOrmModel model2;
	protected FakeOrmModel nullModel;
	protected Connection connection;
	protected DbHelper db;

	protected FakeOrmModel foundOne = null;
	protected List<FakeOrmModel> foundList = null;

	public static final String TABLE_NAME_FAKE_MODEL = "fake_orm_models";
	private static final String DB_HOST = "localhost";
	private static final String DB_SCHEMA = "test";
	private static final String DB_USER = "test";

	public static final Integer ID_EMPTY = null;
	public static final Integer ID_NOT_EMPTY = 8;

	public static final String CONTENT_EMPTY = "";
	public static final String CONTENT_NOT_EMPTY = "this is some content";
	public static final String CONTENT_BY_ONCREATE = "Set by onCreate";
	public static final String CONTENT_BY_ONUPDATE = "Set by onUpdate";

	public static final Integer TOTAL_EMPTY = 0;
	public static final Integer TOTAL_NOT_EMPTY = 5;

	public static final Boolean STATUS_EMPTY = Boolean.FALSE;
	public static final Boolean STATUS_NOT_EMPTY = Boolean.TRUE;

	// Id thresholding to test callbacks by simply adding an error for id field
	public static final Integer THRESHOLD_INTERVAL = 10;
	public static final Integer THRESHOLD_AFTER_FIND_AND_LOAD_ID = 40;
	public static final Integer THRESHOLD_BEFORE_DESTROY_ID = THRESHOLD_AFTER_FIND_AND_LOAD_ID + 3 * THRESHOLD_INTERVAL; // bigger than after so that save does not fail
	public static final Integer THRESHOLD_AFTER_DESTROY_ID = THRESHOLD_AFTER_FIND_AND_LOAD_ID + 1 * THRESHOLD_INTERVAL;

	public static final Integer MAX_ID_LIMIT = 100;
	public static final String ERROR_ID = "This is id validation error";
	public static final String ERROR_ID_MAXED_OUT = "This is id after save error";
	public static final String ERROR_CONTENT = "This is content validation error";
	public static final String ERROR_STATUS = "Status can not be null";
	public static final String ERROR_TOTAL = "Total can not be null";
	public static final String ERROR_CREATED_AT = "Created at can not be in the future";
	public static final Date DATE_EMPTY;
	public static final Date DATE_NOT_EMPTY;
	public static final Date DATE_FUTURE;
	public static final String[] TEST_DATA_QUERIES;
	public static final List<Object[]> IMPORT_ROWS;
	public static final String DATE_NOT_EMPTY_STR;

	public static final String[] IMPORT_COLUMNS = { "status", "total", "content", "created_at", "updated_at" };

	static {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("EDT"));
		c.set(2001, 1, 1, 1, 1, 1);
		c.set(Calendar.MILLISECOND, 0);
		DATE_EMPTY = c.getTime();

		c.set(2005, 5, 5, 5, 5, 5);
		DATE_NOT_EMPTY = c.getTime();

		c.set(2199, 9, 9, 9, 9, 9);
		DATE_FUTURE = c.getTime();

		DATE_NOT_EMPTY_STR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DATE_NOT_EMPTY);

		// @formatter:off
		TEST_DATA_QUERIES = new String[] {
			"INSERT INTO "+TABLE_NAME_FAKE_MODEL+"(status, total, content, created_at, updated_at) values(null, null, null, null, null)",
			"INSERT INTO "+TABLE_NAME_FAKE_MODEL+"(status, total, content, created_at, updated_at) values(0, null, null, null, null)",
			"INSERT INTO "+TABLE_NAME_FAKE_MODEL+"(status, total, content, created_at, updated_at) values(0, 0, null, null, null)",
			"INSERT INTO "+TABLE_NAME_FAKE_MODEL+"(status, total, content, created_at, updated_at) values(1, 1, '" + CONTENT_NOT_EMPTY + "', null, null)",
			"INSERT INTO "+TABLE_NAME_FAKE_MODEL+"(status, total, content, created_at, updated_at) values(1, 2, '" + CONTENT_NOT_EMPTY + "', '" + DATE_NOT_EMPTY_STR + "', '" + DATE_NOT_EMPTY_STR + "')",
		};

		IMPORT_ROWS=Arrays.asList(new Object[][] {
			{null, null, null, null, null},
			{"0", null, null, null, null},
			{"0", "0", null, null, null},
			{"1", "1", CONTENT_NOT_EMPTY, null, null},
			{"1", "2", CONTENT_NOT_EMPTY, DATE_NOT_EMPTY_STR, DATE_NOT_EMPTY_STR},
		});
	// @formatter:on

		pool = new ComboPooledDataSource();
		try {
			pool.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc driver
			pool.setJdbcUrl(getJdbcUrl());
			pool.setUser(DB_USER);
			pool.setMinPoolSize(10);
			pool.setAcquireIncrement(1);
			pool.setMaxPoolSize(10);
		} catch (Exception e) {
			Assert.fail("Failed to instantiate a C3PO combined pooled data source");
		}

	}

	public static String getJdbcUrl() {
		return "jdbc:mysql://" + DB_HOST + "/" + DB_SCHEMA + "?user=" + DB_USER;
	}

	public static Connection getNewConnection() {
		try {
			return DriverManager.getConnection(getJdbcUrl());
		} catch (SQLException e) {
			Assert.fail("Failed to get DB connection");
		}

		return null;
	}

	public static ComboPooledDataSource getNewPooledDataSource() {
		ComboPooledDataSource pool = null;

		try {
			pool = new ComboPooledDataSource();
			pool.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc driver
			pool.setJdbcUrl(getJdbcUrl());
			pool.setUser(DB_USER);

			// the settings below are optional -- c3p0 can work with defaults
			// we will use small numbers since multiple instances of pooled datasource might be active and MySQL might hit max connection limit
			pool.setMinPoolSize(2);
			pool.setAcquireIncrement(1);
			pool.setMaxPoolSize(5);
		} catch (Exception e) {
			Assert.fail("Failed to instantiate a C3PO combined pooled data source");
		}

		return pool;
	}

	public static ComboPooledDataSource getPool() {
		return pool;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Before
	public void setUp() throws SQLException {
		connection = pool.getConnection();
		db = new DbHelper(connection);
		model = new FakeOrmModel(db);
		model2 = new FakeOrmModel(db);
		nullModel = new FakeOrmModel(db);

		Statement stSelect = connection.createStatement();
		try {
			testTableReady = stSelect.execute("SELECT * FROM " + TABLE_NAME_FAKE_MODEL);
		} catch (Exception e) {} finally {
			stSelect.close();
		}

		if (!testTableReady) {
			Statement stCreate = connection.createStatement();
			stCreate.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FAKE_MODEL
					+ " (id INT AUTO_INCREMENT, status INT, total INT,  content CHAR(255), created_at DATETIME, updated_at DATETIME, PRIMARY KEY(id))");
			stCreate.close();
			testTableReady = true;
		}

		if (testTableReady) {
			// truncate table
			Statement stTruncate = connection.createStatement();
			stTruncate.execute("truncate " + TABLE_NAME_FAKE_MODEL);
			stTruncate.close();
		}

		nullModel.initAllNull();
		foundOne = null;
		foundList = null;
	}

	@After
	public void tearDown() throws SQLException {
		if (testTableReady) {
			Statement stDrop = connection.createStatement();
			stDrop.execute("drop table " + TABLE_NAME_FAKE_MODEL);
			stDrop.close();
		}
		if (connection != null)
			connection.close();
	}

	public void populateTestTable() {
		Statement stInsert;
		try {
			stInsert = connection.createStatement();
			for (String query : TEST_DATA_QUERIES)
				stInsert.execute(query);
			stInsert.close();
		} catch (SQLException e) {
			assertTrue(e.getMessage(), e == null);
		}
	}

}
