package paralaks_gmail_com.simpleorm.helper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import paralaks_gmail_com.simpleorm.model.OrmModel;

public final class DbHelper {
	// helper to store class properties
	private class ClassInfo {
		public Pattern fieldsPattern;
		public HashMap<String, FieldProperties> fieldMap = new HashMap<>();
	}

	// helper to store DB/class field mapping
	private class FieldProperties {
		public Field field;
		// below are helper fields to reduce function calls
		public String fieldName;
		public String fieldType;
	}

	public final int MAX_SELECT_LIMIT = 5000;
	public final int MAX_SELECT_LIMIT_IDS = 5000;

	private static final Logger LOGGER = LogHelper.getLogSingleLineOutput(DbHelper.class.getName());

	private final Set<String> mappedTypes = new HashSet<>();
	private final HashMap<String, ClassInfo> cachedClasses = new HashMap<>();
	private final String regWhereClean = "[;]";
	private final String regGroupOrderClean = "[^ A-z0-9@,._;-]";
	private final String PLACEHOLDER_WHERE = "<PLACEHOLDER_WHERE>";
	private final String SELECT_COUNT = "SELECT COUNT(*) ";
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private boolean ansiSql = false;
	private Connection connection = null;

	// Class fields of types below are mapped to DB fields. Primitive types are skipped to allow null test/assignment on fields
	public DbHelper(Connection connection) {
		setConnection(connection);
		mappedTypes.add("java.lang.Byte");
		mappedTypes.add("java.lang.Short");
		mappedTypes.add("java.lang.Integer");
		mappedTypes.add("java.lang.Long");
		mappedTypes.add("java.lang.Double");
		mappedTypes.add("java.lang.Float");
		mappedTypes.add("java.lang.Boolean");
		mappedTypes.add("java.util.Date");
		mappedTypes.add("java.lang.String");
	}

	public static Date getNow() {
		return new Date();
	}

	public static long getNowMillis() {
		// return Calendar.getInstance().getTimeInMillis();
		return (new Date()).getTime();
	}

	public static int getNowSeconds() {
		return (int) (new Date().getTime() / 1000);
	}

