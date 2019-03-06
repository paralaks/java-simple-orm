package paralaks_gmail_com.simpleorm.model;

import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.CONTENT_BY_ONCREATE;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.CONTENT_BY_ONUPDATE;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.CONTENT_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.CONTENT_NOT_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.DATE_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.DATE_NOT_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.ERROR_CREATED_AT;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.ERROR_ID_MAXED_OUT;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.ERROR_STATUS;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.ERROR_TOTAL;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.ID_NOT_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.MAX_ID_LIMIT;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.STATUS_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.STATUS_NOT_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.THRESHOLD_AFTER_DESTROY_ID;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.THRESHOLD_AFTER_FIND_AND_LOAD_ID;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.THRESHOLD_BEFORE_DESTROY_ID;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.THRESHOLD_INTERVAL;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.TOTAL_EMPTY;
import static paralaks_gmail_com.simpleorm.helper.FakeOrmModelTestHelper.TOTAL_NOT_EMPTY;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import paralaks_gmail_com.simpleorm.helper.DbHelper;

public class FakeOrmModel extends OrmModel {

  private Boolean status;
  private Integer total;
  private String content;
  private Date createdAt;
  private Date updatedAt;

  private boolean skipValidation = true;

  public FakeOrmModel(DbHelper dbObject) {
    super(dbObject);
    initAllNull();
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public boolean getSkipValidation() {
    return skipValidation;
  }

  public void setSkipValidation(boolean skipValidation) {
    this.skipValidation = skipValidation;
  }

  private void initOldMap() {
    oldMap = new HashMap<>();
    if (id != null)
      oldMap.put("id", id);
    if (status != null)
      oldMap.put("status", status);
    if (total != null)
      oldMap.put("total", total);
    if (content != null)
      oldMap.put("content", content);
    if (createdAt != null)
      oldMap.put("created_at", createdAt);
    if (updatedAt != null)
      oldMap.put("updated_at", updatedAt);
  }

  public void initAllNull() {
    errorMessages = null;
    frozen = false;

    id = null;
    status = null;
    total = null;
    content = null;
    createdAt = null;
    updatedAt = null;

    initOldMap();
  }

  public void initAllEmpty() {
    errorMessages = null;
    frozen = false;

    id = null;
    status = STATUS_EMPTY;
    total = TOTAL_EMPTY;
    content = CONTENT_EMPTY;
    createdAt = DATE_EMPTY;
    updatedAt = DATE_EMPTY;

    initOldMap();
  }

  public void initAllNotEmpty() {
    errorMessages = null;
    frozen = false;

    id = ID_NOT_EMPTY;
    status = STATUS_NOT_EMPTY;
    total = TOTAL_NOT_EMPTY;
    content = CONTENT_NOT_EMPTY;
    createdAt = DATE_NOT_EMPTY;
    updatedAt = DATE_NOT_EMPTY;

    initOldMap();
  }

  @Override
  public void beforeValidate() {
    if (skipValidation)
      return;

    if (total == null)
      addError("total", ERROR_TOTAL);
  }

  @Override
  public void validate() {
    if (skipValidation)
      return;

    if (status == null)
      addError("status", ERROR_STATUS);

    if (id != null && createdAt != null && createdAt.getTime() > Calendar.getInstance().getTime().getTime())
      addError("created_at", ERROR_CREATED_AT);
  }

  @Override
  public void beforeSave() {
    if (skipValidation)
      return;

    if (content == null)
      content = CONTENT_EMPTY;
  }

  @Override
  public void onCreate() {
    if (skipValidation)
      return;

    if (content == null || content.isEmpty())
      content = CONTENT_BY_ONCREATE;
  }

  @Override
  public void onUpdate() {
    if (skipValidation)
      return;

    content = CONTENT_BY_ONUPDATE;
  }

  @Override
  public void afterSave() {
    if (skipValidation)
      return;

    if (id != null)
      total = id + 1;

    if (id != null && id >= MAX_ID_LIMIT)
      addAfterSaveError(ERROR_ID_MAXED_OUT);
  }

  @Override
  public boolean beforeDestroy() {
    if (id != null && id >= THRESHOLD_BEFORE_DESTROY_ID && id < THRESHOLD_BEFORE_DESTROY_ID + THRESHOLD_INTERVAL) {
      addError("id", "beforeDestroyTest");
      return false;
    }

    return true;
  }

  @Override
  public void afterDestroy() {
    if (id != null && id >= THRESHOLD_AFTER_DESTROY_ID && id < THRESHOLD_AFTER_DESTROY_ID + THRESHOLD_INTERVAL) {
      addError("id", "afterDestroyTest");
    }
  }

  @Override
  public void afterFindAndLoad() {
    if (id != null && id >= THRESHOLD_AFTER_FIND_AND_LOAD_ID
        && id < THRESHOLD_AFTER_FIND_AND_LOAD_ID + THRESHOLD_INTERVAL) {
      addError("id", "afterFindAndLoadTest");
    }
  }

}
