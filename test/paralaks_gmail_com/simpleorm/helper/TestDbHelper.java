package paralaks_gmail_com.simpleorm.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;

import com.mchange.v2.c3p0.DataSources;

import paralaks_gmail_com.simpleorm.model.FakeOrmModel;

public class TestDbHelper extends FakeOrmModelTestHelper {
  @Test
  public void testCamelToSnakeFunction() {
    assertEquals(DbHelper.camelToSnake(null), "");
    assertEquals(DbHelper.camelToSnake(""), "");
    assertEquals(DbHelper.camelToSnake(" "), " ");
    assertEquals(DbHelper.camelToSnake("  "), "  ");

    // some marry had a little lamb variations
    assertEquals(DbHelper.camelToSnake("marryhadalittlelamb"), "marryhadalittlelamb");
    assertEquals(DbHelper.camelToSnake(" marryhadalittlelamb"), " marryhadalittlelamb");
    assertEquals(DbHelper.camelToSnake("marryhadalittlelamb "), "marryhadalittlelamb ");
    assertEquals(DbHelper.camelToSnake(" marryhadalittlelamb "), " marryhadalittlelamb ");
    assertEquals(DbHelper.camelToSnake("marryhada littlelamb"), "marryhada littlelamb");

    assertEquals(DbHelper.camelToSnake("Marryhadalittlelamb"), "marryhadalittlelamb");
    assertEquals(DbHelper.camelToSnake(" Marryhadalittlelamb"), " marryhadalittlelamb");
    assertEquals(DbHelper.camelToSnake("Marryhadalittlelamb "), "marryhadalittlelamb ");
    assertEquals(DbHelper.camelToSnake(" Marryhadalittlelamb "), " marryhadalittlelamb ");
    assertEquals(DbHelper.camelToSnake("Marryhada littlelamb"), "marryhada littlelamb");

    assertEquals(DbHelper.camelToSnake("marryhadalittleLamb"), "marryhadalittle_lamb");
    assertEquals(DbHelper.camelToSnake("marryHadalittlelamb"), "marry_hadalittlelamb");
    assertEquals(DbHelper.camelToSnake("marryHadAlittlelamb"), "marry_had_alittlelamb");
    assertEquals(DbHelper.camelToSnake("marryHadALittlelamb"), "marry_had_alittlelamb");
    assertEquals(DbHelper.camelToSnake("marryHadA_LittleLamb"), "marry_had_a_little_lamb");

    assertEquals(DbHelper.camelToSnake("_marryhadalittlelamb"), "_marryhadalittlelamb");
    assertEquals(DbHelper.camelToSnake("marryhadalittlelamb_"), "marryhadalittlelamb_");
    assertEquals(DbHelper.camelToSnake("_marryhadalittlelamb_"), "_marryhadalittlelamb_");
    assertEquals(DbHelper.camelToSnake("_marryhada_littlelamb_"), "_marryhada_littlelamb_");

    assertEquals(DbHelper.camelToSnake("marry.had,a-l!ttl3 lamb"), "marry.had,a-l!ttl3 lamb");

    // real class/field names
    assertEquals(DbHelper.camelToSnake("status"), "status");
    assertEquals(DbHelper.camelToSnake("dynDnsEnabled"), "dyn_dns_enabled");
    assertEquals(DbHelper.camelToSnake("supportedInAllCountries"), "supported_in_all_countries");
    assertEquals(DbHelper.camelToSnake("TransactionLogMessageDestination"), "transaction_log_message_destination");
  }

