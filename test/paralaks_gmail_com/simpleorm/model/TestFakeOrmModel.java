package paralaks_gmail_com.simpleorm.model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import static paralaks_gmail_com.simpleorm.helper.DbHelper.initHashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper;


public class TestFakeOrmModel extends FakeOrmModelTestHelper {
  @Test
  public void testFieldsAreNullAfter_initAllNull() throws SQLException {
    FakeOrmModel model = new FakeOrmModel(db);
    assertTrue(model.getOldMap().isEmpty());

    model.initAllNull();
    assertNull(model.getId());
    assertNull(model.getStatus());
    assertNull(model.getTotal());
    assertNull(model.getContent());
    assertNull(model.getCreatedAt());
    assertNull(model.getUpdatedAt());

    assertFalse(model.isFrozen());
    assertTrue(model.getOldMap().isEmpty());
    assertNull(model.getErrorsMap());
  }

  @Test
  public void testFieldsAreEmptyAfter_initAllEmpty() throws SQLException {
    FakeOrmModel model = new FakeOrmModel(db);
    assertTrue(model.getOldMap().isEmpty());

    model.initAllEmpty();
    assertNull(model.getId());
    assertEquals(STATUS_EMPTY, model.getStatus());
    assertEquals(TOTAL_EMPTY, model.getTotal());
    assertEquals(CONTENT_EMPTY, model.getContent());
    assertEquals(DATE_EMPTY, model.getCreatedAt());
    assertEquals(DATE_EMPTY, model.getUpdatedAt());

    assertFalse(model.isFrozen());
    assertFalse(model.getOldMap().isEmpty());
    assertNull(model.getErrorsMap());
  }

  @Test
  public void testFieldsAreNotEmptyAfter_initAllNotEmpty() throws SQLException {
    FakeOrmModel model = new FakeOrmModel(db);
    assertTrue(model.getOldMap().isEmpty());

    model.initAllNotEmpty();
    assertEquals(ID_NOT_EMPTY, model.getId());
    assertEquals(STATUS_NOT_EMPTY, model.getStatus());
    assertEquals(TOTAL_NOT_EMPTY, model.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, model.getContent());
    assertEquals(DATE_NOT_EMPTY, model.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, model.getUpdatedAt());

    assertFalse(model.isFrozen());
    assertFalse(model.getOldMap().isEmpty());
    assertNull(model.getErrorsMap());
  }

  @Test
  public void testTableNameFunction() {
    assertEquals(nullModel.tableName(), TABLE_NAME_FAKE_MODEL);
  }

  @Test
  public void testFieldsFunction() {
    HashMap<String, Object> fields = null;

    fields = nullModel.fields();
    assertEquals(0, fields.size());

    model.initAllNull();
    fields = model.fields();
    assertEquals(0, fields.size());

    model.initAllEmpty();
    fields = model.fields();
    assertEquals(5, fields.size());
    assertEquals(ID_EMPTY, fields.get("id"));
    assertEquals(STATUS_EMPTY, fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertEquals(DATE_EMPTY, fields.get("updated_at"));

    model.initAllNotEmpty();
    fields = model.fields();
    assertEquals(6, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));

    model.initAllNull();
    fields = model.fields();
    assertEquals(0, fields.size());
    model.setId(ID_NOT_EMPTY);
    model.setContent(CONTENT_NOT_EMPTY);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = model.fields();
    assertEquals(3, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));
    model.setId(null);
    model.setContent(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = model.fields();
    assertEquals(1, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertNull(fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));
  }

