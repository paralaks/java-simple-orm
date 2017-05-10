package paralaks_gmail_com.simpleorm.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import paralaks_gmail_com.simpleorm.helper.DbHelper;
import paralaks_gmail_com.simpleorm.helper.LogHelper;

// Per http://docs.oracle.com/javase/tutorial/information/glossary.html field is used in method names

public abstract class OrmModel {
	protected Integer id;

	protected HashMap<String, Object> oldMap = null; // initialized only when object is loaded with find() functions to save memory
	protected HashMap<String, String> errorMessages = null; // initialized when needed

	private static final Logger LOGGER = LogHelper.getLogSingleLineOutput(OrmModel.class.getName());

	private static String KEY_SAVE_ERROR = "__save_error__";
	private static String KEY_AFTER_SAVE_ERROR = "__after_save_error__";

	protected boolean frozen = false;
	protected boolean skipValidation = false;

	protected DbHelper db;

	/**
	 * Sets internal {@link DbHelper} object which handles database related operations to the parameter object during object construction.
	 */
	public OrmModel(DbHelper dbObject) {
		db = dbObject;
	}

	/**
	 * Sets internal {@link DbHelper} object which handles database related operations to the parameter object.
	 */
	public void setDb(DbHelper dbObject) {
		db = dbObject;
	}

