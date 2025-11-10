package com.example.payroll.external.importer.service;

import java.util.*;
import com.example.payroll.database.entity.WorkingMonthLog;

public interface SyncWorkingMonthLogService {

  void syncWorkingMonthLogs(List<WorkingMonthLog> workingMonthLogsToSync);

}