  @Test
  public void testFieldsForFunction_Stringarray() {
    HashMap<String, Object> fields = null;

    model.initAllNull();
    fields = model.fieldsFor((String[]) null);
    assertEquals(0, fields.size());
    fields = model.fieldsFor("");
    assertEquals(0, fields.size());
    fields = model.fieldsFor("fake_field");
    assertEquals(0, fields.size());
    fields = model.fieldsFor("id", "status", "total", "fake_field");
    assertEquals(0, fields.size());

    model.initAllEmpty();
    fields = model.fieldsFor((String[]) null);
    assertEquals(0, fields.size());
    fields = model.fieldsFor("");
    assertEquals(0, fields.size());
    fields = model.fieldsFor("id", "status", "total", "fake_field");
    assertEquals(2, fields.size());
    assertNull(fields.get("id"));
    assertEquals(STATUS_EMPTY, fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertNull(fields.get("fake_field"));

    model.initAllNotEmpty();
    fields = model.fieldsFor((String[]) null);
    assertEquals(0, fields.size());
    fields = model.fieldsFor("");
    assertEquals(0, fields.size());
    fields = model.fieldsFor("id", "status", "total", "fake_field");
    assertEquals(3, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertNull(fields.get("fake_field"));

    model.setStatus(null);
    model.setTotal(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = model.fieldsFor("created_at");
    assertEquals(1, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("fake_field"));

    fields = model.fieldsFor("id", "status", "total", "fake_field", "created_at");
    assertEquals(2, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("fake_field"));
  }

  @Test
  public void testFieldsForFunction_Stringlist() {
    HashMap<String, Object> fields = null;

    model.initAllNull();
    fields = model.fieldsFor((List<String>) null);
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList(""));
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList("fake_field"));
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList("id", "status", "total", "fake_field"));
    assertEquals(0, fields.size());


    model.initAllEmpty();
    fields = model.fieldsFor((List<String>) null);
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList(""));
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList("id", "status", "total", "fake_field"));
    assertEquals(2, fields.size());
    assertNull(fields.get("id"));
    assertEquals(STATUS_EMPTY, fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertNull(fields.get("fake_field"));


    model.initAllNotEmpty();
    fields = model.fieldsFor((List<String>) null);
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList(""));
    assertEquals(0, fields.size());
    fields = model.fieldsFor(Arrays.asList("id", "status", "total", "fake_field"));
    assertEquals(3, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertNull(fields.get("fake_field"));

    model.setStatus(null);
    model.setTotal(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = model.fieldsFor(Arrays.asList("created_at"));
    assertEquals(1, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("fake_field"));

    fields = model.fieldsFor(Arrays.asList("id", "status", "total", "fake_field", "created_at"));
    assertEquals(2, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("fake_field"));
  }

  @Test
  public void testFieldsExceptFunction_Stringarray() {
    HashMap<String, Object> fields = null;

    model.initAllNull();
    fields = model.fieldsExcept((String[]) null);
    assertEquals(0, fields.size());
    fields = model.fieldsExcept("");
    assertEquals(0, fields.size());
    fields = model.fieldsExcept("fake_field");
    assertEquals(0, fields.size());
    fields = model.fieldsExcept("id", "status", "total", "fake_field");
    assertEquals(0, fields.size());

    model.initAllEmpty();
    fields = model.fieldsExcept((String[]) null);
    assertEquals(5, fields.size());
    fields = model.fieldsExcept("id", "status", "total", "fake_field");
    assertEquals(3, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertEquals(DATE_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));

    model.initAllNotEmpty();
    fields = model.fieldsExcept((String[]) null);
    assertEquals(6, fields.size());
    fields = model.fieldsExcept("");
    assertEquals(6, fields.size());
    fields = model.fieldsExcept("id", "status", "total", "fake_field");
    assertEquals(3, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));

    model.setStatus(null);
    model.setTotal(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = model.fieldsExcept("created_at");
    assertEquals(3, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertNull(fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));

    fields = model.fieldsExcept("id", "status", "total", "fake_field", "created_at");
    assertEquals(2, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertNull(fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));
  }

  @Test
  public void testFieldsExceptFunction_Stringlist() {
    HashMap<String, Object> fields = null;

    model.initAllNull();
    fields = model.fieldsExcept((List<String>) null);
    assertEquals(0, fields.size());
    fields = model.fieldsExcept(Arrays.asList(""));
    assertEquals(0, fields.size());
    fields = model.fieldsExcept(Arrays.asList("fake_field"));
    assertEquals(0, fields.size());
    fields = model.fieldsExcept(Arrays.asList("id", "status", "total", "fake_field"));
    assertEquals(0, fields.size());

    model.initAllEmpty();
    fields = model.fieldsExcept((List<String>) null);
    assertEquals(5, fields.size());
    fields = model.fieldsExcept(Arrays.asList(""));
    assertEquals(5, fields.size());
    fields = model.fieldsExcept(Arrays.asList("id", "status", "total", "fake_field"));
    assertEquals(3, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertEquals(DATE_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));

    model.initAllNotEmpty();
    fields = model.fieldsExcept((List<String>) null);
    assertEquals(6, fields.size());
    fields = model.fieldsExcept(Arrays.asList(""));
    assertEquals(6, fields.size());
    fields = model.fieldsExcept(Arrays.asList("id", "status", "total", "fake_field"));
    assertEquals(3, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));

    model.setStatus(null);
    model.setTotal(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = model.fieldsExcept(Arrays.asList("created_at"));
    assertEquals(3, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertNull(fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));

    fields = model.fieldsExcept(Arrays.asList("id", "status", "total", "fake_field", "created_at"));
    assertEquals(2, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertNull(fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));
    assertNull(fields.get("fake_field"));
  }

  @Test
  public void testPopulateTestTable_VerifyRecordsSaved() {
    Statement stInsert = null;
    Statement stSelect = null;
    ResultSet rSetSelect = null;
    try {
      stInsert = connection.createStatement();
      stSelect = connection.createStatement();

      for (int i = 0; i < TEST_DATA_QUERIES.length; i++) {
        int id = i + 1;
        // total pre-save
        assertEquals(i, nullModel.count(null));

        // save new
        stInsert.execute(TEST_DATA_QUERIES[i]);
        assertEquals(id, nullModel.count(null));

        // total post-save
        assertEquals(1, nullModel.count("id=" + id));

        // verification
        rSetSelect = stSelect.executeQuery("SELECT * FROM " + TABLE_NAME_FAKE_MODEL + " WHERE id = " + id);
        assertTrue(rSetSelect.next());
        assertEquals(id, rSetSelect.getInt("id"));
        rSetSelect.close();
      }
      stInsert.close();
    } catch (Exception e) {
      try {
        if (stInsert != null && !stInsert.isClosed()) {
          stInsert.close();
        }
      } catch (Exception e2) {
      }
      fail(e.getMessage());
    }
  }

  @Test
  public void testCountFunction() {
    populateTestTable();
    // more total queries with different combinations
    model.initAllNull();
    assertEquals(5, model.count());

    model.initAllNull();
    model.setId(-1);
    assertEquals(0, model.count());

    model.initAllNull();
    model.setContent("teenage mutant ninja turtles");
    assertEquals(0, model.count());

    model.initAllNull();
    model.setStatus(true);
    assertEquals(2, model.count());

    model.initAllNull();
    model.setStatus(false);
    assertEquals(2, model.count());

    model.initAllNull();
    model.setTotal(0);
    assertEquals(1, model.count());

    model.initAllNull();
    model.setTotal(1);
    assertEquals(1, model.count());

    model.initAllNull();
    model.setTotal(999);
    assertEquals(0, model.count());

    model.initAllNull();
    model.setTotal(1);
    model.setStatus(true);
    assertEquals(1, model.count());

    model.initAllNull();
    model.setTotal(1);
    model.setStatus(false);
    assertEquals(0, model.count());

    model.initAllNull();
    model.setTotal(2);
    assertEquals(1, model.count());

    model.initAllNull();
    model.setContent(null);
    assertEquals(5, model.count());

    model.initAllNull();
    model.setContent("total zero");
    assertEquals(0, model.count());

    model.initAllNull();
    model.setStatus(false);
    model.setContent(null);
    assertEquals(2, model.count());

    model.initAllNull();
    model.setStatus(false);
    model.setContent(CONTENT_NOT_EMPTY);
    assertEquals(0, model.count());

    model.initAllNull();
    model.setStatus(true);
    model.setContent(CONTENT_NOT_EMPTY);
    assertEquals(2, model.count());
  }

  private void sharedTestsForTestCountFunction_ClassWhere() {
    assertEquals(5, nullModel.count(null));
    assertEquals(5, nullModel.count(""));

    assertEquals(0, nullModel.count("id IS NULL"));
    assertEquals(1, nullModel.count("id=1"));
    assertEquals(0, nullModel.count("id=999"));

    assertEquals(1, nullModel.count("status IS NULL"));
    assertEquals(2, nullModel.count("status=0"));
    assertEquals(2, nullModel.count("status=1"));

    assertEquals(2, nullModel.count("total IS NULL"));
    assertEquals(1, nullModel.count("total=0"));
    assertEquals(1, nullModel.count("total=1"));
    assertEquals(1, nullModel.count("total=2"));
    assertEquals(0, nullModel.count("total=3"));
    assertEquals(3, nullModel.count("total>=0"));
    assertEquals(2, nullModel.count("total>=1 AND total<=2"));
    assertEquals(0, nullModel.count("total>=999"));

    assertEquals(1, nullModel.count("status IS NULL AND total IS NULL"));
    assertEquals(1, nullModel.count("status=0 AND total=0"));
    assertEquals(1, nullModel.count("status=1 AND total=1"));
    assertEquals(0, nullModel.count("status=0 AND total!=0"));

    assertEquals(3, nullModel.count("content IS NULL"));
    assertEquals(2, nullModel.count("content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(0, nullModel.count("status=0 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(2, nullModel.count("status=1 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(0, nullModel.count("total=0 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(1, nullModel.count("total=1 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(1, nullModel.count("total=2 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(2, nullModel.count("total>=0 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(1, nullModel.count("status=1 AND total=2 AND content='" + CONTENT_NOT_EMPTY + "'"));
  }

  @Test
  public void testCountFunction_ClassWhere() {
    populateTestTable();
    sharedTestsForTestCountFunction_ClassWhere();
  }

  @Test
  public void testFindFunction() {
    // this tests find() function which uses object's own fields to construct the where condition
    populateTestTable();

    foundOne = nullModel.find();
    assertNull(foundOne);

    model.initAllNull();
    foundOne = model.find();
    assertNull(foundOne);

    model.initAllNull();
    model.setId(999);
    foundOne = model.find();
    assertNull(foundOne);

    model.initAllNull();
    model.setId(1); // careful with id=1; saved by query so all values are null
    foundOne = model.find();
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(3);
    foundOne = model.find();
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(TOTAL_EMPTY, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(5);
    foundOne = model.find();
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    model.initAllNull();
    model.setStatus(false);
    model.setTotal(1);
    foundOne = model.find();
    assertNull(foundOne);

    model.initAllNull();
    model.setStatus(true);
    model.setTotal(2);
    foundOne = model.find();
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    model.initAllNull();
    model.setStatus(true);
    model.setTotal(1);
    model.setContent(CONTENT_NOT_EMPTY);
    foundOne = model.find();
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());
  }

  @Test
  public void testFindFunction_Where() {
    populateTestTable();

    // will return the first record from table
    foundOne = nullModel.find(null);
    assertNotNull(foundOne);

    foundOne = model.find(null);
    assertNotNull(foundOne);

    foundOne = model.find("id=999");
    assertNull(foundOne);

    foundOne = model.find("id=1");
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = model.find("id=3");
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(TOTAL_EMPTY, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = model.find("id=5");
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    foundOne = model.find("status=0 AND total=1");
    assertNull(foundOne);

    foundOne = model.find("status=1 AND total=2");
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    foundOne = model.find("status=1 AND total=1 AND content='" + CONTENT_NOT_EMPTY + "'");
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());
  }

  @Test
  public void testFindFunction_WhereOrderby() {
    populateTestTable();

    // will return the first record from table
    foundOne = nullModel.find(null, "id desc");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = model.find(null, "id asc");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());


    foundOne = model.find("id=999", "id desc");
    assertNull(foundOne);

    foundOne = model.find("id<999", "id desc");
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = model.find("id<999", "id asc");
    assertEquals(new Integer(1), foundOne.getId());


    foundOne = model.find("status=1", "total desc");
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = model.find("status=1", "total asc");
    assertEquals(new Integer(4), foundOne.getId());


    foundOne = model.find("content=null", "id desc");
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = model.find("content=null", "id asc");
    assertEquals(new Integer(1), foundOne.getId());


    foundOne = model.find("status!=0 AND content='" + CONTENT_NOT_EMPTY + "'", "id desc");
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = model.find("status!=0 AND content='" + CONTENT_NOT_EMPTY + "'", "id asc");
    assertEquals(new Integer(4), foundOne.getId());
  }

  @Test
  public void testFindAllFunction() {
    populateTestTable();

    foundList = nullModel.findAll();
    assertEquals(0, foundList.size());

    model.initAllNull();
    foundList = model.findAll();
    assertEquals(0, foundList.size());

    model.initAllNull();
    model.setId(1); // careful with id=1; saved by query so all values are null
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(2);
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(2), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(3);
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(TOTAL_EMPTY, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(5);
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    model.initAllNull();
    model.setStatus(false);
    foundList = model.findAll();
    assertEquals(2, foundList.size());
    assertEquals(new Integer(2), foundList.get(0).getId());
    assertEquals(new Integer(3), foundList.get(1).getId());

    model.initAllNull();
    model.setStatus(true);
    foundList = model.findAll();
    assertEquals(2, foundList.size());
    assertEquals(new Integer(4), foundList.get(0).getId());
    assertEquals(new Integer(5), foundList.get(1).getId());

    model.initAllNull();
    model.setTotal(0);
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    assertEquals(new Integer(3), foundList.get(0).getId());

    model.initAllNull();
    model.setTotal(1);
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    assertEquals(new Integer(4), foundList.get(0).getId());

    model.initAllNull();
    model.setTotal(2);
    foundList = model.findAll();
    assertEquals(1, foundList.size());
    assertEquals(new Integer(5), foundList.get(0).getId());
  }

  private void sharedTestsForTestFindAllFunction_ClassWhereOrderbyLimit() {
    foundList = nullModel.findAll(null, "id DESC", null, null);
    assertEquals(5, foundList.size());

    foundList = nullModel.findAll("total IS NOT NULL", "id ASC", null, null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(5), foundOne.getId());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", null, null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 0, null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", null, 100);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 0, 100);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 0, 0);
    assertEquals(3, foundList.size());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 0, 1);
    assertEquals(1, foundList.size());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 0, 2);
    assertEquals(2, foundList.size());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 0, 3);
    assertEquals(3, foundList.size());


    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 1, 3);
    assertEquals(2, foundList.size());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 2, 3);
    assertEquals(1, foundList.size());

    foundList = nullModel.findAll("total IS NOT NULL", "id DESC", 3, 3);
    assertEquals(0, foundList.size());
  }

  @Test
  public void testFindAllFunction_ClassWhereOrderbyLimit() {
    populateTestTable();
    sharedTestsForTestFindAllFunction_ClassWhereOrderbyLimit();
  }

  @Test
  public void testSaveFunctionSkipValidation() {
    int count = 0;
    // make sure empty table
    assertEquals(count, nullModel.count());

    // all fields null; still save
    model.initAllNull();
    assertTrue(model.save());
    count++;
    assertEquals(count, nullModel.count());

    // fields with empty/0 values
    model.initAllEmpty();
    assertTrue(model.save());
    count++;
    assertEquals(count, nullModel.count());

    // fields with non-0, non-blank values
    model.initAllNotEmpty();
    assertTrue(model.save());
    count++;
    assertEquals(count, nullModel.count());

    // first record there?
    model.initAllNull();
    model.setId(1);
    assertEquals(1, model.count());

    // save another record with a non-empty field and see if it is there
    model.initAllEmpty();
    model.setTotal(Integer.MAX_VALUE);
    assertTrue(model.save());
    count++;
    assertEquals(count, nullModel.count());
    model.initAllNull();
    model.setTotal(Integer.MAX_VALUE);
    assertEquals(1, model.count());
    assertEquals(count, nullModel.count());

    // save with same values again
    model.initAllEmpty();
    model.setTotal(Integer.MAX_VALUE);
    assertTrue(model.save());
    count++;
    assertEquals(count, nullModel.count());
    model.initAllNull();
    model.setTotal(Integer.MAX_VALUE);
    assertEquals(2, model.count()); // two objects!!!
    assertEquals(count, nullModel.count());

    // save another record with two non-empty fields and see if it is there
    model.initAllEmpty();
    model.setTotal(Integer.MAX_VALUE - 1);
    model.setContent("hello world");
    assertTrue(model.save());
    count++;
    assertEquals(count, nullModel.count());
    model.initAllNull();
    model.setTotal(Integer.MAX_VALUE - 1);
    model.setContent("hello world");
    assertEquals(1, model.count());
    assertEquals(count, nullModel.count());

    // frozen object
    model.initAllNull();
    model.setFrozen(true);
    assertFalse(model.save());

    model.initAllEmpty();
    model.setFrozen(true);
    assertFalse(model.save());

    model.initAllNotEmpty();
    model.setFrozen(true);
    assertFalse(model.save());
  }

  @Test
  public void testSaveFunctionDoValidation() {
    model.setSkipValidation(false);

    int count = 0;
    // make sure empty table
    assertEquals(count, nullModel.count());

    // insert record
    model.initAllNull();
    assertFalse(model.save());
    model.setStatus(Boolean.FALSE);
    assertFalse(model.save());
    model.setTotal(TOTAL_NOT_EMPTY);
    assertTrue(model.save());
    assertEquals(CONTENT_BY_ONCREATE, model.getContent());
    count++;
    // verify record was saved
    assertEquals(count, nullModel.count());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());
    // update record
    assertTrue(model.save());
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.save());
    model.setCreatedAt(null);
    assertTrue(model.save());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());


    // fields with empty/0 values
    model.initAllEmpty();
    assertTrue(model.save());
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.save());
    count++;
    // verification
    assertEquals(count, nullModel.count());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());
    assertEquals(CONTENT_BY_ONCREATE, model.getContent());
    // update record
    model.setCreatedAt(null);
    assertTrue(model.save());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());
    assertEquals(CONTENT_BY_ONUPDATE, model.getContent());


    // fields with non-0, non-blank values
    model.initAllNotEmpty();
    assertTrue(model.save());
    count++;
    // verification
    assertEquals(count, nullModel.count());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());
    assertEquals(CONTENT_BY_ONUPDATE, model.getContent());
    // update same record
    model.setTotal(new Integer(-1));
    assertTrue(model.save());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());
    assertEquals(CONTENT_BY_ONUPDATE, model.getContent());

    // object is frozen
    model.initAllNull();
    model.setFrozen(true);
    assertFalse(model.save());
    model.setStatus(Boolean.FALSE);
    assertFalse(model.save());
    model.setTotal(TOTAL_NOT_EMPTY);
    assertFalse(model.save());
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.save());

    model.initAllEmpty();
    model.setFrozen(true);
    assertFalse(model.save());
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.save());
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.save());


    model.initAllNotEmpty();
    model.setFrozen(true);
    assertFalse(model.save());
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.save());
  }

  @Test
  public void testImportRowsFunction_ObjectColumnsRows() {
    // empty table?
    foundList = nullModel.findAll(null);
    assertEquals(0, foundList.size());

    assertFalse(nullModel.importRows(null, null, null));
    assertFalse(model.importRows(null, null, null));
    assertFalse(model.importRows(new String[0], null, null));
    assertFalse(model.importRows(new String[0], new ArrayList<>(), null));
    assertFalse(model.importRows(new String[0], IMPORT_ROWS, null));
    assertFalse(model.importRows(IMPORT_COLUMNS, new ArrayList<>(), null));

    // populated?
    assertTrue(model.importRows(IMPORT_COLUMNS, IMPORT_ROWS, null));
    foundList = nullModel.findAll(null);
    assertEquals(5, foundList.size());

    // repeat some earlier tests
    sharedTestsForTestFindAllFunction_ClassWhereOrderbyLimit();
    sharedTestsForTestCountFunction_ClassWhere();
  }

  @Test
  public void testUpdateColumFunction() throws SQLException {
    populateTestTable();
    FakeOrmModel model2 = new FakeOrmModel(db);
    Integer total = new Integer(123);

    // fake columns, non existent objects etc
    model.initAllNull();
    model.setId(1);
    assertFalse(model.updateColumn("fake_column", Boolean.FALSE));
    assertTrue(model.updateColumn("status", STATUS_EMPTY));
    model.setId(99);
    assertFalse(model.updateColumn("status", STATUS_EMPTY));
    model.setId(1);
    assertTrue(model.updateColumn("status", null));

    // update each columns one by one and query
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateColumn("status", STATUS_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateColumn("total", total));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateColumn("content", CONTENT_NOT_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertFalse(model2.updateColumn("created_at", DATE_NOT_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateColumn("updated_at", DATE_NOT_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());
  }

  @Test
  public void testToStringFunction() {
    populateTestTable();

    // @formatter:off
    model.initAllNull();
    assertEquals("FakeOrmModel [total=null, updatedAt=null, createdAt=null, id=null, content=null, status=null]",
        model.toString());

    model.initAllEmpty();
    assertEquals("FakeOrmModel [total=" + TOTAL_EMPTY + ", updatedAt=" + DATE_EMPTY + ", " +
            "createdAt=" + DATE_EMPTY + ", id=" + ID_EMPTY + ", content=" + CONTENT_EMPTY + ", status=" + STATUS_EMPTY + "]",
        model.toString());

    model.initAllNotEmpty();
    assertEquals("FakeOrmModel [total=" + TOTAL_NOT_EMPTY + ", updatedAt=" + DATE_NOT_EMPTY + ", " +
        "createdAt=" + DATE_NOT_EMPTY + ", id=" + ID_NOT_EMPTY + ", content=" + CONTENT_NOT_EMPTY + ", " +
        "status=" + STATUS_NOT_EMPTY + "]", model.toString());

    model.setId(99);
    assertEquals("FakeOrmModel [total=" + TOTAL_NOT_EMPTY + ", updatedAt=" + DATE_NOT_EMPTY + ", " +
            "createdAt=" + DATE_NOT_EMPTY + ", id=99, content=" + CONTENT_NOT_EMPTY + ", status=" + STATUS_NOT_EMPTY + "]",
        model.toString());
    // @formatter:on
  }

  @Test
  public void testBeforeValidateFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.beforeValidate();
    assertTrue(model.hasError(ERROR_TOTAL));
    assertEquals(1, model.getErrorsMap().size());

    model.initAllNull();
    model.setTotal(TOTAL_NOT_EMPTY);
    model.beforeValidate();
    assertFalse(model.hasError(ERROR_TOTAL));
    assertNull(model.getErrorsMap());


    model.initAllEmpty();
    model.beforeValidate();
    assertFalse(model.hasError(ERROR_TOTAL));
    assertNull(model.getErrorsMap());

    model.initAllEmpty();
    model.setTotal(TOTAL_NOT_EMPTY);
    model.beforeValidate();
    assertFalse(model.hasError(ERROR_TOTAL));
    assertNull(model.getErrorsMap());


    model.initAllNotEmpty();
    model.beforeValidate();
    assertFalse(model.hasError(ERROR_TOTAL));
    assertNull(model.getErrorsMap());

    model.initAllNotEmpty();
    model.setTotal(TOTAL_NOT_EMPTY);
    model.beforeValidate();
    assertFalse(model.hasError(ERROR_TOTAL));
    assertNull(model.getErrorsMap());
  }

  @Test
  public void testValidateFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.validate();
    assertTrue(model.hasError(ERROR_STATUS));
    assertEquals(1, model.getErrorsMap().size());

    model.initAllNull();
    model.setStatus(Boolean.TRUE);
    model.validate();
    assertFalse(model.hasError(ERROR_STATUS));
    assertNull(model.getErrorsMap());


    model.initAllEmpty();
    model.validate();
    assertNull(model.getErrorsMap());

    model.initAllEmpty();
    model.setId(1);
    model.setCreatedAt(DATE_FUTURE);
    model.validate();
    assertTrue(model.hasError(ERROR_CREATED_AT));
    assertEquals(1, model.getErrorsMap().size());


    model.initAllNotEmpty();
    model.validate();
    assertNull(model.getErrorsMap());

    model.initAllNotEmpty();
    model.setId(1);
    model.setCreatedAt(DATE_FUTURE);
    model.validate();
    assertTrue(model.hasError(ERROR_CREATED_AT));
    assertEquals(1, model.getErrorsMap().size());
  }

  @Test
  public void testBeforeSaveFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.beforeSave();
    assertNotNull(model.getContent());
    assertEquals(CONTENT_EMPTY, model.getContent());

    model.initAllEmpty();
    model.beforeSave();
    assertNotNull(model.getContent());
    assertEquals(CONTENT_EMPTY, model.getContent());

    model.initAllNotEmpty();
    model.beforeSave();
    assertNotNull(model.getContent());
    assertTrue(model.getContent().equals(CONTENT_NOT_EMPTY));


    model.setSkipValidation(true);

    model.initAllNull();
    model.beforeSave();
    assertNull(model.getContent());

    model.initAllEmpty();
    model.beforeSave();
    assertNotNull(model.getContent());
    assertEquals(CONTENT_EMPTY, model.getContent());

    model.initAllNotEmpty();
    model.beforeSave();
    assertNotNull(model.getContent());
    assertTrue(model.getContent().equals(CONTENT_NOT_EMPTY));
  }

  @Test
  public void testOnCreateFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.onCreate();
    assertNull(model.getId());
    assertNotNull(model.getContent());
    assertEquals(CONTENT_BY_ONCREATE, model.getContent());

    model.initAllEmpty();
    model.onCreate();
    assertNull(model.getId());
    assertNotNull(model.getContent());
    assertEquals(CONTENT_BY_ONCREATE, model.getContent());

    model.initAllNotEmpty();
    model.setId(null);
    model.onCreate();
    assertNull(model.getId());
    assertNotNull(model.getContent());
    assertTrue(model.getContent().equals(CONTENT_NOT_EMPTY));
  }

  @Test
  public void testOnUpdateFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.onUpdate();
    assertNull(model.getId());
    assertNotNull(model.getContent());
    assertEquals(CONTENT_BY_ONUPDATE, model.getContent());

    model.initAllEmpty();
    model.onUpdate();
    assertNull(model.getId());
    assertNotNull(model.getContent());
    assertEquals(CONTENT_BY_ONUPDATE, model.getContent());

    model.initAllNotEmpty();
    model.setId(null);
    model.onUpdate();
    assertNull(model.getId());
    assertNotNull(model.getContent());
    assertTrue(model.getContent().equals(CONTENT_BY_ONUPDATE));
  }

  @Test
  public void testAfterSaveFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.afterSave();
    assertNull(model.getId());
    assertNull(model.getTotal());
    assertNull(model.getErrors());

    model.initAllEmpty();
    model.afterSave();
    assertNull(model.getId());
    assertEquals(TOTAL_EMPTY, model.getTotal());
    assertNull(model.getErrors());

    model.initAllNotEmpty();
    model.setId(null);
    model.afterSave();
    assertNull(model.getId());
    assertEquals(TOTAL_NOT_EMPTY, model.getTotal());
    assertNull(model.getErrors());

    model.setId(3);
    assertEquals(new Integer(3), model.getId());
    assertEquals(TOTAL_NOT_EMPTY, model.getTotal());
    model.afterSave();
    assertEquals(new Integer(3), model.getId());
    assertEquals(new Integer(model.getId() + 1), model.getTotal());
    assertNull(model.getErrors());
  }

  @Test
  public void testValidFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.setStatus(Boolean.FALSE);
    assertFalse(model.valid());
    model.setTotal(TOTAL_NOT_EMPTY);
    assertTrue(model.valid());

    model.initAllEmpty();
    model.setCreatedAt(DATE_FUTURE);
    assertTrue(model.valid());

    model.initAllNotEmpty();
    model.setCreatedAt(DATE_FUTURE);
    assertFalse(model.valid());
    model.setCreatedAt(null);
    assertTrue(model.valid());


    model.setSkipValidation(true);

    model.initAllNull();
    model.setStatus(Boolean.FALSE);
    assertTrue(model.valid());
    model.setTotal(TOTAL_NOT_EMPTY);
    assertTrue(model.valid());

    model.initAllEmpty();
    model.setCreatedAt(DATE_FUTURE);
    assertTrue(model.valid());

    model.initAllNotEmpty();
    model.setCreatedAt(DATE_FUTURE);
    assertTrue(model.valid());
    model.setCreatedAt(null);
    assertTrue(model.valid());
  }

  @Test
  public void testFieldChangedFunction() {
    model.initAllNull();

    assertFalse(model.fieldChanged("id"));
    assertFalse(model.fieldChanged("status"));
    assertFalse(model.fieldChanged("total"));
    assertFalse(model.fieldChanged("content"));

    model.setId(1);
    assertTrue(model.fieldChanged("id"));
    assertFalse(model.fieldChanged("status"));
    assertFalse(model.fieldChanged("total"));
    assertFalse(model.fieldChanged("content"));

    model.setStatus(STATUS_EMPTY);
    assertTrue(model.fieldChanged("id"));
    assertTrue(model.fieldChanged("status"));
    assertFalse(model.fieldChanged("total"));
    assertFalse(model.fieldChanged("content"));

    model.setTotal(TOTAL_EMPTY);
    assertTrue(model.fieldChanged("id"));
    assertTrue(model.fieldChanged("status"));
    assertTrue(model.fieldChanged("total"));
    assertFalse(model.fieldChanged("content"));

    model.setContent(CONTENT_EMPTY);
    assertTrue(model.fieldChanged("id"));
    assertTrue(model.fieldChanged("status"));
    assertTrue(model.fieldChanged("total"));
    assertTrue(model.fieldChanged("content"));

    model.setStatus(null);
    assertFalse(model.fieldChanged("status"));

    model.setStatus(STATUS_NOT_EMPTY);
    assertTrue(model.fieldChanged("status"));

    // after loading from DB
    populateTestTable();
    model.initAllNull();
    model.setId(5);
    foundOne = model.find();

    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    foundOne.setId(3);
    foundOne.setStatus(null);
    foundOne.setTotal(9);
    foundOne.setContent(CONTENT_EMPTY);
    assertTrue(foundOne.fieldChanged("id"));
    assertTrue(foundOne.fieldChanged("status"));
    assertTrue(foundOne.fieldChanged("total"));
    assertTrue(foundOne.fieldChanged("content"));
  }

  @Test
  public void testFieldWasFunction() {
    model.initAllNull();
    model.setOldMap(null);
    assertFalse(model.fieldWas(null, null));
    assertFalse(model.fieldWas("", null));
    assertFalse(model.fieldWas("", ""));
    assertFalse(model.fieldWas("", 1));
    assertFalse(model.fieldWas("id", 1));
    assertTrue(model.fieldWas("fake_field", null));

    model.initAllNull();
    assertFalse(model.fieldWas(null, null));
    assertFalse(model.fieldWas("", null));
    assertFalse(model.fieldWas("", ""));
    assertFalse(model.fieldWas("", 1));

    assertTrue(model.fieldWas("id", null));
    assertTrue(model.fieldWas("status", null));
    assertTrue(model.fieldWas("total", null));
    assertTrue(model.fieldWas("content", null));
    assertTrue(model.fieldWas("fake_field", null));

    model.setId(1);
    assertFalse(model.fieldWas("id", 1));
    assertTrue(model.fieldWas("status", null));
    assertTrue(model.fieldWas("total", null));
    assertTrue(model.fieldWas("content", null));
    assertTrue(model.fieldWas("fake_field", null));

    model.setStatus(STATUS_EMPTY);
    assertFalse(model.fieldWas("id", 1));
    assertFalse(model.fieldWas("status", STATUS_EMPTY));
    assertTrue(model.fieldWas("total", null));
    assertTrue(model.fieldWas("content", null));
    assertTrue(model.fieldWas("fake_field", null));

    model.setTotal(TOTAL_EMPTY);
    assertFalse(model.fieldWas("id", 1));
    assertFalse(model.fieldWas("status", STATUS_EMPTY));
    assertFalse(model.fieldWas("total", TOTAL_EMPTY));
    assertTrue(model.fieldWas("content", null));
    assertTrue(model.fieldWas("fake_field", null));

    model.setContent(CONTENT_EMPTY);
    assertFalse(model.fieldWas("id", 1));
    assertFalse(model.fieldWas("status", STATUS_EMPTY));
    assertFalse(model.fieldWas("total", TOTAL_EMPTY));
    assertFalse(model.fieldWas("content", CONTENT_EMPTY));
    assertTrue(model.fieldWas("fake_field", null));

    model.setStatus(null);
    assertTrue(model.fieldWas("status", null));
    model.setTotal(null);
    assertTrue(model.fieldWas("total", null));
    model.setContent(null);
    assertTrue(model.fieldWas("content", null));
    assertTrue(model.fieldWas("fake_field", null));

    // after loading from DB
    populateTestTable();
    model.initAllNull();
    model.setId(5);
    foundOne = model.find();

    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(STATUS_NOT_EMPTY, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    foundOne.setId(3);
    foundOne.setStatus(null);
    foundOne.setTotal(9);
    foundOne.setContent(CONTENT_EMPTY);
    assertFalse(foundOne.fieldWas("id", 3));
    assertFalse(foundOne.fieldWas("status", null));
    assertFalse(foundOne.fieldWas("total", 9));
    assertFalse(foundOne.fieldWas("content", CONTENT_EMPTY));
    assertTrue(foundOne.fieldWas("id", 5));
    assertTrue(foundOne.fieldWas("status", STATUS_NOT_EMPTY));
    assertTrue(foundOne.fieldWas("total", 2));
    assertTrue(foundOne.fieldWas("content", CONTENT_NOT_EMPTY));
    assertTrue(foundOne.fieldWas("fake_field", null));
  }

  @Test
  public void testAddErrorFunction() {
    model.initAllNull();

    model.addError("id", ERROR_ID);
    assertNotNull(model.getErrorsMap());
    assertEquals(1, model.getErrorsMap().size());
    model.addError("content", ERROR_CONTENT);
    assertEquals(2, model.getErrorsMap().size());
  }

  @Test
  public void testAddAfterSaveErrorFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT - 1);
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT);
    model.afterSave();
    assertEquals(1, model.getErrorsMap().size());
    assertEquals(model.getErrors(), ERROR_ID_MAXED_OUT);

    model.initAllEmpty();
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT - 1);
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT);
    model.afterSave();
    assertEquals(1, model.getErrorsMap().size());
    assertEquals(model.getErrors(), ERROR_ID_MAXED_OUT);

    model.initAllNotEmpty();
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT - 1);
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT);
    model.afterSave();
    assertEquals(1, model.getErrorsMap().size());
    assertEquals(model.getErrors(), ERROR_ID_MAXED_OUT);
  }

  @Test
  public void testGetAfterSaveErrorFunction() {
    model.setSkipValidation(false);

    model.initAllNull();
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT - 1);
    model.afterSave();
    assertNull(model.getErrors());
    model.setId(MAX_ID_LIMIT);
    model.afterSave();
    assertEquals(1, model.getErrorsMap().size());
    assertEquals(model.getErrors(), ERROR_ID_MAXED_OUT);
    assertEquals(model.getErrors(), model.getAfterSaveError());
  }

  @Test
  public void testGetAllErrorsFunction() {
    model.initAllNull();

    model.addError("id", ERROR_ID);
    model.addError("content", ERROR_CONTENT);

    assertNotNull(model.getErrorsMap());
    assertEquals(ERROR_ID, model.getErrorsMap().get("id"));
    assertEquals(ERROR_CONTENT, model.getErrorsMap().get("content"));
  }

  @Test
  public void testGetErrorFunction() {
    model.initAllNull();

    model.addError("id", ERROR_ID);
    model.addError("content", ERROR_CONTENT);

    assertEquals(ERROR_ID, model.getError("id"));
    assertEquals(ERROR_CONTENT, model.getError("content"));
  }

  @Test
  public void testGetErrorsFunction() {
    model.initAllNull();

    model.addError("id", ERROR_ID);
    model.addError("content", ERROR_CONTENT);

    assertTrue(model.getErrors().equals(ERROR_ID + "\n" + ERROR_CONTENT));
  }

  @Test
  public void testHasErrorFunction() {
    model.initAllNull();

    model.addError("id", ERROR_ID);
    model.addError("content", ERROR_CONTENT);

    assertTrue(model.hasError(ERROR_ID));
    assertTrue(model.hasError(ERROR_CONTENT));
  }

  @Test
  public void testSetOldMapFunction() {
    model.initAllNull();

    HashMap<String, Object> map = new HashMap<>();
    map.put("Hello", "World!");

    model.setOldMap(map);

    assertEquals(map, model.getOldMap());
    assertEquals("World!", map.get("Hello"));
  }

  @Test
  public void testGetOldMapFunction() {
    model.initAllNull();
    assertNotNull(model.getOldMap());
    assertTrue(model.getOldMap().isEmpty());

    testSetOldMapFunction();
  }

  @Test
  public void testGetOldValueFunction() {
    model.initAllNull();
    model.setOldMap(null);
    assertNull(model.getOldValue("id"));
    assertNull(model.getOldValue("status"));
    assertNull(model.getOldValue("total"));
    assertNull(model.getOldValue("content"));
    assertNull(model.getOldValue("created_at"));
    assertNull(model.getOldValue("updated_at"));
    assertNull(model.getOldValue("fake_field"));


    model.initAllNull();
    assertNull(model.getOldValue("id"));
    assertNull(model.getOldValue("status"));
    assertNull(model.getOldValue("total"));
    assertNull(model.getOldValue("content"));
    assertNull(model.getOldValue("created_at"));
    assertNull(model.getOldValue("updated_at"));
    assertNull(model.getOldValue("fake_field"));

    model.setId(ID_NOT_EMPTY);
    model.setStatus(STATUS_NOT_EMPTY);
    model.setTotal(TOTAL_NOT_EMPTY);
    model.setContent(CONTENT_NOT_EMPTY);
    model.setCreatedAt(DATE_NOT_EMPTY);
    model.setUpdatedAt(DATE_NOT_EMPTY);

    assertNull(model.getOldValue("id"));
    assertNull(model.getOldValue("status"));
    assertNull(model.getOldValue("total"));
    assertNull(model.getOldValue("content"));
    assertNull(model.getOldValue("created_at"));
    assertNull(model.getOldValue("updated_at"));
    assertNull(model.getOldValue("fake_field"));


    model.initAllNotEmpty();
    assertEquals(ID_NOT_EMPTY, model.getOldValue("id"));
    assertEquals(STATUS_NOT_EMPTY, model.getOldValue("status"));
    assertEquals(TOTAL_NOT_EMPTY, model.getOldValue("total"));
    assertEquals(CONTENT_NOT_EMPTY, model.getOldValue("content"));
    assertEquals(DATE_NOT_EMPTY, model.getOldValue("created_at"));
    assertEquals(DATE_NOT_EMPTY, model.getOldValue("updated_at"));
    assertNull(model.getOldValue("fake_field"));

    model.setId(null);
    model.setStatus(null);
    model.setTotal(null);
    model.setContent(null);
    model.setCreatedAt(null);
    model.setUpdatedAt(null);

    assertEquals(ID_NOT_EMPTY, model.getOldValue("id"));
    assertEquals(STATUS_NOT_EMPTY, model.getOldValue("status"));
    assertEquals(TOTAL_NOT_EMPTY, model.getOldValue("total"));
    assertEquals(CONTENT_NOT_EMPTY, model.getOldValue("content"));
    assertEquals(DATE_NOT_EMPTY, model.getOldValue("created_at"));
    assertEquals(DATE_NOT_EMPTY, model.getOldValue("updated_at"));
    assertNull(model.getOldValue("fake_field"));
  }

  @Test
  public void testLoadFromFunction_Resultset() throws SQLException {
    populateTestTable();

    Connection conn = getNewConnection();

    model.initAllNull();
    model.setId(5);
    foundOne = model.find();
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    model.initAllNull();
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery("SELECT * FROM " + TABLE_NAME_FAKE_MODEL + " WHERE id=5");
    assertTrue(rs.next());
    model.setFrozen(true);
    assertFalse(model.loadFrom(rs));
    model.setFrozen(false);
    assertTrue(model.loadFrom(rs));


    assertEquals(foundOne.getId(), model.getId());
    assertEquals(foundOne.getStatus(), model.getStatus());
    assertEquals(foundOne.getTotal(), model.getTotal());
    assertEquals(foundOne.getContent(), model.getContent());
    assertEquals(foundOne.getCreatedAt(), model.getCreatedAt());
    assertEquals(foundOne.getUpdatedAt(), model.getUpdatedAt());

    rs.close();
    st.close();
    conn.close();
  }

  @Test
  public void testLoadFromFunction_Hashmap() throws SQLException {
    model.initAllNull();

    HashMap<String, Object> fieldValueMap = new HashMap<>();
    fieldValueMap.put("id", ID_NOT_EMPTY);
    fieldValueMap.put("status", STATUS_NOT_EMPTY);
    fieldValueMap.put("total", TOTAL_NOT_EMPTY);
    fieldValueMap.put("content", CONTENT_NOT_EMPTY);
    fieldValueMap.put("created_at", DATE_NOT_EMPTY);
    fieldValueMap.put("updated_at", DATE_NOT_EMPTY);

    model.setFrozen(true);
    assertFalse(model.loadFrom(fieldValueMap));
    assertNull(model.getId());
    assertNull(model.getStatus());
    assertNull(model.getTotal());
    assertNull(model.getContent());
    assertNull(model.getUpdatedAt());


    model.setFrozen(false);
    assertTrue(model.loadFrom(fieldValueMap));
    assertEquals(ID_NOT_EMPTY, model.getId());
    assertEquals(STATUS_NOT_EMPTY, model.getStatus());
    assertEquals(TOTAL_NOT_EMPTY, model.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, model.getContent());
    assertEquals(DATE_NOT_EMPTY, model.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, model.getUpdatedAt());
  }

  @Test
  public void testAssignFieldsFunction() throws SQLException {
    model.initAllNull();
    model2.initAllNotEmpty();

    model.setFrozen(true);
    assertFalse(model.assignFields(model2));
    assertNull(model.getId());
    assertNull(model.getStatus());
    assertNull(model.getTotal());
    assertNull(model.getContent());
    assertNull(model.getCreatedAt());
    assertNull(model.getUpdatedAt());


    model.setFrozen(false);
    assertTrue(model.assignFields(model2));
    assertEquals(ID_NOT_EMPTY, model.getId());
    assertEquals(STATUS_NOT_EMPTY, model.getStatus());
    assertEquals(TOTAL_NOT_EMPTY, model.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, model.getContent());
    assertEquals(DATE_NOT_EMPTY, model.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, model.getUpdatedAt());
  }

  @Test
  public void testIsFrozenFunction() {
    populateTestTable();

    // delete
    model.initAllNull();
    model.setId(1);
    foundOne = model.find();
    int countModelBefore = nullModel.count();
    assertFalse(foundOne.isFrozen());
    assertTrue(foundOne.delete());
    assertTrue(foundOne.isFrozen());
    int countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore - 1);

    // destroy
    model.initAllNull();
    model.setId(2);
    foundOne = model.find();
    countModelBefore = nullModel.count();
    assertFalse(foundOne.isFrozen());
    assertTrue(foundOne.destroy());
    assertTrue(foundOne.isFrozen());
    countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore - 1);

    // already deleted
    model.initAllNull();
    model.setId(3);
    foundOne = model.find();
    foundOne.setId(null);
    countModelBefore = nullModel.count();
    assertFalse(foundOne.isFrozen());
    assertFalse(foundOne.delete());
    assertFalse(foundOne.isFrozen());
    countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore);

    model.initAllNull();
    model.setId(3);
    foundOne = model.find();
    foundOne.setId(null);
    countModelBefore = nullModel.count();
    assertFalse(foundOne.isFrozen());
    assertFalse(foundOne.destroy());
    assertFalse(foundOne.isFrozen());
    countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore);
  }

  @Test
  public void testDeleteFunction() {
    populateTestTable();

    // no id
    model.initAllNull();
    assertFalse(model.delete());

    // record does not exist
    model.initAllNull();
    model.setId(MAX_ID_LIMIT);
    foundOne = model.find();
    assertNull(foundOne);
    int countModelBefore = nullModel.count();
    assertFalse(model.delete());
    int countModelAfter = nullModel.count();
    assertEquals(countModelAfter, countModelBefore);

    // delete
    model.initAllNull();
    model.setId(1);
    foundOne = model.find();
    assertNotNull(foundOne);
    countModelBefore = nullModel.count();
    assertTrue(model.delete());
    foundOne = model.find("id=1");
    assertNull(foundOne);
    countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore - 1);

    // already deleted
    model.initAllNull();
    model.setId(1);
    foundOne = model.find();
    assertNull(foundOne);
    countModelBefore = nullModel.count();
    assertFalse(model.delete());
    countModelAfter = nullModel.count();
    assertEquals(countModelAfter, countModelBefore);
  }

  @Test
  public void testBeforeDestroyFunction() {
    model.initAllNull();
    assertNull(model.getErrorsMap());
    assertTrue(model.beforeDestroy());
    assertNull(model.getErrorsMap());

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_BEFORE_DESTROY_ID - 1);
    assertTrue(model.beforeDestroy());
    assertNull(model.getErrorsMap());

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_BEFORE_DESTROY_ID);
    assertFalse(model.beforeDestroy());
    assertNotNull(model.getErrorsMap());
    assertEquals("beforeDestroyTest", model.getError("id"));

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_BEFORE_DESTROY_ID + 1);
    assertFalse(model.beforeDestroy());
    assertNotNull(model.getErrorsMap());
    assertEquals("beforeDestroyTest", model.getError("id"));
  }

  @Test
  public void testAfterDestroyFunction() {
    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.afterDestroy();
    assertNull(model.getErrorsMap());

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_AFTER_DESTROY_ID - 1);
    model.afterDestroy();
    assertNull(model.getErrorsMap());

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_AFTER_DESTROY_ID);
    model.afterDestroy();
    assertNotNull(model.getErrorsMap());
    assertEquals("afterDestroyTest", model.getError("id"));

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_AFTER_DESTROY_ID + 1);
    model.afterDestroy();
    assertNotNull(model.getErrorsMap());
    assertEquals("afterDestroyTest", model.getError("id"));
  }

  @Test
  public void testDestroyFunction() {
    populateTestTable();
    // no id
    model.initAllNull();
    assertFalse(model.destroy());

    // record does not exist
    model.initAllNull();
    model.setId(MAX_ID_LIMIT);
    foundOne = model.find();
    assertNull(foundOne);
    int countModelBefore = nullModel.count();
    assertFalse(model.destroy());
    int countModelAfter = nullModel.count();
    assertEquals(countModelAfter, countModelBefore);

    // destroy
    model.initAllNull();
    model.setId(1);
    foundOne = model.find();
    assertNotNull(foundOne);
    countModelBefore = nullModel.count();
    assertTrue(model.destroy());
    foundOne = model.find("id=1");
    assertNull(foundOne);
    countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore - 1);

    // already destroyed
    model.initAllNull();
    model.setId(1);
    foundOne = model.find();
    assertNull(foundOne);
    foundOne = model;
    countModelBefore = nullModel.count();
    assertFalse(foundOne.destroy());
    countModelAfter = nullModel.count();
    assertEquals(countModelAfter, countModelBefore);

    // frozen
    model.initAllNull();
    model.setId(2);
    foundOne = model.find();
    assertNotNull(foundOne);
    foundOne.setFrozen(true);
    assertTrue(foundOne.isFrozen());
    countModelBefore = nullModel.count();
    assertFalse(foundOne.destroy());
    countModelAfter = nullModel.count();
    assertEquals(countModelAfter, countModelBefore);
    foundOne.setFrozen(false);
    assertFalse(foundOne.isFrozen());
    countModelBefore = nullModel.count();
    assertTrue(foundOne.destroy());
    countModelAfter = nullModel.count();
    assertTrue(countModelAfter == countModelBefore - 1);

    // beforeDestroy
    model.initAllNull();
    model.setId(THRESHOLD_BEFORE_DESTROY_ID - 1);
    assertTrue(model.save());
    assertTrue(model.beforeDestroy());
    assertTrue(model.destroy());
    assertNull(model.getErrors());
    assertFalse(model.hasError("beforeDestroyTest"));

    model.initAllNull();
    model.setId(THRESHOLD_BEFORE_DESTROY_ID);
    assertTrue(model.save());
    assertFalse(model.beforeDestroy());
    assertFalse(model.destroy());
    assertNotNull(model.getErrors());
    assertTrue(model.hasError("beforeDestroyTest"));

    model.initAllNull();
    model.setId(THRESHOLD_BEFORE_DESTROY_ID + 1);
    assertTrue(model.save());
    assertFalse(model.beforeDestroy());
    assertFalse(model.destroy());
    assertNotNull(model.getErrors());
    assertTrue(model.hasError("beforeDestroyTest"));

    // afterDestroy
    model.initAllNull();
    model.setId(THRESHOLD_AFTER_DESTROY_ID - 1);
    assertTrue(model.save());
    assertNull(model.getErrors());
    assertTrue(model.destroy());
    assertNull(model.getErrors());

    model.initAllNull();
    model.setId(THRESHOLD_AFTER_DESTROY_ID);
    assertTrue(model.save());
    assertNull(model.getErrors());
    assertTrue(model.destroy());
    assertNotNull(model.getErrors());
    assertTrue(model.hasError("afterDestroyTest"));

    model.initAllNull();
    model.setId(THRESHOLD_AFTER_DESTROY_ID + 1);
    assertTrue(model.save());
    assertNull(model.getErrors());
    assertTrue(model.destroy());
    assertNotNull(model.getErrors());
    assertTrue(model.hasError("afterDestroyTest"));
  }

  @Test
  public void testAfterFindAndLoadFunction() {
    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.afterFindAndLoad();
    assertNull(model.getErrorsMap());

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_AFTER_FIND_AND_LOAD_ID - 1);
    model.afterFindAndLoad();
    assertNull(model.getErrorsMap());

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_AFTER_FIND_AND_LOAD_ID);
    model.afterFindAndLoad();
    assertNotNull(model.getErrorsMap());
    assertEquals("afterFindAndLoadTest", model.getError("id"));

    model.initAllNull();
    assertNull(model.getErrorsMap());
    model.setId(THRESHOLD_AFTER_FIND_AND_LOAD_ID + 1);
    model.afterFindAndLoad();
    assertNotNull(model.getErrorsMap());
    assertEquals("afterFindAndLoadTest", model.getError("id"));
  }

  @Test
  public void testReloadFunction() {
    populateTestTable();

    foundOne = model.find("id=5");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());

    foundOne.initAllNull();
    assertFalse(foundOne.reload());
    foundOne.setId(5);
    foundOne.setFrozen(true);
    assertFalse(foundOne.reload());
    foundOne.setFrozen(false);
    assertTrue(foundOne.reload());
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());
  }

  @Test
  public void testUpdateFieldFunction() throws SQLException {
    populateTestTable();
    FakeOrmModel model2 = new FakeOrmModel(db);
    Integer total = new Integer(123);

    // fake columns, non existent objects etc
    model.initAllNull();
    assertFalse(model.updateField("status", STATUS_EMPTY));
    assertFalse(model.updateField("fake_column", Boolean.FALSE));
    model.setId(99);
    assertFalse(model.updateField("status", STATUS_EMPTY));
    model.setId(1);
    assertFalse(model.updateField("status", null));

    model.setId(1);
    model.setFrozen(true);
    assertFalse(model.updateField("status", STATUS_EMPTY));
    model.setFrozen(false);
    assertTrue(model.updateField("status", STATUS_EMPTY));
    assertTrue(model.updateField("status", STATUS_NOT_EMPTY));

    // old map set to updated fields
    Object oldValue = model.getOldValue("status");
    assertTrue(oldValue.equals(STATUS_NOT_EMPTY));
    assertTrue(model.updateField("status", STATUS_EMPTY));
    assertFalse(oldValue.equals(model.getStatus()));
    assertFalse(model.fieldChanged("status"));

    // update each columns one by one and query
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateField("status", STATUS_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateField("total", total));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateField("content", CONTENT_NOT_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertFalse(model2.updateField("created_at", DATE_NOT_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = nullModel.find("id=1");
    assertFalse(model2.updateField("updated_at", DATE_NOT_EMPTY));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertFalse(DATE_NOT_EMPTY.equals(foundOne.getUpdatedAt()));
  }


  @Test
  public void testUpdateFieldsFunction() throws SQLException {
    populateTestTable();
    FakeOrmModel model2 = new FakeOrmModel(db);
    HashMap<String, Object> updates = initHashMap("status", STATUS_EMPTY);

    // fake columns, non existent objects etc
    model.initAllNull();
    assertFalse(model.updateFields(updates));
    model.setId(1);
    assertTrue(model.updateFields(updates));
    assertFalse(model.updateFields(initHashMap("fake_column", Boolean.FALSE)));
    model.setId(99);
    assertFalse(model.updateFields(updates));

    updates.put("status", STATUS_NOT_EMPTY);
    model.setId(1);
    model.setFrozen(true);
    assertFalse(model.updateFields(updates));
    model.setFrozen(false);
    assertTrue(model.updateFields(updates));

    // old map set to updated fields
    Object oldValue = model.getOldValue("status");
    updates.put("status", STATUS_EMPTY);
    assertTrue(model.updateFields(updates));
    assertFalse(oldValue.equals(model.getStatus()));
    assertFalse(model.fieldChanged("status"));

    // update all columns and query
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_EMPTY, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    updates.clear();
    updates.put("id", "99");
    updates.put("status", STATUS_NOT_EMPTY);
    updates.put("created_at", DATE_NOT_EMPTY);
    updates.put("updated_at", DATE_NOT_EMPTY);
    updates.put("total", TOTAL_NOT_EMPTY);

    model2 = nullModel.find("id=1");
    assertTrue(model2.updateFields(updates));
    foundOne = nullModel.find("id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(STATUS_NOT_EMPTY, foundOne.getStatus());
    assertEquals(TOTAL_NOT_EMPTY, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());
    assertFalse(DATE_NOT_EMPTY.equals(foundOne.getUpdatedAt()));
  }
}