  @Test
  public void testSnakeToCamelFunction() {
    assertEquals(DbHelper.snakeToCamel(null), "");
    assertEquals(DbHelper.snakeToCamel(""), "");
    assertEquals(DbHelper.snakeToCamel(" "), " ");
    assertEquals(DbHelper.snakeToCamel("  "), "  ");

    // some marry had a little lamb variations
    assertEquals(DbHelper.snakeToCamel("marryhadalittlelamb"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel(" marryhadalittlelamb"), " marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel("marryhadalittlelamb "), "Marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamel(" marryhadalittlelamb "), " marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamel("marryhada littlelamb"), "Marryhada littlelamb");

    assertEquals(DbHelper.snakeToCamel("Marryhadalittlelamb"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel(" Marryhadalittlelamb"), " Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel("Marryhadalittlelamb "), "Marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamel(" Marryhadalittlelamb "), " Marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamel("Marryhada littlelamb"), "Marryhada littlelamb");

    assertEquals(DbHelper.snakeToCamel("marryhadalittle_lamb"), "MarryhadalittleLamb");
    assertEquals(DbHelper.snakeToCamel("marry_hadalittlelamb"), "MarryHadalittlelamb");
    assertEquals(DbHelper.snakeToCamel("marry_had_alittlelamb"), "MarryHadAlittlelamb");
    assertEquals(DbHelper.snakeToCamel("marry_had_a_littlelamb"), "MarryHadALittlelamb");
    assertEquals(DbHelper.snakeToCamel("marry_had_a_little_lamb"), "MarryHadALittleLamb");

    assertEquals(DbHelper.snakeToCamel("_marryhadalittlelamb"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel("marryhadalittlelamb_"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel("_marryhadalittlelamb_"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamel("_marryhada_littlelamb_"), "MarryhadaLittlelamb");

    assertEquals(DbHelper.snakeToCamel("marry.had,a-l!ttl3 lamb"), "Marry.had,a-l!ttl3 lamb");

    // real class/field names
    assertEquals(DbHelper.snakeToCamel("status"), "Status");
    assertEquals(DbHelper.snakeToCamel("dyn_dns_enabled"), "DynDnsEnabled");
    assertEquals(DbHelper.snakeToCamel("supported_in_all_countries"), "SupportedInAllCountries");
    assertEquals(DbHelper.snakeToCamel("transaction_log_message_destination"), "TransactionLogMessageDestination");
  }

  @Test
  public void testSnakeToCamelLowerFunction() {
    assertEquals(DbHelper.snakeToCamel(null), "");
    assertEquals(DbHelper.snakeToCamelLower(""), "");
    assertEquals(DbHelper.snakeToCamelLower(" "), " ");
    assertEquals(DbHelper.snakeToCamelLower("  "), "  ");

    // some marry had a little lamb variations
    assertEquals(DbHelper.snakeToCamelLower("marryhadalittlelamb"), "marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower(" marryhadalittlelamb"), " marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("marryhadalittlelamb "), "marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamelLower(" marryhadalittlelamb "), " marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamelLower("marryhada littlelamb"), "marryhada littlelamb");

    assertEquals(DbHelper.snakeToCamelLower("Marryhadalittlelamb"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower(" Marryhadalittlelamb"), " Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("Marryhadalittlelamb "), "Marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamelLower(" Marryhadalittlelamb "), " Marryhadalittlelamb ");
    assertEquals(DbHelper.snakeToCamelLower("Marryhada littlelamb"), "Marryhada littlelamb");

    assertEquals(DbHelper.snakeToCamelLower("marryhadalittle_lamb"), "marryhadalittleLamb");
    assertEquals(DbHelper.snakeToCamelLower("marry_hadalittlelamb"), "marryHadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("marry_had_alittlelamb"), "marryHadAlittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("marry_had_a_littlelamb"), "marryHadALittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("marry_had_a_little_lamb"), "marryHadALittleLamb");

    assertEquals(DbHelper.snakeToCamelLower("_marryhadalittlelamb"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("marryhadalittlelamb_"), "marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("_marryhadalittlelamb_"), "Marryhadalittlelamb");
    assertEquals(DbHelper.snakeToCamelLower("_marryhada_littlelamb_"), "MarryhadaLittlelamb");

    assertEquals(DbHelper.snakeToCamelLower("marry.had,a-l!ttl3 lamb"), "marry.had,a-l!ttl3 lamb");

    // real class/field names
    assertEquals(DbHelper.snakeToCamelLower("status"), "status");
    assertEquals(DbHelper.snakeToCamelLower("dyn_dns_enabled"), "dynDnsEnabled");
    assertEquals(DbHelper.snakeToCamelLower("supported_in_all_countries"), "supportedInAllCountries");
    assertEquals(DbHelper.snakeToCamelLower("transaction_log_message_destination"),
        "transactionLogMessageDestination");
  }

  @Test
  public void testTableNameFunction() {
    // for all implemented DB models
    assertEquals(DbHelper.tableName(FakeOrmModel.class), "fake_orm_models");
  }

  @Test
  public void testFieldsForFunction_ObjectFieldlist() {
    HashMap<String, Object> fieldsFor = null;

    fieldsFor = db.fieldsFor(null, (String[]) null);
    assertEquals(0, fieldsFor.size());

    model.initAllNull();
    fieldsFor = db.fieldsFor(model, (String[]) null);
    assertEquals(0, fieldsFor.size());

    model.initAllEmpty();
    fieldsFor = db.fieldsFor(model, "");
    assertEquals(0, fieldsFor.size());

    model.initAllEmpty();
    fieldsFor = db.fieldsFor(model, "id", "status", "total");
    assertEquals(2, fieldsFor.size());
    assertEquals(ID_EMPTY, fieldsFor.get("id"));
    assertEquals(STATUS_EMPTY, fieldsFor.get("status"));
    assertEquals(TOTAL_EMPTY, fieldsFor.get("total"));

    model.initAllNotEmpty();
    fieldsFor = db.fieldsFor(model, "id", "status", "total", "content", "created_at", "updated_at");
    assertEquals(6, fieldsFor.size());
    assertEquals(ID_NOT_EMPTY, fieldsFor.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fieldsFor.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fieldsFor.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fieldsFor.get("content"));
    assertEquals(DATE_NOT_EMPTY, fieldsFor.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fieldsFor.get("updated_at"));

    model.initAllNull();
    fieldsFor = db.fieldsFor(model, "id", "content", "created_at");
    assertEquals(0, fieldsFor.size());
    model.setId(ID_NOT_EMPTY);
    model.setContent(CONTENT_NOT_EMPTY);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fieldsFor = db.fieldsFor(model, "id", "content", "created_at");
    assertEquals(3, fieldsFor.size());
    assertEquals(ID_NOT_EMPTY, fieldsFor.get("id"));
    assertNull(fieldsFor.get("status"));
    assertNull(fieldsFor.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fieldsFor.get("content"));
    assertEquals(DATE_NOT_EMPTY, fieldsFor.get("created_at"));
    assertNull(fieldsFor.get("updated_at"));
    model.setId(null);
    model.setContent(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fieldsFor = db.fieldsFor(model, "id", "content", "created_at");
    assertEquals(1, fieldsFor.size());
    assertNull(fieldsFor.get("id"));
    assertNull(fieldsFor.get("status"));
    assertNull(fieldsFor.get("total"));
    assertNull(fieldsFor.get("content"));
    assertEquals(DATE_NOT_EMPTY, fieldsFor.get("created_at"));
    assertNull(fieldsFor.get("updated_at"));
  }

  @Test
  public void testFieldsFunction_Object() {
    HashMap<String, Object> fields = null;

    fields = db.fields(null);
    assertEquals(0, fields.size());

    model.initAllNull();
    fields = db.fields(model);
    assertEquals(0, fields.size());

    model.initAllEmpty();
    fields = db.fields(model);
    assertEquals(5, fields.size());
    assertEquals(ID_EMPTY, fields.get("id"));
    assertEquals(STATUS_EMPTY, fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertEquals(DATE_EMPTY, fields.get("updated_at"));

    model.initAllNotEmpty();
    fields = db.fields(model);
    assertEquals(6, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));

    model.initAllNull();
    fields = db.fields(model);
    assertEquals(0, fields.size());
    model.setId(9);
    model.setContent(CONTENT_NOT_EMPTY);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = db.fields(model);
    assertEquals(3, fields.size());
    assertEquals(new Integer(9), fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));
    model.setId(null);
    model.setContent(null);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = db.fields(model);
    assertEquals(1, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertNull(fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));
  }

  @Test
  public void testFieldsFunction_ObjectExcludedfieldslist() {
    HashMap<String, Object> fields = null;

    fields = db.fields(null, (List<String>) null);
    assertEquals(0, fields.size());


    model.initAllNull();
    fields = db.fields(model, (List<String>) null);
    assertEquals(0, fields.size());

    model.initAllNull();
    fields = db.fields(model, Arrays.asList("updated_at"));
    assertEquals(0, fields.size());


    model.initAllEmpty();
    fields = db.fields(model, (List<String>) null);
    assertEquals(5, fields.size());
    assertEquals(ID_EMPTY, fields.get("id"));
    assertEquals(STATUS_EMPTY, fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertEquals(DATE_EMPTY, fields.get("updated_at"));

    model.initAllEmpty();
    fields = db.fields(model, Arrays.asList("status", "updated_at"));
    assertEquals(3, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));


    model.initAllNotEmpty();
    fields = db.fields(model, (List<String>) null);
    assertEquals(6, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));

    model.initAllNotEmpty();
    fields = db.fields(model, Arrays.asList("status", "content"));
    assertEquals(4, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertNull(fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));


    model.initAllNull();
    fields = db.fields(model, (List<String>) null);
    assertEquals(0, fields.size());
    model.setId(ID_NOT_EMPTY);
    model.setContent(CONTENT_NOT_EMPTY);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = db.fields(model, (List<String>) null);
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
    fields = db.fields(model, (List<String>) null);
    assertEquals(1, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertNull(fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));
  }

  @Test
  public void testFieldsFunction_ObjectExcludedfieldsarray() {
    HashMap<String, Object> fields = null;

    fields = db.fields(null, (String[]) null);
    assertEquals(0, fields.size());


    model.initAllNull();
    fields = db.fields(model, (String[]) null);
    assertEquals(0, fields.size());

    model.initAllNull();
    fields = db.fields(model, "updated_at");
    assertEquals(0, fields.size());


    model.initAllEmpty();
    fields = db.fields(model, (String[]) null);
    assertEquals(5, fields.size());
    assertNull(fields.get("id"));
    assertEquals(STATUS_EMPTY, fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertEquals(DATE_EMPTY, fields.get("updated_at"));

    model.initAllEmpty();
    fields = db.fields(model, "status", "updated_at");
    assertEquals(3, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertEquals(TOTAL_EMPTY, fields.get("total"));
    assertEquals(CONTENT_EMPTY, fields.get("content"));
    assertEquals(DATE_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));


    model.initAllNotEmpty();
    fields = db.fields(model, (String[]) null);
    assertEquals(6, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertEquals(STATUS_NOT_EMPTY, fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertEquals(CONTENT_NOT_EMPTY, fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));

    model.initAllNotEmpty();
    fields = db.fields(model, "status", "content");
    assertEquals(4, fields.size());
    assertEquals(ID_NOT_EMPTY, fields.get("id"));
    assertNull(fields.get("status"));
    assertEquals(TOTAL_NOT_EMPTY, fields.get("total"));
    assertNull(fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertEquals(DATE_NOT_EMPTY, fields.get("updated_at"));


    model.initAllNull();
    fields = db.fields(model, (String[]) null);
    assertEquals(0, fields.size());
    model.setId(ID_NOT_EMPTY);
    model.setContent(CONTENT_NOT_EMPTY);
    model.setCreatedAt(DATE_NOT_EMPTY);
    fields = db.fields(model, (String[]) null);
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
    fields = db.fields(model, (String[]) null);
    assertEquals(1, fields.size());
    assertNull(fields.get("id"));
    assertNull(fields.get("status"));
    assertNull(fields.get("total"));
    assertNull(fields.get("content"));
    assertEquals(DATE_NOT_EMPTY, fields.get("created_at"));
    assertNull(fields.get("updated_at"));
  }

  @Test
  public void testPopulateTestTableAndVerifyRecordsAreSaved() {
    Statement stInsert = null;
    Statement stSelect = null;
    ResultSet rSetSelect = null;
    try {
      stInsert = connection.createStatement();
      stSelect = connection.createStatement();

      for (int i = 0; i < TEST_DATA_QUERIES.length; i++) {
        int id = i + 1;
        // total pre-save
        assertEquals(i, db.count(FakeOrmModel.class, null));

        // save new
        stInsert.execute(TEST_DATA_QUERIES[i]);
        assertEquals(id, db.count(FakeOrmModel.class, null));

        // total post-save
        assertEquals(1, db.count(FakeOrmModel.class, "id=" + id));

        // verification
        rSetSelect = stSelect.executeQuery("SELECT * FROM " + TABLE_NAME_FAKE_MODEL + " WHERE id = " + id);
        assertTrue(rSetSelect.next());
        assertEquals(id, rSetSelect.getInt("id"));
        rSetSelect.close();
      }
      stInsert.close();
    } catch (Exception e) {
      try {
        if (stInsert != null && !stInsert.isClosed())
          stInsert.close();
      } catch (Exception e2) {
      }
      assertNull(e.getMessage(), e);
    }
  }

  @Test
  public void testCountFunction_Object() {
    populateTestTable();

    // more total queries with different combinations
    assertEquals(0, db.count((FakeOrmModel) null));

    model.initAllNull();
    assertEquals(5, db.count(model));

    model.initAllNull();
    model.setId(-1);
    assertEquals(0, db.count(model));

    model.initAllNull();
    model.setContent("teenage mutant ninja turtles");
    assertEquals(0, db.count(model));

    model.initAllNull();
    model.setStatus(true);
    assertEquals(2, db.count(model));

    model.initAllNull();
    model.setStatus(false);
    assertEquals(2, db.count(model));

    model.initAllNull();
    model.setTotal(0);
    assertEquals(1, db.count(model));

    model.initAllNull();
    model.setTotal(1);
    assertEquals(1, db.count(model));

    model.initAllNull();
    model.setTotal(999);
    assertEquals(0, db.count(model));

    model.initAllNull();
    model.setTotal(1);
    model.setStatus(true);
    assertEquals(1, db.count(model));

    model.initAllNull();
    model.setTotal(1);
    model.setStatus(false);
    assertEquals(0, db.count(model));

    model.initAllNull();
    model.setTotal(2);
    assertEquals(1, db.count(model));

    model.initAllNull();
    model.setContent(null);
    assertEquals(5, db.count(model));

    model.initAllNull();
    model.setContent("total zero");
    assertEquals(0, db.count(model));

    model.initAllNull();
    model.setStatus(false);
    model.setContent(null);
    assertEquals(2, db.count(model));

    model.initAllNull();
    model.setStatus(false);
    model.setContent(CONTENT_NOT_EMPTY);
    assertEquals(0, db.count(model));

    model.initAllNull();
    model.setStatus(true);
    model.setContent(CONTENT_NOT_EMPTY);
    assertEquals(2, db.count(model));
  }

  @Test
  public void testCountFunction_Class() {
    populateTestTable();

    // more total queries with different combinations
    assertEquals(5, db.count(FakeOrmModel.class));
  }

  private void sharedTestsForTestCountFunction_ClassWhere() {
    assertEquals(5, db.count(FakeOrmModel.class, null));
    assertEquals(5, db.count(FakeOrmModel.class, ""));

    assertEquals(0, db.count(FakeOrmModel.class, "id IS NULL"));
    assertEquals(1, db.count(FakeOrmModel.class, "id=1"));
    assertEquals(0, db.count(FakeOrmModel.class, "id=999"));

    assertEquals(1, db.count(FakeOrmModel.class, "status IS NULL"));
    assertEquals(2, db.count(FakeOrmModel.class, "status=0"));
    assertEquals(2, db.count(FakeOrmModel.class, "status=1"));

    assertEquals(2, db.count(FakeOrmModel.class, "total IS NULL"));
    assertEquals(1, db.count(FakeOrmModel.class, "total=0"));
    assertEquals(1, db.count(FakeOrmModel.class, "total=1"));
    assertEquals(1, db.count(FakeOrmModel.class, "total=2"));
    assertEquals(0, db.count(FakeOrmModel.class, "total=3"));
    assertEquals(3, db.count(FakeOrmModel.class, "total>=0"));
    assertEquals(2, db.count(FakeOrmModel.class, "total>=1 AND total<=2"));
    assertEquals(0, db.count(FakeOrmModel.class, "total>=999"));

    assertEquals(1, db.count(FakeOrmModel.class, "status IS NULL AND total IS NULL"));
    assertEquals(1, db.count(FakeOrmModel.class, "status=0 AND total=0"));
    assertEquals(1, db.count(FakeOrmModel.class, "status=1 AND total=1"));
    assertEquals(0, db.count(FakeOrmModel.class, "status=0 AND total!=0"));

    assertEquals(3, db.count(FakeOrmModel.class, "content IS NULL"));
    assertEquals(2, db.count(FakeOrmModel.class, "content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(0, db.count(FakeOrmModel.class, "status=0 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(2, db.count(FakeOrmModel.class, "status=1 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(0, db.count(FakeOrmModel.class, "total=0 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(1, db.count(FakeOrmModel.class, "total=1 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(1, db.count(FakeOrmModel.class, "total=2 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(2, db.count(FakeOrmModel.class, "total>=0 AND content='" + CONTENT_NOT_EMPTY + "'"));
    assertEquals(1, db.count(FakeOrmModel.class, "status=1 AND total=2 AND content='" + CONTENT_NOT_EMPTY + "'"));
  }

  @Test
  public void testCountFunction_ClassWhere() {
    populateTestTable();
    sharedTestsForTestCountFunction_ClassWhere();
  }

  @Test
  public void testFindFunction_Object() {
    populateTestTable();

    foundOne = db.find(null);
    assertNull(foundOne);

    model.initAllNull();
    foundOne = db.find(model);
    assertNull(foundOne);

    model.initAllNull();
    model.setId(999);
    foundOne = db.find(model);
    assertNull(foundOne);

    model.initAllNull();
    model.setId(1); // careful with id=1; saved by query so all values are null
    foundOne = db.find(model);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(3);
    foundOne = db.find(model);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(5);
    foundOne = db.find(model);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    model.initAllNull();
    model.setStatus(false);
    model.setTotal(1);
    foundOne = db.find(model);
    assertNull(foundOne);

    model.initAllNull();
    model.setStatus(true);
    model.setTotal(2);
    foundOne = db.find(model);
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
    foundOne = db.find(model);
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());
  }

  @Test
  public void testFindAllFunction_Object() {
    populateTestTable();

    foundList = db.findAll(null);
    assertEquals(0, foundList.size());

    model.initAllNull();
    foundList = db.findAll(model);
    assertEquals(0, foundList.size());

    model.initAllNull();
    model.setId(1); // careful with id=1; saved by query so all values are null
    foundList = db.findAll(model);
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
    foundList = db.findAll(model);
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(2), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(3);
    foundList = db.findAll(model);
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model.initAllNull();
    model.setId(5);
    foundList = db.findAll(model);
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
    foundList = db.findAll(model);
    assertEquals(2, foundList.size());
    assertEquals(new Integer(2), foundList.get(0).getId());
    assertEquals(new Integer(3), foundList.get(1).getId());

    model.initAllNull();
    model.setStatus(true);
    foundList = db.findAll(model);
    assertEquals(2, foundList.size());
    assertEquals(new Integer(4), foundList.get(0).getId());
    assertEquals(new Integer(5), foundList.get(1).getId());

    model.initAllNull();
    model.setTotal(0);
    foundList = db.findAll(model);
    assertEquals(1, foundList.size());
    assertEquals(new Integer(3), foundList.get(0).getId());

    model.initAllNull();
    model.setTotal(1);
    foundList = db.findAll(model);
    assertEquals(1, foundList.size());
    assertEquals(new Integer(4), foundList.get(0).getId());

    model.initAllNull();
    model.setTotal(2);
    foundList = db.findAll(model);
    assertEquals(1, foundList.size());
    assertEquals(new Integer(5), foundList.get(0).getId());
  }

  private void sharedTestsForTestFindAllFunction_ClassWhereOrderbyLimit() {
    foundList = db.findAll(FakeOrmModel.class, null, "id DESC", null, null);
    assertEquals(5, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id ASC", null, null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(5), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", null, null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 0, null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", null, 100);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 0, 100);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 0, 0);
    assertEquals(3, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 0, 1);
    assertEquals(1, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 0, 2);
    assertEquals(2, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 0, 3);
    assertEquals(3, foundList.size());


    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 1, 3);
    assertEquals(2, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 2, 3);
    assertEquals(1, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC", 3, 3);
    assertEquals(0, foundList.size());
  }

  @Test
  public void testFindAllFunction_ClassWhereOrderbyLimit() {
    populateTestTable();
    sharedTestsForTestFindAllFunction_ClassWhereOrderbyLimit();
  }

  // No need to test individual object which is covered by testFindAllFunctionAcceptingClass_Where
  @Test
  public void testFindAllFunction_ClassWhereOrderby() {
    populateTestTable();

    foundList = db.findAll(FakeOrmModel.class, null, null);
    assertEquals(5, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, null, "id DESC");
    assertEquals(5, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(5), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id ASC");
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(5), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL", "id DESC");
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NULL", "id ASC");
    assertEquals(2, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(1), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(2), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total IS NULL", "id DESC");
    assertEquals(2, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(2), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(1), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total>='" + 0 + "'", null);
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(5), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total>='" + 0 + "'", "id ASC");
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(5), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total>='" + 0 + "'", "id DESC");
    assertEquals(3, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(5), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(4), foundOne.getId());
    foundOne = foundList.get(2);
    assertEquals(new Integer(3), foundOne.getId());
  }

  private void sharedTestsForTestFindAllFunction_ClassWhere() {
    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NOT NULL");
    assertEquals(3, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total IS NULL");
    assertEquals(2, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "total='" + 0 + "'");
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundList = db.findAll(FakeOrmModel.class, "total='" + 1 + "'");
    assertEquals(1, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundList = db.findAll(FakeOrmModel.class, "status='" + 1 + "'");
    assertEquals(2, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());
    foundOne = foundList.get(1);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());

    foundList = db.findAll(FakeOrmModel.class, "status='" + 0 + "'");
    assertEquals(2, foundList.size());
    foundOne = foundList.get(0);
    assertEquals(new Integer(2), foundOne.getId());
    foundOne = foundList.get(1);
    assertEquals(new Integer(3), foundOne.getId());

    foundList = db.findAll(FakeOrmModel.class, "total>='" + 0 + "'");
    assertEquals(3, foundList.size());
    foundList = db.findAll(FakeOrmModel.class, "total>='" + 1 + "'");
    assertEquals(2, foundList.size());
    foundList = db.findAll(FakeOrmModel.class, "total>='" + 2 + "'");
    assertEquals(1, foundList.size());

    foundList = db.findAll(FakeOrmModel.class, "status=0 AND total>='" + 0 + "'");
    assertEquals(1, foundList.size());
    foundList = db.findAll(FakeOrmModel.class, "status=0 AND total>='" + 1 + "'");
    assertEquals(0, foundList.size());
  }

  @Test
  public void testFindAllFunction_ClassWhere() {
    populateTestTable();
    sharedTestsForTestFindAllFunction_ClassWhere();
  }

  @Test
  public void testSaveFunction() {
    assertEquals(0, db.count((FakeOrmModel) null));

    int total = 0;
    // make sure empty table
    assertEquals(total, db.count(model));

    // all fields null; still save
    model.initAllNull();
    db.save(model);
    total++;
    assertEquals(total, db.count(FakeOrmModel.class));

    // fields with empty/0 values
    model.initAllEmpty();
    db.save(model);
    total++;
    assertEquals(total, db.count(FakeOrmModel.class));

    // fields with non-0, non-blank values
    // test oldMap updates as well
    model.initAllNotEmpty();
    model.setContent("aaa");
    assertTrue(model.fieldChanged("content"));
    model.setContent(CONTENT_NOT_EMPTY);
    assertFalse(model.fieldChanged("content"));
    db.save(model);
    total++;
    assertEquals(total, db.count(FakeOrmModel.class));
    assertEquals(model.getOldValue("content"), CONTENT_NOT_EMPTY);

    // first record there?
    model.initAllNull();
    model.setId(1);
    assertEquals(1, db.count(model));

    // save another record with a non-empty field and see if it is there
    model.initAllEmpty();
    model.setTotal(Integer.MAX_VALUE);
    db.save(model);
    total++;
    assertEquals(total, db.count(FakeOrmModel.class));
    model.initAllNull();
    model.setTotal(Integer.MAX_VALUE);
    assertEquals(1, db.count(model));
    assertEquals(total, db.count(FakeOrmModel.class));

    // save with same values again
    model.initAllEmpty();
    model.setTotal(Integer.MAX_VALUE);
    db.save(model);
    total++;
    assertEquals(total, db.count(FakeOrmModel.class));
    model.initAllNull();
    model.setTotal(Integer.MAX_VALUE);
    assertEquals(2, db.count(model)); // two objects!!!
    assertEquals(total, db.count(FakeOrmModel.class));

    // save another record with two non-empty fields and see if it is there
    // test oldMap updates as well
    model.initAllEmpty();
    model.setTotal(Integer.MAX_VALUE - 1);
    model.setContent("hello world");
    assertTrue(model.fieldChanged("total"));
    assertTrue(model.fieldChanged("content"));
    db.save(model);
    assertFalse(model.fieldChanged("total"));
    assertFalse(model.fieldChanged("content"));
    assertEquals(model.getOldValue("total"), model.getTotal());
    assertEquals(model.getOldValue("content"), model.getContent());
    total++;
    assertEquals(total, db.count(FakeOrmModel.class));
    model.initAllNull();
    model.setTotal(Integer.MAX_VALUE - 1);
    model.setContent("hello world");
    assertEquals(1, db.count(model));
    assertEquals(total, db.count(FakeOrmModel.class));
  }

  private void sharedTestsForTestFindFunction_ClassWhere() {
    foundOne = db.find(FakeOrmModel.class, null);
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total IS NULL");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total IS NOT NULL");
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total='" + 0 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total='" + 1 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total='" + 2 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total='" + 999 + "'");
    assertNull(foundOne);


    foundOne = db.find(FakeOrmModel.class, "status='" + 1 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(4), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(1), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());


    foundOne = db.find(FakeOrmModel.class, "status='" + 0 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(2), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 0 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 1 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(4), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 2 + "'");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "status=0 AND total>='" + 0 + "'");
    assertNotNull(foundOne);

    foundOne = db.find(FakeOrmModel.class, "status=0 AND total>='" + 1 + "'");
    assertNull(foundOne);
  }

  @Test
  public void testFindFunction_ClassWhere() {
    populateTestTable();
    sharedTestsForTestFindFunction_ClassWhere();
  }

  @Test
  public void testFindFunction_ClassWhereOrderby() {
    populateTestTable();

    foundOne = db.find(FakeOrmModel.class, null, null);
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, null, "id ASC");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, null, "id DESC");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    foundOne = db.find(FakeOrmModel.class, "total IS NULL", null);
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total IS NULL", "id ASC");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total IS NULL", "id DESC");
    assertNotNull(foundOne);
    assertEquals(new Integer(2), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());


    foundOne = db.find(FakeOrmModel.class, "total IS NOT NULL", null);
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total IS NOT NULL", "id ASC");
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(0), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    foundOne = db.find(FakeOrmModel.class, "total IS NOT NULL", "id DESC");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());


    foundOne = db.find(FakeOrmModel.class, "total>='" + 0 + "'", null);
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 0 + "'", "id ASC");
    assertNotNull(foundOne);
    assertEquals(new Integer(3), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 0 + "'", "id DESC");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());


    foundOne = db.find(FakeOrmModel.class, "total>='" + 2 + "'", null);
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 2 + "'", "id ASC");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "total>='" + 2 + "'", "id DESC");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());


    foundOne = db.find(FakeOrmModel.class, "created_at IS NULL", null);
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "created_at IS NULL", "id ASC");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());

    foundOne = db.find(FakeOrmModel.class, "created_at IS NULL", "id DESC");
    assertNotNull(foundOne);
    assertEquals(new Integer(4), foundOne.getId());
  }

  @Test
  public void testImportRowsFunction() {
    // empty table?
    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(0, foundList.size());
    assertFalse(db.importRows((Class<FakeOrmModel>) null, null, null, null));
    assertFalse(db.importRows(FakeOrmModel.class, null, null, null));
    assertFalse(db.importRows(FakeOrmModel.class, new String[0], null, null));
    assertFalse(db.importRows(FakeOrmModel.class, new String[0], new ArrayList<>(), null));
    assertFalse(db.importRows(FakeOrmModel.class, new String[0], IMPORT_ROWS, null));
    assertFalse(db.importRows(FakeOrmModel.class, IMPORT_COLUMNS, new ArrayList<>(), null));

    // populated?
    assertTrue(db.importRows(FakeOrmModel.class, IMPORT_COLUMNS, IMPORT_ROWS, null));
    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());

    // repeat some earlier tests
    sharedTestsForTestFindFunction_ClassWhere();
    sharedTestsForTestFindAllFunction_ClassWhere();
    sharedTestsForTestFindAllFunction_ClassWhereOrderbyLimit();
    sharedTestsForTestCountFunction_ClassWhere();

    // duplicate updates
    String[] importColumns = {"id", "status", "total", "content", "created_at", "updated_at"};
    String[] updateColumns = new String[]{"content"};
    List<Object[]> duplicateRows = new ArrayList<>(Arrays.asList(new String[][]{
        {"1", null, null, CONTENT_NOT_EMPTY, null, null}, {"2", "0", null, CONTENT_NOT_EMPTY, null, null},
        {"3", "0", "0", CONTENT_NOT_EMPTY, null, null}, {"4", "1", "1", CONTENT_NOT_EMPTY, null, null},
        {"5", "1", "2", CONTENT_NOT_EMPTY, DATE_NOT_EMPTY_STR, DATE_NOT_EMPTY_STR}}));

    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertNull(foundList.get(0).getContent());
    assertNull(foundList.get(1).getContent());
    assertNull(foundList.get(2).getContent());
    assertTrue(foundList.get(3).getContent().equals(CONTENT_NOT_EMPTY));
    assertTrue(foundList.get(4).getContent().equals(CONTENT_NOT_EMPTY));

    assertTrue(db.importRows(FakeOrmModel.class, importColumns, duplicateRows, updateColumns));
    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());

    assertTrue(foundList.get(0).getContent().equals(CONTENT_NOT_EMPTY));
    assertTrue(foundList.get(1).getContent().equals(CONTENT_NOT_EMPTY));
    assertTrue(foundList.get(2).getContent().equals(CONTENT_NOT_EMPTY));
    assertTrue(foundList.get(3).getContent().equals(CONTENT_NOT_EMPTY));
    assertTrue(foundList.get(4).getContent().equals(CONTENT_NOT_EMPTY));
  }

  @Test
  public void testUpdateColumFunction_ObjectColumnValue() throws SQLException {
    populateTestTable();

    Integer total = new Integer(123);

    // fake columns, non existent objects etc
    assertFalse(db.updateColumn(null, null, null));
    assertFalse(db.updateColumn(null, "status", Boolean.FALSE));
    assertFalse(db.updateColumn(model, null, Boolean.FALSE));
    assertFalse(db.updateColumn(model, "status", null));
    assertFalse(db.updateColumn(null, null, Boolean.FALSE));
    assertFalse(db.updateColumn(null, "status", null));
    assertFalse(db.updateColumn(model, null, null));
    assertFalse(db.updateColumn(model, "status", Boolean.FALSE));
    model.initAllNull(); // no id to match
    assertFalse(db.updateColumn(model, "status", Boolean.FALSE));
    model.setId(1);
    assertFalse(db.updateColumn(model, "fake_column", Boolean.FALSE));
    assertTrue(db.updateColumn(model, "status", Boolean.FALSE));
    assertTrue(db.updateColumn(model, "status", null));

    // update each columns one by one and query
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(model2, "status", Boolean.FALSE));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(model2, "total", total));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(model2, "content", CONTENT_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertFalse(db.updateColumn(model2, "created_at", DATE_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(model2, "updated_at", DATE_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());
  }

  @Test
  public void testUpdateColumFunction_ClassIdColumnValue() throws SQLException {
    populateTestTable();

    // fake columns, non existent objects etc
    assertFalse(db.updateColumn(null, null, null, null));

    assertFalse(db.updateColumn(FakeOrmModel.class, null, null, null));
    assertFalse(db.updateColumn(null, 1, null, null));
    assertFalse(db.updateColumn(null, null, "status", null));
    assertFalse(db.updateColumn(null, null, null, Boolean.FALSE));

    assertFalse(db.updateColumn(FakeOrmModel.class, 1, null, null));
    assertFalse(db.updateColumn(FakeOrmModel.class, null, "status", null));
    assertFalse(db.updateColumn(FakeOrmModel.class, null, null, Boolean.FALSE));
    assertFalse(db.updateColumn(null, 1, null, Boolean.FALSE));
    assertFalse(db.updateColumn(null, 1, "status", null));
    assertFalse(db.updateColumn(null, null, "status", Boolean.FALSE));

    assertTrue(db.updateColumn(FakeOrmModel.class, 1, "status", null));
    assertFalse(db.updateColumn(FakeOrmModel.class, 1, null, Boolean.FALSE));
    assertFalse(db.updateColumn(FakeOrmModel.class, null, "status", Boolean.FALSE));
    assertFalse(db.updateColumn(null, 1, "status", Boolean.FALSE));

    assertFalse(db.updateColumn(FakeOrmModel.class, 1, "fake_column", Boolean.FALSE));


    // update each columns one by one and query
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(FakeOrmModel.class, model2.getId(), "status", Boolean.FALSE));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(FakeOrmModel.class, model2.getId(), "total", 123));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(123), foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(FakeOrmModel.class, model2.getId(), "content", CONTENT_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(123), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertFalse(db.updateColumn(FakeOrmModel.class, model2.getId(), "created_at", DATE_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(123), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(db.updateColumn(FakeOrmModel.class, model2.getId(), "updated_at", DATE_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(new Integer(123), foundOne.getTotal());
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
        db.toString(model));

    model.initAllEmpty();
    assertEquals("FakeOrmModel [total=0, updatedAt=" + DATE_EMPTY + ", " +
            "createdAt=" + DATE_EMPTY + ", id=null, content=, status=false]",
        db.toString(model));

    model.initAllNotEmpty();
    assertEquals("FakeOrmModel [total=" + TOTAL_NOT_EMPTY + ", updatedAt=" + DATE_NOT_EMPTY + ", " +
            "createdAt=" + DATE_NOT_EMPTY + ", id=" + ID_NOT_EMPTY + ", content=" + CONTENT_NOT_EMPTY + ", status=" + STATUS_NOT_EMPTY + "]",
        db.toString(model));

    model.setId(99);
    assertEquals("FakeOrmModel [total=" + TOTAL_NOT_EMPTY + ", updatedAt=" + DATE_NOT_EMPTY + ", " +
            "createdAt=" + DATE_NOT_EMPTY + ", id=99, content=" + CONTENT_NOT_EMPTY + ", status=" + STATUS_NOT_EMPTY + "]",
        db.toString(model));
    // @formatter:on
  }

  @Test
  public void testGetConnectionFunction() {
    db.setConnection(null);
    assertNull(db.getConnection());

    assertNotNull(connection);
    db.setConnection(connection);
    assertTrue(connection == db.getConnection());
  }

  @Test
  public void testCloseConnectionFunction() throws SQLException {
    DataSource cpds = getNewPooledDataSource();
    Connection newConnection = cpds.getConnection();

    assertNotNull(newConnection);
    assertFalse(newConnection.isClosed());

    db.setConnection(newConnection);
    assertEquals(newConnection, db.getConnection());

    db.closeConnection();
    assertTrue(newConnection.isClosed());

    DataSources.destroy(cpds);
  }

  @Test
  public void testFieldChangedFunction() {
    model.initAllNull();

    assertFalse(db.fieldChanged(model, "id"));
    assertFalse(db.fieldChanged(model, "status"));
    assertFalse(db.fieldChanged(model, "total"));
    assertFalse(db.fieldChanged(model, "content"));

    model.setId(1);
    assertTrue(db.fieldChanged(model, "id"));
    assertFalse(db.fieldChanged(model, "status"));
    assertFalse(db.fieldChanged(model, "total"));
    assertFalse(db.fieldChanged(model, "content"));

    model.setStatus(STATUS_EMPTY);
    assertTrue(db.fieldChanged(model, "id"));
    assertTrue(db.fieldChanged(model, "status"));
    assertFalse(db.fieldChanged(model, "total"));
    assertFalse(db.fieldChanged(model, "content"));

    model.setTotal(TOTAL_EMPTY);
    assertTrue(db.fieldChanged(model, "id"));
    assertTrue(db.fieldChanged(model, "status"));
    assertTrue(db.fieldChanged(model, "total"));
    assertFalse(db.fieldChanged(model, "content"));

    model.setContent(CONTENT_EMPTY);
    assertTrue(db.fieldChanged(model, "id"));
    assertTrue(db.fieldChanged(model, "status"));
    assertTrue(db.fieldChanged(model, "total"));
    assertTrue(db.fieldChanged(model, "content"));

    model.setStatus(null);
    assertFalse(db.fieldChanged(model, "status"));

    model.setStatus(STATUS_NOT_EMPTY);
    assertTrue(db.fieldChanged(model, "status"));

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
  public void testCloseSqlObjectsFunction() throws SQLException {
    Connection conn = getNewConnection();
    assertNotNull(conn);
    String query = "SELECT NOW()";

    Statement st = conn.createStatement();
    PreparedStatement pst = conn.prepareStatement(query);
    ResultSet rs = pst.executeQuery();

    assertFalse(conn.isClosed());
    assertFalse(st.isClosed());
    assertFalse(pst.isClosed());
    assertFalse(rs.isClosed());

    DbHelper.closeSqlObjects(rs, pst, st, conn);

    assertTrue(st.isClosed());
    assertTrue(pst.isClosed());
    assertTrue(rs.isClosed());

    assertFalse(conn.isClosed());
    conn.close();
  }

  @Test
  public void testLoadFromFunction_ObjectResultset() throws SQLException {
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
    assertFalse(db.loadFrom(model, rs));
    model.setFrozen(false);
    assertTrue(db.loadFrom(model, rs));

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
  public void testLoadFromFunction_ObjectFieldvaluehashmap() throws SQLException {

    model.initAllNotEmpty();
    assertEquals(ID_NOT_EMPTY, model.getId());
    assertEquals(STATUS_NOT_EMPTY, model.getStatus());
    assertEquals(TOTAL_NOT_EMPTY, model.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, model.getContent());
    assertEquals(DATE_NOT_EMPTY, model.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, model.getUpdatedAt());

    HashMap<String, Object> fieldValueMap = new HashMap<>();
    fieldValueMap.put("id", model.getId());
    fieldValueMap.put("status", model.getStatus());
    fieldValueMap.put("total", model.getTotal());
    fieldValueMap.put("content", model.getContent());
    fieldValueMap.put("created_at", model.getCreatedAt());
    fieldValueMap.put("updated_at", model.getUpdatedAt());


    model2.initAllEmpty();
    model2.setFrozen(true);
    assertFalse(db.loadFrom(model2, fieldValueMap));
    model2.setFrozen(false);
    assertTrue(db.loadFrom(model2, fieldValueMap));

    assertEquals(ID_NOT_EMPTY, model2.getId());
    assertEquals(model.getStatus(), model2.getStatus());
    assertEquals(model.getTotal(), model2.getTotal());
    assertEquals(model.getContent(), model2.getContent());
    assertEquals(model.getCreatedAt(), model2.getCreatedAt());
    assertEquals(model.getUpdatedAt(), model2.getUpdatedAt());
  }

  @Test
  public void testSetConnectionFunction() throws SQLException {
    Connection conn = getNewConnection();
    assertNotNull(conn);

    db.setConnection(null);
    assertNull(db.getConnection());

    db.setConnection(conn);
    assertNotNull(db.getConnection());
    assertTrue(conn == db.getConnection());

    conn.close();
  }

  @Test
  public void testAssignResultSetToListOrObjectFunction() throws Exception {
    populateTestTable();
    ResultSet rSet = null;
    List<FakeOrmModel> resultList = null;
    FakeOrmModel tmpObject = null;

    Connection conn = getNewConnection();
    assertNotNull(conn);

    rSet = connection.createStatement().executeQuery("SELECT * FROM " + TABLE_NAME_FAKE_MODEL + " ORDER BY id ASC");
    db.assignResultSetToListOrObject(rSet, resultList, FakeOrmModel.class, tmpObject);
    assertNull(resultList);
    assertNull(tmpObject);

    // resultList is not null
    resultList = new ArrayList<>();
    db.assignResultSetToListOrObject(rSet, resultList, FakeOrmModel.class, null);
    assertEquals(db.count(FakeOrmModel.class), resultList.size());
    assertEquals(new Integer(1), resultList.get(0).getId());
    assertEquals(new Integer(2), resultList.get(1).getId());
    assertEquals(new Integer(3), resultList.get(2).getId());
    assertEquals(new Integer(4), resultList.get(3).getId());

    model.initAllNull();
    model.setId(5);
    foundOne = db.find(model);
    assertEquals(foundOne.getId(), resultList.get(4).getId());
    assertEquals(foundOne.getStatus(), resultList.get(4).getStatus());
    assertEquals(foundOne.getTotal(), resultList.get(4).getTotal());
    assertEquals(foundOne.getContent(), resultList.get(4).getContent());
    assertEquals(foundOne.getCreatedAt(), resultList.get(4).getCreatedAt());
    assertEquals(foundOne.getUpdatedAt(), resultList.get(4).getUpdatedAt());

    // object
    tmpObject = new FakeOrmModel(db);
    rSet.close();
    rSet = connection.createStatement().executeQuery("SELECT * FROM " + TABLE_NAME_FAKE_MODEL + " WHERE id=5");
    assertTrue(rSet.next());
    db.assignResultSetToListOrObject(rSet, null, null, tmpObject);
    assertEquals(foundOne.getId(), tmpObject.getId());
    assertEquals(foundOne.getStatus(), tmpObject.getStatus());
    assertEquals(foundOne.getTotal(), tmpObject.getTotal());
    assertEquals(foundOne.getContent(), tmpObject.getContent());
    assertEquals(foundOne.getCreatedAt(), tmpObject.getCreatedAt());
    assertEquals(foundOne.getUpdatedAt(), tmpObject.getUpdatedAt());

    rSet.close();
    conn.close();
  }

  @Test
  public void testDeleteFunction() {
    populateTestTable();

    assertFalse(db.delete(null));

    model.initAllNull();
    assertFalse(db.delete(model));

    model.initAllNotEmpty();
    model.setId(null);
    assertFalse(db.delete(model));

    model.initAllNull();
    model.setId(5);
    foundOne = db.find(model);
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());

    int countModelBefore = db.count(FakeOrmModel.class);
    assertTrue(db.delete(model));
    int countModelAfter = db.count(FakeOrmModel.class);

    model.initAllNull();
    model.setId(5);
    foundOne = db.find(model);
    assertNull(foundOne);
    assertTrue(countModelAfter == countModelBefore - 1);
  }

  @Test
  public void testTetQueryForLogFunction() {
    PreparedStatement stSelect = null;
    try {
      stSelect = connection.prepareStatement("SELECT * FROM service_providers WHERE name=? LIMIT 1");
      stSelect.setString(1, "SelectiveVPN");

      String queryDirty = stSelect.toString();
      String queryClean = DbHelper.getQueryForLog(stSelect);

      assertFalse(queryDirty.equals("[SELECT * FROM service_providers WHERE name='SelectiveVPN' LIMIT 1]"));
      assertTrue(queryClean.equals("[SELECT * FROM service_providers WHERE name='SelectiveVPN' LIMIT 1]"));
    } catch (Exception e) {
    }
  }

  @Test
  public void testDeleteAllFunction_ClassColumnValueslist() {
    populateTestTable();

    int countModelBefore = db.count(FakeOrmModel.class);
    assertFalse(db.deleteAll(null, null, (List<FakeOrmModel>) null));
    assertFalse(db.deleteAll(FakeOrmModel.class, null, (List<FakeOrmModel>) null));
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", (List<FakeOrmModel>) null));
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", Arrays.asList((Integer) null)));
    int countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore);

    countModelBefore = db.count(FakeOrmModel.class);
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", Arrays.asList(-1)));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore);

    countModelBefore = db.count(FakeOrmModel.class);
    assertTrue(db.deleteAll(FakeOrmModel.class, "status", Arrays.asList(0)));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore - 2);

    countModelBefore = db.count(FakeOrmModel.class);
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", Arrays.asList(0)));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore);

    countModelBefore = db.count(FakeOrmModel.class);
    assertTrue(db.deleteAll(FakeOrmModel.class, "total", Arrays.asList(2)));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore - 1);
  }

  @Test
  public void testDeleteAllFunction_ClassColumnValuesarray() {
    populateTestTable();

    int countModelBefore = db.count(FakeOrmModel.class);
    assertFalse(db.deleteAll(null, null, (FakeOrmModel[]) null));
    assertFalse(db.deleteAll(FakeOrmModel.class, null, (FakeOrmModel[]) null));
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", (FakeOrmModel[]) null));
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", new Integer[]{}));
    int countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore);

    countModelBefore = db.count(FakeOrmModel.class);
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", new Integer[]{-1}));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore);

    countModelBefore = db.count(FakeOrmModel.class);
    assertTrue(db.deleteAll(FakeOrmModel.class, "status", new Integer[]{0}));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore - 2);

    countModelBefore = db.count(FakeOrmModel.class);
    assertFalse(db.deleteAll(FakeOrmModel.class, "status", new Integer[]{0}));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore);

    countModelBefore = db.count(FakeOrmModel.class);
    assertTrue(db.deleteAll(FakeOrmModel.class, "total", new Integer[]{2}));
    countModelAfter = db.count(FakeOrmModel.class);
    assertTrue(countModelAfter == countModelBefore - 1);
  }

  @Test
  public void testFindIdsFunction_ClassWhere() {
    populateTestTable();

    List<Integer> foundIds = null;
    Integer foundId = null;

    foundIds = db.findIds(FakeOrmModel.class, null);
    assertEquals(5, foundIds.size());

    foundIds = db.findIds(FakeOrmModel.class, "total IS NOT NULL");
    assertEquals(3, foundIds.size());

    foundIds = db.findIds(FakeOrmModel.class, "total IS NULL");
    assertEquals(2, foundIds.size());

    foundIds = db.findIds(FakeOrmModel.class, "total='" + 0 + "'");
    assertEquals(1, foundIds.size());
    foundId = foundIds.get(0);
    assertEquals(new Integer(3), foundId);

    foundIds = db.findIds(FakeOrmModel.class, "total='" + 1 + "'");
    assertEquals(1, foundIds.size());
    foundId = foundIds.get(0);
    assertEquals(new Integer(4), foundId);

    foundIds = db.findIds(FakeOrmModel.class, "status='" + 1 + "'");
    assertEquals(2, foundIds.size());
    foundId = foundIds.get(0);
    assertEquals(new Integer(4), foundId);

    foundId = foundIds.get(1);
    assertEquals(new Integer(5), foundId);

    foundIds = db.findIds(FakeOrmModel.class, "status='" + 0 + "'");
    assertEquals(2, foundIds.size());
    foundId = foundIds.get(0);
    assertEquals(new Integer(2), foundId);
    foundId = foundIds.get(1);
    assertEquals(new Integer(3), foundId);

    foundIds = db.findIds(FakeOrmModel.class, "total>='" + 0 + "'");
    assertEquals(3, foundIds.size());
    foundIds = db.findIds(FakeOrmModel.class, "total>='" + 1 + "'");
    assertEquals(2, foundIds.size());
    foundIds = db.findIds(FakeOrmModel.class, "total>='" + 2 + "'");
    assertEquals(1, foundIds.size());

    foundIds = db.findIds(FakeOrmModel.class, "status=0 AND total>='" + 0 + "'");
    assertEquals(1, foundIds.size());
    foundIds = db.findIds(FakeOrmModel.class, "status=0 AND total>='" + 1 + "'");
    assertEquals(0, foundIds.size());
  }

  @Test
  public void testUpdateAllFunction_ClassColumnValueIdlist() {
    populateTestTable();

    String column = "total";

    assertEquals(0, db.updateAll(null, (String) null, null, null));

    assertEquals(0, db.updateAll(FakeOrmModel.class, (String) null, null, null));
    assertEquals(0, db.updateAll(null, column, null, null));
    assertEquals(0, db.updateAll(null, (String) null, new Object(), null));
    assertEquals(0, db.updateAll(null, (String) null, null, new ArrayList<>()));

    assertEquals(0, db.updateAll(FakeOrmModel.class, column, null, null));
    assertEquals(0, db.updateAll(FakeOrmModel.class, (String) null, new Object(), null));
    assertEquals(0, db.updateAll(FakeOrmModel.class, (String) null, null, new ArrayList<>()));
    assertEquals(0, db.updateAll(null, column, new Object(), null));
    assertEquals(0, db.updateAll(null, column, null, new ArrayList<>()));
    assertEquals(0, db.updateAll(null, (String) null, new Object(), new ArrayList<>()));

    assertEquals(0, db.updateAll(FakeOrmModel.class, column, new Object(), null));
    assertEquals(0, db.updateAll(null, column, new Object(), new ArrayList<>()));
    assertEquals(0, db.updateAll(FakeOrmModel.class, (String) null, new Object(), new ArrayList<>()));
    assertEquals(0, db.updateAll(FakeOrmModel.class, column, null, new ArrayList<>()));

    assertEquals(0, db.updateAll(FakeOrmModel.class, column, new Object(), new ArrayList<>()));


    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertNull(foundList.get(1).getTotal());
    assertEquals(new Integer(0), foundList.get(2).getTotal());
    assertEquals(new Integer(1), foundList.get(3).getTotal());
    assertEquals(new Integer(2), foundList.get(4).getTotal());

    assertEquals(4, db.updateAll(FakeOrmModel.class, column, TOTAL_NOT_EMPTY, Arrays.asList(2, 3, 4, 5)));

    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(1).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(2).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(3).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(4).getTotal());
  }

  @Test
  public void testUpdateAllFunction_ClassColumnsValuesIdlist() {
    populateTestTable();

    String[] columns = {"total", "content"};
    String NEW_CONTENT = "Hello kitty!";

    assertEquals(0, db.updateAll(null, (String[]) null, null, null));

    assertEquals(0, db.updateAll(FakeOrmModel.class, (String[]) null, null, null));
    assertEquals(0, db.updateAll(null, columns, null, null));
    assertEquals(0, db.updateAll(null, (String[]) null, new Object[0], null));
    assertEquals(0, db.updateAll(null, (String[]) null, null, new ArrayList<>()));

    assertEquals(0, db.updateAll(FakeOrmModel.class, columns, null, null));
    assertEquals(0, db.updateAll(FakeOrmModel.class, (String[]) null, new Object[0], null));
    assertEquals(0, db.updateAll(FakeOrmModel.class, (String[]) null, null, new ArrayList<>()));
    assertEquals(0, db.updateAll(null, columns, new Object[0], null));
    assertEquals(0, db.updateAll(null, columns, null, new ArrayList<>()));
    assertEquals(0, db.updateAll(null, (String[]) null, new Object[0], new ArrayList<>()));

    assertEquals(0, db.updateAll(FakeOrmModel.class, columns, new Object[0], null));
    assertEquals(0, db.updateAll(null, columns, new Object[0], new ArrayList<>()));
    assertEquals(0, db.updateAll(FakeOrmModel.class, (String[]) null, new Object[0], new ArrayList<>()));
    assertEquals(0, db.updateAll(FakeOrmModel.class, columns, null, new ArrayList<>()));

    assertEquals(0, db.updateAll(FakeOrmModel.class, columns, new Object[0], new ArrayList<>()));


    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertNull(foundList.get(1).getTotal());
    assertEquals(new Integer(0), foundList.get(2).getTotal());
    assertEquals(new Integer(1), foundList.get(3).getTotal());
    assertEquals(new Integer(2), foundList.get(4).getTotal());
    assertNull(foundList.get(1).getContent());
    assertNull(foundList.get(2).getContent());
    assertEquals(CONTENT_NOT_EMPTY, foundList.get(3).getContent());
    assertEquals(CONTENT_NOT_EMPTY, foundList.get(4).getContent());

    assertEquals(4, db.updateAll(FakeOrmModel.class, columns, new Object[]{TOTAL_NOT_EMPTY, NEW_CONTENT},
        Arrays.asList(2, 3, 4, 5)));

    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(1).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(2).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(3).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(4).getTotal());
    assertEquals(NEW_CONTENT, foundList.get(1).getContent());
    assertEquals(NEW_CONTENT, foundList.get(2).getContent());
    assertEquals(NEW_CONTENT, foundList.get(3).getContent());
    assertEquals(NEW_CONTENT, foundList.get(4).getContent());
  }

  @Test
  public void testEscapeFunction() {
    assertNull(DbHelper.escape(null, true));
    assertNull(DbHelper.escape(null, false));

    assertEquals("", DbHelper.escape("", true));
    assertEquals("", DbHelper.escape("", false));

    assertEquals("&#59;", DbHelper.escape(";", true));
    assertEquals("&#59;", DbHelper.escape(";", false));

    assertEquals("\\\\", DbHelper.escape("\\", true));
    assertEquals("\\\\", DbHelper.escape("\\", false));

    assertEquals("&quot;", DbHelper.escape("\"", true));
    assertEquals("&quot;", DbHelper.escape("\"", false));

    assertEquals("'", DbHelper.escape("'", true));
    assertEquals("'", DbHelper.escape("'", false));
    assertEquals("'", DbHelper.escape(" '", true));
    assertEquals("'", DbHelper.escape(" '", false));
    assertEquals("'", DbHelper.escape("' ", true));
    assertEquals("'", DbHelper.escape("' ", false));
    assertEquals("'", DbHelper.escape(" ' ", true));
    assertEquals("'", DbHelper.escape(" ' ", false));

    assertEquals("''", DbHelper.escape("''", true));
    assertEquals("''", DbHelper.escape("''", false));
    assertEquals("''", DbHelper.escape(" ''", true));
    assertEquals("''", DbHelper.escape(" ''", false));
    assertEquals("''", DbHelper.escape("'' ", true));
    assertEquals("''", DbHelper.escape("'' ", false));
    assertEquals("''", DbHelper.escape(" '' ", true));
    assertEquals("''", DbHelper.escape(" '' ", false));

    assertEquals("'ain''t ya'", DbHelper.escape("'ain't ya'", true));
    assertEquals("'ain\\'t ya'", DbHelper.escape("'ain't ya'", false));
    assertEquals("'ain''t ya'", DbHelper.escape(" 'ain't ya'", true));
    assertEquals("'ain\\'t ya'", DbHelper.escape(" 'ain't ya'", false));
    assertEquals("'ain''t ya'", DbHelper.escape("'ain't ya' ", true));
    assertEquals("'ain\\'t ya'", DbHelper.escape("'ain't ya' ", false));
    assertEquals("'ain''t ya'", DbHelper.escape(" 'ain't ya' ", true));
    assertEquals("'ain\\'t ya'", DbHelper.escape(" 'ain't ya' ", false));

    assertEquals("total=total+1", DbHelper.escape("total=total+1", true));
    assertEquals("total=total+1", DbHelper.escape("total=total+1", false));
    assertEquals("total=total+1", DbHelper.escape(" total=total+1", true));
    assertEquals("total=total+1", DbHelper.escape(" total=total+1", false));
    assertEquals("total=total+1", DbHelper.escape("total=total+1 ", true));
    assertEquals("total=total+1", DbHelper.escape("total=total+1 ", false));
    assertEquals("total=total+1", DbHelper.escape(" total=total+1 ", true));
    assertEquals("total=total+1", DbHelper.escape(" total=total+1 ", false));

    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape("created_at=DATE('Y-M-D')", true));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape("created_at=DATE('Y-M-D')", false));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape(" created_at=DATE('Y-M-D')", true));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape(" created_at=DATE('Y-M-D')", false));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape("created_at=DATE('Y-M-D') ", true));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape("created_at=DATE('Y-M-D') ", false));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape(" created_at=DATE('Y-M-D') ", true));
    assertEquals("created_at=DATE('Y-M-D')", DbHelper.escape(" created_at=DATE('Y-M-D') ", false));
  }

  @Test
  public void testUpdateAllRawFunction_ClassColumnValueIdlist() {
    populateTestTable();

    String column = "total";

    assertEquals(0, db.updateAllRaw(null, (String) null, null, null));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, null, null));
    assertEquals(0, db.updateAllRaw(null, column, null, null));
    assertEquals(0, db.updateAllRaw(null, (String) null, new Object(), null));
    assertEquals(0, db.updateAllRaw(null, (String) null, null, new ArrayList<>()));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, null, null));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, new Object(), null));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, null, new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(null, column, new Object(), null));
    assertEquals(0, db.updateAllRaw(null, column, null, new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(null, (String) null, new Object(), new ArrayList<>()));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, new Object(), null));
    assertEquals(0, db.updateAllRaw(null, column, new Object(), new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, new Object(), new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, null, new ArrayList<>()));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, new Object(), new ArrayList<>()));


    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertNull(foundList.get(1).getTotal());
    assertEquals(new Integer(0), foundList.get(2).getTotal());
    assertEquals(new Integer(1), foundList.get(3).getTotal());
    assertEquals(new Integer(2), foundList.get(4).getTotal());

    assertEquals(4, db.updateAllRaw(FakeOrmModel.class, column, TOTAL_NOT_EMPTY, Arrays.asList(2, 3, 4, 5)));

    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(1).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(2).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(3).getTotal());
    assertEquals(TOTAL_NOT_EMPTY, foundList.get(4).getTotal());

    column = "content";
    String RAW_CONTENT = "'Hello!  , ; ( ' ) \\ \" %  kitty!'";
    String DB_CONTENT = DbHelper.escape(RAW_CONTENT.substring(1, RAW_CONTENT.length() - 1), db.isAnsiSql())
        .replace((db.isAnsiSql() ? "''" : "\\'"), "'").replace("\\\\", "\\");

    assertEquals(0, db.updateAllRaw(null, (String) null, null, null));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, null, null));
    assertEquals(0, db.updateAllRaw(null, column, null, null));
    assertEquals(0, db.updateAllRaw(null, (String) null, new Object(), null));
    assertEquals(0, db.updateAllRaw(null, (String) null, null, new ArrayList<>()));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, null, null));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, new Object(), null));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, null, new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(null, column, new Object(), null));
    assertEquals(0, db.updateAllRaw(null, column, null, new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(null, (String) null, new Object(), new ArrayList<>()));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, new Object(), null));
    assertEquals(0, db.updateAllRaw(null, column, new Object(), new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, (String) null, new Object(), new ArrayList<>()));
    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, null, new ArrayList<>()));

    assertEquals(0, db.updateAllRaw(FakeOrmModel.class, column, new Object(), new ArrayList<>()));


    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertNull(foundList.get(1).getContent());
    assertNull(foundList.get(2).getContent());
    assertEquals(CONTENT_NOT_EMPTY, foundList.get(3).getContent());
    assertEquals(CONTENT_NOT_EMPTY, foundList.get(4).getContent());

    assertEquals(4, db.updateAllRaw(FakeOrmModel.class, column, RAW_CONTENT, Arrays.asList(2, 3, 4, 5)));


    foundList = db.findAll(FakeOrmModel.class, null);
    assertEquals(5, foundList.size());
    assertEquals(DB_CONTENT, foundList.get(1).getContent());
    assertEquals(DB_CONTENT, foundList.get(2).getContent());
    assertEquals(DB_CONTENT, foundList.get(3).getContent());
    assertEquals(DB_CONTENT, foundList.get(4).getContent());
  }

  @Test
  public void testUpdateFieldFunction() throws SQLException {
    populateTestTable();

    Integer total = new Integer(123);

    // fake columns, non existent objects etc
    assertFalse(db.updateColumn(null, null, null));
    assertFalse(db.updateColumn(null, "status", Boolean.FALSE));
    assertFalse(db.updateColumn(model, null, Boolean.FALSE));
    assertFalse(db.updateColumn(model, "status", null));
    assertFalse(db.updateColumn(null, null, Boolean.FALSE));
    assertFalse(db.updateColumn(null, "status", null));
    assertFalse(db.updateColumn(model, null, null));
    assertFalse(db.updateColumn(model, "status", Boolean.FALSE));
    model.initAllNull(); // no id to match
    assertFalse(db.updateColumn(model, "status", Boolean.FALSE));
    model.setId(1);
    assertFalse(db.updateColumn(model, "fake_column", Boolean.FALSE));
    assertTrue(db.updateColumn(model, "status", Boolean.FALSE));
    assertTrue(db.updateColumn(model, "status", null));

    // update each columns one by one and query
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertNull(foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(model2.updateField("status", Boolean.FALSE));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertNull(foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(model2.updateField("total", total));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertNull(foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertTrue(model2.updateField("content", CONTENT_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertFalse(model2.updateField("created_at", DATE_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());

    model2 = db.find(FakeOrmModel.class, "id=1");
    assertFalse(model2.updateField("updated_at", DATE_NOT_EMPTY));
    foundOne = db.find(FakeOrmModel.class, "id=1");
    assertNotNull(foundOne);
    assertEquals(new Integer(1), foundOne.getId());
    assertEquals(Boolean.FALSE, foundOne.getStatus());
    assertEquals(total, foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertNull(foundOne.getCreatedAt());
    assertNotNull(foundOne.getUpdatedAt());
  }

  @Test
  public void testReloadFunction() {
    populateTestTable();

    assertFalse(db.reload((FakeOrmModel) null));

    foundOne = db.find(FakeOrmModel.class, "id=5");
    assertNotNull(foundOne);
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());

    foundOne.initAllNull();
    assertFalse(db.reload(foundOne));
    foundOne.setId(5);
    foundOne.setFrozen(true);
    assertFalse(db.reload(foundOne));
    foundOne.setFrozen(false);

    assertNull(foundOne.getOldValue("status"));
    assertNull(foundOne.getOldValue("total"));
    assertNull(foundOne.getOldValue("content"));
    assertNull(foundOne.getOldValue("created_at"));
    assertNull(foundOne.getOldValue("updated_at"));

    assertTrue(db.reload(foundOne));
    assertEquals(new Integer(5), foundOne.getId());
    assertEquals(Boolean.TRUE, foundOne.getStatus());
    assertEquals(new Integer(2), foundOne.getTotal());
    assertEquals(CONTENT_NOT_EMPTY, foundOne.getContent());
    assertEquals(DATE_NOT_EMPTY, foundOne.getCreatedAt());
    assertEquals(DATE_NOT_EMPTY, foundOne.getUpdatedAt());

    // reload updated the oldMap
    assertEquals(foundOne.getOldValue("status"), foundOne.getStatus());
    assertEquals(foundOne.getOldValue("total"), foundOne.getTotal());
    assertEquals(foundOne.getOldValue("content"), foundOne.getContent());
    assertEquals(foundOne.getOldValue("created_at"), foundOne.getCreatedAt());
    assertEquals(foundOne.getOldValue("updated_at"), foundOne.getUpdatedAt());
  }
}
