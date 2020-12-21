package com.notebook.dao.mapper;

import com.notebook.domain.RecordDo;
import com.notebook.service.RecordService;
import com.notebook.service.ShareRecordService;
import com.notebook.service.ShareService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Set;

/**
 * Project: notebook
 * File: TestShareRecordService
 *
 * @author evan
 * @date 2020/12/8
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestShareRecordService {
    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private RecordService recordService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private ShareRecordService shareRecordService;

    @Test
    public void testDeleteRecord() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);

        try {
            Integer recordId = 26;
            boolean result = recordService.removeById(recordId);
            if (result) {
                Set<Integer> emptyShareIds = this.shareRecordService.deleteShareRecord(recordId);
                // 删除没有记录share
                shareService.removeByIds(emptyShareIds);
            }
        } catch (Exception e) {
            txManager.rollback(status);
            throw new RuntimeException(e);
        }
        txManager.commit(status);
    }
}