	/**
	 * Returns internal {@link DbHelper} object.
	 */
	public DbHelper getDb() {
		return db;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Called by {@link #vpn.sel.mw.helper.DbHelper}'s find(...) functions to initialize and populate {@link #oldMap} which is used used by
	 * {@link #fieldChanged(String)} function.
	 *
	 * @param map
	 */
	public final void setOldMap(HashMap<String, Object> map) {
		oldMap = map;
	}

	/**
	 * Returns {@link #oldMap} to be used by {@link #fieldChanged(String)} function.
	 *
	 * @return copy of oldMap which is populated when model is loaded by {@link #vpn.sel.mw.helper.DbHelper}'s find(...) functions.
	 */
	public final HashMap<String, Object> getOldMap() {
		// return a copy to prevent updates
		return oldMap == null ? null : new HashMap<String, Object>(oldMap);
	}

	/**
	 * Returns the value of fieldName parameter field which was set when object was created or loaded from database with find(...) functions.
	 *
	 * @return value of the field set when object was created or loaded from database with find(...) functions.
	 */
	public final Object getOldValue(String fieldName) {
		return oldMap == null ? null : oldMap.get(fieldName);
	}

	/**
	 * Returns database table name corresponding to object's class.
	 *
	 * @return Empty string or table name.
	 */
	public final String tableName() {
		return DbHelper.tableName(this.getClass());
	}

	/**
	 * Returns number of records using object's non-null fields to construct the where clause. For instance, if the object has
	 * fields as: firstName=John and lastName=Doe
	 * count query will have: first_name = "John" and last_name="Doe" as where condition.
	 *
	 * @return 0 or actual count from database table representing the object class.
	 */
	public int count() {
		return db.count(this);
	}

	/**
	 * Returns number of records from database. Parameter where is added to query if not-null.
	 *
	 * @param klass
	 * @param where
	 * @return 0 or number of records.
	 */
	public int count(String where) {
		return db.count(this.getClass(), where);
	}

	/**
	 * Searches for a database record matching object's non-null fields. If all the fields are null, returns null.
	 *
	 * @return null or a new object matching object's non-null fields.
	 */
	@SuppressWarnings("unchecked")
	public <T extends OrmModel> T find() {
		return db.find((T) this);
	}

	/**
	 * Searches for a single object from database. Parameter where is used as search criteria if not-null.
	 *
	 * @param where
	 * @return null or a new object
	 */
	@SuppressWarnings("unchecked")
	public <T extends OrmModel> T find(String where) {
		return db.find((Class<T>) this.getClass(), where, null);
	}

	/**
	 * Searches for a single object from database. Parameter where is used as search criteria if not-null. Search order direction can be set using
	 * parameter orderBy.
	 *
	 * @param where
	 * @param orderby
	 * @return An object or null
	 */
	@SuppressWarnings("unchecked")
	public <T extends OrmModel> T find(String where, String orderBy) {
		return db.find((Class<T>) this.getClass(), where, orderBy);
	}

	/**
	 * Searches database using object's non-null fields as search criteria.
	 *
	 * @return An empty list or a list of maximum {@link #MAX_SELECT_LIMIT} objects.
	 */
	@SuppressWarnings("unchecked")
	public <T extends OrmModel> List<T> findAll() {
		return (List<T>) db.findAll(this);
	}

	/**
	 * Searches database for records. Parameter where is used as search criteria if not-null.
	 *
	 * @param where
	 * @return An empty list or a list of maximum {@link #MAX_SELECT_LIMIT} objects.
	 */
	@SuppressWarnings("unchecked")
	public <T extends OrmModel> List<T> findAll(String where) {
		return (List<T>) db.findAll(this.getClass(), where, null, null, null);
	}

	/**
	 * Searches database for records. Parameter where is used as search criteria if not-null. Results can be ordered using parameter orderBy. Number
	 * of results can be limited by limitOffset and limitCount parameters.
	 *
	 * @param where
	 * @param orderBy
	 * @param limitOffset
	 * @param limitCount
	 * @return An empty list or a list of maximum limitCount or {@link #MAX_SELECT_LIMIT} objects.
	 */
	@SuppressWarnings("unchecked")
	public <T extends OrmModel> List<T> findAll(String where, String orderBy, Integer limitOffset, Integer limitCount) {
		return (List<T>) db.findAll(this.getClass(), where, orderBy, limitOffset, limitCount);
	}

	/**
	 * Callback executed by {@link #valid()} and {@link #save()} functions.
	 */
	public void beforeValidate() {}

	/**
	 * Callback executed by {@link #valid()} and {@link #save()} functions.
	 */
	public void validate() {}

	/**
	 * Callback executed by {@link #save()} function.
	 */
	public void beforeSave() {}

	/**
	 * Callback executed by {@link #save()} function while creating a new record in database (id equals null).
	 */
	public void onCreate() {}

	/**
	 * Callback executed by {@link #save()} function while updating a record in database (id is not null).
	 */
	public void onUpdate() {}

	/**
	 * Callback executed by {@link #save()} function after a successful upsert.
	 */
	public void afterSave() {}

	private void initErrorMessages() {
		if (errorMessages == null)
			errorMessages = new HashMap<>();
		else
			errorMessages.clear();
	}

	/**
	 * Executes {@link #beforeValidate()} and {@link #validate()} callbacks. If any of them returns an error message, returns false.
	 *
	 * @return false if validations fail; true otherwise.
	 */
	public final boolean valid() {
		initErrorMessages();
		beforeValidate();
		validate();

		return errorMessages.size() == 0 ? true : false;
	}

	/**
	 * Upserts the object to database. Executes {@link #beforeValidate()}, {@link #validate()}, {@link #beforeSave()},
	 * {@link #onCreate()}/{@link #onUpdate()} callbacks before upserting the object. If any of the callbacks returns an error message, aborts further
	 * execution and returns the error message. Executes afterSave() callback when upsert is a success.
	 *
	 * @return true on success; false otherwise.
	 */
	public final boolean save() {
		if (frozen)
			return false;

		LOGGER.info((id == null ? "Inserting" : "Updating") + ": " + toString());

		if (skipValidation)
			initErrorMessages();
		else if (!valid()) {
			LOGGER.info("Validation failed for " + this.getClass().getSimpleName() + " : " + getErrors());
			return false;
		}

		if (errorMessages.size() == 0)
			beforeSave();

		if (errorMessages.size() == 0)
			if (id == null)
				onCreate();
			else
				onUpdate();

		String saveResult = null;
		if (errorMessages.size() == 0)
			saveResult = db.save(this);

		if (saveResult != null) {
			errorMessages.put(KEY_SAVE_ERROR, saveResult);
			LOGGER.info("Upsert failed: " + saveResult);
		}

		if (errorMessages.size() == 0) {
			afterSave();
			if (errorMessages.size() != 0)
				LOGGER.info("After save failed: " + getAfterSaveError());
		}

		if (errorMessages.size() == 0)
			return true;
		else {
			LOGGER.info("Upsert failed: " + getErrors());
			return false;
		}
	}

	/**
	 * Saves new records to database table representing object's class. Columns to be populated are determined by parameter columns. Records are
	 * provided in the form of an {@link #java.lang.Object} array by the {@link #java.util.List} parameter rows. Columns which need to be updated for
	 * duplicate records can be passed as the {@link #java.lang.String} array parameter onDuplicateUpdateColumns.
	 *
	 * @param columns
	 * @param rows
	 * @param onDuplicateUpdateColumns
	 * @return true if all records are saved successfully; false otherwise.
	 */
	public boolean importRows(String[] columns, List<Object[]> rows, String[] onDuplicateUpdateColumns) {
		return db.importRows(this.getClass(), columns, rows, onDuplicateUpdateColumns);
	}

	/**
	 * Returns object's non-null field/value pairs as a {@link #java.util.HashMap} for the fields given in the parameter fieldList.
	 *
	 * @param fieldList
	 * @return HashMap, empty or populated with non-null field/value pairs.
	 */
	public HashMap<String, Object> fieldsFor(String... fieldList) {
		return db.fieldsFor(this, fieldList);
	}

	/**
	 * Returns object's non-null field/value pairs as a {@link #java.util.HashMap} for the fields given in the parameter fieldList.
	 *
	 * @param fieldList
	 * @return HashMap, empty or populated with non-null field/value pairs.
	 */
	public HashMap<String, Object> fieldsFor(List<String> fieldList) {
		return db.fieldsFor(this, fieldList);
	}

	/**
	 * Returns object's non-null field/value pairs as a {@link #java.util.HashMap}.
	 *
	 * @return HashMap, empty or populated with non-null field/value pairs.
	 */
	public HashMap<String, Object> fields() {
		return db.fields(this, (String[]) null);
	}

	/**
	 * Returns object's non-null field/value pairs as a {@link #java.util.HashMap}. Fields given in the parameter excludeList are not included.
	 *
	 * @param excludeList
	 * @return HashMap, empty or populated with non-null field/value pairs.
	 */
	public HashMap<String, Object> fieldsExcept(List<String> excludeList) {
		return db.fields(this, excludeList);
	}

	/**
	 * Returns object's non-null field/value pairs as a {@link #java.util.HashMap}. Fields given in the parameter excludeList are not included.
	 *
	 * @return HashMap, empty or populated with non-null field/value pairs.
	 */
	public HashMap<String, Object> fieldsExcept(String... excludeList) {
		return db.fields(this, excludeList);
	}

	/**
	 * Updates a database record corresponding to the object based on matching id. Column is set to the given value (including null). Object must have
	 * a non-null id field. Id, created_at, updated_at columns can not be updated. Callbacks and validations are skipped.
	 *
	 * @param column
	 * @param value
	 * @return true for successful update; false otherwise
	 */
	public boolean updateColumn(String column, Object value) {
		if (frozen)
			return false;
		return db.updateColumn(this, column, value);
	}

	/**
	 * Returns a string representation of the object. Only fields mapped to database columns are included unless overridden.
	 *
	 * @return String comprised of class name followed by field/value pairs in square brackets.
	 */
	@Override
	public String toString() {
		return db.toString(this);
	}

	/**
	 * Assigns fields to columns of parameter resultset columns. Fields which are not in the resultset are not updated.
	 *
	 * @param resultSet
	 * @return true for successful assignment; false in case of an exception.
	 */
	public boolean loadFrom(ResultSet resultSet) {
		if (frozen)
			return false;
		return db.loadFrom(this, resultSet);
	}

	/**
	 * Assigns fields using key/value pairs of parameter fieldValueMap.
	 *
	 * @param object
	 * @return true for successful assignment; false in case of an exception.
	 */
	public boolean loadFrom(HashMap<String, Object> fieldValueMap) {
		return db.loadFrom(this, fieldValueMap);
	}

	/**
	 * Assigns fields using key/value pairs of parameter fieldValueMap.
	 *
	 * @param object
	 * @return true for successful assignment; false in case of an exception.
	 */
	public boolean assignFields(HashMap<String, Object> fieldValueMap) {
		// TODO :permission assignment for user; errors to base: addError("base", errors)
		return db.loadFrom(this, fieldValueMap);
	}

	/**
	 * Assigns fields to parameter object's non-null fields including id field.
	 *
	 * @param object
	 * @return true for successful assignment; false in case of an exception.
	 */
	public <T extends OrmModel> boolean assignFields(T object) {
		// TODO :permission assignment for user; errors to base: addError("base", errors)
		return db.loadFrom(this, object.fields());
	}

	/**
	 * Checks whether field was changed since object was created or loaded from database with find(...) functions.
	 *
	 * @param fieldName
	 * @return true if field value was changed; false otherwise.
	 */
	public boolean fieldChanged(String fieldName) {
		return db.fieldChanged(this, fieldName);
	}

	/**
	 * Checks whether field value is equal to the parameter value since object was created or loaded from database with find(...) functions.
	 *
	 * @param fieldName
	 * @param value
	 * @return true if field value was equal to value parameter; false otherwise.
	 */
	public boolean fieldWas(String fieldName, Object value) {
		if (fieldName == null || fieldName.isEmpty())
			return false;

		if (oldMap == null)
			return value == null;

		Object oldValue = oldMap.get(fieldName);

		return oldValue == null ? (value == null ? true : false) : oldValue.equals(value);
	}

	/**
	 * Adds the error message for fieldName field to the error messages hash map.
	 *
	 * @param fieldName
	 * @param error
	 */
	public final void addError(String fieldName, String error) {
		if (fieldName == null || error == null)
			return;

		if (errorMessages == null)
			errorMessages = new HashMap<>();

		// add only if was not added before
		if (errorMessages.containsKey(fieldName)) {
			String existing = errorMessages.get(fieldName);

			if (existing.indexOf(error) < 0)
				errorMessages.put(fieldName, existing + " " + error);
		} else
			errorMessages.put(fieldName, error);
	}

	/**
	 * Adds error(s) occurred during {@link #afterSave()} callback execution to error messages hash map.
	 *
	 * @param error
	 */
	public final void addAfterSaveError(String error) {
		addError(KEY_AFTER_SAVE_ERROR, error);
	}

	/**
	 * Returns error message(s) generated during {@link #afterSave()} callback execution.
	 *
	 * @return null or after save error(s)
	 */
	public final String getAfterSaveError() {
		return getError(KEY_AFTER_SAVE_ERROR);
	}

	/**
	 * Returns true if error messages hash map contains the error; false otherwise.
	 *
	 * @param error
	 * @return true if error messages hash map includes error; false otherwise.
	 */
	public final boolean hasError(String error) {
		if (error == null || errorMessages == null || error.isEmpty() || errorMessages.size() == 0)
			return false;

		return errorMessages.containsValue(error) || getErrors().indexOf(error) > -1;
	}

	/**
	 * Returns error message for the fieldName field if exists; null otherwise
	 *
	 * @param fieldName
	 * @return null or error message for the fieldName field
	 */
	public final String getError(String fieldName) {
		if (fieldName == null || errorMessages == null)
			return null;

		return errorMessages.get(fieldName);
	}

	/**
	 * Concatenates errors with <strong>\n</strong> and returns result if error messages hash map is not empty.
	 *
	 * @return null or all error messages concatenating with \n
	 */
	public String getErrors() {
		if (errorMessages == null || errorMessages.size() == 0)
			return null;

		StringBuilder errors = new StringBuilder();

		for (String error : errorMessages.values())
			errors.append(error + "\n");

		return errors.toString().substring(0, errors.length() - 1);
	}

	/**
	 * @return null or error messages hash map if it was initialized
	 */
	public final HashMap<String, String> getAllErrors() {
		return errorMessages;
	}

	/**
	 * Freezes/unfreezes object. If object object is frozen {@link #save()}/{@link #delete()}/{@link #destroy()} functions can not be executed.
	 * Ideally should only be used for testing purposes.
	 */
	public final void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/**
	 * @return true if object is frozen (after successful {@link #delete()} or {@link #destroy()} calls); false otherwise.
	 */
	public final boolean isFrozen() {
		return frozen;
	}

	/**
	 * Deletes a record from database corresponding to the object matching object's id. Marks object as frozen if delete is a success.
	 *
	 * @return true when delete succeeds; false otherwise or or if object is already frozen.
	 */
	public final boolean delete() {
		if (frozen)
			return false;

		LOGGER.info("Deleting: " + toString());

		if (db.delete(this))
			return (frozen = true);

		return false;
	}

	/**
	 * Callback executed by {@link #destroy()}function. If it returns false, {@link #destroy()} call will be aborted.
	 *
	 * @return true or false.
	 */
	public boolean beforeDestroy() {
		return true;
	}

	/**
	 * Callback executed by {@link #destroy()} function after a successful {@link #beforeDestroy()} and {@link #delete()} calls.
	 */
	public void afterDestroy() {}

	/**
	 * Deletes object from database by executing beforeDestroy() callback, delete() function and afterDestroy() callback consecutively. If
	 * beforeDestroy() fails, aborts deleting object and afterDestroy() callback execution.
	 *
	 * @return true when object is destroy successfully; false otherwise or if object is already frozen.
	 */
	public final boolean destroy() {
		if (frozen)
			return false;

		LOGGER.info("Destroying: " + toString());

		if (beforeDestroy() && delete()) {
			afterDestroy();
			return (frozen = true);
		}

		return false;
	}

	/**
	 * Callback executed after a record is loaded from database by either {@link #find()} or {@link #reload()} functions
	 */
	public void afterFindAndLoad() {}

	/**
	 * Reloads the object from database unless it is frozen and id field is not null, initializes oldMap, executes afterFindAndLoad callback and
	 * returns true if all the steps succeed.
	 *
	 * @return true if object if reloaded and {@link #afterFindAndLoad()} callback executed successfully; false otherwise.
	 */
	public <T extends OrmModel> boolean reload() {
		return db.reload(this);
	}

	/**
	 * Assigns value parameter to objects' field given by field parameter, disables validation and saves object. Callbacks are executed.
	 *
	 * @param field
	 * @param value
	 * @return true for successful update; false otherwise
	 */
	public boolean updateField(String field, Object value) {
		if (id == null || field == null || value == null || field.equalsIgnoreCase("id")
				|| field.equalsIgnoreCase("created_at") || field.equalsIgnoreCase("updated_at") || frozen
				|| db.find(this.getClass(), "id=" + id) == null)
			return false;

		boolean loadResult = db.loadFrom(this, DbHelper.initHashMap(field, value));
		boolean saveResult = false;

		if (loadResult) {
			skipValidation = true;
			saveResult = save();
			skipValidation = false;
		}

		return saveResult;
	}

	/**
	 * Assigns fields using key/value pairs of parameter fieldValueMap, disables validation and saves object. Callbacks are executed.
	 *
	 * @param fieldValueMap
	 * @return true for successful update; false otherwise
	 */
	public boolean updateFields(HashMap<String, Object> fieldValueMap) {
		if (id == null || fieldValueMap == null || fieldValueMap.isEmpty() || frozen
				|| db.find(this.getClass(), "id=" + id) == null)
			return false;

		// exclude id, created_at, updated_at
		fieldValueMap.remove("id");
		fieldValueMap.remove("created_at");
		fieldValueMap.remove("updated_at");

		boolean loadResult = db.loadFrom(this, fieldValueMap);
		boolean saveResult = false;

		if (loadResult) {
			skipValidation = true;
			saveResult = save();
			skipValidation = false;
		}

		return saveResult;
	}
}