	public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, minute, second);
		return c.getTime();
	}

	public static StringBuilder removeEndChar(StringBuilder string, char remove) {
		if (string != null && string.length() > 0) {
			int lastCharPos = string.length() - 1;
			if (string.charAt(lastCharPos) == remove)
				string.deleteCharAt(lastCharPos);
		}

		return string;
	}

	public static String removeEndChar(String string, char remove) {
		if (string != null && string.length() > 0) {
			int lastCharPos = string.length() - 1;
			if (string.charAt(lastCharPos) == remove)
				string = string.substring(0, lastCharPos);
		}

		return string;
	}

	public static <K, V> HashMap<K, V> initHashMap(K key, V value) {
		HashMap<K, V> newMap = new HashMap<K, V>();
		if (key != null)
			newMap.put(key, value);

		return newMap;
	}

	/**
	 * Sets internal connection object to the parameter and queries sql_mode in order to detect ANSI_QUOTES flag is set
	 *
	 * @param {@link java.sql.Connection}
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
		Statement stmnt = null;
		ResultSet rset = null;

		if (connection != null)
			try {
				String sqlMode = null;
				stmnt = connection.createStatement();
				rset = stmnt.executeQuery("SELECT @@SESSION.sql_mode");
				if (rset.next())
					ansiSql = (sqlMode = rset.getString(1)) != null
							&& sqlMode.toUpperCase().indexOf("ANSI_QUOTES") > -1;
			} catch (Exception e) {
				LOGGER.severe("Failed to get sql mode " + e.getStackTrace().toString());
			} finally {
				closeSqlObjects(rset, stmnt);
			}
	}

	/**
	 * Returns the internal active connection object (either requested from the pool or assigned by {@link #setConnection(Connection)} )
	 *
	 * @return null or an active connection object.
	 */
	public Connection getConnection() {
		try {
			return (connection != null && !connection.isClosed()) ? connection : null;
		} catch (SQLException e) {
			LOGGER.severe(e.getSQLState());
		}

		return null;
	}

	/**
	 * Closes internal connection object
	 */
	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			LOGGER.severe(e.getStackTrace().toString());
		}
	}

	/**
	 * Accepts any number of {@link java.sql.ResultSet}, {@link java.sql.PreparedStatement} or {@link java.sql.Statement} objects and closes them by
	 * wrapping in try/catch blocks in order to avoid verbose close() calls.
	 *
	 * @param Multiple objects
	 */
	public static void closeSqlObjects(Object... objects) {
		for (Object o : objects)
			try {
				if (o != null)
					if (o instanceof ResultSet)
						((ResultSet) o).close();
					else if (o instanceof PreparedStatement)
						((PreparedStatement) o).close();
					else if (o instanceof Statement)
						((Statement) o).close();
					else
						LOGGER.severe("Unknown type passed to closeSqlObjects : " + o.getClass());
			} catch (Exception e) {
				LOGGER.severe(e.getStackTrace().toString());
			}
	}

	private static String _snakeToCamel(final String snake, final boolean isLower) {
		if (snake == null || snake.isEmpty())
			return "";

		final char underscore = '_';
		StringBuffer camel = new StringBuffer(snake.length());

		// convert input to lowercase
		snake.toLowerCase();

		// capitalize char following an underscore
		char charCurr = snake.charAt(0);
		boolean isUnderScorePrev = charCurr == underscore ? true : false;
		camel.append(isLower ? charCurr : Character.toUpperCase(charCurr));

		for (int i = 1, end = snake.length(); i < end; i++) {
			charCurr = snake.charAt(i);
			camel.append((isUnderScorePrev && charCurr != underscore) ? Character.toUpperCase(charCurr) : charCurr);
			isUnderScorePrev = (charCurr == underscore) ? true : false;
		}

		// remove underscores
		return camel.toString().replaceAll("" + underscore, "");
	}

	/**
	 * Returns class names corresponding to database table names.
	 *
	 * @param snake
	 * @return null or parameter as camel case.
	 */
	public static String snakeToCamel(String snake) {
		return _snakeToCamel(snake, false);
	}

	/**
	 * Returns class field names corresponding to database table column names.
	 *
	 * @param snake
	 * @return null or parameter as camel case (first letter lowercase).
	 */
	public static String snakeToCamelLower(String snake) {
		return _snakeToCamel(snake, true);
	}

	/**
	 * Returns database table/column names corresponding to class or field names.
	 *
	 * @param camel
	 * @return null or parameter as snake case.
	 */
	public static String camelToSnake(String camel) {
		if (camel == null || camel.isEmpty())
			return "";

		StringBuffer snake = new StringBuffer(camel.length() + 5); // +5 for extra underscores
		char charCurr = camel.charAt(0);
		boolean isLowerPrev = Character.isLowerCase(charCurr);
		snake.append(charCurr);

		// find transitions from lowercase to uppercase and insert an underscore in between
		for (int i = 1, end = camel.length(); i < end; i++) {
			charCurr = camel.charAt(i);
			snake.append((isLowerPrev && Character.isUpperCase(charCurr)) ? "_" + charCurr : charCurr);
			isLowerPrev = Character.isLowerCase(charCurr);
		}

		// return result in lowercase
		return snake.toString().toLowerCase();
	}


	/**
	 * Returns database table name for given model class.
	 *
	 * @param klass
	 * @return Empty string or table name
	 */
	public static <T extends OrmModel> String tableName(Class<T> klass) {
		String tmp = klass.getSimpleName();
		String name = null;

		// pesky names
		if (tmp.endsWith("Key")) // ApiKey causes trouble
			name = tmp + "s";
		else if (tmp.endsWith("y"))
			name = tmp.substring(0, tmp.length() - 1) + "ies";
		else if (tmp.endsWith("s"))
			name = tmp + "es";
		else
			name = tmp + "s";

		return camelToSnake(name.replaceAll("[^a-zA-Z0-9_]", ""));
	}

	// Finds DB columns which will be mapped to model class fields.
	// Column list is used to construct regex pattern which is used to rewrite where clause in findSql methods as prepared statements.
	// Column/model list and regex pattern is cached to speed up the application
	private ClassInfo getCachedClass(Class<? extends OrmModel> klass) {
		ClassInfo classInfo;

		synchronized (cachedClasses) {
			classInfo = cachedClasses.get(klass.getName());

			if (classInfo == null) {
				classInfo = new ClassInfo();

				// construct fields based on table columns
				String sqlDesc = "DESCRIBE " + tableName(klass);
				PreparedStatement pStFDesc = null;
				ResultSet rSetDesc = null;

				try {
					pStFDesc = connection.prepareStatement(sqlDesc);
					rSetDesc = pStFDesc.executeQuery();
					FieldProperties fpDb = null;
					StringBuilder regex = new StringBuilder("\\b(?<f>");

					while (rSetDesc.next()) {
						fpDb = new FieldProperties();
						fpDb.fieldName = rSetDesc.getString(1);
						classInfo.fieldMap.put(fpDb.fieldName, fpDb);
						// add to regex as well
						regex.append(fpDb.fieldName + '|');
					}

					regex.replace(regex.length() - 1, regex.length(), ")\\b");
					// add "operator operand" part and compile regex for class
					regex.append("\\s{0,}(?<op>[!=<>][!=<>]?)\\s{0,}(['`](?<v1>[^'`]{0,})['`]|(?<v2>[^\\s]*))");
					classInfo.fieldsPattern = Pattern.compile(regex.toString(),
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				} catch (Exception e) {
					LOGGER.severe(e.getStackTrace().toString());
				} finally {
					closeSqlObjects(rSetDesc, pStFDesc);
				}

				// extract field properties from classes (including parent) and complete DB field to class field mapping
				Field[] fields = null;
				FieldProperties fp = null;
				String fieldNameSnake = null;
				List<Class<?>> classList = new ArrayList<>();
				Class<?> tmpClass = klass;

				while (tmpClass != null && !(tmpClass.isAnnotation() || tmpClass.isArray() || tmpClass.isPrimitive()
						|| tmpClass.isSynthetic())) {
					classList.add(tmpClass);
					tmpClass = tmpClass.getSuperclass();
				}

				for (int i = 0, endI = classList.size(); i < endI; i++) {
					tmpClass = classList.get(i);
					fields = tmpClass.getDeclaredFields();
					for (int j = 0, endJ = fields.length; j < endJ; j++) {
						fieldNameSnake = camelToSnake(fields[j].getName());

						if (!classInfo.fieldMap.containsKey(fieldNameSnake))
							continue;

						fp = classInfo.fieldMap.get(fieldNameSnake);
						fp.field = fields[j];
						fp.field.setAccessible(true);
						fp.fieldType = fp.field.getType().getSimpleName();
					}

				}

				cachedClasses.put(klass.getName(), classInfo);
			}
		}

		return classInfo;
	}

	// regex pattern helps to extract conditions using object fields
	private Pattern getClassPattern(Class<? extends OrmModel> klass) {
		return getCachedClass(klass).fieldsPattern;
	}

	private HashMap<String, FieldProperties> getClassFields(Class<? extends OrmModel> klass) {
		return getCachedClass(klass).fieldMap;
	}

	public static String getQueryForLog(Statement statement) {
		String query = statement.toString();
		return "[" + query.substring(query.indexOf(':', query.indexOf(':') + 1) + 2);
	}

	// helper to construct prepared statement using object fields with non-null values
	private <T extends OrmModel> PreparedStatement prepareWhereAndStatement(T object,
			HashMap<String, FieldProperties> fieldMap, String queryWithPlaceholder) throws SQLException {
		StringBuilder partWhere = new StringBuilder(150);
		PreparedStatement pStDynamic = null;
		HashMap<String, Object> values = new HashMap<>();
		// pull field values from object
		for (FieldProperties fp : fieldMap.values())
			try {
				values.put(fp.fieldName, fp.field.get(object));
			} catch (Exception e) {
				values.put(fp.fieldName, null);
				LOGGER.warning(e.getStackTrace().toString());
			}

		for (String column : fieldMap.keySet())
			if (values.get(column) != null)
				partWhere.append(" AND " + column + "=?");

		// ONLY count query should be allowed without a where condition
		if (queryWithPlaceholder.indexOf(SELECT_COUNT) < 0 && partWhere.length() == 0) {
			LOGGER.warning("Ignoring search without where condition. Query is: "
					+ queryWithPlaceholder.replaceFirst(PLACEHOLDER_WHERE, " "));
			return null;
		}

		String query = queryWithPlaceholder.replaceFirst(PLACEHOLDER_WHERE,
				partWhere.length() == 0 ? "" : " WHERE" + partWhere.toString().replaceFirst(" AND", ""));
		pStDynamic = connection.prepareStatement(query);

		int idx = 1;
		for (String column : fieldMap.keySet())
			if (values.get(column) != null)
				pStDynamic.setObject(idx++, values.get(column));

		LOGGER.info(getQueryForLog(pStDynamic));

		return pStDynamic;
	}

	private <T extends OrmModel> PreparedStatement prepareStatementFromQuery(Class<T> klass, String query, String where)
			throws SQLException {
		PreparedStatement pStDynamic = null;

		if (where.isEmpty()) {
			pStDynamic = connection.prepareStatement(query);

			LOGGER.info(getQueryForLog(pStDynamic));

			return pStDynamic;
		}

		String partWhere = new String(where);
		StringBuilder querySt = new StringBuilder(query);

		// time to turn partWhere clause into a prepared statement for security reasons
		// regex will search for "field operator operand" in partWhere clause
		List<Object> values = new ArrayList<>();
		Matcher matcher = getClassPattern(klass).matcher(partWhere);
		while (matcher.find()) {
			partWhere = partWhere.replaceFirst(matcher.group(), matcher.group("f") + matcher.group("op") + "?");
			Object value = matcher.group("v1") == null ? matcher.group("v2") : matcher.group("v1");

			// null value fix
			if (value == null || value.toString().equals("null"))
				partWhere = partWhere.replaceFirst(matcher.group("f") + matcher.group("op") + "\\?",
						matcher.group("f") + " IS NULL ");
			else
				values.add(value);
		}

		// replace uses regex which might mess with the query.
		int from = querySt.indexOf(where);
		querySt = querySt.replace(from, from + where.length(), partWhere);
		// System.out.println("Where before: " + where + "\nWhere after: " + partWhere);

		pStDynamic = connection.prepareStatement(querySt.toString());
		for (int i = 0, end = values.size(); i < end; i++)
			pStDynamic.setObject(i + 1, values.get(i));

		LOGGER.info(getQueryForLog(pStDynamic));

		return pStDynamic;
	}

	/**
	 * Returns number of records from database table representing the parameter object. Object's fields are used to construct the where clause. ie: if
	 * the object has fields as: firstName=John and lastName=Doe
	 * count query will have: first_name = "John" and last_name="Doe" as where condition.
	 *
	 * @param object
	 * @return 0 or actual count from database table representing the object class.
	 */
	public <T extends OrmModel> int count(T object) {
		if (object == null)
			return 0;

		Class<? extends OrmModel> klass = object.getClass();
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return 0;

		String sqlCount = SELECT_COUNT + "FROM " + tableName(klass) + PLACEHOLDER_WHERE;
		PreparedStatement pStCount = null;
		ResultSet rSetCount = null;

		// construct select statement and execute
		try {
			pStCount = prepareWhereAndStatement(object, fieldMap, sqlCount);
			if (pStCount == null)
				return 0;

			rSetCount = pStCount.executeQuery();
			if (rSetCount.next())
				return rSetCount.getInt(1);
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(rSetCount, pStCount);
		}

		return 0;
	}

	/**
	 * Returns number of records from database table representing objects of the klass parameter. Where parameter is added to query if present.
	 *
	 * @param klass
	 * @param where
	 * @return 0 or number of records.
	 */
	public <T extends OrmModel> int count(Class<T> klass, String where) {
		where = where == null || where.trim().length() == 0 ? "" : " WHERE " + where.trim();

		String sqlCount = SELECT_COUNT + "FROM " + tableName(klass) + where;
		PreparedStatement pStCount = null;
		ResultSet rSetCount = null;

		// construct count statement and execute
		try {
			pStCount = prepareStatementFromQuery(klass, sqlCount, where);
			rSetCount = pStCount.executeQuery();

			if (rSetCount.next())
				return rSetCount.getInt(1);
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(rSetCount, pStCount);
		}

		return 0;
	}

	/**
	 * Returns number of records from database table representing objects of the klass parameter.
	 *
	 * @param klass
	 * @return 0 or number of records.
	 */
	public <T extends OrmModel> int count(Class<T> klass) {
		return klass == null ? 0 : count(klass, null);
	}

	// populates first object's oldmap which can be used to check whether an field value was changed after it was loaded by find.
	// Also executes afterFindAndLoad function for the first object in the list.
	private <T extends OrmModel> void callbacksForFind(List<T> results, HashMap<String, FieldProperties> fieldMap) {
		if (results.size() == 0)
			return;

		HashMap<String, Object> map = new HashMap<>();
		T firstItem = results.get(0);
		Object fieldValue = null;
		for (FieldProperties fp : fieldMap.values())
			try {
				fieldValue = fp.field.get(firstItem);

				if (fieldValue != null)
					map.put(fp.fieldName, fieldValue);
			} catch (Exception e) {
				LOGGER.severe(e.getStackTrace().toString());
			}

		firstItem.setOldMap(map);
		firstItem.afterFindAndLoad();
	}

	/**
	 * Using resultSet parameter, either constructs objects of the klass parameter type and adds them to results parameter list, or if results
	 * parameter is null, updates object parameter's fields.
	 *
	 * @param resultSet Result set from a successful query execution
	 * @param results List to store new objects
	 * @param klass Type of object to be constructed
	 * @param object If not populating a list; then object to be updated
	 * @throws Exception
	 */
	public <T extends OrmModel> void assignResultSetToListOrObject(ResultSet resultSet, List<T> results,
			Class<? extends OrmModel> klass, T object) throws Exception {

		if (results == null && object == null)
			return;

		klass = object != null ? object.getClass() : klass;
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);
		ResultSetMetaData metas = resultSet.getMetaData();
		String metaColumName = null;
		FieldProperties fp = null;
		Object dbValue = null;

		// why this? Before loadFrom() calls, rSet.next() function should already be executed and we will skip records.. ouch!
		boolean validResultset = true;
		if (results != null)
			validResultset = resultSet.next() ? true : false;

		if (validResultset)
			do {
				@SuppressWarnings("unchecked")
				T newObj = results != null ? (T) klass.getConstructor(DbHelper.class).newInstance(this) : object;
				for (int i = 1, end = metas.getColumnCount() + 1; i < end; i++)
					try {
						metaColumName = metas.getColumnLabel(i);
						if (!fieldMap.containsKey(metaColumName))
							continue;

						dbValue = resultSet.getObject(i);
						fp = fieldMap.get(metaColumName);
						fp.field.set(newObj, null);

						if (dbValue != null) {
							if (fp.fieldType.equals("Integer"))
								fp.field.set(newObj, resultSet.getInt(i));
							else if (fp.fieldType.equals("String"))
								fp.field.set(newObj, resultSet.getString(i));
							else if (fp.fieldType.equals("Date"))
								fp.field.set(newObj, DATE_FORMAT.parse(resultSet.getString(i)));
							else if (fp.fieldType.equals("Boolean"))
								fp.field.set(newObj, resultSet.getBoolean(i));
							else
								fp.field.set(newObj, dbValue);
						}
					} catch (Exception e) {
						LOGGER.severe(e.getStackTrace().toString());
					}

				if (results != null)
					results.add(newObj);
				else
					break;

			} while (resultSet.next());
	}


	// Loads records from DB using reflection. Argument must be a valid object; non-null fields are used to construct the where condition.
	// Returns a list of: new objects or single object
	private <T extends OrmModel> List<T> _find(Connection connection, T object, boolean findOne) {
		List<T> results = new ArrayList<>();

		if (object == null)
			return results;

		Class<? extends OrmModel> klass = object.getClass();
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return results;

		// construct prepared statement dynamically
		String query = "SELECT * FROM " + tableName(klass) + PLACEHOLDER_WHERE + " LIMIT "
				+ (findOne ? 1 : MAX_SELECT_LIMIT);
		PreparedStatement pStFind = null;
		ResultSet rSetFind = null;

		try {
			pStFind = prepareWhereAndStatement(object, fieldMap, query);
			if (pStFind == null)
				return results;

			rSetFind = pStFind.executeQuery();
			if (rSetFind == null)
				return results;

			assignResultSetToListOrObject(rSetFind, results, klass, null);

		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(rSetFind, pStFind);
		}

		// being called for find(() so execute callbacks
		if (findOne)
			callbacksForFind(results, fieldMap);

		return results;
	}

	/**
	 * Searches for a database record matching parameter object's non-null fields.
	 *
	 * @param object
	 * @return null or a new object matching parameter object's non-null fields.
	 */
	public <T extends OrmModel> T find(T object) {
		List<T> results = _find(connection, object, true);

		return results.size() > 0 ? results.get(0) : null;
	}

	/**
	 * Searches for database records matching parameter object's non-null fields.
	 *
	 * @param object
	 * @return An empty list or a list of maximum {@link #MAX_SELECT_LIMIT} objects.
	 */
	public <T extends OrmModel> List<T> findAll(T object) {
		return _find(connection, object, false);
	}

	/**
	 * Searches for records from database table representing the klass parameter objects. Where parameter is added to query if present. Results can be
	 * ordered using orderBy parameter. Number of results can be limited by limitOffset and limitCount parameters.
	 *
	 * @param klass
	 * @param where
	 * @param orderBy
	 * @param limitOffset
	 * @param limitCount
	 * @return An empty list or a list of maximum limitCount or {@link #MAX_SELECT_LIMIT} objects.
	 */
	public <T extends OrmModel> List<T> findAll(Class<T> klass, String where, String orderBy, Integer limitOffset,
			Integer limitCount) {
		List<T> results = new ArrayList<>();
		if (klass == null)
			return results;

		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return results;

		String partWhere = where == null || where.trim().length() == 0 ? ""
				: " WHERE " + where.replaceAll(regWhereClean, "").trim();
		String partOrderBy = orderBy == null || orderBy.trim().length() == 0 ? ""
				: " ORDER BY " + orderBy.replaceAll(regGroupOrderClean, "").trim();
		String limitBy = " LIMIT "
				+ ((limitCount == null || limitCount < 1 || limitCount > MAX_SELECT_LIMIT) ? MAX_SELECT_LIMIT
						: limitCount)
				+ (limitOffset == null || limitOffset < 1 ? "" : " OFFSET " + limitOffset);

		String query = "SELECT * FROM " + tableName(klass) + partWhere + partOrderBy + limitBy;
		PreparedStatement pStFind = null;
		ResultSet rSetFind = null;

		try {
			pStFind = prepareStatementFromQuery(klass, query, partWhere);
			rSetFind = pStFind.executeQuery();

			if (rSetFind == null)
				return results;

			assignResultSetToListOrObject(rSetFind, results, klass, null);

		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(rSetFind, pStFind);
		}

		// being called for find() so execute callbacks
		if (limitOffset != null && limitCount != null && limitOffset.equals(0) && limitCount.equals(1))
			callbacksForFind(results, fieldMap);

		return results;
	}

	/**
	 * Reloads parameter object from database unless it is frozen and id field is not null, initializes oldMap, executes afterFindAndLoad callback and
	 * returns true if all the steps succeed.
	 *
	 * @param object
	 * @return true if object was reloaded from database & callbacks executed successfully, false otherwise.
	 */
	public <T extends OrmModel> boolean reload(T object) {
		boolean result = false;
		if (object == null || object.getId() == null || object.isFrozen())
			return result;

		Class<? extends OrmModel> klass = object.getClass();
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return result;

		Integer id = object.getId();
		String query = "SELECT * FROM " + tableName(klass) + " WHERE id=?";
		PreparedStatement pStFind = null;
		ResultSet rSetFind = null;

		try {
			for (FieldProperties fp : fieldMap.values())
				fp.field.set(object, null);

			pStFind = connection.prepareStatement(query);
			pStFind.setInt(1, id);

			LOGGER.info(getQueryForLog(pStFind));

			rSetFind = pStFind.executeQuery();
			if (rSetFind.next())
				assignResultSetToListOrObject(rSetFind, null, klass, object);

			object.setOldMap(object.fields());
			object.afterFindAndLoad();
			result = true;
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(rSetFind, pStFind);
		}

		return result;
	}

	/**
	 * Returns only ids of records from database table representing the klass parameter objects. Where parameter is added to query if present.
	 *
	 * @param klass
	 * @param where
	 * @return An empty list or a list of maximum {@link #MAX_SELECT_LIMIT_IDS} ids.
	 */
	public <T extends OrmModel> List<Integer> findIds(Class<T> klass, String where) {
		List<Integer> ids = new ArrayList<>();
		if (klass == null)
			return ids;

		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return ids;

		String partWhere = where != null && (where = where.trim()).length() > 0
				? " WHERE " + where.replaceAll(regWhereClean, "")
				: " ";

		String query = "SELECT * FROM " + tableName(klass) + partWhere + " LIMIT " + MAX_SELECT_LIMIT_IDS;
		PreparedStatement pStFind = null;
		ResultSet rSetFind = null;

		try {
			pStFind = prepareStatementFromQuery(klass, query, partWhere);
			rSetFind = pStFind.executeQuery();

			if (rSetFind == null)
				return ids;
			while (rSetFind.next())
				ids.add(rSetFind.getInt(1));

		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(rSetFind, pStFind);
		}

		return ids;
	}

	/**
	 * Searches for records from database table representing the klass parameter objects. Where parameter is added to query if present. Results can be
	 * ordered using orderBy parameter.
	 *
	 * @param klass
	 * @param where
	 * @param orderBy
	 * @return An empty list or a list of maximum {@link #MAX_SELECT_LIMIT} objects.
	 */
	public <T extends OrmModel> List<T> findAll(Class<T> klass, String where, String orderBy) {
		return findAll(klass, where, orderBy, null, null);
	}

	/**
	 * Searches for records from database table representing the klass parameter objects. Where parameter is added to query if present.
	 *
	 * @param klass
	 * @param where
	 * @return An empty list or a list of maximum {@link #MAX_SELECT_LIMIT} objects.
	 */
	public <T extends OrmModel> List<T> findAll(Class<T> klass, String where) {
		return findAll(klass, where, null, null, null);
	}

	/**
	 * Searches for the first record from database table representing the klass parameter objects. Where parameter is added to query if present.
	 * Search order direction can be set using orderBy parameter.
	 *
	 * @param klass
	 * @param where
	 * @param orderby
	 * @return null or a new object.
	 */
	public <T extends OrmModel> T find(Class<T> klass, String where, String orderBy) {
		List<T> results = findAll(klass, where, orderBy, 0, 1);

		return (results.size() > 0 ? results.get(0) : null);
	}

	/**
	 * Searches for the first record from database table representing the klass parameter objects. Where parameter is added to query if present.
	 *
	 * @param klass
	 * @param where
	 * @return null or a new object.
	 */
	public <T extends OrmModel> T find(Class<T> klass, String where) {
		List<T> results = findAll(klass, where, null, 0, 1);

		return (results.size() > 0 ? results.get(0) : null);
	}

	/**
	 * Saves parameter object to database table corresponding to object class name. Only the fields which are mapped to database columns and of type
	 * below are saved (primitive and any other object types fields are ignored):
	 * <ul>
	 * <li>java.lang.Byte</li>
	 * <li>java.lang.Short</li>
	 * <li>java.lang.Integer</li>
	 * <li>java.lang.Long</li>
	 * <li>java.lang.Double</li>
	 * <li>java.lang.Float</li>
	 * <li>java.lang.Boolean</li>
	 * <li>java.lang.Date</li>
	 * <li>java.lang.String</li>
	 * </ul>
	 *
	 * @param object
	 * @return null or error message
	 */
	public <T extends OrmModel> String save(T object) {
		if (object == null)
			return "Null object can not be saved";

		Class<? extends OrmModel> klass = object.getClass();
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return "Object has no recognized fields to save";

		HashMap<String, Object> oldMap = object.getOldMap();
		FieldProperties fpCreatedAt = fieldMap.get("created_at");
		FieldProperties fpUpdatedAt = fieldMap.get("updated_at");

		HashMap<String, Object> values = new HashMap<>();
		// pull field values from object
		for (FieldProperties fp : fieldMap.values())
			try {
				// id can not be changed once set
				if (fp.fieldName.equals("id") && !object.fieldWas("id", null) && object.fieldChanged("id"))
					values.put("id", object.getOldValue("id"));
				else
					values.put(fp.fieldName, fp.field.get(object));
			} catch (Exception e) {
				values.put(fp.fieldName, null);
				LOGGER.warning(e.getStackTrace().toString());
			}

		PreparedStatement pStUpsert = null;
		ResultSet keys = null;

		Timestamp now = new Timestamp(getNowMillis());
		StringBuilder sqlUpsert = new StringBuilder(250);
		StringBuilder partValues = new StringBuilder(50);
		StringBuilder partOnDup = new StringBuilder(150);
		String upsertResult = null;

		sqlUpsert.append("INSERT INTO " + tableName(klass) + "(");
		partValues.append("");
		partOnDup.append(" ON DUPLICATE KEY UPDATE  ");

		// timestamp values
		if (values.get("id") == null && fpCreatedAt != null) // set created_at for a new record if model has it
			values.put("created_at", now);
		if (fpUpdatedAt != null) // set updated_at if model has it
			values.put("updated_at", now);

		for (String column : fieldMap.keySet())
			if (values.get(column) != null) {
				sqlUpsert.append(column + ",");
				partValues.append("?,");
				if (column.equals("id"))
					partOnDup.append("id=LAST_INSERT_ID(id),");
				else
					partOnDup.append(column + "=VALUES(" + column + "),");
			}

		boolean isBlankObject = partValues.length() == 0 ? true : false;

		// object with all fields null? Save it
		if (isBlankObject)
			for (String column : fieldMap.keySet()) {
				sqlUpsert.append(column + ",");
				partValues.append("?,");
				if (column.equals("id"))
					partOnDup.append("id=LAST_INSERT_ID(id),");
				else
					partOnDup.append(column + "=VALUES(" + column + "),");
			}


		// remove trailing commas
		sqlUpsert.deleteCharAt(sqlUpsert.length() - 1);
		partValues.deleteCharAt(partValues.length() - 1);
		partOnDup.deleteCharAt(partOnDup.length() - 1);

		sqlUpsert.append(") VALUES (" + partValues.toString() + ") " + partOnDup.toString());

		try {
			pStUpsert = connection.prepareStatement(sqlUpsert.toString(), Statement.RETURN_GENERATED_KEYS);

			int idx = 1;
			for (FieldProperties fp : fieldMap.values())
				if (isBlankObject)
					pStUpsert.setObject(idx++, null);
				else if (values.get(fp.fieldName) != null)
					pStUpsert.setObject(idx++, values.get(fp.fieldName));

			LOGGER.info(getQueryForLog(pStUpsert));

			pStUpsert.executeUpdate();

			// set object's id with auto increment id
			keys = pStUpsert.getGeneratedKeys();
			if (keys.next()) {
				FieldProperties idProps = fieldMap.get("id");
				if (idProps != null)
					if (idProps.fieldType.equals("Integer"))
						idProps.field.set(object, keys.getInt("GENERATED_KEY"));
					else if (idProps.fieldType.equals("Long"))
						idProps.field.set(object, keys.getLong("GENERATED_KEY"));
					else if (idProps.fieldType.equals("Short"))
						idProps.field.set(object, keys.getShort("GENERATED_KEY"));
					else
						idProps.field.set(object, keys.getByte("GENERATED_KEY"));
			}
			// set created and updated at fields
			if (values.get("id") == null && fpCreatedAt != null)
				fpCreatedAt.field.set(object, now);
			if (fpUpdatedAt != null) // set updated_at if model has it
				fpUpdatedAt.field.set(object, now);

			object.setOldMap(object.fields());
		} catch (Exception e) {
			object.setOldMap(oldMap);
			upsertResult = "Error saving object " + object.toString();
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(keys, pStUpsert);
		}

		return upsertResult;
	}

	/**
	 * Saves new records to database table corresponding to parameter class. Columns to be populated are determined by array parameter columns.
	 * Records are provided in the form of a {@link #java.lang.String} array by the {@link #java.util.List} parameter rows.
	 *
	 * @param klass
	 * @param columns
	 * @param rows
	 * @return true if all records are saved successfully; false otherwise.
	 */
	public boolean importRows(Class<? extends OrmModel> klass, String[] columns, List<Object[]> rows,
			String[] onDuplicateUpdateColumns) {
		// TODO: switches like :  boolean validate,	boolean useTimestamps
		if (klass == null || columns == null || columns.length == 0 || rows == null || rows.size() == 0)
			return false;

		boolean result = true;

		// construct upsert statement
		List<String> partValues = new ArrayList<>();
		List<String> partUpdate = new ArrayList<>();

		HashSet<String> updateColumns = new HashSet<>();
		if (onDuplicateUpdateColumns != null && onDuplicateUpdateColumns.length > 0)
			for (String column : onDuplicateUpdateColumns)
				updateColumns.add(column);
		else
			for (String col : columns)
				updateColumns.add(col);

		for (String column : columns) {
			partValues.add("?");
			if (updateColumns.contains(column))
				partUpdate.add(column + "=VALUES(" + column + ")");
		}

		String sqlUpsert = "INSERT INTO " + tableName(klass) + "(" + String.join(",", columns) + ") VALUES ("
				+ String.join(",", partValues) + ") ON DUPLICATE KEY UPDATE " + String.join(",", partUpdate);
		PreparedStatement pStUpsert = null;

		try {
			pStUpsert = connection.prepareStatement(sqlUpsert);

			// construct statements and add to batch
			for (Object[] row : rows) {
				if (row.length != columns.length) {
					LOGGER.warning("Column/value count mismatch in importRows. Record not saved.");
					continue;
				}

				for (int i = 0, end = columns.length; i < end; i++)
					pStUpsert.setObject(i + 1, row[i]);
				pStUpsert.addBatch();

				LOGGER.info(getQueryForLog(pStUpsert));
			}

			// execute batch
			pStUpsert.executeBatch();
		} catch (Exception e) {
			result = false;
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(pStUpsert);
		}

		return result;
	}

	/**
	 * Returns parameter object's non-null field/value pairs as a {@link #java.util.HashMap}. Only the fields given in the fieldsArray parameter are
	 * returned unless they are null.
	 *
	 * @param object
	 * @param fieldsArray
	 * @return Hashmap, empty or populated with non-null field/value pairs.
	 */
	public <T extends OrmModel> HashMap<String, Object> fieldsFor(T object, String... fieldsArray) {
		return fieldsFor(object, (fieldsArray == null ? null : Arrays.asList(fieldsArray)));
	}

	/**
	 * Returns parameter object's non-null field/value pairs as a {@link #java.util.HashMap}. Only the fields given in the fieldList parameter are
	 * returned unless they are null.
	 *
	 * @param object
	 * @param fieldsList
	 * @return Hashmap, empty or populated with non-null field/value pairs.
	 */
	public <T extends OrmModel> HashMap<String, Object> fieldsFor(T object, List<String> fieldsList) {
		HashMap<String, Object> fields = new LinkedHashMap<>(); // preserve field order with LinkedHashMap

		if (object == null || fieldsList == null || fieldsList.size() == 0)
			return fields;

		HashMap<String, FieldProperties> fieldMap = getClassFields(object.getClass());

		// no recognized type
		if (fieldMap.size() == 0)
			return fields;

		FieldProperties fp = null;

		// pull field values from object
		for (String field : fieldsList)
			if ((fp = fieldMap.get(field)) != null)
				try {
					Object value = fp.field.get(object);
					if (value != null)
						fields.put(field, fp.field.get(object));
				} catch (Exception e) {
					LOGGER.warning(e.getStackTrace().toString());
				}

		return fields;
	}

	/**
	 * Returns parameter object's non-null field/value pairs a {@link #java.util.HashMap}. Fields given in the excludedFields parameter are ignored.
	 *
	 * @param object
	 * @param excludedFieldsArray
	 * @return Hashmap, empty or populated with non-null field/value pairs excluding given fields.
	 */
	public <T extends OrmModel> HashMap<String, Object> fields(T object, String... excludedFieldsArray) {
		return fields(object, (excludedFieldsArray == null ? null : Arrays.asList(excludedFieldsArray)));
	}

	/**
	 * Returns parameter object's non-null field/value pairs as a {@link #java.util.HashMap}. Fields given in the excludedFields parameter are
	 * ignored.
	 *
	 * @param object
	 * @param excludedFieldsList
	 * @return Hashmap, empty or populated with non-null field/value pairs excluding given fields.
	 */
	public <T extends OrmModel> HashMap<String, Object> fields(T object, List<String> excludedFieldsList) {
		HashMap<String, Object> fields = new HashMap<>();

		if (object == null)
			return fields;

		HashMap<String, FieldProperties> fieldMap = getClassFields(object.getClass());

		// no recognized type
		if (fieldMap.size() == 0)
			return fields;

		HashSet<String> excludes = excludedFieldsList == null ? new HashSet<>() : new HashSet<>(excludedFieldsList);

		// pull field values from object
		fieldMap.forEach((field, fp) -> {
			if (field != null && !excludes.contains(field))
				try {
					Object value = fp.field.get(object);
					if (value != null)
						fields.put(field, fp.field.get(object));
				} catch (Exception e) {
					LOGGER.warning(e.getStackTrace().toString());
				}
		});

		return fields;
	}

	/**
	 * Returns parameter object's non-null field/value pairs as a {@link #java.util.HashMap}. Fields given in the excludedFields parameter are
	 * ignored.
	 *
	 * @param object
	 * @param excludedFields
	 * @return Hashmap, empty or populated with non-null field/value pairs excluding given fields.
	 */
	public <T extends OrmModel> HashMap<String, Object> fields(T object) {
		return fields(object, (String[]) null);
	}

	/**
	 * Updates a database table record corresponding to parameter class identified by id parameter. Column is updated to the given parameter value
	 * (including null). Id and (if exists) created_at columns are not updated.
	 *
	 * @param object
	 * @param column
	 * @param value
	 * @return true for successful update; false otherwise
	 */
	public <T extends OrmModel> boolean updateColumn(Class<? extends OrmModel> klass, Integer id, String column,
			Object value) {
		if (klass == null || id == null || column == null || column.isEmpty() || column.equalsIgnoreCase("id")
				|| column.equalsIgnoreCase("created_at"))
			return false;

		PreparedStatement pStUpdate = null;
		try {
			// construct update statement
			String sqlUpdate = "UPDATE " + tableName(klass) + " SET " + column + " = ? WHERE id = ?";
			pStUpdate = connection.prepareStatement(sqlUpdate);
			pStUpdate.setObject(1, value);
			pStUpdate.setObject(2, id);

			LOGGER.info(getQueryForLog(pStUpdate));

			if (pStUpdate.executeUpdate() == 1)
				return true;
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(pStUpdate);
		}

		return false;
	}

	/**
	 * Updates a database record corresponding to parameter object. Column is updated to the given value (including null). Object's id field must not
	 * be null. Id and (if exists) created_at columns can not be updated.
	 *
	 * @param object
	 * @param column
	 * @param value
	 * @return true for successful update; false otherwise
	 */
	public <T extends OrmModel> boolean updateColumn(T object, String column, Object value) {
		if (object == null || column == null)
			return false;

		Class<? extends OrmModel> klass = object.getClass();
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);
		if (fieldMap.size() == 0)
			return false;

		FieldProperties fpColumn = fieldMap.get(column);
		if (fpColumn == null)
			return false;

		boolean result = updateColumn(klass, object.getId(), column, value);
		if (result)
			try {
				fpColumn.field.set(object, null);

				if (fpColumn.fieldType.equals("Date") && value != null && value instanceof String)
					fpColumn.field.set(object, DATE_FORMAT.parse((String) value));
				else
					fpColumn.field.set(object, value);
			} catch (Exception e) {
				LOGGER.severe(e.getStackTrace().toString());
			}

		return result;
	}

	/**
	 * Returns string representation of the object. Only fields mapped to database columns are included.
	 *
	 * @param object
	 * @return String comprised of class name followed by field/value pairs in square brackets. Values of fields with the word password are masked.
	 */
	public <T extends OrmModel> String toString(T object) {
		Class<? extends OrmModel> klass = object.getClass();
		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);

		// no recognized type
		if (fieldMap.size() == 0)
			return klass.getSimpleName() + "[]";

		StringBuilder asString = new StringBuilder(fieldMap.size() * 30); // 25 chars per field name and value initially
		asString.append(klass.getSimpleName() + " [");

		String fieldName = null;
		Object fieldValue = null;
		// pull field values from object
		for (FieldProperties fp : fieldMap.values())
			try {
				fieldName = fp.field.getName();
				fieldValue = fieldName.toLowerCase().indexOf("password") > -1 ? "*****" : fp.field.get(object);

				asString.append(fieldName + '=' + fieldValue + ", ");
			} catch (Exception e) {
				LOGGER.warning(e.getStackTrace().toString());
			}

		return asString.substring(0, asString.length() - 2) + ']';
	}

	/**
	 * Assigns parameter object's fields to corresponding {@link java.sql.ResultSet} fields. Fields which are not in the resultset are not updated.
	 *
	 * @param object
	 * @param resultSet
	 */
	public <T extends OrmModel> boolean loadFrom(T object, ResultSet resultSet) {
		if (object == null || resultSet == null || object.isFrozen())
			return false;

		try {
			assignResultSetToListOrObject(resultSet, null, null, object);
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
			return false;
		}

		return true;
	}

	/**
	 * Assigns parameter object's fields to corresponding {@link #java.util.HashMap} fields. Fields which are not in the hashmap are not updated.
	 *
	 * @param object
	 * @param fieldValueMap
	 */
	public <T extends OrmModel> boolean loadFrom(T object, HashMap<String, Object> fieldValueMap) {
		if (object == null || fieldValueMap == null || object.isFrozen())
			return false;

		int fieldCount = 0;
		HashMap<String, FieldProperties> fieldMap = getClassFields(object.getClass());

		for (String fieldName : fieldValueMap.keySet())
			if (fieldName == null || (fieldName = fieldName.toLowerCase()).isEmpty())
				continue;
			else if (fieldMap.containsKey(fieldName))
				try {
					FieldProperties fp = fieldMap.get(fieldName);
					Object value = fieldValueMap.get(fieldName);
					fp.field.set(object, null);
					fieldCount++;

					if (value != null) {
						String stringValue = value.toString();
						String trimmedValue = stringValue.trim();

						if (fp.fieldType.equals("Integer"))
							try {
								fp.field.set(object, new Integer(trimmedValue));
							} catch (Exception e1) {
								fp.field.set(object, trimmedValue.equalsIgnoreCase("true") ? 1 : 0);
							}
						else if (fp.fieldType.equals("String"))
							fp.field.set(object, stringValue);
						else if (fp.fieldType.equals("Date") && (value instanceof String))
							fp.field.set(object, DATE_FORMAT.parse(trimmedValue));
						else if (fp.fieldType.equals("Boolean"))
							try {
								fp.field.set(object, Boolean.parseBoolean(trimmedValue));
							} catch (Exception e1) {
								fp.field.set(object, trimmedValue.equalsIgnoreCase("true"));
							}
						else
							fp.field.set(object, value);
					}
				} catch (Exception e) {
					LOGGER.severe(e.getStackTrace().toString());
				}

		return fieldCount > 0;
	}

	/**
	 * Checks whether parameter object's field was changed since it was created or loaded from database with find(...) functions.
	 *
	 * @param object
	 * @param fieldName
	 * @return true if field value changed; false otherwise.
	 */
	public <T extends OrmModel> boolean fieldChanged(T object, String fieldName) {
		if (object == null || fieldName == null || fieldName.isEmpty())
			return false;

		try {
			FieldProperties fp = getClassFields(object.getClass()).get(fieldName);
			HashMap<String, Object> map = object.getOldMap();

			Object value = fp == null ? null : fp.field.get(object);
			Object oldValue = map == null ? null : map.get(fieldName);

			return value == null ? (oldValue != null ? true : false) : !value.equals(oldValue);
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		}

		return false;
	}

	/**
	 * Deletes a record from database matching parameter object's id value.
	 *
	 * @param object
	 * @return true when object is deleted successfully; false otherwise
	 */
	public <T extends OrmModel> boolean delete(T object) {
		if (object == null || object.getId() == null)
			return false;

		int countDeleted = 0;
		Class<? extends OrmModel> klass = object.getClass();

		String where = " WHERE id=" + object.getId();
		String sqlDelete = "DELETE FROM " + tableName(klass) + where;
		PreparedStatement pStDelete = null;

		try {
			pStDelete = prepareStatementFromQuery(klass, sqlDelete, where);
			pStDelete.executeUpdate();
			countDeleted = pStDelete.getUpdateCount();
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(pStDelete);
		}

		return countDeleted == 1 ? true : false;
	}

	/**
	 * Deletes multiple records from database table corresponding to klass parameter such that; column given as column parameter matches a value
	 * given in values array parameter.
	 *
	 * @param klass
	 * @param column
	 * @param values
	 * @return true when matching records are deleted successfully; false otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> boolean deleteAll(Class<? extends OrmModel> klass, String column, T... values) {

		if (values == null || values.length == 0)
			return false;

		return deleteAll(klass, column, Arrays.asList(values));
	}

	/**
	 * Deletes multiple records from database table corresponding to klass parameter such that; column given as column parameter matches a value
	 * given in values list parameter.
	 *
	 * @param klass
	 * @param column
	 * @param values
	 * @return true when matching records are deleted successfully; false otherwise
	 */
	public <T extends Object> boolean deleteAll(Class<? extends OrmModel> klass, String column, List<T> values) {
		if (klass == null || column == null || (column = column.trim()).isEmpty() || values == null || values.isEmpty())
			return false;

		int countDeleted = 0;
		PreparedStatement pStDelete = null;

		StringBuilder partValues = new StringBuilder(values.size() * 2);
		for (int i = 0, end = values.size(); i < end; i++)
			partValues.append("?,");

		String sqlDelete = "DELETE FROM " + tableName(klass) + " WHERE " + column.replaceAll("[^a-zA-Z0-9_]", "")
				+ " IN (" + removeEndChar(partValues, ',') + ")";

		try {
			pStDelete = connection.prepareStatement(sqlDelete);

			for (int i = 0, end = values.size(); i < end; i++)
				try {
					pStDelete.setObject(i + 1, values.get(i));
				} catch (Exception e) {
					pStDelete.setObject(i + 1, null);
					LOGGER.severe(e.getStackTrace().toString());
				}

			LOGGER.info(getQueryForLog(pStDelete));

			pStDelete.executeUpdate();
			countDeleted = pStDelete.getUpdateCount();
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(pStDelete);
		}

		return countDeleted > 0 ? true : false;
	}

	/**
	 * Updates multiple records from database table corresponding to klass parameter. Table column given by column is updated to value given by
	 * value. Records matching ids passed as idList are updated. Value can not be an expression.
	 *
	 * @param klass
	 * @param column
	 * @param value
	 * @param idList
	 * @return number of updated records
	 */
	public int updateAll(Class<? extends OrmModel> klass, String column, Object value, List<Integer> idList) {
		return updateAll(klass, new String[] { column }, new Object[] { value }, idList, false);
	}

	/**
	 * Updates multiple records from database table corresponding to klass parameter. Table column given by column is updated to value given by
	 * value. Records matching ids passed as idList are updated. Value can be expressions like <b>total=total+1 or created_at=DATE('Y-m-d')</b>.
	 * given in values list parameter.
	 *
	 * @param klass
	 * @param column
	 * @param value
	 * @param idList
	 * @return number of updated records
	 */
	public int updateAllRaw(Class<? extends OrmModel> klass, String column, Object value, List<Integer> idList) {
		return updateAll(klass, new String[] { column }, new Object[] { value }, idList, true);
	}

	/**
	 * Updates multiple records from database table corresponding to klass parameter. Table columns given in columns array is updated to the
	 * corresponding value in values array. Records matching ids passed as idList are updated. Value can be expressions like <b>total=total+1 or
	 * created_at=DATE('Y-m-d')</b>.
	 * given in values list parameter.
	 *
	 * @param klass
	 * @param columns
	 * @param values
	 * @param idList
	 * @return number of updated records
	 */
	public int updateAll(Class<? extends OrmModel> klass, String[] columns, Object[] values, List<Integer> idList) {
		return updateAll(klass, columns, values, idList, false);
	}

	/**
	 * Returns true if connection's SQL mode is ANSI_QUOTES, false otherwise.
	 */
	public boolean isAnsiSql() {
		return ansiSql;
	}

	/**
	 * Escapes special characters in the value parameter taking isAnsiSql parameter into consideration for MySQL's handling of single quotes.
	 *
	 * @param value
	 * @param isAnsiSql
	 *
	 * @return escaped value
	 */
	public static String escape(String value, boolean isAnsiSql) {
		if (value == null)
			return null;

		// @formatter:off
		value=value.trim()
				.replace("\\", "\\\\")
				.replace(";", "&#59;")
				.replace("%", "&#37;")
				.replace("\"", "&quot;");

		if (value.indexOf("'") == 0 && value.length() > 1) // looks like a quoted string value
			value = "'" + value.substring(1, value.length() - 1).replace("'", (isAnsiSql ? "''" : "\\'")) + "'";
		// @formatter:on

		return value;
	}

	private int updateAll(Class<? extends OrmModel> klass, String[] columns, Object[] values,
			Collection<Integer> idList, boolean rawAssignment) {
		if (klass == null || columns == null || columns.length == 0 || values == null || values.length == 0
				|| columns.length != values.length || idList == null || idList.isEmpty())
			return 0;

		HashMap<String, FieldProperties> fieldMap = getClassFields(klass);
		List<String> updates = new ArrayList<>();
		PreparedStatement pStUpdate = null;
		int countUpdated = 0;
		String column = null;
		Object value = null;

		for (int i = 0, end = columns.length; i < end; i++) {
			column = columns[i];
			if (column == null || (column = column.toLowerCase()).isEmpty() || column.equals("id")
					|| column.equals("created_at"))
				continue;

			value = column.equals("updated_at") ? getNowMillis() : values[i];

			if (fieldMap.containsKey(column))
				if (rawAssignment)
					if (value == null)
						updates.add(column + "=null");
					else
						updates.add(column + "=" + escape(value.toString(), ansiSql));
				else
					updates.add(column + "=?");
		}

		List<String> ids = new ArrayList<>();
		for (Integer id : idList)
			ids.add(id.toString());

		String sqlUpdate = "UPDATE " + tableName(klass) + " SET " + String.join(",", updates) + " WHERE id IN ("
				+ String.join(",", ids) + ")";

		try {
			pStUpdate = connection.prepareStatement(sqlUpdate);

			if (!rawAssignment)
				for (int i = 0, end = values.length; i < end; i++)
					pStUpdate.setObject(i + 1, values[i]);

			LOGGER.info(getQueryForLog(pStUpdate));

			pStUpdate.executeUpdate();
			countUpdated = pStUpdate.getUpdateCount();
		} catch (Exception e) {
			LOGGER.severe(e.getStackTrace().toString());
		} finally {
			closeSqlObjects(pStUpdate);
		}

		return countUpdated;
	}
}
